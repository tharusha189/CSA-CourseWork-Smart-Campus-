package com.smartcampus.exceptions;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class RoomNotEmptyExceptionMapper implements ExceptionMapper<RoomNotEmptyException> {
    @Override
    public Response toResponse(RoomNotEmptyException e) {
        return Response.status(Response.Status.CONFLICT)
                .entity("{\"errorCode\":409,\"errorType\":\"Conflict\",\"errorMessage\":\"" + e.getMessage() + "\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
