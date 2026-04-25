# Smart Campus API

Public GitHub repository for the Smart Campus Sensor and Room Management coursework API.

---

## 👥 Authors

**Tharusha Perera**

- UoW ID: `W2120205`
- IIT ID: `20231639`
- GitHub: [https://github.com/tharusha189/CSA-CourseWork-Smart-Campus-](https://github.com/tharusha189/CSA-CourseWork-Smart-Campus-)

---

## 📖 API Design Overview

This project is a Java RESTful web application developed using **JAX-RS (Jersey)** and deployed on **Apache Tomcat 10** as a WAR file.

The API manages three core resources:

- **Rooms** – campus locations such as labs, libraries, and lecture halls
- **Sensors** – monitoring devices assigned to rooms
- **Sensor Readings** – time-stamped historical readings recorded by each sensor

The system follows a hierarchical resource structure:

```
Room → Sensor → SensorReading
```

Meaning:
- A room can contain multiple sensors
- Each sensor belongs to exactly one room
- Each sensor stores a full history of readings

---

## 🗂️ Project Structure

```
SmartCampusFinal/
├── pom.xml
├── nb-configuration.xml
├── .gitignore
├── README.md
└── src/
    └── main/
        ├── java/com/smartcampus/
        │   ├── config/
        │   │   └── SmartCampusApplication.java       # Jersey app registration (@ApplicationPath)
        │   ├── database/
        │   │   └── MockDatabase.java                 # In-memory ConcurrentHashMap store
        │   ├── exceptions/
        │   │   ├── GenericExceptionMapper.java        # Catch-all → 500 (no stack trace)
        │   │   ├── LinkedResourceNotFoundException.java
        │   │   ├── LinkedResourceNotFoundExceptionMapper.java  # → 422
        │   │   ├── RoomNotEmptyException.java
        │   │   ├── RoomNotEmptyExceptionMapper.java   # → 409
        │   │   ├── SensorUnavailableException.java
        │   │   └── SensorUnavailableExceptionMapper.java       # → 403
        │   ├── filters/
        │   │   └── ApiLoggingFilter.java              # Request/response logging filter
        │   ├── models/
        │   │   ├── Room.java
        │   │   ├── Sensor.java
        │   │   └── SensorReading.java
        │   └── resources/
        │       ├── DiscoveryResource.java             # GET /api/v1/
        │       ├── RoomResource.java                  # /api/v1/rooms
        │       ├── SensorResource.java                # /api/v1/sensors
        │       └── SensorReadingResource.java         # /api/v1/sensors/{id}/readings
        └── webapp/
            ├── META-INF/context.xml
            ├── WEB-INF/
            │   ├── beans.xml
            │   └── web.xml                           # Maps /api/v1/* to Jersey servlet
            └── index.html
```

---

## 🌐 Base URL

The WAR is named `SmartCampus.war` (set in `pom.xml` `<finalName>`).
Jersey is mapped to `/api/v1/*` in `web.xml` and confirmed by `@ApplicationPath("/api/v1")` in `SmartCampusApplication.java`.

**All endpoints are available at:**

```
http://localhost:8080/SmartCampus/api/v1
```

---

## 📍 Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `http://localhost:8080/SmartCampus/api/v1/` | Discovery — HATEOAS metadata |
| `GET` | `http://localhost:8080/SmartCampus/api/v1/rooms` | Get all rooms |
| `POST` | `http://localhost:8080/SmartCampus/api/v1/rooms` | Create room → `201` + `Location` header |
| `GET` | `http://localhost:8080/SmartCampus/api/v1/rooms/{id}` | Get room by ID |
| `DELETE` | `http://localhost:8080/SmartCampus/api/v1/rooms/{id}` | Delete room (→ `409` if sensors exist) |
| `GET` | `http://localhost:8080/SmartCampus/api/v1/sensors` | Get all sensors |
| `GET` | `http://localhost:8080/SmartCampus/api/v1/sensors?type=CO2` | Filter sensors by type |
| `POST` | `http://localhost:8080/SmartCampus/api/v1/sensors` | Register sensor → `201` + `Location` header |
| `GET` | `http://localhost:8080/SmartCampus/api/v1/sensors/{id}` | Get sensor by ID |
| `GET` | `http://localhost:8080/SmartCampus/api/v1/sensors/{id}/readings` | Get reading history |
| `POST` | `http://localhost:8080/SmartCampus/api/v1/sensors/{id}/readings` | Add reading → updates `currentValue` |

---

## ⚠️ Error Handling

All errors return a structured JSON body — no raw stack traces ever reach the client.

| Status | Meaning | Triggered By |
|--------|---------|--------------|
| `404` | Not Found | Room or sensor ID does not exist |
| `409` | Conflict | Deleting a room that still has sensors assigned |
| `422` | Unprocessable Entity | Sensor `roomId` references a non-existent room |
| `403` | Forbidden | Posting a reading to a `MAINTENANCE` sensor |
| `415` | Unsupported Media Type | Wrong `Content-Type` (not `application/json`) |
| `500` | Internal Server Error | Unhandled exception caught by `GenericExceptionMapper` |

---

## 🚀 Build and Run (Step-by-Step)

### Prerequisites

- Java 11+
- Maven 3.6+
- Apache Tomcat 10.1+

---

### Step 1 – Clone the Repository

```bash
git clone https://github.com/tharusha189/CSA-CourseWork-Smart-Campus-.git
cd CSA-CourseWork-Smart-Campus-
```

---

### Step 2 – Build the Project

```bash
mvn clean package
```

Expected output:

```
BUILD SUCCESS
target/SmartCampus.war
```

---

### Step 3 – Deploy WAR to Tomcat

**Windows**
```powershell
Copy-Item target\SmartCampus.war C:\apache-tomcat-10\webapps\
```

---

### Step 4 – Start Tomcat

**Windows**
```powershell
C:\apache-tomcat-10\bin\startup.bat
```

---

### Step 5 – Verify Server is Running

Open your browser and go to:

```
http://localhost:8080/SmartCampus/api/v1/
```

Expected JSON response:

```json
{
  "api_version": "1.0",
  "description": "Smart Campus Sensor & Room Management API",
  "admin_contact": "admin@smartcampus.ac.uk",
  "resources": {
    "rooms": "/api/v1/rooms",
    "sensors": "/api/v1/sensors"
  }
}
```

---

## 🧪 Sample curl Commands

```bash
BASE_URL="http://localhost:8080/SmartCampus/api/v1"
```

### 1. Discovery Endpoint
```bash
curl -X GET "$BASE_URL/"
```

### 2. Create Room (201 + Location header)
```bash
curl -X POST "$BASE_URL/rooms" \
  -H "Content-Type: application/json" \
  -d '{"id":"LIB-301","name":"Library Quiet Study","capacity":80}'
```

### 3. Get All Rooms
```bash
curl -X GET "$BASE_URL/rooms"
```

### 4. Get Room by ID
```bash
curl -X GET "$BASE_URL/rooms/LIB-301"
```

### 5. Delete Room — Success (no sensors)
```bash
curl -X DELETE "$BASE_URL/rooms/LAB-101"
```

### 6. Delete Room — 409 Conflict (has sensors)
```bash
curl -X DELETE "$BASE_URL/rooms/LIB-301"
```

### 7. Create Sensor (201 + Location header)
```bash
curl -X POST "$BASE_URL/sensors" \
  -H "Content-Type: application/json" \
  -d '{"id":"CO2-001","type":"CO2","status":"ACTIVE","currentValue":0,"roomId":"LIB-301"}'
```

### 8. Get All Sensors
```bash
curl -X GET "$BASE_URL/sensors"
```

### 9. Filter Sensors by Type
```bash
curl -X GET "$BASE_URL/sensors?type=CO2"
```

### 10. Get Sensor by ID (verify currentValue)
```bash
curl -X GET "$BASE_URL/sensors/CO2-001"
```

### 11. Add Reading (updates parent currentValue)
```bash
curl -X POST "$BASE_URL/sensors/CO2-001/readings" \
  -H "Content-Type: application/json" \
  -d '{"value":580.0}'
```

### 12. Get Reading History
```bash
curl -X GET "$BASE_URL/sensors/CO2-001/readings"
```

### 13. Create Sensor with Invalid roomId — 422
```bash
curl -X POST "$BASE_URL/sensors" \
  -H "Content-Type: application/json" \
  -d '{"id":"TEMP-999","type":"Temperature","roomId":"GHOST-ROOM"}'
```

### 14. Post Reading to MAINTENANCE Sensor — 403
```bash
curl -X POST "$BASE_URL/sensors" \
  -H "Content-Type: application/json" \
  -d '{"id":"OCC-001","type":"Occupancy","status":"MAINTENANCE","roomId":"LIB-301"}'

curl -X POST "$BASE_URL/sensors/OCC-001/readings" \
  -H "Content-Type: application/json" \
  -d '{"value":99.9}'
```

### 15. Wrong Content-Type — 415
```bash
curl -X POST "$BASE_URL/sensors" \
  -H "Content-Type: text/plain" \
  -d 'id=BAD&type=Temperature&roomId=LIB-301'
```

---

## 🛠️ Technologies Used

| Technology | Purpose |
|------------|---------|
| Java 11 | Core language |
| Maven | Build and dependency management |
| Jersey (JAX-RS 3.1) | REST framework |
| Jackson | JSON serialization/deserialization |
| Apache Tomcat 10.1 | Servlet container / WAR deployment |
| ConcurrentHashMap | Thread-safe in-memory data store |

---

## 📝 Coursework Questions and Answers

### Part 1 – Setup & Discovery

**Question 1: Explain the default lifecycle of a JAX-RS Resource class. How does this impact in-memory data management?**

JAX-RS resource classes follow a **per-request lifecycle** by default — a new instance is created for every incoming HTTP request and discarded when the response is sent. This eliminates accidental sharing of instance-level state between concurrent requests, which would otherwise cause race conditions.

However, the per-request lifecycle only protects instance fields. In this project, shared data is stored in static `ConcurrentHashMap` collections inside `MockDatabase`. These maps persist across all requests regardless of how many resource instances are created. `ConcurrentHashMap` provides segment-level locking, allowing concurrent reads without blocking while protecting writes at the key level — critical when multiple sensors or rooms may be created simultaneously. Without this thread-safe structure, data corruption would occur under concurrent load even with per-request resource instances.

---

**Question 2: Why is HATEOAS considered a hallmark of advanced RESTful design?**

HATEOAS (Hypermedia as the Engine of Application State) means API responses include navigable links to related resources, allowing clients to discover valid actions at runtime rather than relying on hardcoded URLs or static documentation.

For example, `GET http://localhost:8080/SmartCampus/api/v1/` returns:

```json
{
  "resources": {
    "rooms": "/api/v1/rooms",
    "sensors": "/api/v1/sensors"
  }
}
```

Key benefits:
- **Reduces coupling** — clients follow server-provided links instead of hardcoding URIs
- **Self-documenting** — new developers explore the API without external docs
- **Enables API evolution** — if a route changes, clients follow the updated link automatically
- **Runtime discoverability** — clients adapt to available actions dynamically

This makes HATEOAS the defining characteristic of a Level 3 REST API in the Richardson Maturity Model.

---

### Part 2 – Room Management

**Question 3: What are the implications of returning only IDs versus returning full room objects?**

Returning only IDs is bandwidth-efficient but introduces the **N+1 problem** — for 100 rooms the client must make 1 list request plus 100 individual GET requests, creating significant latency and server load.

Returning full room objects costs more bandwidth per response but eliminates N+1 entirely. A campus dashboard displaying room name, capacity, and sensor count for every room is far better served by one request returning full objects than hundreds of round trips.

In this project, full room objects are returned because the dataset is small, clients typically need all metadata immediately, and one efficient request is significantly better than hundreds of individual fetches.

---

**Question 4: Is the DELETE operation idempotent in your implementation?**

Yes. DELETE is idempotent per RFC 9110 — repeated identical requests must leave the server in the same final state.

- First `DELETE http://localhost:8080/SmartCampus/api/v1/rooms/LAB-101` → removes the room, returns `204 No Content`
- Second call → room is already gone, returns `404 Not Found`

Although the status code differs, the **server state is identical** after each call: the room does not exist. Idempotency refers to server state, not the response code. The room is gone after the first call and remains gone after every subsequent call.

---

### Part 3 – Sensors & Filtering

**Question 5: What happens if a client sends data in a different format than application/json?**

When a method declares `@Consumes(MediaType.APPLICATION_JSON)`, the runtime inspects the `Content-Type` header before invoking the method. If the header is `text/plain` or `application/xml`, Jersey searches for a compatible `MessageBodyReader` and finds none — immediately returning **415 Unsupported Media Type** before the method body ever executes.

Technical flow:
1. Client sends `POST http://localhost:8080/SmartCampus/api/v1/sensors` with `Content-Type: text/plain`
2. JAX-RS runtime reads the `Content-Type` header
3. Compares against `@Consumes(APPLICATION_JSON)` — mismatch detected
4. Method is skipped entirely → returns `415 Unsupported Media Type`

---

**Question 6: Why is the query parameter approach superior to path segments for filtering?**

Query parameters (`http://localhost:8080/SmartCampus/api/v1/sensors?type=CO2`) are semantically correct — the client requests the same collection resource with optional narrowing criteria applied on top.

A path-based approach (`/sensors/type/CO2`) implies `CO2` is a distinct sub-resource — semantically wrong for a filter operation. Per RFC 3986, path segments identify resources; query components represent optional parameters.

Additional advantages:
- **Optional by default** — omitting `?type=` returns the full list with no extra code
- **Composable** — `?type=CO2&status=ACTIVE` combines filters naturally
- **Semantically clean** — no pollution of the URI path structure

---

### Part 4 – Sub-Resources

**Question 7: What are the architectural benefits of the Sub-Resource Locator pattern?**

The Sub-Resource Locator pattern delegates nested paths to dedicated classes. `SensorResource` does not handle `http://localhost:8080/SmartCampus/api/v1/sensors/{id}/readings` directly — it returns `new SensorReadingResource(sensorId)` and JAX-RS continues path matching inside that class.

Key benefits:
- **Single Responsibility** — `SensorResource` manages sensors; `SensorReadingResource` manages readings only
- **Independent testability** — sub-resources can be unit tested without the parent class
- **Scalable** — adding `/sensors/{id}/alerts` only requires a new class, not modifying existing code
- **No God Object** — no single class accumulates every method for every nested path

---

### Part 5 – Error Handling & Logging

**Question 8: Why is HTTP 422 more semantically accurate than 404 for invalid payload references?**

A `POST http://localhost:8080/SmartCampus/api/v1/sensors` with a `roomId` referencing a non-existent room is **syntactically valid JSON** sent to an **endpoint that exists** — so 404 is wrong, because the URI was found and routed correctly.

**422 Unprocessable Entity** (RFC 4918) is correct because:
- The request is well-formed ✓
- The endpoint exists and was routed correctly ✓
- But the payload references a dependency that cannot be resolved ✗

---

**Question 9: What security risks are associated with exposing Java stack traces?**

Raw stack traces expose:
1. **Internal package and class names** — reveals architecture
2. **Framework and library versions** — enables CVE cross-referencing
3. **Server file paths** — reveals deployment directory structure
4. **Method names and logic flow** — enables targeted exploitation
5. **Third-party dependency names** — identifies potentially unpatched libraries

`GenericExceptionMapper<Throwable>` catches all unhandled exceptions, logs the full trace server-side, and returns only `{"errorCode": 500, "message": "An unexpected internal error occurred."}` to the client — preventing any information leakage.

---

**Question 10: Why use filters instead of manual Logger.info() calls?**

`ApiLoggingFilter` applies logging uniformly across all endpoints as a cross-cutting concern.

Benefits over manual logging:
- **Centralised** — one file controls all logging for the entire API
- **DRY** — no repeated code across every resource method
- **Guaranteed** — filter runs for every request including those that throw exceptions, whereas manual calls may be skipped if an exception fires before reaching them
- **Clean business logic** — resource classes stay focused on their core responsibility

Without filters: 50+ endpoints would require 100+ duplicated logging statements. Changing the log format would require editing every resource file.

---
