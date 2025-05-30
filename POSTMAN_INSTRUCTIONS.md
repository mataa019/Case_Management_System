# Postman Testing Instructions for Case Management System

## Setup Instructions

### 1. Import the Postman Collection
1. Open Postman
2. Click on "Import" button
3. Select the file: `Case_Management_System_API.postman_collection.json`
4. The collection will be imported with all test cases

### 2. Configure Environment Variables
The collection uses the following variables that are automatically managed:

- `baseUrl`: http://localhost:8080/cms (pre-configured)
- `caseId`: Automatically set when creating cases
- `taskId`: Automatically set when creating tasks  
- `userId`: Default test user ID (testuser001)

### 3. Prerequisites
- Ensure your Case Management System is running on `http://localhost:8080/cms`
- PostgreSQL database is running and accessible
- Application has started successfully (check logs for startup confirmation)

## Testing Workflow

### Phase 1: Complete Case Lifecycle Testing

#### Step 1: Create a New Case
**Collection:** Case Management → 1. Create Case - Fraud

**What it does:**
- Creates a new fraud case
- Automatically captures the `caseId` for subsequent tests
- Validates response structure and status

**Expected Result:**
- Status: 200 OK
- Case created with DRAFT status
- `caseId` variable is set automatically

#### Step 2: Get Case Details
**Collection:** Case Management → 2. Get Case Details

**What it does:**
- Retrieves the case created in Step 1
- Uses the automatically captured `caseId`

**Expected Result:**
- Returns complete case information
- Shows DRAFT status and all case details

#### Step 3: Assign Case to User
**Collection:** Case Management → 3. Assign Case to User

**What it does:**
- Assigns the case to `investigator002`
- Changes case status from DRAFT to READY
- Logs the assignment action

**Expected Result:**
- Status: 200 OK
- Case status changes to READY
- `assigneeId` is set to `investigator002`

#### Step 4: Suspend Case
**Collection:** Case Management → 4. Suspend Case

**What it does:**
- Suspends the case with a reason
- Changes status from READY to SUSPENDED

**Expected Result:**
- Status: 200 OK
- Case status changes to SUSPENDED

#### Step 5: Resume Case
**Collection:** Case Management → 5. Resume Case

**What it does:**
- Resumes the suspended case
- Changes status from SUSPENDED back to READY

**Expected Result:**
- Status: 200 OK
- Case status changes back to READY

#### Step 6: List Active Cases
**Collection:** Case Management → 6. List Active Cases

**What it does:**
- Retrieves all active cases (READY or SUSPENDED status)

**Expected Result:**
- Returns array of active cases
- Should include your test case

### Phase 2: Task Management Testing

#### Step 7: Create Task
**Collection:** Task Management → 1. Create Task

**What it does:**
- Creates a task within a case
- Automatically captures `taskId`
- Assigns task to "Investigations" group

**Important Note:** 
- Modify the `caseId` in the request body to use ID `1` or the ID from your created case
- Or use the database ID of an existing case

**Expected Result:**
- Status: 200 OK
- Task created with UNASSIGNED status
- `taskId` variable is set automatically

#### Step 8: Get Task Details
**Collection:** Task Management → 2. Get Task Details

**What it does:**
- Retrieves details of the created task

#### Step 9: Assign Task to User
**Collection:** Task Management → 3. Assign Task to User

**What it does:**
- Assigns task to `investigator002`
- Changes task status to ASSIGNED

**Expected Result:**
- Task status changes to ASSIGNED
- `assigneeId` is set

#### Step 10: Update Task Status to IN_PROGRESS
**Collection:** Task Management → 4. Update Task Status to IN_PROGRESS

**What it does:**
- Changes task status to IN_PROGRESS
- Simulates user starting work on the task

#### Step 11: Complete Task
**Collection:** Task Management → 5. Complete Task

**What it does:**
- Marks task as COMPLETED
- Adds completion notes

### Phase 3: Queue Management Testing

#### Step 12: Get Investigations Queue
**Collection:** Queue Management → 1. Get Investigations Queue

**What it does:**
- Retrieves all tasks in the Investigations queue
- Shows both UNASSIGNED and ASSIGNED tasks

**Expected Result:**
- Returns array of tasks for Investigations group

### Phase 4: Additional Test Cases

#### Step 13: Create Different Case Types
**Collection:** Additional Test Cases → Create Dispute Case

**What it does:**
- Creates a DISPUTE type case
- Tests different case type functionality

#### Step 14: Filter Testing
Run these to test filtering capabilities:
- **Get Cases by Status**: Filters cases by READY status
- **Get Cases by Type**: Filters cases by FRAUD type
- **Get Tasks by Assignee**: Filters tasks by specific user

### Phase 5: Error Testing

#### Step 15: Test Invalid Operations
**Collection:** Error Testing

Run these tests to verify error handling:
- **Invalid Case Status Transition**: Tries to suspend a DRAFT case (should fail)
- **Case Not Found**: Tries to get non-existent case
- **Missing Required Header**: Tests validation

## Manual Testing Scenarios

### Scenario 1: Complete Investigation Workflow
1. Create fraud case → Assign to investigator → Create investigation task → Assign task → Start task → Complete task → Close case

### Scenario 2: Case Suspension Workflow  
1. Create case → Assign → Suspend (waiting for docs) → Resume → Complete

### Scenario 3: Task Reassignment
1. Create task → Assign to user1 → Reassign to user2 → Complete

## Validation Checklist

After running the tests, verify:

### Database Validation
```sql
-- Check cases were created
SELECT case_id, status, assignee_id FROM cases ORDER BY created_at DESC LIMIT 5;

-- Check tasks were created  
SELECT task_id, status, assignee_id, group_name FROM tasks ORDER BY created_at DESC LIMIT 5;

-- Check audit logs were created
SELECT user_id, action_type, entity_type, entity_id, description FROM audit_logs ORDER BY timestamp DESC LIMIT 10;
```

### API Response Validation
- All responses return proper JSON structure
- Status codes are appropriate (200 for success, 4xx/5xx for errors)
- Timestamps are in correct format
- Enum values are valid

### Business Logic Validation
- Status transitions follow the defined rules
- Audit logs are created for all operations
- Tasks are properly associated with cases
- Queue functionality works correctly

## Troubleshooting

### Common Issues

#### 1. Connection Refused
- **Problem**: Cannot connect to localhost:8080
- **Solution**: Ensure the Spring Boot application is running

#### 2. Database Errors
- **Problem**: Database connection issues
- **Solution**: Check PostgreSQL is running and credentials in application.yml are correct

#### 3. Case ID Not Found
- **Problem**: Tests fail because caseId variable is empty
- **Solution**: Run the "Create Case" test first to populate the variable

#### 4. Invalid Status Transition
- **Problem**: Status transition fails
- **Solution**: Check the current case status and ensure the transition is valid per the business rules

### Debug Steps
1. Check application logs for errors
2. Verify database connectivity
3. Confirm all required services are running
4. Test with simple GET requests first
5. Check Postman console for variable values

## Advanced Testing

### Load Testing
1. Create multiple cases simultaneously
2. Assign many tasks to the same user
3. Test queue performance with large datasets

### Boundary Testing
1. Test with maximum string lengths
2. Test with edge case dates
3. Test with invalid enum values

### Integration Testing
1. Verify Flowable process instances are created
2. Test that database triggers work correctly
3. Validate audit trail completeness

## Test Data Cleanup

After testing, clean up test data:

```sql
-- Remove test cases (cascades to tasks and audit logs)
DELETE FROM cases WHERE case_id LIKE 'CASE-%' AND creator_id LIKE '%test%';

-- Or reset completely
TRUNCATE TABLE audit_logs, tasks, cases RESTART IDENTITY CASCADE;
```

Then restart the application to recreate sample data.
