package org.gmagnotta.observer;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.gmagnotta.smarthome.model.event.Smarthomeevent;
import org.gmagnotta.smarthome.model.event.Smarthomecommand.SmartHomeCommandRequest;
import org.gmagnotta.smarthome.model.event.Smarthomecommand.SmartHomeCommandResponse;
import org.gmagnotta.smarthome.model.event.Smarthomecommand.SmartHomeCommandRequest.Operation;
import org.gmagnotta.smarthome.model.event.Smarthomeevent.SmartHomeEvent;
import org.gmagnotta.utils.Service;
import org.jboss.logging.Logger;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import org.gmagnotta.utils.Service;

@ApplicationScoped
public class EventReceiver {

    @Inject
    Logger logger;

    @Inject
    Service service;

    Map<String, String> map  = new HashMap<String, String>() {{
        put("zwave_device_b4728f7c_node9_sensor_temperature", "Target_Temp_Bathroom");
        put("zwave_device_b4728f7c_node10_sensor_temperature", "Target_Temp_Bedroom1");
        put("zwave_device_b4728f7c_node7_sensor_temperature", "Target_Temp_Bedroom2");
        put("zwave_device_b4728f7c_node6_sensor_temperature", "Target_Temp_Hallway");
        put("zwave_device_b4728f7c_node12_sensor_temperature", "Target_Temp_Livingroom");
    }};

    @Incoming("smarthomeevent")
    public void receive(byte[] payload) throws Exception {

        SmartHomeEvent smarthomeevent = Smarthomeevent.SmartHomeEvent.parseFrom(payload);
        logger.info("Received msg " + smarthomeevent);

        if (smarthomeevent.getItem().equals("zwave_device_b4728f7c_node2_sensor_door")) {

            SmartHomeCommandRequest req = SmartHomeCommandRequest.newBuilder()
             .setId(""+System.currentTimeMillis())
             .setReplyto("debug")
             .setRealm("Telegram")
             .setResource("Aaa")
             .setOperation(Operation.READ)
             .setType("String")
             .setValue("Door changed state to: " + smarthomeevent.getValue()).build();

             service.sendMqtt("smarthomeesb", req);

        } else if (map.containsKey(smarthomeevent.getItem())) {

            SmartHomeCommandRequest req = SmartHomeCommandRequest.newBuilder()
             .setId(""+System.currentTimeMillis())
             .setReplyto("debug")
             .setRealm("OpenHAB")
             .setResource(map.get(smarthomeevent.getItem()))
             .setOperation(Operation.READ)
             .setType("String")
             .setValue("").build();

             SmartHomeCommandResponse response = service.sendRecMqtt("smarthomeesb", req);

             Double readTemp = Double.parseDouble(smarthomeevent.getValue().substring(0, smarthomeevent.getValue().indexOf(" ")));
             Double thresholdTemp = Double.parseDouble(response.getStatus());

             logger.info("Read Temp " + readTemp + ", threshold " + thresholdTemp);

        }
        
    }
}
