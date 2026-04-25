# Smart Campus API

**Public GitHub repository for the Smart Campus Sensor and Room Management coursework API.**

---

## 👥 Authors

**Tharusha Perera**  
- UoW ID: W2120205  
- IIT ID: 20231639

---

## 📖 API Design Overview

This project is a Java RESTful web application developed using **JAX-RS (Jersey)** and deployed on **Apache Tomcat** as a WAR file.

The API is designed to manage three core resources:

- **Rooms** – campus locations such as labs, libraries, lecture halls
- **Sensors** – monitoring devices assigned to rooms
- **Sensor Readings** – time-stamped historical readings recorded by sensors

The system follows a hierarchical resource structure:

```
Room → Sensor → SensorReading
```

**Meaning:**
- A room can contain multiple sensors
- Each sensor belongs to one room
- Each sensor can store multiple readings

---

## 🌐 API Base Path

All endpoints are available under:

```
/api/v1
```

If deployed on Tomcat as:

```
SmartCampus.war
```

The full base URL becomes:

```
http://localhost:8080/SmartCampus/api/v1
```

---

## 📍 Main Endpoints

| Method | Endpoint | Purpose |
|--------|----------|---------|
| GET | `/api/v1/` | Discovery endpoint |
| GET | `/api/v1/rooms` | Get all rooms |
| POST | `/api/v1/rooms` | Create room |
| GET | `/api/v1/rooms/{id}` | Get room by ID |
| DELETE | `/api/v1/rooms/{id}` | Delete room |
| GET | `/api/v1/sensors` | Get all sensors |
| GET | `/api/v1/sensors?type=CO2` | Filter sensors by type |
| POST | `/api/v1/sensors` | Create sensor |
| GET | `/api/v1/sensors/{id}/readings` | Get reading history |
| POST | `/api/v1/sensors/{id}/readings` | Add reading |

---

## ⚠️ Error Handling

Custom exception mappers are implemented.

| Status Code | Meaning |
|-------------|---------|
| 404 | Resource not found |
| 409 | Room still contains linked sensors |
| 422 | Sensor references missing room |
| 403 | Sensor under maintenance |
| 415 | Unsupported media type |
| 500 | Internal server error |

---

## 🚀 Build and Launch Server (Step-by-Step)

### Prerequisites

Install the following:

- **Java 11+**
- **Maven 3.6+**
- **Apache Tomcat 10.1+**

---

### Step 1 – Clone Repository

```bash
git clone https://github.com/YOUR_USERNAME/SmartCampus.git
cd SmartCampus
```

---

### Step 2 – Build the Project

```bash
mvn clean package
```

**Expected output:**

```
target/SmartCampus.war
```

---

### Step 3 – Deploy WAR to Tomcat

Copy the WAR file into Tomcat's `webapps` folder.

**Windows**
```powershell
Copy-Item target\SmartCampus.war C:\apache-tomcat-10\webapps\
```

**macOS / Linux**
```bash
cp target/SmartCampus.war /path/to/apache-tomcat/webapps/
```

---

### Step 4 – Start Tomcat

**Windows**
```cmd
C:\apache-tomcat-10\bin\startup.bat
```

**macOS / Linux**
```bash
/path/to/apache-tomcat/bin/startup.sh
```

---

### Step 5 – Verify Server

Open browser:

```
http://localhost:8080/SmartCampus/api/v1/
```

You should receive JSON discovery metadata.

---

## 🧪 Sample curl Commands

Use this base URL:

```bash
BASE_URL="http://localhost:8080/SmartCampus/api/v1"
```

### 1. Discovery Endpoint

```bash
curl -X GET "$BASE_URL/"
```

### 2. Create Room

```bash
curl -X POST "$BASE_URL/rooms" \
-H "Content-Type: application/json" \
-d '{"id":"LIB-301","name":"Library Quiet Study","capacity":80}'
```

### 3. Get All Rooms

```bash
curl -X GET "$BASE_URL/rooms"
```

### 4. Create Sensor

```bash
curl -X POST "$BASE_URL/sensors" \
-H "Content-Type: application/json" \
-d '{"id":"CO2-001","type":"CO2","status":"ACTIVE","currentValue":0,"roomId":"LIB-301"}'
```

### 5. Get Sensors by Type

```bash
curl -X GET "$BASE_URL/sensors?type=CO2"
```

### 6. Add Reading

```bash
curl -X POST "$BASE_URL/sensors/CO2-001/readings" \
-H "Content-Type: application/json" \
-d '{"timestamp":1710000000000,"value":650}'
```

### 7. Get Reading History

```bash
curl -X GET "$BASE_URL/sensors/CO2-001/readings"
```

### 8. Delete Room with Sensors (409 Error)

```bash
curl -X DELETE "$BASE_URL/rooms/LIB-301"
```

### 9. Create Sensor with Invalid roomId (422 Error)

```bash
curl -X POST "$BASE_URL/sensors" \
-H "Content-Type: application/json" \
-d '{"id":"TEMP-999","type":"Temperature","roomId":"GHOST-ROOM"}'
```

### 10. Post Reading to MAINTENANCE Sensor (403 Error)

```bash
# First create a sensor in MAINTENANCE
curl -X POST "$BASE_URL/sensors" \
-H "Content-Type: application/json" \
-d '{"id":"OCC-001","type":"Occupancy","status":"MAINTENANCE","roomId":"LIB-301"}'

# Then try to add a reading (will fail with 403)
curl -X POST "$BASE_URL/sensors/OCC-001/readings" \
-H "Content-Type: application/json" \
-d '{"value":99.9}'
```

---

## 🛠️ Technologies Used

- Java 11
- Maven
- Jersey (JAX-RS)
- Jackson JSON
- Apache Tomcat 10
- REST API Design

---

## 📝 Coursework Questions and Answers

### Part 1 - Setup & Discovery

#### Question 1

**Explain the default lifecycle of a JAX-RS Resource class. How does this impact in-memory data management?**

The JAX-RS resource classes are normally handled by a **per-request lifecycle**. Each incoming HTTP request is served by creating a new resource object. This is a better approach to thread safety since there is no sharing of instance fields between concurrent users. Each request is assigned to an isolated object instance and this minimizes the chances of accidental state leakage.

**Singleton lifecycle** permits only one common instance of a resource class to exist throughout the application. This may reduce the overhead of object creation but it introduces concurrency problems as two or more requests may be using the same object simultaneously.

In this project, shared in-memory stores such as **ConcurrentHashMap** and **ArrayList** are used to store application data. These shared collections can be accessed by more than one request at a time even in cases where per-request resource instances are used. The **static** nature of these collections ensures data persists across all requests. ConcurrentHashMap provides thread-safety, preventing race conditions when multiple requests read and write concurrently. While the per-request lifecycle enhances the safety of the resource objects themselves, synchronized structures like ConcurrentHashMap are essential for protecting shared data in a production environment.

---

#### Question 2

**Why is HATEOAS considered a hallmark of advanced RESTful design?**

**HATEOAS (Hypermedia as the Engine of Application State)** is a REST architectural principle where API responses include links to related resources or available next actions. Instead of relying entirely on external documentation, the client can discover valid navigation paths dynamically from the server response.

For example, a discovery endpoint may return links such as:
- `/api/v1/rooms`
- `/api/v1/sensors`

This benefits client developers because integrations become **easier to maintain**. If routes change or new endpoints are added, the client can follow server-provided links rather than hardcoding every URI. It also improves **usability, self-documentation, and API discoverability** compared with static documentation that may become outdated.

**Key benefits:**
- Reduces coupling between client and server
- Makes the API self-documenting
- Enables runtime discovery of available actions
- Simplifies API evolution without breaking existing clients

---

### Part 2 - Room Management

#### Question 3

**What are the implications of returning only IDs versus returning full room objects?**

**Returning only room IDs** reduces response payload size and bandwidth consumption. It is efficient when the client only needs identifiers and can request full details separately when required.

**Returning full room objects** increases payload size but reduces the number of additional requests needed, because the client immediately receives all metadata such as room name, capacity, and associated sensors.

**The trade-off** is between network efficiency and client convenience. Returning only IDs creates the **N+1 problem** – for 100 rooms, the client must make 101 total requests (1 for the list + 100 individual fetches). This significantly increases latency and server load.

In this coursework project, **returning full room objects** is practical because:
- The dataset is small
- Clients (e.g., dashboards) typically need all room data immediately
- It simplifies testing and management operations
- One request is far more efficient than hundreds

---

#### Question 4

**Is the DELETE operation idempotent in your implementation?**

Yes, DELETE is considered **idempotent** because repeating the same request should leave the server in the same final state.

**For example:**
1. The first `DELETE /rooms/LIB-301` removes the room successfully and returns `204 No Content`
2. A second `DELETE /rooms/LIB-301` returns `404 Not Found` because the room no longer exists

Although the response status changes between the first and subsequent calls, the **state of the server remains unchanged** after the first successful deletion. The room is gone after the first call and remains gone after every subsequent call.

Therefore, the operation still satisfies **idempotency** because repeated identical requests do not create additional side effects beyond the initial deletion. The key is the server state (room deleted), not the HTTP status code returned.

---

### Part 3 - Sensors & Filtering

#### Question 5

**What happens if a client sends data in a different format than `application/json`?**

When a JAX-RS method is annotated with:

```java
@Consumes(MediaType.APPLICATION_JSON)
```

the server expects a JSON request body. If a client sends a request with `Content-Type` of `text/plain` or `application/xml`, Jersey attempts to locate a compatible message body reader for that media type.

If no provider supports that media type, the framework returns **`415 Unsupported Media Type`** response before the method body ever executes. This is correct REST behavior because the request format does not match the contract defined by the endpoint annotation.

**Technical flow:**
1. Client sends POST with `Content-Type: text/plain`
2. JAX-RS runtime inspects the `Content-Type` header
3. Runtime compares it against the `@Consumes` annotation
4. Mismatch detected → method is never invoked
5. Returns `415 Unsupported Media Type`

This protects the resource method from receiving and attempting to deserialize malformed or unexpected input formats.

---

#### Question 6

**Why is the query parameter approach superior to path segments for filtering?**

Filtering with query parameters such as:

```
/api/v1/sensors?type=CO2
```

is preferable because the client is still requesting the **same collection resource** (sensors) but applying filter criteria on top of it.

A path-based approach such as:

```
/api/v1/sensors/type/CO2
```

implies a **nested resource hierarchy** rather than a search or filter operation, which is semantically misleading.

**Query parameters are also more flexible** because multiple filters can be combined easily:

```
/api/v1/sensors?type=CO2&status=ACTIVE
```

This makes them the standard REST approach for searching, sorting, and filtering collection resources without polluting the URI path structure.

**Summary:**
- Query params = filtering an existing collection (semantically correct)
- Path segments = navigating a resource hierarchy (wrong for filters)
- Query params are optional by default (omitting them returns full list)
- Easy to compose multiple filters without complex nested paths

---

### Part 4 - Sub-Resources

#### Question 7

**What are the architectural benefits of the Sub-Resource Locator pattern?**

The **Sub-Resource Locator pattern** delegates nested resource paths to dedicated classes. In this project, requests to the path:

```
/api/v1/sensors/{id}/readings
```

are handled by a separate `SensorReadingResource` class rather than placing all logic inside `SensorResource`.

**This provides several key architectural benefits:**

1. **Separation of concerns** – each class manages one logical responsibility
2. **Smaller, more readable classes** – easier to understand and review
3. **Easier maintenance and debugging** – faults are isolated to specific classes
4. **Improved unit testability** – sub-resources can be tested independently
5. **Simpler future expansion** – new nested resources can be added without modifying existing classes

In large APIs, this pattern prevents one massive controller class from containing every route and responsibility, which would become increasingly difficult to maintain as the API grows.

**Without this pattern:** A single `SensorResource` would contain dozens of methods for `/sensors/{id}/readings`, `/sensors/{id}/readings/{rid}`, `/sensors/{id}/config`, etc., becoming a "God Object" that violates single-responsibility and becomes unmaintainable.

---

### Part 5 - Error Handling & Logging

#### Question 8

**Why is HTTP 422 more semantically accurate than 404 for invalid payload references?**

A request that contains valid JSON but references a non-existent `roomId` is **syntactically correct**. The endpoint URI itself exists and the request was successfully routed to the correct resource method.

Therefore, returning **`422 Unprocessable Entity`** is more semantically accurate than `404 Not Found`.

**A 404 status** normally indicates that the **requested URI resource** does not exist. In this case, the URI (`POST /api/v1/sensors`) is valid and was found successfully.

However, the submitted payload contains a reference to a resource (`roomId: "GHOST-ROOM"`) that cannot be resolved – making it a **semantic or business logic error** rather than a routing failure.

**HTTP 422 communicates precisely that:**
- The request was well-formed (valid JSON syntax)
- The endpoint exists (URL found successfully)
- But it could not be processed due to **invalid content** (missing dependency)

**Analogy:** Submitting a job application (valid form, correct address = 200) but listing a degree from a non-existent university. The application arrived (not 404), but can't be processed due to invalid reference data (422).

---

#### Question 9

**What security risks are associated with exposing Java stack traces?**

Returning raw Java stack traces to external API consumers presents significant security risks. An attacker can extract the following types of sensitive information from an exposed trace:

1. **Internal package and class names**, revealing the application's architecture
2. **Framework and library versions**, which can be cross-referenced with known CVEs
3. **Server file paths and directory structures**
4. **Method names and logic flow**, enabling targeted exploitation of business logic
5. **Evidence of third-party dependencies** and possible unpatched vulnerabilities

**This information can support reconnaissance and targeted attacks** against the application.

**The correct approach** is to return a generic `500 Internal Server Error` response to external consumers while logging the full diagnostic details internally for developer review only.

**In this project:** The `GenericExceptionMapper` catches all unhandled exceptions, logs the full stack trace **server-side** for debugging, but returns only a safe, generic error message to the client. This prevents information leakage while preserving observability for the development team.

---

#### Question 10

**Why use filters instead of manual Logger.info() calls?**

JAX-RS filters such as `ContainerRequestFilter` and `ContainerResponseFilter` are specifically designed to handle **cross-cutting concerns** like logging, authentication, metrics, and auditing – concerns that apply uniformly across all endpoints.

**Using filters is preferable to manually writing `Logger.info()` inside every endpoint because:**

1. **Logging logic is centralized** in one place, making it easy to update globally
2. **Eliminates repeated code** across all resource methods
3. **Guarantees consistent logging** for every request and response automatically
4. **Keeps business logic classes clean** and focused on their core responsibility
5. **Easier to maintain** as the number of endpoints grows over time

This approach follows the **principle of separation of concerns** and significantly improves the long-term scalability and maintainability of the application.

**Without filters:** A codebase with 50+ endpoints would require 100+ duplicated logging statements. If the log format needs changing, every file must be edited. Filters solve this by applying the logging concern declaratively and universally.

---

## 📜 License

This project is submitted as coursework for the **Client-Server Architectures** module at the University of Westminster (2025/26).

---

## 👤 Author

**Tharusha Perera**  
- UoW ID: W2120205  
- IIT ID: 20231639  
- GitHub: [https://github.com/YOUR_USERNAME/SmartCampus](https://github.com/YOUR_USERNAME/SmartCampus)

---

## 📞 Support

For questions or issues:
- Email: admin@smartcampus.ac.uk
- GitHub Issues: [Create an issue](https://github.com/YOUR_USERNAME/SmartCampus/issues)

