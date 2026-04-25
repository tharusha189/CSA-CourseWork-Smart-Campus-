package com.smartcampus.config;

import com.smartcampus.exceptions.*;
import com.smartcampus.filters.ApiLoggingFilter;
import com.smartcampus.resources.*;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * JAX-RS Application configuration.
 *
 * Lifecycle (Report Answer):
 * By default, JAX-RS creates a NEW resource class instance per HTTP request
 * (per-request scope). This means instance fields are NOT shared between
 * requests and cannot be used to store data. To safely share in-memory state
 * across all requests, we use static ConcurrentHashMaps inside MockDatabase.
 * ConcurrentHashMap is thread-safe, preventing race conditions when multiple
 * requests read/write simultaneously.
 */
@ApplicationPath("/api/v1")
public class SmartCampusApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> s = new HashSet<>();

        // Resources
        s.add(DiscoveryResource.class);
        s.add(RoomResource.class);
        s.add(SensorResource.class);

        // Exception Mappers
        s.add(RoomNotEmptyExceptionMapper.class);
        s.add(LinkedResourceNotFoundExceptionMapper.class);
        s.add(SensorUnavailableExceptionMapper.class);
        s.add(GenericExceptionMapper.class);

        // Filters
        s.add(ApiLoggingFilter.class);

        return s;
    }
}
