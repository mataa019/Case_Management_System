# Case Management System

A minimal case management system built with Flowable, Spring Boot 3, PostgreSQL, and Hibernate.

## Features

### Case Management
- Create cases with valid types (Fraud, Dispute, Compliance, Risk)
- Full case lifecycle support with status transitions:
  - DRAFT → READY → SUSPENDED → READY → CLOSED
- Assign cases to users
- Suspend and resume cases
- Close cases with reason tracking

### Task Management
- Create tasks within cases
- Assign tasks to users (claim)
- Reassign tasks to other users
- Unassign tasks (return to group queue)
- Task status management:
  - UNASSIGNED, ASSIGNED, IN PROGRESS, BLOCKED, COMPLETED

### Workflow Queue
- Investigations group queue for task management
- API endpoints to retrieve queue tasks

### Audit Logging
- Complete audit trail for all case and task actions
- Tracks user ID, action type, timestamp, and value changes
- Searchable audit logs by entity, user, and date range

## Technology Stack

- **Spring Boot 3.5.0** - Application framework
- **Flowable 7.0.1** - Business Process Management
- **PostgreSQL** - Database
- **Hibernate/JPA** - ORM
- **Lombok** - Code generation
- **Maven** - Dependency management

## Database Schema

### Cases Table
- id (Primary Key)
- case_id (Unique identifier)
- case_type (FRAUD, DISPUTE, COMPLIANCE, RISK)
- status (DRAFT, READY, SUSPENDED, CLOSED)
- creator_id, assignee_id
- title, description, priority
- flowable_process_instance_id
- created_at, updated_at

### Tasks Table
- id (Primary Key)
- task_id (Unique identifier)
- task_name, description
- status (UNASSIGNED, ASSIGNED, IN_PROGRESS, BLOCKED, COMPLETED)
- assignee_id, group_name
- case_id (Foreign Key to cases)
- flowable_task_id
- due_date, created_at, updated_at

### Audit Logs Table
- id (Primary Key)
- user_id, action_type
- entity_type, entity_id
- description, old_value, new_value
- timestamp

## API Endpoints

### Case Management
- `POST /cms/api/cases` - Create a new case
- `GET /cms/api/cases/{caseId}` - Get case details
- `PUT /cms/api/cases/{caseId}/assign` - Assign case to user
- `PUT /cms/api/cases/{caseId}/suspend` - Suspend case
- `PUT /cms/api/cases/{caseId}/resume` - Resume case
- `PUT /cms/api/cases/{caseId}/close` - Close case
- `GET /cms/api/cases` - List cases with filters

### Task Management
- `POST /cms/api/tasks` - Create a new task
- `GET /cms/api/tasks/{taskId}` - Get task details
- `PUT /cms/api/tasks/{taskId}/assign` - Assign task to user
- `PUT /cms/api/tasks/{taskId}/unassign` - Unassign task
- `PUT /cms/api/tasks/{taskId}/status` - Update task status
- `GET /cms/api/tasks` - List tasks with filters

### Queue Management
- `GET /cms/api/queues/investigations` - Get Investigations queue tasks
- `GET /cms/api/queues/{groupName}` - Get tasks for specific group

## Setup and Installation

### Prerequisites
- Java 17 or higher
- PostgreSQL 12 or higher
- Maven 3.6 or higher

### Database Setup
1. Create a PostgreSQL database named `case_management`
2. Update database credentials in `application.yml`
3. The application will automatically create tables on startup

### Running the Application
```bash
# Clone the repository
git clone <repository-url>

# Navigate to project directory
cd CaseManagementSystem

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080/cms`

### Sample Requests

#### Create a Case
```json
POST /cms/api/cases
{
  "caseType": "FRAUD",
  "title": "Suspicious Transaction Investigation",
  "description": "Investigation of unusual payment patterns",
  "priority": "HIGH",
  "creatorId": "investigator1"
}
```

#### Assign a Case
```json
PUT /cms/api/cases/CASE-12345678/assign
Headers: X-User-Id: manager1
{
  "assigneeId": "investigator2"
}
```

#### Create a Task
```json
POST /cms/api/tasks
{
  "taskName": "Review Transaction Logs",
  "description": "Analyze transaction patterns for anomalies",
  "groupName": "Investigations",
  "caseId": 1,
  "dueDate": "2025-06-15T10:00:00"
}
```

## Configuration

Key configuration properties in `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/case_management
    username: postgres
    password: Admin
  
  flowable:
    database-schema-update: true
    async-executor-activate: true
    history-level: audit

server:
  port: 8080
  servlet:
    context-path: /cms
```

## Development

### Adding New Case Types
1. Update `CaseType` enum
2. Add validation logic if needed
3. Update documentation

### Adding New Task Statuses
1. Update `TaskStatus` enum
2. Update `TaskService.mapStatusToActionType()` method
3. Add corresponding `ActionType` if needed

### Extending Audit Logging
- Audit logging is automatically handled for all case and task operations
- Custom audit entries can be added using `LoggingService.logAction()`

## Testing

Run tests with:
```bash
mvn test
```

## License

This project is licensed under the MIT License.
