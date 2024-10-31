package event.pipeline;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;

import org.apache.http.HttpHost;
import com.google.gson.Gson;
import java.util.Properties;
import java.util.Arrays;
import java.util.List;


public class KafkaStream {
    private static RestHighLevelClient elasticClient;
    private static Gson gson = new Gson();

    public static void main(String[] args) throws InterruptedException {
        // Kafka Streams Konfiguration
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "camunda-event-stream");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        //Erstellen der Kafka Topics
        createKafkaTopics(props);

        waitForTopics(props, Arrays.asList("camunda-events", "camunda-variables"));

        StreamsBuilder builder = new StreamsBuilder();

        // Camunda Variables Stream als KTable zuerst erstellen
        KTable<String, String> camundaVariablesTable = builder.table("camunda-variables") 
            .mapValues(value -> value.toString())
            .toStream()
            .selectKey((key, value) -> extractProcessInstanceKeyOfCamundaVariablesTopic(value))
            .toTable();

        // Camunda Events Stream erstellen
        KStream<String, String> camundaEventsStream = builder.stream("camunda-events")
            .mapValues(value -> value.toString())
            .selectKey((key, value) -> extractProcessInstanceKeyOfCamundaEventsTopic(value));

        // Verarbeitung der Events und Join mit der OrderID aus camunda_variables
        KStream<String, String> enrichedEventsStream = camundaEventsStream.leftJoin(
            camundaVariablesTable,
            (eventValue, variableValue) -> {
                if (variableValue == null || eventValue == null) {
                    System.err.println("Fehlende Variable oder Event: Event - " + eventValue + ", Variable - " + variableValue);
                    return eventValue;  // Oder alternative Behandlung
                }
                return enrichEventWithOrderId(eventValue, variableValue);
            }
        );

        // Ausgabe des Streams nach Elasticsearch
        enrichedEventsStream.foreach((key, event) -> sendToElasticsearch(event));

        // Kafka Streams starten
        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();

        // Elasticsearch-Client initialisieren
        initElasticsearchClient();

        // HTTP-Server für Healthcheck
        HealthCheckServer.startHealthCheckServer(streams);

        // Shutdown-Hook
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    // Funktion zur Erstellung der benötigten Kafka Topics
    public static void createKafkaTopics(Properties props) {
        try (AdminClient adminClient = AdminClient.create(props)) {
            List<NewTopic> topics = Arrays.asList(
                new NewTopic("camunda-events", 1, (short) 1),
                new NewTopic("camunda-variables", 1, (short) 1)
            );
            adminClient.createTopics(topics);
            System.out.println("Kafka-Topics erstellt oder existieren bereits.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Funktion um auf die Erstellung der Topics zu warten
    private static void waitForTopics(Properties props, List<String> topicNames) throws InterruptedException {
        try (AdminClient adminClient = AdminClient.create(props)) {
            boolean topicsExist = false;
            while (!topicsExist) {
                var existingTopics = adminClient.listTopics().names().get();
                topicsExist = existingTopics.containsAll(topicNames);
                if (!topicsExist) {
                    System.out.println("Warte auf die Erstellung der Kafka-Topics...");
                    Thread.sleep(5000); // Warte 5 Sekunden
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Funktion zur Anreicherung des Events mit der OrderID
    private static String enrichEventWithOrderId(String event, String variable) {
        Event camundaEvent;
        try {
            camundaEvent = gson.fromJson(event, Event.class);
        } catch (Exception e) {
            System.err.println("Fehler beim Parsen des Events: " + event);
            e.printStackTrace();
            return event; // Rückgabe des Originalevents im Fehlerfall
        }
    
        if (variable != null) {
            Variable camundaVariable;
            try {
                camundaVariable = gson.fromJson(variable, Variable.class);
            } catch (Exception e) {
                System.err.println("Fehler beim Parsen der Variablen: " + variable);
                e.printStackTrace();
                return event;
            }
    
            // Hier wird die CaseID angereichert, ohne dass andere Felder überschrieben werden.
            if ("orderID".equals(camundaVariable.getName())) {
                camundaEvent.setCaseID(camundaVariable.getValue());
            }
        }

        // Event nach dem Anreichern loggen
        System.out.println("Angereichertes Event: " + gson.toJson(camundaEvent));
    
        // Rückgabe des vollständigen Events, einschließlich der neuen CaseID
        return gson.toJson(camundaEvent); 
    }    

    private static String extractProcessInstanceKeyOfCamundaEventsTopic(String json) {
        Event event;
        try {
            event = gson.fromJson(json, Event.class);
        } catch (Exception e) {
            System.err.println("Fehler beim Extrahieren von processInstanceKey aus JSON: " + json);
            e.printStackTrace();
            return null;
        }

        if (event.getProcessInstanceKey() == null) {
            System.err.println("processInstanceKey ist null für das Event: " + json);
            return null;
        }

        return String.valueOf(event.getProcessInstanceKey());
    }

    private static String extractProcessInstanceKeyOfCamundaVariablesTopic(String json) {
        Variable variable;
        try {
            variable = gson.fromJson(json, Variable.class);
        } catch (Exception e) {
            System.err.println("Fehler beim Extrahieren des eindeutigen Keys aus JSON: " + json);
            e.printStackTrace();
            return null;
        }
    
        return String.valueOf(variable.getprocessInstanceKey());
    }

    // Elasticsearch-Client initialisieren
    private static void initElasticsearchClient() {
        try {
            elasticClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("elasticsearch", 9200, "http"))
            );
            System.out.println("Elasticsearch-Client erfolgreich initialisiert.");
        } catch (Exception e) {
            System.err.println("Fehler beim Initialisieren des Elasticsearch-Clients.");
            e.printStackTrace();
        }
    }

    // Funktion zum Senden der angereicherten Events nach Elasticsearch
    private static void sendToElasticsearch(String event) {
        try {
            System.out.println("Sende Event an Elasticsearch: " + event);
    
            Event camundaEvent = gson.fromJson(event, Event.class);
            if (camundaEvent.getProcessInstanceKey() == null || camundaEvent.getCaseID() == null) {
                System.err.println("Fehlende Daten im Event: " + event);
                return; // Überspringen, wenn kritische Daten fehlen
            }
    
            String enrichedEvent = gson.toJson(camundaEvent);
    
            IndexRequest request = new IndexRequest("end_to_end_event_log").source(enrichedEvent, XContentType.JSON);
            var response = elasticClient.index(request, RequestOptions.DEFAULT);
    
            // Elasticsearch-Antwort loggen
            if (response != null) {
                System.out.println("Elasticsearch Response: " + response.toString());
                if (response.status() != null && response.status().getStatus() == 201) {
                    System.out.println("Dokument erfolgreich mit ID: " + response.getId() + " in Elasticsearch gespeichert.");
                } else {
                    System.err.println("Fehler beim Speichern des Dokuments: " + response.status());
                }
            } else {
                System.err.println("Die Antwort von Elasticsearch war null.");
            }
        } catch (NullPointerException e) {
            // NullPointerException explizit loggen
            System.err.println("NullPointerException: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
             System.err.println("Fehler beim Senden an Elasticsearch: " + event);
             e.printStackTrace();
        }
        
    }    

}