package org.gmagnotta.observer;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.gmagnotta.smarthome.model.ClimateMode;
import org.gmagnotta.smarthome.model.Home;
import org.gmagnotta.smarthome.model.Room;
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
    Home home;

    @Inject
    Service service;

    Map<String, String> tempsensorRoom = new HashMap<String, String>() {
        {
            put("zwave_device_b4728f7c_node9_sensor_temperature", "Bathroom");
            put("zwave_device_b4728f7c_node10_sensor_temperature", "Bedroom1");
            put("zwave_device_b4728f7c_node7_sensor_temperature", "Bedroom2");
            put("zwave_device_b4728f7c_node6_sensor_temperature", "Hallway");
            put("zwave_device_b4728f7c_node12_sensor_temperature", "Livingroom");
        }
    };

    Map<String, String> humidsensorRoom = new HashMap<String, String>() {
        {
            put("zwave_device_b4728f7c_node9_sensor_relhumidity", "Bathroom");
            put("zwave_device_b4728f7c_node10_sensor_relhumidity", "Bedroom1");
            put("zwave_device_b4728f7c_node7_sensor_relhumidity", "Bedroom2");
            put("zwave_device_b4728f7c_node6_sensor_relhumidity", "Hallway");
            put("zwave_device_b4728f7c_node12_sensor_relhumidity", "Livingroom");
        }
    };

    @Incoming("smarthomeevent")
    public void receive(byte[] payload) throws Exception {

        SmartHomeEvent smarthomeevent = Smarthomeevent.SmartHomeEvent.parseFrom(payload);
        logger.info("Received msg " + smarthomeevent);

        if (smarthomeevent.getItem().equals("zwave_device_b4728f7c_node2_sensor_door")) {

            SmartHomeCommandRequest req = SmartHomeCommandRequest.newBuilder().setId("" + System.currentTimeMillis())
                    .setReplyto("debug").setRealm("Telegram").setResource("Aaa").setOperation(Operation.READ)
                    .setType("String").setValue("Door changed state to: " + smarthomeevent.getValue()).build();

            service.sendMqtt("smarthomeesb", req);

        } else if (tempsensorRoom.containsKey(smarthomeevent.getItem())) {

            Double readTemp = Double
                    .parseDouble(smarthomeevent.getValue().substring(0, smarthomeevent.getValue().indexOf(" ")));

            logger.info("Read Temp " + readTemp);

            home.getRoom(tempsensorRoom.get(smarthomeevent.getItem())).setTemperature(readTemp);

        } else if (humidsensorRoom.containsKey(smarthomeevent.getItem())) {

            Double readHum = Double.parseDouble(smarthomeevent.getValue());

            logger.info("Read Humid " + readHum);

            home.getRoom(humidsensorRoom.get(smarthomeevent.getItem())).setHumidity(readHum);

        } else if (smarthomeevent.getItem().equals("Climate_Control")) {

            if ("OFF".equals(smarthomeevent.getValue())) {
                home.setClimateControl(false);
            } else if ("ON".equals(smarthomeevent.getValue())) {
                home.setClimateControl(true);
            } else {
                logger.info("Unknown value Climate_Control " + smarthomeevent.getValue());
                home.setClimateControl(false);
            }

        } else if (smarthomeevent.getItem().equals("Climate_Mode")) {

            if ("COMFORT".equals(smarthomeevent.getValue())) {
                home.setClimateMode(ClimateMode.COMFORT);
            } else if ("ECO".equals(smarthomeevent.getValue())) {
                home.setClimateMode(ClimateMode.ECO);
            } else {
                logger.info("Unknown value Climate_Mode " + smarthomeevent.getValue());
                home.setClimateMode(ClimateMode.ECO);
            }

        } else if (smarthomeevent.getItem().equals("Comfort_Temp_Livingroom")) {

            Double temp = Double.parseDouble(smarthomeevent.getValue());
            home.getRoom("Livingroom").setComfortTemp(temp);
        
        } else if (smarthomeevent.getItem().equals("Eco_Temp_Livingroom")) {
            
            Double temp = Double.parseDouble(smarthomeevent.getValue());
            home.getRoom("Livingroom").setEcoTemp(temp);

        } else if (smarthomeevent.getItem().equals("Comfort_Temp_Hallway")) {

            Double temp = Double.parseDouble(smarthomeevent.getValue());
            home.getRoom("Hallway").setComfortTemp(temp);
        
        } else if (smarthomeevent.getItem().equals("Eco_Temp_Hallway")) {
            
            Double temp = Double.parseDouble(smarthomeevent.getValue());
            home.getRoom("Hallway").setEcoTemp(temp);

        } else if (smarthomeevent.getItem().equals("Comfort_Temp_Bedroom1")) {

            Double temp = Double.parseDouble(smarthomeevent.getValue());
            home.getRoom("Bedroom1").setComfortTemp(temp);
        
        } else if (smarthomeevent.getItem().equals("Eco_Temp_Bedroom1")) {
            
            Double temp = Double.parseDouble(smarthomeevent.getValue());
            home.getRoom("Bedroom1").setEcoTemp(temp);

        } else if (smarthomeevent.getItem().equals("Comfort_Temp_Bedroom2")) {

            Double temp = Double.parseDouble(smarthomeevent.getValue());
            home.getRoom("Bedroom2").setComfortTemp(temp);
        
        } else if (smarthomeevent.getItem().equals("Eco_Temp_Bedroom2")) {
            
            Double temp = Double.parseDouble(smarthomeevent.getValue());
            home.getRoom("Bedroom2").setEcoTemp(temp);

        } else if (smarthomeevent.getItem().equals("Comfort_Temp_Bathroom")) {

            Double temp = Double.parseDouble(smarthomeevent.getValue());
            home.getRoom("Bathroom").setComfortTemp(temp);
        
        } else if (smarthomeevent.getItem().equals("Eco_Temp_Bathroom")) {
            
            Double temp = Double.parseDouble(smarthomeevent.getValue());
            home.getRoom("Bathroom").setEcoTemp(temp);
        
        }

        logger.info(home);
    }

}
