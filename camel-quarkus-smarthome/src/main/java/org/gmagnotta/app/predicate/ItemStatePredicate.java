package org.gmagnotta.app.predicate;

import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.gmagnotta.model.ServerSentEventModel;

/**
 * This class filters only messages referred to items
 */
public class ItemStatePredicate implements Predicate {

	@Override
	public boolean matches(Exchange exchange) {
		
		ServerSentEventModel model = exchange.getIn().getBody(ServerSentEventModel.class);
		
		if (model.topic.startsWith("smarthome/items/") &&
				model.topic.endsWith("/state"))
			return true;
		
		return false;
	}

}
