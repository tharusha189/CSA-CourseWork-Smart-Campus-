package com.smartcampus.exceptions;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class SensorUnavailableExceptionMapper implements ExceptionMapper<SensorUnavailableException> {
    @Override
    public Response toResponse(SensorUnavailableException e) {
        return Response.status(Response.Status.FORBIDDEN)
                .entity("{\"errorCode\":403,\"errorType\":\"Forbidden\",\"errorMessage\":\"" + e.getMessage() + "\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
