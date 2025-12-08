# Liquibase Architecture & Flow Diagram

## 1. Application Startup Flow

```
┌─────────────────────────────────────┐
│  Spring Boot Application Starts     │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│  Liquibase Initialization Begins    │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│  Reads db.changelog-master.xml      │
│  (Main Entry Point)                 │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│  Parse Include Statements           │
│  - 001-create-users-table.xml       │
│  - 002-create-products-table.xml    │
│    (if uncommented)                 │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│  For Each Changeset:                │
│  1. Check DATABASECHANGELOG         │
│  2. If new: Execute SQL             │
│  3. If executed: Skip               │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│  Update DATABASECHANGELOG Table     │
│  Record execution details           │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│  All Tables Ready                   │
│  Application Uses Database          │
└─────────────────────────────────────┘
```

---

## 2. Project Layer Architecture

```
┌──────────────────────────────────────────────────────┐
│                    REST Controller                    │
│             UserController (/api/users)              │
│                                                      │
│  - POST /api/users (create)                          │
│  - GET /api/users (list)                             │
│  - GET /api/users/{id} (retrieve)                    │
│  - PUT /api/users/{id} (update)                      │
│  - DELETE /api/users/{id} (delete)                   │
└────────────────────┬─────────────────────────────────┘
                     │
                     ▼
┌──────────────────────────────────────────────────────┐
│                   Service Layer                      │
│                 UserService                          │
│                                                      │
│  - Business Logic                                    │
│  - Validation                                        │
│  - Transaction Management                           │
└────────────────────┬─────────────────────────────────┘
                     │
                     ▼
┌──────────────────────────────────────────────────────┐
│              Repository Layer (JPA)                  │
│              UserRepository                          │
│                                                      │
│  - CRUD Operations                                   │
│  - Custom Queries                                    │
│  - Database Access                                   │
└────────────────────┬─────────────────────────────────┘
                     │
                     ▼
┌──────────────────────────────────────────────────────┐
│                   Entity Layer                       │
│                    User.java                         │
│                                                      │
│  - Database Table Mapping                            │
│  - Field Definitions                                 │
│  - Constraints                                       │
└────────────────────┬─────────────────────────────────┘
                     │
                     ▼
┌──────────────────────────────────────────────────────┐
│           Liquibase + Database Layer                 │
│                                                      │
│  - Schema Management                                 │
│  - Table Creation (001-create-users-table.xml)       │
│  - Migrations                                        │
│  - Change Tracking (DATABASECHANGELOG)               │
└──────────────────────────────────────────────────────┘
```

---

## 3. Data Flow: Creating a User

```
User Request
    │
    ▼
curl -X POST /api/users {user data}
    │
    ▼
UserController.createUser()
    │
    ├─> Check username exists?
    ├─> Check email exists?
    │
    ▼
UserService.createUser()
    │
    ├─> Validate data
    ├─> Create User object
    │
    ▼
UserRepository.save(user)
    │
    ▼
Hibernate (JPA)
    │
    ├─> Generate SQL INSERT
    ├─> Execute on database
    │
    ▼
users Table
    │
    ├─> Insert row
    ├─> Auto-increment ID
    ├─> Set timestamps
    │
    ▼
Return created User
    │
    ▼
HTTP 201 Created Response
```

---

## 4. Liquibase File Structure

```
db/changelog/
│
├── db.changelog-master.xml
│   │
│   ├── <include> 001-create-users-table.xml
│   │
│   └── <include> 002-create-products-table.xml (commented)
│       │
│       └── Can be uncommented when ready
│
├── 001-create-users-table.xml
│   │
│   ├── ChangeSet ID: 001-create-users-table
│   ├── Author: developer
│   │
│   ├── <createTable>
│   │   ├── Column: id (BIGINT, PRIMARY KEY)
│   │   ├── Column: username (VARCHAR, UNIQUE)
│   │   ├── Column: email (VARCHAR, UNIQUE)
│   │   ├── Column: password (VARCHAR)
│   │   ├── Column: first_name (VARCHAR)
│   │   ├── Column: last_name (VARCHAR)
│   │   ├── Column: created_at (TIMESTAMP)
│   │   ├── Column: updated_at (TIMESTAMP)
│   │   └── Column: is_active (BOOLEAN)
│   │
│   └── <createIndex>
│       ├── idx_users_email
│       └── idx_users_username
│
└── 002-create-products-table.xml
    │
    ├── ChangeSet ID: 002-create-products-table
    ├── Author: developer
    │
    └── <createTable>
        ├── Column: id (BIGINT, PRIMARY KEY)
        ├── Column: name (VARCHAR)
        ├── Column: description (TEXT)
        ├── Column: price (DECIMAL)
        ├── Column: stock_quantity (INT)
        ├── Column: category (VARCHAR)
        ├── Column: created_at (TIMESTAMP)
        └── Column: updated_at (TIMESTAMP)
```

---

## 5. Database Schema Diagram

```
┌─────────────────────────────────────┐
│          USERS TABLE                │
├─────────────────────────────────────┤
│ PK  id (BIGINT)                     │
│ UQ  username (VARCHAR 255)          │
│ UQ  email (VARCHAR 255)             │
│     first_name (VARCHAR 100)        │
│     last_name (VARCHAR 100)         │
│     password (VARCHAR 255)          │
│     created_at (TIMESTAMP)          │
│     updated_at (TIMESTAMP)          │
│     is_active (BOOLEAN) DEFAULT=T   │
├─────────────────────────────────────┤
│ IX  idx_users_email                 │
│ IX  idx_users_username              │
└─────────────────────────────────────┘
            │
            │ FK (optional)
            │
            ▼
┌─────────────────────────────────────┐
│       PRODUCTS TABLE                │
├─────────────────────────────────────┤
│ PK  id (BIGINT)                     │
│     name (VARCHAR 255)              │
│     description (TEXT)              │
│     price (DECIMAL 10,2)            │
│     stock_quantity (INT)            │
│     category (VARCHAR 100)          │
│     created_at (TIMESTAMP)          │
│     updated_at (TIMESTAMP)          │
├─────────────────────────────────────┤
│ IX  idx_products_category           │
└─────────────────────────────────────┘
```

---

## 6. Configuration & Dependencies

```
application.properties
│
├── Liquibase Config
│   ├── spring.liquibase.change-log=db/changelog/master
│   ├── spring.liquibase.enabled=true
│   └── spring.liquibase.drop-first=false
│
├── Database Config
│   ├── spring.datasource.url
│   ├── spring.datasource.username
│   ├── spring.datasource.password
│   └── spring.datasource.driver-class-name
│
└── JPA Config
    ├── spring.jpa.hibernate.ddl-auto=validate
    ├── spring.jpa.show-sql=false
    └── spring.jpa.database-platform=...


pom.xml (Dependencies)
│
├── spring-boot-starter-liquibase
├── spring-boot-starter-data-jpa
├── spring-boot-starter-web
└── h2 (for testing)
```

---

## 7. Execution Sequence Diagram

```
TIME ──────────────────────────────────────────────────────────────>

START
  │
  ├─ Check DATABASECHANGELOG table
  │  └─ Table doesn't exist? Create it
  │
  ├─ Read db.changelog-master.xml
  │  └─ Parse includes
  │
  ├─ For Changeset: 001-create-users-table
  │  ├─ Check if already executed
  │  ├─ Not found? Execute SQL
  │  ├─ Create users table
  │  ├─ Create indexes
  │  └─ Record in DATABASECHANGELOG
  │
  ├─ For Changeset: 002-create-products-table
  │  ├─ Check if file included
  │  ├─ Not included? Skip
  │  └─ (Ready to execute when uncommented)
  │
  └─ Application ready to use database

DATABASECHANGELOG Table:
┌─────────────────────────────────────────────────────────┐
│ ID | AUTHOR | FILENAME | DATEEXECUTED | ORDEREXECUTED  │
├─────────────────────────────────────────────────────────┤
│ 001-create-users-table | developer | 001-create... │ 1 │
└─────────────────────────────────────────────────────────┘
```

---

## 8. Adding New Migration Flow

```
Requirement: Create new table
        │
        ▼
1. Create new changeset file
   └─ 003-create-orders-table.xml
        │
        ▼
2. Define table in XML
   ├─ Columns
   ├─ Constraints
   └─ Indexes
        │
        ▼
3. Add include in master file
   └─ <include file="003-...xml"/>
        │
        ▼
4. Restart application
        │
        ▼
5. Liquibase detects new changeset
        │
        ▼
6. Executes SQL
        │
        ▼
7. Records in DATABASECHANGELOG
        │
        ▼
8. Table ready to use
        │
        ▼
9. Create entity & repository
        │
        ▼
10. Use in service & controller
```

---

## 9. Request-Response Cycle

```
CLIENT (Postman / curl)
    │
    ▼
HTTP POST /api/users
    │
    ▼
UserController
    ├─ Receive request
    ├─ Validate input
    │
    ▼
UserService
    ├─ Check business rules
    ├─ Prepare data
    │
    ▼
UserRepository
    ├─ Call save()
    │
    ▼
JPA/Hibernate
    ├─ Map User entity to SQL
    ├─ Generate INSERT statement
    │
    ▼
Database Layer
    ├─ Execute INSERT
    ├─ Assign ID (auto-increment)
    ├─ Set timestamps
    │
    ▼
Database Commit
    │
    ▼
Return to JPA
    │
    ▼
Return to Service
    │
    ▼
Return to Controller
    │
    ▼
HTTP 201 Created + User JSON
    │
    ▼
CLIENT Response Received
```

---

## 10. File Dependency Chain

```
Application.java (Spring Boot Main)
    │
    ├─> Loads application.properties
    │   └─> Liquibase configuration
    │       └─> Loads db.changelog-master.xml
    │           ├─> 001-create-users-table.xml
    │           │   └─> Creates users table
    │           │       ├─> User.java (entity)
    │           │       ├─> UserRepository.java
    │           │       ├─> UserService.java
    │           │       └─> UserController.java
    │           │
    │           └─> 002-create-products-table.xml (template)
    │               └─> Ready when uncommented
    │
    ├─> Initializes dependencies
    └─> Application starts successfully
```

---

This architecture ensures:
✅ Database schema is version controlled
✅ Changes are tracked and repeatable
✅ Entities map directly to database tables
✅ Service layer provides business logic
✅ Controller exposes REST API
✅ Everything is integrated and testable
