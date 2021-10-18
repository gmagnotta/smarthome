package org.gmagnotta;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.gmagnotta.smarthome.model.ClimateMode;
import org.gmagnotta.smarthome.model.Home;
import org.gmagnotta.smarthome.model.HouseController;
import org.gmagnotta.smarthome.model.Room;
import org.gmagnotta.smarthome.model.event.Smarthomecommand.SmartHomeCommandRequest;
import org.gmagnotta.smarthome.model.event.Smarthomecommand.SmartHomeCommandResponse;
import org.gmagnotta.smarthome.model.event.Smarthomecommand.SmartHomeCommandRequest.Operation;
import org.gmagnotta.utils.Service;

import io.quarkus.runtime.Startup;

@ApplicationScoped
@Startup
public class Main {

     @Inject
     Service service;

     @Inject
     HouseController controller;

     @Produces
     public Home createHome() throws Exception {
          Home home = new Home("Magnotta's");

          SmartHomeCommandRequest comfortTempReq = SmartHomeCommandRequest.newBuilder()
                    .setId("" + System.currentTimeMillis()).setReplyto("debug").setRealm("OpenHAB")
                    .setResource("Comfort_Temp_Livingroom").setOperation(Operation.READ).setType("String").setValue("")
                    .build();

          SmartHomeCommandResponse comfortTempRes = service.sendRecMqtt("smarthomeesb", comfortTempReq);

          SmartHomeCommandRequest ecoTempReq = SmartHomeCommandRequest.newBuilder()
                    .setId("" + System.currentTimeMillis()).setReplyto("debug").setRealm("OpenHAB")
                    .setResource("Eco_Temp_Livingroom").setOperation(Operation.READ).setType("String").setValue("")
                    .build();

          SmartHomeCommandResponse ecoTempRes = service.sendRecMqtt("smarthomeesb", ecoTempReq);

          Room livingRoom = new Room("Livingroom", home);
          livingRoom.setComfortTemp(Double.parseDouble(comfortTempRes.getStatus()));
          livingRoom.setEcoTemp(Double.parseDouble(ecoTempRes.getStatus()));
          home.addRoom(livingRoom);

          comfortTempReq = SmartHomeCommandRequest.newBuilder().setId("" + System.currentTimeMillis())
                    .setReplyto("debug").setRealm("OpenHAB").setResource("Comfort_Temp_Hallway")
                    .setOperation(Operation.READ).setType("String").setValue("").build();

          comfortTempRes = service.sendRecMqtt("smarthomeesb", comfortTempReq);

          ecoTempReq = SmartHomeCommandRequest.newBuilder().setId("" + System.currentTimeMillis()).setReplyto("debug")
                    .setRealm("OpenHAB").setResource("Eco_Temp_Hallway").setOperation(Operation.READ).setType("String")
                    .setValue("").build();

          ecoTempRes = service.sendRecMqtt("smarthomeesb", ecoTempReq);

          Room hallway = new Room("Hallway", home);
          hallway.setComfortTemp(Double.parseDouble(comfortTempRes.getStatus()));
          hallway.setEcoTemp(Double.parseDouble(ecoTempRes.getStatus()));
          home.addRoom(hallway);

          comfortTempReq = SmartHomeCommandRequest.newBuilder().setId("" + System.currentTimeMillis())
                    .setReplyto("debug").setRealm("OpenHAB").setResource("Comfort_Temp_Bedroom1")
                    .setOperation(Operation.READ).setType("String").setValue("").build();

          comfortTempRes = service.sendRecMqtt("smarthomeesb", comfortTempReq);

          ecoTempReq = SmartHomeCommandRequest.newBuilder().setId("" + System.currentTimeMillis()).setReplyto("debug")
                    .setRealm("OpenHAB").setResource("Eco_Temp_Bedroom1").setOperation(Operation.READ).setType("String")
                    .setValue("").build();

          ecoTempRes = service.sendRecMqtt("smarthomeesb", ecoTempReq);

          Room bedroom1 = new Room("Bedroom1", home);
          bedroom1.setComfortTemp(Double.parseDouble(comfortTempRes.getStatus()));
          bedroom1.setEcoTemp(Double.parseDouble(ecoTempRes.getStatus()));
          home.addRoom(bedroom1);

          comfortTempReq = SmartHomeCommandRequest.newBuilder().setId("" + System.currentTimeMillis())
                    .setReplyto("debug").setRealm("OpenHAB").setResource("Comfort_Temp_Bedroom2")
                    .setOperation(Operation.READ).setType("String").setValue("").build();

          comfortTempRes = service.sendRecMqtt("smarthomeesb", comfortTempReq);

          ecoTempReq = SmartHomeCommandRequest.newBuilder().setId("" + System.currentTimeMillis()).setReplyto("debug")
                    .setRealm("OpenHAB").setResource("Eco_Temp_Bedroom2").setOperation(Operation.READ).setType("String")
                    .setValue("").build();

          ecoTempRes = service.sendRecMqtt("smarthomeesb", ecoTempReq);

          Room bedroom2 = new Room("Bedroom2", home);
          bedroom2.setComfortTemp(Double.parseDouble(comfortTempRes.getStatus()));
          bedroom2.setEcoTemp(Double.parseDouble(ecoTempRes.getStatus()));
          home.addRoom(bedroom2);

          comfortTempReq = SmartHomeCommandRequest.newBuilder().setId("" + System.currentTimeMillis())
                    .setReplyto("debug").setRealm("OpenHAB").setResource("Comfort_Temp_Bathroom")
                    .setOperation(Operation.READ).setType("String").setValue("").build();

          comfortTempRes = service.sendRecMqtt("smarthomeesb", comfortTempReq);

          ecoTempReq = SmartHomeCommandRequest.newBuilder().setId("" + System.currentTimeMillis()).setReplyto("debug")
                    .setRealm("OpenHAB").setResource("Eco_Temp_Bathroom").setOperation(Operation.READ).setType("String")
                    .setValue("").build();

          ecoTempRes = service.sendRecMqtt("smarthomeesb", ecoTempReq);

          Room bathroom = new Room("Bathroom", home);
          bathroom.setComfortTemp(Double.parseDouble(comfortTempRes.getStatus()));
          bathroom.setEcoTemp(Double.parseDouble(ecoTempRes.getStatus()));
          home.addRoom(bathroom);

          SmartHomeCommandRequest climateCtrlReq = SmartHomeCommandRequest.newBuilder()
                    .setId("" + System.currentTimeMillis()).setReplyto("debug").setRealm("OpenHAB")
                    .setResource("Climate_Control").setOperation(Operation.READ).setType("String").setValue("").build();

          SmartHomeCommandResponse climateCtrlRes = service.sendRecMqtt("smarthomeesb", climateCtrlReq);

          if ("OFF".equals(climateCtrlRes.getStatus())) {
               home.setClimateControl(false);
          } else if ("ON".equals(climateCtrlRes.getStatus())) {
               home.setClimateControl(true);
          } else {
               home.setClimateControl(false);
               // logger.info("Unknown value Climate_Control " + smarthomeevent.getValue());
          }

          SmartHomeCommandRequest climateModeReq = SmartHomeCommandRequest.newBuilder()
                    .setId("" + System.currentTimeMillis()).setReplyto("debug").setRealm("OpenHAB")
                    .setResource("Climate_Mode").setOperation(Operation.READ).setType("String").setValue("").build();

          SmartHomeCommandResponse climateModeRes = service.sendRecMqtt("smarthomeesb", climateModeReq);

          if ("COMFORT".equals(climateModeRes.getStatus())) {
               home.setClimateMode(ClimateMode.COMFORT);
          } else if ("ECO".equals(climateModeRes.getStatus())) {
               home.setClimateMode(ClimateMode.ECO);
          } else {
               home.setClimateMode(ClimateMode.ECO);
               // logger.info("Unknown value Climate_Mode " + smarthomeevent.getValue());
          }

          home.addObserver(controller);

          return home;
     }

}
