package org.gmagnotta;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.gmagnotta.smarthome.model.ClimateMode;
import org.gmagnotta.smarthome.model.Home;
import org.gmagnotta.smarthome.model.event.Smarthomecommand.SmartHomeCommandRequest;
import org.gmagnotta.smarthome.model.event.Smarthomecommand.SmartHomeCommandResponse;
import org.gmagnotta.smarthome.model.event.Smarthomecommand.SmartHomeCommandRequest.Operation;
import org.gmagnotta.utils.Service;
import org.jboss.logging.Logger;

import io.quarkus.scheduler.Scheduled;

@ApplicationScoped
public class AstroBean {

    public static Logger LOGGER = Logger.getLogger(AstroBean.class); 

    @Inject
    Service service;

    @Inject
    Home home;

    @Scheduled(cron = "0 21 * * *")
    void closeBlinds() throws Exception {

        LOGGER.info("Closing blinds");
        
        SmartHomeCommandRequest req = SmartHomeCommandRequest.newBuilder()
        .setRealm("OpenHAB")
        .setId(""+System.currentTimeMillis())
        .setReplyto("smarthomeresponse")
        .setResource("ZWaveNode023MHC221MCOHOMEMicroShutter_BlindsControl")
        .setOperation(Operation.CREATE)
        .setType("String")
        .setValue("5").build();

        service.sendMqtt("smarthomeesb", req);
    }
    
    @Scheduled(cron = "0 0 * * *")
    void nightTime() throws Exception {
        
        LOGGER.info("Night time");

        SmartHomeCommandRequest req = SmartHomeCommandRequest.newBuilder()
        .setRealm("OpenHAB")
        .setId(""+System.currentTimeMillis())
        .setReplyto("smarthomeresponse")
        .setResource("AutoOff")
        .setOperation(Operation.CREATE)
        .setType("String")
        .setValue("ON").build();

        service.sendMqtt("smarthomeesb", req);
    }

    @Scheduled(cron = "0 7 * * *")
    public void openBlinds() throws Exception {
        
        LOGGER.info("Morning time");

        SmartHomeCommandRequest req = SmartHomeCommandRequest.newBuilder()
             .setId(""+System.currentTimeMillis())
             .setReplyto("smarthomeresponse")
             .setRealm("OpenHAB")
             .setResource("ZWaveNode022_BlindsControl")
             .setOperation(Operation.READ)
             .setType("String")
             .setValue("").build();

        SmartHomeCommandResponse response = service.sendRecMqtt("smarthomeesb", req);

        Double currentState = Double.parseDouble(response.getStatus());

        LOGGER.info("Current state is " + currentState);

        if (currentState <= 30) {
            LOGGER.info("Opening blind");
            // Only if current state is < 30
            req = SmartHomeCommandRequest.newBuilder()
            .setRealm("OpenHAB")
            .setId(""+System.currentTimeMillis())
            .setReplyto("smarthomeresponse")
            .setResource("ZWaveNode022_BlindsControl")
            .setOperation(Operation.CREATE)
            .setType("String")
            .setValue("62").build();

            service.sendMqtt("smarthomeesb", req);
        }

        req = SmartHomeCommandRequest.newBuilder()
            .setRealm("OpenHAB")
            .setId(""+System.currentTimeMillis())
            .setReplyto("smarthomeresponse")
            .setResource("Climate_Mode")
            .setOperation(Operation.CREATE)
            .setType("String")
            .setValue("COMFORT").build();

        service.sendMqtt("smarthomeesb", req);
        
    }

    @Scheduled(cron = "0 23 * * *")
    void setEconomy() throws Exception {

        SmartHomeCommandRequest req = SmartHomeCommandRequest.newBuilder()
            .setRealm("OpenHAB")
            .setId(""+System.currentTimeMillis())
            .setReplyto("smarthomeresponse")
            .setResource("Climate_Mode")
            .setOperation(Operation.CREATE)
            .setType("String")
            .setValue("ECO").build();

        service.sendMqtt("smarthomeesb", req);
    }
}
