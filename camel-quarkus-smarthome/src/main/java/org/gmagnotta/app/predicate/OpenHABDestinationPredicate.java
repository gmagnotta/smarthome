package org.gmagnotta.app.predicate;

import org.apache.camel.Exchange;
import org.apache.camel.Predicate;

import org.gmagnotta.smarthome.model.event.Smarthomecommand.SmartHomeCommandRequest;

/**
 * This class filters only messages referred to items
 */
public class OpenHABDestinationPredicate implements Predicate {

	@Override
	public boolean matches(Exchange exchange) {
		
		SmartHomeCommandRequest model = exchange.getIn().getBody(SmartHomeCommandRequest.class);
		
		if (model.getRealm().equals("OpenHAB"))
			return true;
		
		return false;
	}

}
