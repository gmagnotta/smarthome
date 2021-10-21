package org.gmagnotta.smarthome.model;

import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.gmagnotta.smarthome.model.event.Smarthomecommand.SmartHomeCommandRequest;
import org.gmagnotta.smarthome.model.event.Smarthomecommand.SmartHomeCommandRequest.Operation;
import org.jboss.logging.Logger;

import org.gmagnotta.utils.Service;

@ApplicationScoped
public class HomeController implements HomeObserver {

    @Inject
    Logger logger;

    @Inject
    Service service;

    @Override
    public void temperatureChanged(Room room, Double temperature) {
        logger.info("Temperature changed in " + room + " to " + temperature);

        // if climate control is disabled just exit
        if (!room.getHome().getClimateControl()) {
            logger.info("Climate control is disabled. Nothing to do!");
            return;
        }

        // if temperature in this room is < threshold, then start heating
        if (temperature.compareTo(room.getThresholdTemp()) < 0) {
            // temperature under threshold!
            logger.info("Temperature in " + room + " is under threshold!");

            if (!room.getHome().isBoilerWorking())
                updateBoiler("ON");
            // otherwise we need to check all temperatures in all rooms
        } else if (allTempsAboveThreshold(room.getHome())) {
            // all temperature above threshold
            logger.info("All temperatures above threshold.");
            if (room.getHome().isBoilerWorking())
                updateBoiler("OFF");
        }

    }

    @Override
    public void humidityChanged(Room room, Double humidity) {
        logger.info("Humidity changed in " + room + " to " + humidity);
    }

    @Override
    public void climateModeChanged(Home house, ClimateMode climateMode) {
        logger.info("Climate mode changed in " + house + " to " + climateMode);

        // update TRVs accordingly
        String value = "";
        if (ClimateMode.COMFORT.equals(climateMode)) {
            value = "1";
        } else {
            value = "11";
        }

        updateTRVs(value);

        // reevaluate all temperatures in rooms
        for (Room room : house.getRooms()) {
            temperatureChanged(room, room.getTemperature());
        }
    }

    private static boolean allTempsAboveThreshold(Home home) {
        Collection<Room> rooms = home.getRooms();

        boolean retval = true;
        for (Room room : rooms) {
            if (room.getTemperature() != null && room.getTemperature().compareTo(room.getThresholdTemp()) > 0) {
                continue;
            } else {
                retval = false;
                break;
            }
        }

        return retval;
    }

    @Override
    public void climateControlChanged(Home house, boolean climateControl) {
        logger.info("Climate control changed in " + house + " to " + climateControl);

        // if off turn off heating and set off TRVs
        // 0 off
        // 1 heat
        // 11 economy
        // 15 full power

        if (climateControl == false) {
            // turn off everything

            if (house.isBoilerWorking())
                updateBoiler("OFF");
            
            updateTRVs("0");

        } else {

            climateModeChanged(house, house.getClimateMode());

        }

    }

    @Override
    public void comfortTemperatureChanged(Room room, Double temperature) {
        for (Room troom : room.getHome().getRooms()) {
            temperatureChanged(troom, troom.getTemperature());
        }
    }

    @Override
    public void ecoTemperatureChanged(Room room, Double temperature) {
        for (Room troom : room.getHome().getRooms()) {
            temperatureChanged(troom, troom.getTemperature());
        }
    }

    private void updateBoiler(String value) {

        SmartHomeCommandRequest req = SmartHomeCommandRequest.newBuilder().setRealm("OpenHAB")
                .setId("" + System.currentTimeMillis()).setReplyto("smarthomeresponse").setResource("Caldaia_Switch")
                .setOperation(Operation.CREATE).setType("String").setValue(value).build();

        try {

            service.sendMqtt("smarthomeesb", req);

        } catch (Exception ex) {
            logger.error("Exception while changing boiler", ex);
        }

    }

    private void updateTRVs(String value) {

        String[] trvs = { "Livingroom_TRV_ThermostatMode", "Hallway_TRV_ThermostatMode", "Bathroom_TRV_ThermostatMode",
                "Bedroom1_TRV_ThermostatMode", "Bedroom2_TRV_ThermostatMode" };

        for (String trv : trvs) {
            SmartHomeCommandRequest req = SmartHomeCommandRequest.newBuilder().setRealm("OpenHAB")
                    .setId("" + System.currentTimeMillis()).setReplyto("smarthomeresponse").setResource(trv)
                    .setOperation(Operation.CREATE).setType("String").setValue(value).build();

            try {

                service.sendMqtt("smarthomeesb", req);

            } catch (Exception ex) {
                logger.error("Exception while changing trv", ex);
            }
        }

    }

    @Override
    public void awayModeChanged(Home house, boolean awayMode) {

        if (awayMode) {
    
            SmartHomeCommandRequest req = SmartHomeCommandRequest.newBuilder().setRealm("OpenHAB")
                    .setId("" + System.currentTimeMillis()).setReplyto("smarthomeresponse").setResource("Climate_Mode")
                    .setOperation(Operation.CREATE).setType("String").setValue("ECO").build();

            try {

                service.sendMqtt("smarthomeesb", req);

            } catch (Exception ex) {
                logger.error("Exception while changing trv", ex);
            }
    
        } else {
    
            SmartHomeCommandRequest req = SmartHomeCommandRequest.newBuilder().setRealm("OpenHAB")
                    .setId("" + System.currentTimeMillis()).setReplyto("smarthomeresponse").setResource("Climate_Mode")
                    .setOperation(Operation.CREATE).setType("String").setValue("COMFORT").build();

            try {

                service.sendMqtt("smarthomeesb", req);

            } catch (Exception ex) {
                logger.error("Exception while changing trv", ex);
            }
    
        }
        
    }

    @Override
    public void boilerChanged(Home house, boolean boilerState) {
       logger.info("Boiler state changed to " + boilerState);
    }

}
