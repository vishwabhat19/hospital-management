# Hospital Management System

This is a Spring Boot-based Hospital Management System that handles **group creation** and **group deletion** operations. It processes messages using **Embedded Artemis ActiveMQ** and maintains an **H2 in-memory database** for data persistence.

---

## 🚀 Running the Project
To start the application, simply use:
```sh
mvn spring-boot:run
```

---
## 📜 API Documentation (Swagger)
Swagger UI is implemented and can be accessed at:
[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
This provides details on all endpoints and the expected request formats.

---

## 🗄️ Database Details (H2 In-Memory DB)
- **H2 Console:** [http://localhost:8080/h2-console/login.do](http://localhost:8080/h2-console/login.do)
- **JDBC URL:** `jdbc:h2:mem:hospital_management`
- **Username:** `sa`
- **Password:** _(blank)_

### **📝 Database Tables**
#### 1️⃣ **Groups Table**
Stores details of the groups and parent groups.
```sql
CREATE TABLE IF NOT EXISTS groups (
    id SERIAL PRIMARY KEY,
    group_id VARCHAR(255),
    parent_group_id VARCHAR(255),
    group_name VARCHAR(255),
    created_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```
#### 2️⃣ **Group Activity Log Table**
Stores a log of operations performed on groups.
```sql
CREATE TABLE IF NOT EXISTS group_activity_log (
    id SERIAL PRIMARY KEY,
    group_id VARCHAR(255) NOT NULL,
    parent_group_id VARCHAR(255),
    action_type VARCHAR(10),
    action_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```
#### 3️⃣ **Dead Letter Queue Messages Table**
Stores messages that are deemed invalid.
```sql
CREATE TABLE IF NOT EXISTS dlq_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    message_content TEXT,
    timestamp TIMESTAMP
);
```

---

## 📩 JMS Messaging (Embedded Artemis ActiveMQ)
The application utilizes three JMS queues:
- **Dead Letter Queues:** `DLQ.GROUP_EVENTS_QUEUE`, `group.events.queue`, `REPLY_QUEUE`
- **`REPLY_QUEUE`** is used to return response statuses.

### **Example Listener Implementation**
```java
@JmsListener(destination = GROUP_EVENTS_QUEUE, selector = "operation = 'CREATE'", containerFactory = "jmsListenerContainerFactory", concurrency = "3-5")
public void receiveCreateMessage(Map<String, Object> message) throws Exception {
     logger.info("Received CREATE message: {}", message);
        String replyToQueue = (String) message.get("JMSReplyTo");

        Map<String, Object> responseMessage = new HashMap<>();
        responseMessage.put("groupId", message.get("groupId"));

        ResponseEntity<?> responseEntity = groupService.createGroup(message);
        responseMessage.put("status", responseEntity.getStatusCodeValue());
        responseMessage.put("message", responseEntity.getBody());

        if (replyToQueue != null) {
            jmsTemplate.convertAndSend(replyToQueue, responseMessage);
        }
}
```

---

## 🎧 Listeners
The system has **4 JMS listeners**:
1. **CreateGroupListener** → Handles group creation.
2. **DeleteGroupListener** → Handles group deletion.
3. **InvalidOperationListener** → Catches incorrect operation types and sends them to Dead Letter Queue.
4. **DeadLetterQueueListener** → Picks up messages from `DLQ.GROUP_EVENTS_QUEUE` and inserts them into `dlq_messages`.

---

## ✅ Validations Implemented
- **Mandatory Fields:** `groupId`, `parentGroupId`, and `operation` are required.
- **Timestamp Validation:** Deletion timestamp cannot be the same as or before the creation timestamp.
- **Deletion Restriction:** A record (queried by `parent_group_id` + `group_id`) must exist before it can be deleted.
- **Appropriate HTTP Response Codes** are returned for validation errors.

---

## 🧪 Testing
Tests are written for:
- **Listeners** (JMS consumers)
- **Producers** (JMS message senders)
- **Controllers** (REST endpoints)

To run the tests:
```sh
mvn test
```

---

## 🚀 Future Enhancements
- Have better exception and error handling
- Add **external database support (PostgreSQL/MySQL)**.
- Improve **retry mechanisms** for failed JMS messages.
- Add more unit tests to test the actual functionality
- Centralise all the constants to AppConstants.java file
- Centralise all error and exception messages to a single file
---

