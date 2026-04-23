# CSA-CourseWork-Smart-Campus-
Client Server Architecture CoursWork 

Tharusha Perera
w2120205
20231639

# Smart Campus – Sensor & Room Management API

A fully RESTful JAX-RS (Jersey 3) API for managing campus rooms and IoT sensors, deployed as a WAR on Apache Tomcat 10. All data is stored in-memory using thread-safe `ConcurrentHashMap` collections — no database required.

---

## 📋 Table of Contents
1. [API Overview](#api-overview)
2. [Technology Stack](#technology-stack)
3. [Project Structure](#project-structure)
4. [Build & Deployment](#build--deployment)
5. [Testing with curl](#testing-with-curl)
6. [Testing with Postman](#testing-with-postman)
7. [Report – Coursework Question Answers](#report--coursework-question-answers)

---

## 🌐 API Overview

| Part | Resource | Endpoints |
|------|----------|-----------|
| **1** | Discovery | `GET /api/v1/` |
| **2** | Rooms | `GET /api/v1/rooms`<br>`POST /api/v1/rooms`<br>`GET /api/v1/rooms/{id}`<br>`DELETE /api/v1/rooms/{id}` |
| **3** | Sensors | `GET /api/v1/sensors`<br>`GET /api/v1/sensors?type={type}`<br>`POST /api/v1/sensors` |
| **4** | Readings | `GET /api/v1/sensors/{id}/readings`<br>`POST /api/v1/sensors/{id}/readings` |
| **5** | Error Handling | Custom exception mappers (409, 422, 403, 500)<br>Request/response logging filter |

**Base URL:** `http://localhost:8080/SmartCampus/api/v1`

---

## 🛠️ Technology Stack

| Component | Technology | Version |
|-----------|------------|---------|
| Language | Java | 11+ |
| REST Framework | JAX-RS (Jersey) | 3.1.5 |
| Servlet Container | Apache Tomcat | 10.x |
| Build Tool | Maven | 3.6+ |
| JSON Processing | Jackson | (via Jersey) |
| Data Store | ConcurrentHashMap | In-memory |

---

## 📁 Project Structure

```
SmartCampus/
├── pom.xml                                    # Maven configuration
├── nb-configuration.xml                       # NetBeans IDE settings
├── README.md                                  # This file
└── src/main/
    ├── java/com/smartcampus/
    │   ├── config/
    │   │   └── SmartCampusApplication.java    # JAX-RS bootstrap (@ApplicationPath)
    │   ├── database/
    │   │   └── MockDatabase.java              # In-memory data store
    │   ├── models/
    │   │   ├── Room.java                      # Room POJO
    │   │   ├── Sensor.java                    # Sensor POJO
    │   │   └── SensorReading.java             # SensorReading POJO
    │   ├── resources/
    │   │   ├── DiscoveryResource.java         # GET /api/v1
    │   │   ├── RoomResource.java              # /api/v1/rooms
    │   │   ├── SensorResource.java            # /api/v1/sensors
    │   │   └── SensorReadingResource.java     # /api/v1/sensors/{id}/readings
    │   ├── exceptions/
    │   │   ├── RoomNotEmptyException.java            # 409 Conflict
    │   │   ├── RoomNotEmptyExceptionMapper.java
    │   │   ├── LinkedResourceNotFoundException.java  # 422 Unprocessable
    │   │   ├── LinkedResourceNotFoundExceptionMapper.java
    │   │   ├── SensorUnavailableException.java       # 403 Forbidden
    │   │   ├── SensorUnavailableExceptionMapper.java
    │   │   └── GenericExceptionMapper.java           # 500 catch-all
    │   └── filters/
    │       └── ApiLoggingFilter.java          # Request/response logging
    └── webapp/
        ├── index.html                         # Landing page
        ├── META-INF/
        │   └── context.xml                    # Tomcat context config
        └── WEB-INF/
            ├── web.xml                        # Servlet mapping
            └── beans.xml                      # CDI config (disabled)
```

---

## 🚀 Build & Deployment

### Prerequisites

- **Java 11** or higher (`java -version`)
- **Maven 3.6+** (`mvn -version`)
- **Apache Tomcat 10.x** ([download here](https://tomcat.apache.org/download-10.cgi))
- **NetBeans IDE** (optional, project is pre-configured)

> ⚠️ **Critical:** Must use Tomcat **10.x** — this project uses `jakarta.*` namespaces. Tomcat 9 uses the old `javax.*` and **will not work**.

### Step 1 – Clone the repository

```bash
git clone https://github.com/YOUR_USERNAME/SmartCampus.git
cd SmartCampus
```

### Step 2 – Build the WAR

```bash
mvn clean package
```

This generates `target/SmartCampus.war`.

### Step 3 – Deploy to Tomcat

**Option A – Manual deployment**
```bash
cp target/SmartCampus.war /path/to/tomcat/webapps/
```

**Option B – NetBeans**
1. Open project: `File → Open Project` → select `SmartCampus` folder
2. Ensure Tomcat 10 is registered: `Tools → Servers`
3. Right-click project → `Run`

### Step 4 – Start Tomcat

```bash
# Linux / macOS
/path/to/tomcat/bin/startup.sh

# Windows
C:\path\to\tomcat\bin\startup.bat
```

### Step 5 – Verify deployment

Open browser or Postman:
```
GET http://localhost:8080/SmartCampus/api/v1/
```

Expected response: `200 OK` with JSON metadata.

---

## 🧪 Testing with curl

### 1️⃣ Discovery
```bash
curl -X GET http://localhost:8080/SmartCampus/api/v1/
```

### 2️⃣ Create a room
```bash
curl -X POST http://localhost:8080/SmartCampus/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id":"LIB-301","name":"Library Quiet Study","capacity":40}'
```
**Expected:** `201 Created` + `Location` header

### 3️⃣ Get all rooms
```bash
curl -X GET http://localhost:8080/SmartCampus/api/v1/rooms
```

### 4️⃣ Get room by ID
```bash
curl -X GET http://localhost:8080/SmartCampus/api/v1/rooms/LIB-301
```

### 5️⃣ Register a sensor (linked to room)
```bash
curl -X POST http://localhost:8080/SmartCampus/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id":"CO2-001","type":"CO2","status":"ACTIVE","roomId":"LIB-301"}'
```
**Expected:** `201 Created` + `Location` header

### 6️⃣ Get sensors filtered by type
```bash
curl -X GET "http://localhost:8080/SmartCampus/api/v1/sensors?type=CO2"
```

### 7️⃣ Add a sensor reading
```bash
curl -X POST http://localhost:8080/SmartCampus/api/v1/sensors/CO2-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value":412.5}'
```

### 8️⃣ Get reading history
```bash
curl -X GET http://localhost:8080/SmartCampus/api/v1/sensors/CO2-001/readings
```

### 9️⃣ Error scenarios

**409 Conflict – Delete room with sensors**
```bash
curl -X DELETE http://localhost:8080/SmartCampus/api/v1/rooms/LIB-301
```

**422 Unprocessable – Invalid roomId**
```bash
curl -X POST http://localhost:8080/SmartCampus/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id":"TEMP-999","type":"Temperature","roomId":"GHOST-ROOM"}'
```

**403 Forbidden – Sensor in MAINTENANCE**
```bash
# First create a maintenance sensor
curl -X POST http://localhost:8080/SmartCampus/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id":"OCC-001","type":"Occupancy","status":"MAINTENANCE","roomId":"LIB-301"}'

# Then try to post a reading to it
curl -X POST http://localhost:8080/SmartCampus/api/v1/sensors/OCC-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value":99.9}'
```

---

## 📬 Testing with Postman

Import the provided `SmartCampus.postman_collection.json` file into Postman. The collection includes all endpoints organized by coursework part with pre-configured requests.

**To import:**
1. Open Postman
2. Click **Import** (top left)
3. Drag and drop the JSON file
4. Collection appears as **"Smart Campus API"**

The base URL variable is pre-set to `http://localhost:8080/SmartCampus/api/v1`.

---

## 📝 Report – Coursework Question Answers

### Part 1.1 – JAX-RS Resource Lifecycle

**Question:** Explain the default lifecycle of a JAX-RS Resource class. Is a new instance instantiated for every incoming request, or does the runtime treat it as a singleton? How does this impact in-memory data management?

**Answer:**

By default, JAX-RS creates a **new instance** of each resource class for every incoming HTTP request (per-request scope). This means instance fields are never shared between requests and cannot be used to store application state. 

To safely persist data across all requests, this project uses **static `ConcurrentHashMap`** fields inside `MockDatabase`. `ConcurrentHashMap` is fully thread-safe, preventing race conditions when multiple requests read and write simultaneously. Its entries exist for the lifetime of the JVM, meaning data persists across all requests without requiring a database or external storage.

**Impact on synchronization:**
- Instance fields → isolated per request, no synchronization needed but cannot store shared state
- Static collections → shared across all requests, requires thread-safe implementations (ConcurrentHashMap prevents data corruption during concurrent access)

---

### Part 1.2 – HATEOAS

**Question:** Why is the provision of "Hypermedia" (links and navigation within responses) considered a hallmark of advanced RESTful design (HATEOAS)? How does this approach benefit client developers compared to static documentation?

**Answer:**

HATEOAS (Hypermedia as the Engine of Application State) is considered advanced RESTful design because it embeds navigable links to related resources inside every response. This means clients can discover available endpoints and actions at runtime rather than relying on static external documentation.

**Key benefits:**

1. **Reduced coupling** — If a server-side URL changes, the client simply follows the updated link in the response without requiring a code change on the client side
2. **Self-documenting** — New developers can explore the entire API by starting at a single entry point and following embedded links, much like browsing a website
3. **Discoverability** — Clients learn what actions are available based on the current resource state (e.g., a "delete" link only appears if deletion is allowed)
4. **Evolvability** — The server can introduce new endpoints without breaking existing clients, as clients follow links rather than hardcoding URLs

Compared to static documentation which becomes outdated and requires manual synchronization between docs and code, HATEOAS makes the API itself the source of truth for navigation.

---

### Part 2.1 – IDs vs Full Objects

**Question:** When returning a list of rooms, what are the implications of returning only IDs versus returning the full room objects? Consider network bandwidth and client-side processing.

**Answer:**

**Returning only IDs:**
- **Pros:** Minimal bandwidth usage, fast initial response
- **Cons:** Forces the client to make one additional HTTP request per room to fetch details (the "N+1 problem"). For 100 rooms, this means 101 total requests, significantly increasing latency and server load

**Returning full objects:**
- **Pros:** Single request gives clients all necessary data immediately, drastically reducing total latency and server round-trips
- **Cons:** Larger response payload consumes more bandwidth upfront

**For this API:** Returning full objects is the better design choice because clients (e.g., a campus dashboard) are likely to need room names, capacities, and sensor lists immediately for display. The bandwidth cost of a slightly larger initial response is far outweighed by eliminating 100+ additional HTTP requests.

---

### Part 2.2 – DELETE Idempotency

**Question:** Is the DELETE operation idempotent in your implementation? Provide a detailed justification by describing what happens if a client mistakenly sends the exact same DELETE request multiple times.

**Answer:**

Yes, DELETE is **idempotent** in this implementation.

**What happens:**
1. **First DELETE** → Room exists, is removed from the store, returns `204 No Content`
2. **Second DELETE** → Room no longer exists, returns `404 Not Found`
3. **Third DELETE** → Still doesn't exist, returns `404 Not Found`

**Why this is idempotent:**

Idempotency means that multiple identical requests produce the same **server-side state**, not necessarily the same HTTP response code. After the first DELETE, the room is gone. After every subsequent DELETE, the room is still gone. The final state of the server is identical regardless of how many times the request is repeated.

The HTTP status code differs (204 vs 404), but this does not violate idempotency — the RFC definition focuses on resource state, not response codes. The key invariant holds: "the room with ID X does not exist" is true after one call and remains true after one hundred calls.

---

### Part 3.1 – @Consumes Mismatch

**Question:** We explicitly use `@Consumes(MediaType.APPLICATION_JSON)` on the POST method. Explain the technical consequences if a client attempts to send data in a different format, such as `text/plain` or `application/xml`. How does JAX-RS handle this mismatch?

**Answer:**

When `@Consumes(MediaType.APPLICATION_JSON)` is declared and a client sends a request with `Content-Type: text/plain` or `Content-Type: application/xml`, JAX-RS automatically returns **`415 Unsupported Media Type`** before the resource method body ever executes.

**Technical flow:**
1. Client sends POST with `Content-Type: text/plain`
2. JAX-RS runtime inspects the `Content-Type` header
3. Runtime compares it against the `@Consumes` annotation
4. Mismatch detected → method is never invoked
5. Returns `415 Unsupported Media Type` with no method execution

This protects the resource method from receiving and attempting to deserialize malformed or unexpected input formats. The rejection happens at the framework level, ensuring type safety without manual validation code.

---

### Part 3.2 – @QueryParam vs Path Segment

**Question:** You implemented filtering using `@QueryParam`. Contrast this with an alternative design where the type is part of the URL path (e.g., `/api/v1/sensors/type/CO2`). Why is the query parameter approach generally considered superior for filtering and searching collections?

**Answer:**

**Query parameters (`?type=CO2`):**
- Semantically correct for **filtering** — they narrow down an existing collection without implying a new resource hierarchy
- **Optional by nature** — omitting `?type=` gracefully returns the full list without requiring separate endpoint logic
- **Composable** — easy to add multiple filters: `?type=CO2&status=ACTIVE&roomId=LIB-301`
- Clearly communicates to developers that this is a search/filter operation, not a distinct resource

**Path segments (`/sensors/type/CO2`):**
- Incorrectly implies that "CO2" is a **distinct sub-resource** or collection in its own right
- Not RESTful semantics — path segments represent resource hierarchies (parent/child), not filters
- Requires separate route definitions for every filter type
- Cannot easily support multiple simultaneous filters without deeply nested paths

**Conclusion:** Query parameters are architecturally superior for filtering because they match REST semantics (refining a collection) and scale naturally as filtering requirements grow.

---

### Part 4.1 – Sub-Resource Locator Pattern

**Question:** Discuss the architectural benefits of the Sub-Resource Locator pattern. How does delegating logic to separate classes help manage complexity in large APIs compared to defining every nested path in one massive controller class?

**Answer:**

The Sub-Resource Locator pattern delegates path handling to a dedicated class (`SensorReadingResource`) rather than defining every nested route (`/sensors/{id}/readings`, `/sensors/{id}/readings/{rid}`, etc.) inside one large `SensorResource` controller.

**Architectural benefits:**

1. **Separation of concerns** — Each class handles one resource type only. `SensorResource` manages sensors; `SensorReadingResource` manages readings. Clear boundaries make code easier to understand
2. **Scalability** — Adding `/sensors/{id}/alerts` or `/sensors/{id}/config` requires only creating a new class, not modifying existing code. Follows the Open/Closed Principle
3. **Readability** — A single controller with 50+ methods for every nested path becomes unmanageable. Small, focused classes remain navigable as the API grows
4. **Testability** — Smaller classes with fewer dependencies are easier to unit test in isolation
5. **Team collaboration** — Different developers can work on different sub-resource classes without merge conflicts

**Without this pattern:** A single `SensorResource` class would grow to contain dozens of methods for every nested path level, becoming a "God Object" that violates single-responsibility and becomes increasingly difficult to maintain, test, and extend.

---

### Part 5.1 – HTTP 422 vs 404

**Question:** Why is HTTP 422 often considered more semantically accurate than a standard 404 when the issue is a missing reference inside a valid JSON payload?

**Answer:**

**404 Not Found** means the **requested URL/resource** does not exist on the server. It communicates "I couldn't find what you asked for at this location."

**In this scenario:**
- The URL `POST /api/v1/sensors` is perfectly valid and exists
- The server found the endpoint successfully
- The JSON payload is syntactically correct (valid JSON)
- **The problem:** A field *inside* the body (`roomId: "GHOST-ROOM"`) references an entity that doesn't exist

**422 Unprocessable Entity** is more semantically accurate because:
- The request reached the correct endpoint (URL found ✓)
- The request is well-formed JSON (syntax valid ✓)
- The request fails a **business-rule validation** — a required dependency (the referenced room) is missing
- `422` specifically means: "I understood your request, but I cannot process it due to semantic/validation errors"

**Analogy:** It's like submitting a job application (valid form, correct address) but listing a university degree from a non-existent institution. The application arrived (not 404), but it can't be processed due to invalid reference data (422).

This distinction helps API consumers understand whether the problem is the URL they called (404) or the data they sent (422).

---

### Part 5.2 – Stack Trace Exposure Risk

**Question:** From a cybersecurity standpoint, explain the risks associated with exposing internal Java stack traces to external API consumers. What specific information could an attacker gather from such a trace?

**Answer:**

Exposing raw Java stack traces to external API consumers is a significant security vulnerability. Attackers can extract:

**1. Internal package and class names**
- Example: `com.smartcampus.database.MockDatabase.getSensor(MockDatabase.java:42)`
- Reveals codebase structure, helping attackers target specific classes or methods

**2. Library versions**
- Example: `at org.glassfish.jersey.servlet.ServletContainer.service(ServletContainer.java:372)`
- Exposes exact dependency versions (Jersey 3.1.5), allowing attackers to look up known CVEs (Common Vulnerabilities and Exposures) for those specific versions

**3. Server filesystem paths**
- Example: `/opt/tomcat/webapps/SmartCampus/WEB-INF/classes/...`
- Reveals directory structure, deployment paths, and server configuration details

**4. Business logic and SQL hints**
- Exception messages may expose query fragments, variable names, or conditional logic useful for crafting injection attacks
- Example: `Invalid room_id in SELECT * FROM sensors WHERE room_id = 'X'` teaches an attacker the database schema

**5. Technology fingerprinting**
- Stack traces reveal the framework (JAX-RS), server (Tomcat), language version (Java 11), and runtime environment
- Enables targeted attacks specific to that technology stack

**Mitigation in this project:**

The `GenericExceptionMapper` catches all unhandled `Throwable` exceptions, logs the full stack trace **server-side** for developers to debug, but returns only a safe, generic `500 Internal Server Error` message to the client. This prevents information leakage while preserving observability for the development team.

---

### Part 5.3 – Filters vs Manual Logging

**Question:** Why is it advantageous to use JAX-RS filters for cross-cutting concerns like logging, rather than manually inserting `Logger.info()` statements inside every single resource method?

**Answer:**

Using JAX-RS filters (specifically `ContainerRequestFilter` and `ContainerResponseFilter`) for logging is architecturally superior to manual `Logger.info()` calls for several reasons:

**1. Single point of control**
- Logging logic lives in one class (`ApiLoggingFilter`)
- Any format change (e.g., adding timestamps, request IDs) applies everywhere automatically
- No need to hunt through dozens of files to update logging

**2. Zero code duplication**
- Adding a new resource endpoint gets request/response logging for free without writing a single extra line
- Eliminates copy-paste errors where logging format differs between methods

**3. Separation of concerns**
- Resource methods contain **only business logic**
- Infrastructure concerns (logging, auth, CORS) live in filters
- Makes resource classes cleaner, shorter, and easier to read

**4. Guaranteed consistency**
- Every request and response is logged in exactly the same format
- Cannot forget to add logging to a new method (a common manual logging mistake)

**5. Maintainability**
- If log format needs changing, modify one file instead of every resource class
- Easier to add new cross-cutting concerns (e.g., performance timing, audit trails) without touching business logic

**6. Testability**
- Resource methods can be unit tested without needing to mock or verify logging calls
- Filter can be tested independently from business logic

**Without filters:** A codebase with 50+ resource methods requires 100+ duplicated logging statements, creating maintenance burden and inconsistency risk. Filters apply the logging concern declaratively and universally.

---

## 📜 License

This project is submitted as coursework for the **Client-Server Architectures** module at the University of Westminster (2025/26).

---

## 👤 Author

**Tharusha Mihiruwan Perera**  
Student ID: w2120205 
GitHub: https://github.com/tharusha189/CSA-CourseWork-Smart-Campus-.git

---

