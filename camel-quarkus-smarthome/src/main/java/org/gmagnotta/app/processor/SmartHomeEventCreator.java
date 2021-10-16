package org.gmagnotta.app.processor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.json.bind.JsonbBuilder;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.gmagnotta.model.ItemStateEventValue;
import org.gmagnotta.model.ServerSentEventModel;
import org.gmagnotta.smarthome.model.event.Smarthomeevent.SmartHomeEvent;
import org.jboss.logging.Logger;

/**
 * Dummy class to debug incoming messages
 * 
 */
@ApplicationScoped
public class SmartHomeEventCreator implements Processor {
	
	@Override
    public void process(Exchange exchange) throws Exception {

		String realm = exchange.getIn().getHeader("realm", String.class);
		String item = exchange.getIn().getHeader("item", String.class);
		String type = exchange.getIn().getHeader("type", String.class);
		String value = exchange.getIn().getHeader("value", String.class);
    	
		SmartHomeEvent smarthomeEvent = SmartHomeEvent.newBuilder()
			.setRealm(realm)
			.setItem(item)
			.setType(type)
			.setValue(value)
			.build();
		
		exchange.getIn().setBody(smarthomeEvent);
    	
    }

}
