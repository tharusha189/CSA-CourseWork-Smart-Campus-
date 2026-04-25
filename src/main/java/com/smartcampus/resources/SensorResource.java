package com.smartcampus.resources;

import com.smartcampus.database.MockDatabase;
import com.smartcampus.exceptions.LinkedResourceNotFoundException;
import com.smartcampus.models.Room;
import com.smartcampus.models.Sensor;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.net.URI;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Manages /api/v1/sensors
 *
 * Report Answer – @Consumes mismatch:
 * When @Consumes(APPLICATION_JSON) is declared and a client sends text/plain
 * or application/xml, JAX-RS returns 415 Unsupported Media Type before the
 * method body even executes. The runtime checks the Content-Type header and
 * rejects the request immediately, protecting the method from receiving
 * malformed input.
 *
 * Report Answer – @QueryParam vs path segment for filtering:
 * Query parameters (?type=CO2) are semantically correct for filtering a
 * collection – they narrow an existing resource without creating a new
 * resource path. A path segment (/sensors/type/CO2) implies "CO2" is a
 * distinct resource or sub-collection, which it isn't. Query params are also
 * optional by nature, so omitting them gracefully returns the full list
 * without any extra code branching.
 */
@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    @Context
    private UriInfo uriInfo;

    /** GET /api/v1/sensors?type={type} – list all, optionally filtered by type */
    @GET
    public Collection<Sensor> getAllSensors(@QueryParam("type") String type) {
        Collection<Sensor> all = MockDatabase.getSensors().values();
        if (type != null && !type.trim().isEmpty()) {
            return all.stream()
                    .filter(s -> type.equalsIgnoreCase(s.getType()))
                    .collect(Collectors.toList());
        }
        return all;
    }

    /** POST /api/v1/sensors – register a new sensor (roomId must exist), returns 201 + Location */
    @POST
    public Response createSensor(Sensor sensor) {
        Room room = MockDatabase.getRooms().get(sensor.getRoomId());
        if (room == null) {
            throw new LinkedResourceNotFoundException(
                "Cannot create sensor. Room ID '" + sensor.getRoomId() + "' does not exist.");
        }
        if (sensor.getId() == null || sensor.getId().trim().isEmpty()) {
            sensor.setId(UUID.randomUUID().toString());
        }
        if (sensor.getStatus() == null || sensor.getStatus().trim().isEmpty()) {
            sensor.setStatus("ACTIVE");
        }
        MockDatabase.getSensors().put(sensor.getId(), sensor);
        room.getSensorIds().add(sensor.getId());

        URI location = uriInfo.getAbsolutePathBuilder()
                .path(sensor.getId())
                .build();

        return Response.created(location).entity(sensor).build();
    }

    /** GET /api/v1/sensors/{sensorId} – fetch one sensor by ID */
    @GET
    @Path("/{sensorId}")
    public Response getSensorById(@PathParam("sensorId") String sensorId) {
        Sensor sensor = MockDatabase.getSensors().get(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Sensor not found: " + sensorId + "\"}")
                    .build();
        }
        return Response.ok(sensor).build();
    }

    /**
     * Sub-resource locator – delegates /{sensorId}/readings/* to
     * SensorReadingResource without declaring an HTTP verb here.
     *
     * Report Answer – Sub-Resource Locator pattern:
     * By returning a new SensorReadingResource instance, we keep each class
     * focused on one responsibility. As the API grows (e.g., adding
     * /sensors/{id}/alerts or /sensors/{id}/config), we simply add new
     * sub-resource classes rather than bloating SensorResource with dozens of
     * methods. JAX-RS continues path matching inside the returned object,
     * making the hierarchy explicit and maintainable.
     */
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}
