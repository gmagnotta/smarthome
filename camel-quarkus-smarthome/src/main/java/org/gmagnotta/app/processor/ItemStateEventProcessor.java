package org.gmagnotta.app.processor;

import javax.json.bind.JsonbBuilder;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.gmagnotta.model.ItemStateEventValue;
import org.gmagnotta.model.ServerSentEventModel;

public class ItemStateEventProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {

        ServerSentEventModel model = exchange.getIn().getBody(ServerSentEventModel.class);
    	
    	ItemStateEventValue val = JsonbBuilder.create().fromJson(model.payload, ItemStateEventValue.class);

    	String item = model.topic.substring(16, model.topic.length() - 6);

        exchange.getIn().setHeader("item", item);
        exchange.getIn().setHeader("type", val.type);
        exchange.getIn().setHeader("value", val.value);
        
    }
    
}
