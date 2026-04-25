package com.smartcampus.resources;

import com.smartcampus.database.MockDatabase;
import com.smartcampus.exceptions.RoomNotEmptyException;
import com.smartcampus.models.Room;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.net.URI;
import java.util.Collection;
import java.util.UUID;

/**
 * Manages /api/v1/rooms
 *
 * Report Answer – IDs vs full objects:
 * Returning only IDs is bandwidth-efficient but forces the client to make N
 * additional requests to fetch each room's details ("N+1 problem"). Returning
 * full objects costs more bandwidth upfront but is more efficient overall when
 * clients need all the data. For a campus dashboard that lists all rooms with
 * their capacity, returning full objects is the better choice.
 *
 * Report Answer – DELETE idempotency:
 * Yes, DELETE is idempotent in this implementation. The first call removes the
 * room and returns 204 No Content. Every subsequent call finds no room and
 * returns 404 Not Found. The server state after the first call is identical to
 * the state after any repeated call – the room is gone – so the operation is
 * idempotent even though the HTTP status code differs.
 */
@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    @Context
    private UriInfo uriInfo;

    /** GET /api/v1/rooms – list all rooms */
    @GET
    public Collection<Room> getAllRooms() {
        return MockDatabase.getRooms().values();
    }

    /** POST /api/v1/rooms – create a new room, returns 201 + Location header */
    @POST
    public Response createRoom(Room room) {
        if (room.getId() == null || room.getId().trim().isEmpty()) {
            room.setId(UUID.randomUUID().toString());
        }
        MockDatabase.getRooms().put(room.getId(), room);

        URI location = uriInfo.getAbsolutePathBuilder()
                .path(room.getId())
                .build();

        return Response.created(location).entity(room).build();
    }

    /** GET /api/v1/rooms/{roomId} – fetch one room */
    @GET
    @Path("/{roomId}")
    public Response getRoomById(@PathParam("roomId") String roomId) {
        Room room = MockDatabase.getRooms().get(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Room not found: " + roomId + "\"}")
                    .build();
        }
        return Response.ok(room).build();
    }

    /**
     * DELETE /api/v1/rooms/{roomId}
     * Business rule: cannot delete a room that still has sensors assigned → 409.
     */
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = MockDatabase.getRooms().get(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Room not found: " + roomId + "\"}")
                    .build();
        }
        if (!room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException(
                "Cannot delete room '" + roomId + "'. It still has " +
                room.getSensorIds().size() + " sensor(s) assigned.");
        }
        MockDatabase.getRooms().remove(roomId);
        return Response.noContent().build();
    }
}
