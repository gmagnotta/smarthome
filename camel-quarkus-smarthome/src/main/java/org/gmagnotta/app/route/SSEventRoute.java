package org.gmagnotta.app.route;

import javax.enterprise.context.ApplicationScoped;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.protobuf.ProtobufDataFormat;
import org.apache.camel.model.dataformat.JsonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.gmagnotta.app.predicate.ItemStatePredicate;
import org.gmagnotta.app.processor.ItemStateEventProcessor;
import org.gmagnotta.app.processor.SmartHomeEventCreator;
import org.gmagnotta.smarthome.model.event.Smarthomecommand.SmartHomeCommandResponse;
import org.gmagnotta.smarthome.model.event.Smarthomeevent.SmartHomeEvent;

@ApplicationScoped
public class SSEventRoute extends RouteBuilder {

	@ConfigProperty(name = "sseserver")
	String sseServer;
	
	@ConfigProperty(name = "mqttbroker")
	String brokerUrl;
	
	@Override
	public void configure() throws Exception {
		
		JsonDataFormat format = new JsonDataFormat(JsonLibrary.Jsonb);
    	format.setUnmarshalType(org.gmagnotta.model.ServerSentEventModel.class);

		ProtobufDataFormat protoFormat = new ProtobufDataFormat(SmartHomeEvent.getDefaultInstance());

		ProtobufDataFormat smartHomeCommandResponse = new ProtobufDataFormat(SmartHomeCommandResponse.getDefaultInstance());
		
		from("sse://" + sseServer)
		 .routeId("sse")
		 .unmarshal(format)
		 .filter(new ItemStatePredicate())
		 .log("Read SSE message ${body}")
		 .process(new ItemStateEventProcessor())
		 .setHeader("realm", constant("OpenHAB"))
		 .process(new SmartHomeEventCreator())
		 .marshal(protoFormat)
		 .to("paho:smarthomeevent?brokerUrl=" + brokerUrl);

		 from("paho:debug?brokerUrl=" + brokerUrl)
		 .routeId("debug")
		 .unmarshal(smartHomeCommandResponse)
		 .log("Read Debug message ${body}");
		 
		
	}
}
