package org.acme;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.gmagnotta.AstroBean;

import org.jboss.logging.Logger;

@Path("/hello")
public class GreetingResource {

    @Inject
    Logger logger;

    @Inject
    AstroBean astroBean;
	
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() throws Exception {
        astroBean.openBlinds();

        return "Done";
    }
}