package com.smartcampus.resources;

import com.smartcampus.database.MockDatabase;
import com.smartcampus.exceptions.SensorUnavailableException;
import com.smartcampus.models.Sensor;
import com.smartcampus.models.SensorReading;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

/**
 * Manages /api/v1/sensors/{sensorId}/readings
 * Instantiated by SensorResource sub-resource locator.
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    /** GET /api/v1/sensors/{sensorId}/readings – full history for this sensor */
    @GET
    public Response getReadings() {
        Sensor sensor = MockDatabase.getSensors().get(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Sensor not found: " + sensorId + "\"}")
                    .build();
        }
        List<SensorReading> history = MockDatabase.getReadingsForSensor(sensorId);
        return Response.ok(history).build();
    }

    /**
     * POST /api/v1/sensors/{sensorId}/readings – append a new reading.
     * Side effect: updates the parent sensor's currentValue.
     * 403 if the sensor is in MAINTENANCE.
     */
    @POST
    public Response addReading(SensorReading reading) {
        Sensor sensor = MockDatabase.getSensors().get(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Sensor not found: " + sensorId + "\"}")
                    .build();
        }
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(
                "Sensor '" + sensorId + "' is in MAINTENANCE and cannot accept readings.");
        }
        reading.setId(UUID.randomUUID().toString());
        if (reading.getTimestamp() == 0) {
            reading.setTimestamp(System.currentTimeMillis());
        }
        // Persist reading
        MockDatabase.getReadingsForSensor(sensorId).add(reading);
        // Keep parent sensor's currentValue in sync
        sensor.setCurrentValue(reading.getValue());
        return Response.status(Response.Status.CREATED).entity(reading).build();
    }
}
