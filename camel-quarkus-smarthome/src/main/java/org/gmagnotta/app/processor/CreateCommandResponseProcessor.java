package org.gmagnotta.app.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.gmagnotta.smarthome.model.event.Smarthomecommand.SmartHomeCommandResponse;

public class CreateCommandResponseProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {

        String id = exchange.getIn().getHeader("id", String.class);
        String value = exchange.getIn().getHeader("value", String.class);

        SmartHomeCommandResponse reponse = SmartHomeCommandResponse.newBuilder()
        .setCorrelationid(id)
        .setStatus(value)
        .build();

        exchange.getIn().setBody(reponse);
        
    }
    
}
