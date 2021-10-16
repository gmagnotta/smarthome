package org.gmagnotta.app.predicate;

import org.apache.camel.Exchange;
import org.apache.camel.Predicate;

import org.gmagnotta.smarthome.model.event.Smarthomecommand.SmartHomeCommandRequest;
import org.gmagnotta.smarthome.model.event.Smarthomecommand.SmartHomeCommandRequest.Operation;

/**
 * This class filters only messages referred to items
 */
public class IsUpdateOperation implements Predicate {

	@Override
	public boolean matches(Exchange exchange) {
		
		SmartHomeCommandRequest model = exchange.getIn().getBody(SmartHomeCommandRequest.class);
		
		if (model.getOperation().equals(Operation.UPDATE))
			return true;
		
		return false;
	}

}
