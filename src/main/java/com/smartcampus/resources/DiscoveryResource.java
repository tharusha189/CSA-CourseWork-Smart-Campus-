package com.smartcampus.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * GET /api/v1
 * Returns API metadata with hypermedia links (HATEOAS).
 *
 * Report Answer – HATEOAS:
 * Hypermedia as the Engine of Application State means every response includes
 * navigable links to related resources. Clients discover available actions from
 * the response itself rather than consulting external documentation. This
 * reduces coupling: if a URL changes, clients follow the updated link instead
 * of requiring a code change. It also self-documents the API and makes it
 * easier for new developers to explore available endpoints at runtime.
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {

    @GET
    public Response getApiMetadata() {
        Map<String, Object> meta = new HashMap<>();
        meta.put("api_version", "1.0");
        meta.put("description", "Smart Campus Sensor & Room Management API");
        meta.put("admin_contact", "admin@smartcampus.ac.uk");

        Map<String, String> links = new HashMap<>();
        links.put("rooms",   "/api/v1/rooms");
        links.put("sensors", "/api/v1/sensors");
        meta.put("resources", links);

        return Response.ok(meta).build();
    }
}
