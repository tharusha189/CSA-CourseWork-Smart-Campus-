package com.smartcampus.exceptions;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Report Answer – 422 vs 404:
 * A 404 means the requested URL/resource was not found. But here the URL
 * (/sensors) is perfectly valid – the problem is that a field *inside* the
 * JSON body (roomId) references a non-existent entity. 422 Unprocessable
 * Entity is more accurate: the request is syntactically correct JSON but
 * semantically invalid because a dependency is missing.
 */
@Provider
public class LinkedResourceNotFoundExceptionMapper implements ExceptionMapper<LinkedResourceNotFoundException> {
    @Override
    public Response toResponse(LinkedResourceNotFoundException e) {
        return Response.status(422)
                .entity("{\"errorCode\":422,\"errorType\":\"Unprocessable Entity\",\"errorMessage\":\"" + e.getMessage() + "\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
