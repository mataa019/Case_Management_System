# Case Management System - Testing Guide

## Prerequisites
- Application running on `http://localhost:8080/cms`
- PostgreSQL database running with case_management database created
- Postman or similar API testing tool

## Test Data Setup

The application includes sample data that's automatically inserted when the database is first created:

### Sample Cases
- **CASE-SAMPLE01**: Fraud case (Status: DRAFT)
- **CASE-SAMPLE02**: Dispute case (Status: READY)

## API Testing Scenarios

### 1. Case Management Testing

#### Scenario 1: Complete Case Lifecycle
Test the full case lifecycle from creation to closure.

#### Scenario 2: Case Assignment and Management
Test case assignment, suspension, and resumption.

### 2. Task Management Testing

#### Scenario 1: Task Creation and Assignment
Test task creation within cases and assignment to users.

#### Scenario 2: Task Status Management
Test task status transitions and queue management.

### 3. Queue Management Testing

#### Scenario 1: Investigations Queue
Test the investigations queue functionality.

## Detailed Test Cases

### Test Case 1: Create a New Case

**Endpoint:** `POST /cms/api/cases`

**Request Body:**
```json
{
  "caseType": "FRAUD",
  "title": "Suspicious Payment Investigation",
  "description": "Customer reported unauthorized transactions on their account",
  "priority": "HIGH",
  "creatorId": "investigator001"
}
```

**Expected Response:**
- Status: 200 OK
- Response should include generated caseId, status as DRAFT, and timestamps

### Test Case 2: Assign Case to User

**Endpoint:** `PUT /cms/api/cases/{caseId}/assign`
**Headers:** `X-User-Id: manager001`

**Request Body:**
```json
{
  "assigneeId": "investigator002"
}
```

**Expected Response:**
- Status: 200 OK
- Case status should change to READY
- assigneeId should be updated

### Test Case 3: Create Task for Case

**Endpoint:** `POST /cms/api/tasks`

**Request Body:**
```json
{
  "taskName": "Review Transaction History",
  "description": "Analyze the last 30 days of transaction history for anomalies",
  "groupName": "Investigations",
  "caseId": 1,
  "dueDate": "2025-06-15T17:00:00"
}
```

**Expected Response:**
- Status: 200 OK
- Task should be created with UNASSIGNED status
- Should include generated taskId

### Test Case 4: Assign Task to User

**Endpoint:** `PUT /cms/api/tasks/{taskId}/assign`
**Headers:** `X-User-Id: manager001`

**Request Body:**
```json
{
  "assigneeId": "investigator002"
}
```

**Expected Response:**
- Status: 200 OK
- Task status should change to ASSIGNED
- assigneeId should be updated

### Test Case 5: Update Task Status

**Endpoint:** `PUT /cms/api/tasks/{taskId}/status`
**Headers:** `X-User-Id: investigator002`

**Request Body:**
```json
{
  "status": "IN_PROGRESS",
  "reason": "Started reviewing transaction logs"
}
```

**Expected Response:**
- Status: 200 OK
- Task status should change to IN_PROGRESS

### Test Case 6: Suspend Case

**Endpoint:** `PUT /cms/api/cases/{caseId}/suspend`
**Headers:** `X-User-Id: manager001`

**Request Body:**
```json
{
  "status": "SUSPENDED",
  "reason": "Waiting for additional documentation from customer"
}
```

**Expected Response:**
- Status: 200 OK
- Case status should change to SUSPENDED

### Test Case 7: Resume Case

**Endpoint:** `PUT /cms/api/cases/{caseId}/resume`
**Headers:** `X-User-Id: manager001`

**Request Body:**
```json
{
  "status": "READY",
  "reason": "Customer provided required documentation"
}
```

**Expected Response:**
- Status: 200 OK
- Case status should change back to READY

### Test Case 8: Get Investigations Queue

**Endpoint:** `GET /cms/api/queues/investigations`

**Expected Response:**
- Status: 200 OK
- List of tasks assigned to Investigations group
- Should include both UNASSIGNED and ASSIGNED tasks

### Test Case 9: Close Case

**Endpoint:** `PUT /cms/api/cases/{caseId}/close`
**Headers:** `X-User-Id: investigator002`

**Request Body:**
```json
{
  "status": "CLOSED",
  "reason": "Investigation completed - no fraud detected"
}
```

**Expected Response:**
- Status: 200 OK
- Case status should change to CLOSED

## Error Testing Scenarios

### Invalid Status Transitions
Test invalid status transitions to ensure validation works:

1. Try to suspend a DRAFT case (should fail)
2. Try to change status of a CLOSED case (should fail)
3. Try to assign a task that doesn't exist (should fail)

### Missing Required Fields
Test API validation:

1. Create case without required fields
2. Assign task without assigneeId
3. Update status without required headers

## Performance Testing

### Load Testing Scenarios
1. Create 100 cases simultaneously
2. Assign 50 tasks to the same user
3. Retrieve investigations queue with large number of tasks

## Data Validation Testing

### Boundary Testing
1. Test with maximum length strings
2. Test with invalid enum values
3. Test with future/past dates

## Integration Testing

### Flowable Integration
1. Verify process instances are created for new cases
2. Test that Flowable tasks are synchronized with application tasks
3. Verify audit logging for all operations

## Cleanup Testing Data

After testing, you can clean up test data using direct database queries:

```sql
-- Delete test cases (this will cascade to related tasks and audit logs)
DELETE FROM cases WHERE case_id LIKE 'CASE-%' AND case_id NOT IN ('CASE-SAMPLE01', 'CASE-SAMPLE02');

-- Or reset to initial state
DELETE FROM audit_logs;
DELETE FROM tasks;
DELETE FROM cases;
-- Then restart the application to recreate sample data
```

## Monitoring and Debugging

### Check Application Logs
Monitor the application logs for:
- Audit log entries
- Flowable process execution
- Database operations
- Error messages

### Database Verification
Use these queries to verify data integrity:

```sql
-- Check case status distribution
SELECT status, COUNT(*) FROM cases GROUP BY status;

-- Check task assignment distribution
SELECT status, COUNT(*) FROM tasks GROUP BY status;

-- View recent audit log entries
SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT 10;

-- Check investigations queue
SELECT t.task_id, t.task_name, t.status, c.case_id, c.title 
FROM tasks t 
JOIN cases c ON t.case_id = c.id 
WHERE t.group_name = 'Investigations' 
AND t.status IN ('UNASSIGNED', 'ASSIGNED');
```
