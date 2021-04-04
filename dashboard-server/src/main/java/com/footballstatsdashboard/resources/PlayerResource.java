package com.footballstatsdashboard.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/players")
@Produces(MediaType.APPLICATION_JSON)
public class PlayerResource {
    
    @GET
    public String getPlayer() {
        return "Found player";
    }
}
