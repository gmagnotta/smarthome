package org.gmagnotta.utils;

import java.util.Random;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;

import com.google.protobuf.InvalidProtocolBufferException;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.gmagnotta.smarthome.model.event.Smarthomecommand.SmartHomeCommandRequest;
import org.gmagnotta.smarthome.model.event.Smarthomecommand.SmartHomeCommandResponse;
import org.jboss.logging.Logger;

@Dependent
public class Service {
    
    public static Logger LOGGER = Logger.getLogger(Service.class);

    @ConfigProperty(name = "mqttbroker")
    String mqttbroker;

    public void sendMqtt(String topic, SmartHomeCommandRequest request) throws InterruptedException, InvalidProtocolBufferException {

        int qos             = 2;
        String clientId     = "JavaSample";
        MemoryPersistence persistence = new MemoryPersistence();
        MqttClient sampleClient;

            try {

                sampleClient = new MqttClient(mqttbroker, clientId, persistence);
                MqttConnectOptions connOpts = new MqttConnectOptions();
                connOpts.setCleanSession(true);
                
                sampleClient.setCallback(new MqttCallbackExtended() {

                    @Override
                    public void connectionLost(Throwable cause) { //Called when the client lost the connection to the broker 
                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {

                    }

                    @Override
                    public void connectComplete(boolean reconnect, String serverURI) {

                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {
                        
                    }
                });

                sampleClient.connect(connOpts);

                LOGGER.debug("Publishing message: "+ request);
                MqttMessage message = new MqttMessage(request.toByteArray());
                message.setQos(qos);
                sampleClient.publish(topic, message);
                LOGGER.debug("Message published");

                LOGGER.debug("Disconnected");
                sampleClient.disconnect();

                sampleClient.close();
            } catch(MqttException me) {
                LOGGER.error("Exception", me);
            }
    }

    public SmartHomeCommandResponse receiveMqtt(String topic) throws InterruptedException, InvalidProtocolBufferException {

        String clientId     = "JavaSample";
        MemoryPersistence persistence = new MemoryPersistence();
        SycObj waitObj = new SycObj();
        MqttClient sampleClient;

            try {

                sampleClient = new MqttClient(mqttbroker, clientId, persistence);
                MqttConnectOptions connOpts = new MqttConnectOptions();
                connOpts.setCleanSession(true);
                
                sampleClient.setCallback(new MqttCallbackExtended() {

                    @Override
                    public void connectionLost(Throwable cause) { //Called when the client lost the connection to the broker 
                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {

                        synchronized(waitObj) {
                            SmartHomeCommandResponse response = SmartHomeCommandResponse.parseFrom(message.getPayload());
                            LOGGER.debug("Received msg " + response);
                            waitObj.setResponse(response);
                            waitObj.setFinished(true);
                            waitObj.notifyAll();
                        }
                    }

                    @Override
                    public void connectComplete(boolean reconnect, String serverURI) {

                        try {
                            LOGGER.debug("Connected");
                            sampleClient.subscribe(topic);
                        } catch (MqttException e) {
                            LOGGER.error("Exception", e);
                        }
                        
                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {
                    }
                });

                sampleClient.connect(connOpts);
                LOGGER.debug("Wait for response");
                synchronized(waitObj) {

                    long start = System.currentTimeMillis();

                    while(
                        !waitObj.isFinished() &&
                        (System.currentTimeMillis() - start) < 5000 ) {
                        waitObj.wait(5000);
                    }

                }
                
                LOGGER.debug("Wakeup");

                sampleClient.unsubscribe(topic);
                sampleClient.disconnect();

                sampleClient.close();
                
            } catch(MqttException me) {
                LOGGER.error("Exception", me);
            }

            return waitObj.getResponse();
    }

    public SmartHomeCommandResponse sendRecMqtt(String sendtopic, SmartHomeCommandRequest request) throws Exception {

        int qos             = 2;
        String clientId     = "JavaSample";
        MemoryPersistence persistence = new MemoryPersistence();
        SycObj waitObj = new SycObj();
        MqttClient sampleClient;

            try {

                sampleClient = new MqttClient(mqttbroker, clientId, persistence);
                MqttConnectOptions connOpts = new MqttConnectOptions();
                connOpts.setCleanSession(true);
                
                sampleClient.setCallback(new MqttCallbackExtended() {

                    @Override
                    public void connectionLost(Throwable cause) { //Called when the client lost the connection to the broker 
                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {

                        synchronized(waitObj) {
                            
                            SmartHomeCommandResponse response = SmartHomeCommandResponse.parseFrom(message.getPayload());
                            LOGGER.debug("Received msg " + response);

                            if (response.getCorrelationid().equals(request.getId())) {
                                waitObj.setResponse(response);
                                waitObj.setFinished(true);
                                waitObj.notifyAll();
                            }
                        }
                    }

                    @Override
                    public void connectComplete(boolean reconnect, String serverURI) {

                        try {
                            LOGGER.debug("Connected");
                            sampleClient.subscribe(request.getReplyto());
                        } catch (MqttException e) {
                            LOGGER.error("Exception", e);
                        }
                        
                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {
                    }
                });

                sampleClient.connect(connOpts);
                
                LOGGER.debug("Publishing message: "+ request);
                MqttMessage message = new MqttMessage(request.toByteArray());
                message.setQos(qos);
                sampleClient.publish(sendtopic, message);
                LOGGER.debug("Message published");

                LOGGER.debug("Wait for response");
                synchronized(waitObj) {

                    long start = System.currentTimeMillis();

                    while(
                        !waitObj.isFinished() &&
                        (System.currentTimeMillis() - start) < 10000 ) {
                            LOGGER.debug("waiting...");
                        waitObj.wait(10000);
                    }

                }
                
                LOGGER.debug("Wakeup");

                sampleClient.unsubscribe(request.getReplyto());
                sampleClient.disconnect();

                sampleClient.close();

                LOGGER.debug("Response is " + waitObj.getResponse());
                
            } catch(MqttException me) {
                LOGGER.error("Exception", me);
            }
            
            return waitObj.getResponse();
    }

    public class SycObj {

        private volatile boolean finished = false;
        private volatile SmartHomeCommandResponse response;

        public boolean isFinished() {
            return finished;
        }

        public void setFinished(boolean finished) {
            this.finished = finished;
        }

        public SmartHomeCommandResponse getResponse() {
            return response;
        }

        public void setResponse(SmartHomeCommandResponse response) {
            this.response = response;
        }
    }
    
}
