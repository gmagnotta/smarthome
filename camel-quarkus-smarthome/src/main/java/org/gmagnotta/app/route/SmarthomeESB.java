package org.gmagnotta.app.route;

import javax.enterprise.context.ApplicationScoped;

import org.apache.camel.builder.RouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.gmagnotta.app.predicate.IsDeleteOperation;
import org.gmagnotta.app.predicate.IsReadOperation;
import org.gmagnotta.app.predicate.IsUpdateOperation;
import org.gmagnotta.app.predicate.IsCreateOperation;
import org.gmagnotta.app.predicate.OpenHABDestinationPredicate;
import org.gmagnotta.app.predicate.TelegramDestinationPredicate;
import org.gmagnotta.app.processor.CreateCommandResponseProcessor;
import org.gmagnotta.smarthome.model.event.Smarthomecommand.SmartHomeCommandRequest;
import org.gmagnotta.smarthome.model.event.Smarthomecommand.SmartHomeCommandResponse;
import org.apache.camel.dataformat.protobuf.ProtobufDataFormat;
import org.apache.camel.model.dataformat.JsonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;

@ApplicationScoped
public class SmarthomeESB extends RouteBuilder {

	@ConfigProperty(name = "mqttbroker")
	String brokerUrl;

	@ConfigProperty(name = "telegram.token")
	String telegramToken;

	@ConfigProperty(name = "telegram.chatid")
	String telegramChatId;

	@ConfigProperty(name = "openhab.instance")
	String openhab;
	
	@Override
	public void configure() throws Exception {
		
		ProtobufDataFormat smartHomeCommandRequest = new ProtobufDataFormat(SmartHomeCommandRequest.getDefaultInstance());
		ProtobufDataFormat smartHomeCommandResponse = new ProtobufDataFormat(SmartHomeCommandResponse.getDefaultInstance());

		JsonDataFormat jsonFormat = new JsonDataFormat(JsonLibrary.Jsonb);
    	jsonFormat.setUnmarshalType(org.gmagnotta.model.OpenHABRestItem.class);
		
		from("paho:smarthomeesb?brokerUrl=" + brokerUrl)
		 .routeId("esb")
		 .unmarshal(smartHomeCommandRequest)
		 .setHeader("id", simple("${body.id}"))
		 .setHeader("replyto", simple("${body.replyto}"))
		 .log("Received ESB message ${body}")
		 .choice()
		  .when(new TelegramDestinationPredicate())
		   .to("direct:telegram")
		  .when(new OpenHABDestinationPredicate())
		   .to("direct:openhab")
		  .otherwise()
		   .log("unknown realm ${body.realm}")
		 .endChoice()
		.end();
		
		from("direct:telegram")
		 .setHeader(org.apache.camel.component.telegram.TelegramConstants.TELEGRAM_CHAT_ID, constant(telegramChatId))
		 .setBody(simple("${body.value}"))
		 .to("telegram:bots?authorizationToken=" + telegramToken);

		from("direct:openhab")
		 .choice()
		  .when(new IsReadOperation())
		    .toD("rest:get:/rest/items/${body.resource}?host=" + openhab)
			.unmarshal(jsonFormat)
			.setHeader("value", simple("${body.state}"))
			.process(new CreateCommandResponseProcessor())
			.marshal(smartHomeCommandResponse)
			.toD("paho:${header.replyto}?brokerUrl=" + brokerUrl)
		  .when(new IsUpdateOperation())
			.setHeader("resource", simple("${body.resource}"))
			.setBody(simple("${body.value}"))
		    .toD("rest:put:/rest/items/${header.resource}?host=" + openhab)
			.setHeader("value", constant("OK"))
			.process(new CreateCommandResponseProcessor())
			.marshal(smartHomeCommandResponse)
			.toD("paho:${header.replyto}?brokerUrl=" + brokerUrl)
		  .when(new IsCreateOperation())
			.setHeader("resource", simple("${body.resource}"))
			.setBody(simple("${body.value}"))
		    .toD("rest:post:/rest/items/${header.resource}?host=" + openhab)
			.setHeader("value", constant("OK"))
			.process(new CreateCommandResponseProcessor())
			.marshal(smartHomeCommandResponse)
			.toD("paho:${header.replyto}?brokerUrl=" + brokerUrl)
		  .when(new IsDeleteOperation())
		    .toD("rest:delete:/rest/items/${body.resource}?host=" + openhab)
			.setHeader("value", constant("OK"))
			.process(new CreateCommandResponseProcessor())
			.marshal(smartHomeCommandResponse)
			.toD("paho:${header.replyto}?brokerUrl=" + brokerUrl)
		 .endChoice();
	}
}
