package de.thkoeln.inf.bpalab.demofactory.bpalabmanufacturing.mqtt.subscriber;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.thkoeln.inf.bpalab.demofactory.bpalabmanufacturing.mqtt.FtfactoryMQTTClient;
import de.thkoeln.inf.bpalab.demofactory.bpalabmanufacturing.mqtt.CloudTopics;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Service
public class FtfactoryBME680 implements ISubscriber {
	private static Logger log = LoggerFactory.getLogger(FtfactoryBME680.class);
	
	private boolean messageReceived = false;

	@Autowired
	private CloudTopics cloudTopics;

	@Autowired
	protected FtfactoryMQTTClient ftfactoryMQTTClient;

	@PostConstruct
	public void postConstruct() {
//		prepare Subscriber
		log.info("PostConstruct {}", getSubscriptionTopic());
		ftfactoryMQTTClient.subscribe(getSubscriptionTopic(), this);
	}
	
	@Override
	public String getSubscriptionTopic() {
		return this.cloudTopics.getCloudBME680Topic();
	}

	@Override
	public boolean isMessageReceived() {
		return messageReceived;
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) {
		// Sample Topic: baplab/ftfactory/i/bme680
		// PayLoad(JSON): {"ts":"2023-06-05T15:20:41.613Z","t":15.6,"rt":0,"h":56,"rh":0,"p":56.45,"iaq":60,"aq":0,"gr":0}
		ObjectMapper om = new ObjectMapper();
		FtfactoryBME680 fS = null;
		try {
			fS = om.readValue(new String(message.getPayload()), FtfactoryBME680.class);
			this.copyFtfactoryBME680(fS);
			this.messageReceived = true;
			
			log.info(
					"\nmessageArrived>>> [topic: {}, message: {}]",
				      topic,
				      new String(message.getPayload(), StandardCharsets.UTF_8));
		} catch (JsonMappingException e) {
			System.out.println("JsonMappingException " + e.getMessage());
			this.messageReceived = false;
		} catch (JsonProcessingException e) {
			System.out.println("JsonProcessingException " + e.getMessage());
			this.messageReceived = false;
		}

	}

//---------------- JSON stuff ---------------------------
	private String ts;
	private float t;
	private float rt;
	private float h;
	private float rh;
	private float p;
	private float iaq;
	private float aq;
	private float gr;
	

	private void copyFtfactoryBME680(FtfactoryBME680 other) {
		this.ts = other.ts;
		this.t = other.t;
		this.rt = other.rt;
		this.h = other.h;
		this.rh = other.rh;
		this.p = other.p;
		this.iaq = other.iaq;
		this.aq = other.aq;
		this.gr = other.gr;		
	}
	
	public String getTs() {
		return ts;
	}

	public void setTs(String ts) {
		this.ts = ts;
	}

	public float getT() {
		return t;
	}

	public void setT(float t) {
		this.t = t;
	}

	public float getRt() {
		return rt;
	}

	public void setRt(float rt) {
		this.rt = rt;
	}

	public float getH() {
		return h;
	}

	public void setH(float h) {
		this.h = h;
	}

	public float getRh() {
		return rh;
	}

	public void setRh(float rh) {
		this.rh = rh;
	}

	public float getP() {
		return p;
	}

	public void setP(float p) {
		this.p = p;
	}

	public float getIaq() {
		return iaq;
	}

	public void setIaq(float iaq) {
		this.iaq = iaq;
	}

	public float getAq() {
		return aq;
	}

	public void setAq(float aq) {
		this.aq = aq;
	}

	public float getGr() {
		return gr;
	}

	public void setGr(float gr) {
		this.gr = gr;
	}

}
