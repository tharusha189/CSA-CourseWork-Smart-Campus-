package com.smartcampus.database;

import com.smartcampus.models.Room;
import com.smartcampus.models.Sensor;
import com.smartcampus.models.SensorReading;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory data store using thread-safe ConcurrentHashMaps.
 * All fields are static so they persist for the lifetime of the application
 * regardless of how many resource class instances JAX-RS creates.
 *
 * Readings are stored per-sensor: sensorId -> ordered List<SensorReading>
 */
public class MockDatabase {

    private static final Map<String, Room>          rooms            = new ConcurrentHashMap<>();
    private static final Map<String, Sensor>        sensors          = new ConcurrentHashMap<>();
    private static final Map<String, List<SensorReading>> readingsBySensor = new ConcurrentHashMap<>();

    public static Map<String, Room>   getRooms()   { return rooms; }
    public static Map<String, Sensor> getSensors() { return sensors; }

    /** Returns the reading list for a sensor, creating it on first access. */
    public static List<SensorReading> getReadingsForSensor(String sensorId) {
        return readingsBySensor.computeIfAbsent(sensorId, k -> new ArrayList<>());
    }
}
