package event.pipeline;

import fi.iki.elonen.NanoHTTPD;
import org.apache.kafka.streams.KafkaStreams;
import java.io.IOException;

public class HealthCheckServer extends NanoHTTPD {

    private KafkaStreams streams;

    public HealthCheckServer(int port, KafkaStreams streams) {
        super(port);
        this.streams = streams;
    }

    @Override
    public Response serve(IHTTPSession session) {
        String responseMsg;
        if (streams.state().isRunningOrRebalancing()) {
            responseMsg = "RUNNING";
        } else {
            responseMsg = "NOT_RUNNING";
        }
        return newFixedLengthResponse(Response.Status.OK, "text/plain", responseMsg);
    }

    public static void startHealthCheckServer(KafkaStreams streams) {
        try {
            HealthCheckServer server = new HealthCheckServer(9095, streams);
            server.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
            System.out.println("Healthcheck server started on port 8080.");
        } catch (IOException e) {
            System.err.println("Failed to start healthcheck server.");
            e.printStackTrace();
        }
    }
}