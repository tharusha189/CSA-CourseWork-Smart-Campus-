package com.smartcampus.exceptions;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.logging.Logger;

/**
 * Global safety net – catches any unhandled Throwable.
 *
 * Report Answer – Stack trace exposure risk:
 * A raw stack trace reveals: (1) internal package/class names that help an
 * attacker map the codebase; (2) library versions that expose known CVEs;
 * (3) file paths on the server filesystem; (4) SQL or logic hints that aid
 * injection attacks. Returning only a generic 500 message hides all of this
 * while still logging the full trace server-side for developers.
 */
@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = Logger.getLogger(GenericExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable e) {
        // Let JAX-RS handle its own exceptions (404, 415, etc.) normally
        if (e instanceof WebApplicationException) {
            return ((WebApplicationException) e).getResponse();
        }
        LOGGER.severe("Unhandled exception: " + e.getMessage());
        e.printStackTrace();
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"errorCode\":500,\"errorType\":\"Internal Server Error\","
                        + "\"errorMessage\":\"An unexpected error occurred. Please try again later.\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
