# 🚀 Liquibase Quick Reference Card

## TL;DR - The 30-Second Summary

**What is Liquibase?** Version control for your database schema (like Git for SQL)

**What was created?**
- Master changelog file
- Sample user table migration
- JPA Entity, Repository, Service, Controller
- Complete documentation
- REST API endpoints

**Quick Start:**
```bash
mvn clean install
mvn spring-boot:run
```

**Your database is ready!** Tables are auto-created on startup.

---

## 📁 Key Files You Need to Know

### **Liquibase Files** (Database Schema)
```
db/changelog/db.changelog-master.xml          ← Start here when adding tables
db/changelog/001-create-users-table.xml       ← Sample (already active)
db/changelog/002-create-products-table.xml    ← Template to copy
```

### **Java Files** (Code)
```
entity/User.java              ← Database table as Java object
repository/UserRepository.java ← How to access database
service/UserService.java      ← Business logic
controller/UserController.java ← REST API endpoints
```

### **Config** (Settings)
```
application.properties        ← Database connection & Liquibase config
pom.xml                       ← Dependencies
```

---

## 🔄 How It Works (3 Steps)

```
1. START APP → 2. LIQUIBASE READS MASTER FILE → 3. CREATES TABLES
```

That's it! Liquibase:
- Reads `db.changelog-master.xml`
- Includes all referenced changelog files
- Creates tables if they don't exist
- Tracks changes in `DATABASECHANGELOG` table

---

## 📝 REST API Endpoints

### Create User
```bash
POST /api/users
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe"
}
```

### Get All Users
```bash
GET /api/users
```

### Get User by ID
```bash
GET /api/users/{id}
```

### Get User by Username
```bash
GET /api/users/username/{username}
```

### Update User
```bash
PUT /api/users/{id}
{
  "firstName": "John",
  "lastName": "Smith"
}
```

### Deactivate User
```bash
PATCH /api/users/{id}/deactivate
```

### Delete User
```bash
DELETE /api/users/{id}
```

---

## 🗄️ Database Schema

**users Table:**
| Field | Type | Constraints |
|-------|------|-------------|
| id | BIGINT | PK, AUTO_INCREMENT |
| username | VARCHAR | UNIQUE, NOT NULL |
| email | VARCHAR | UNIQUE, NOT NULL |
| password | VARCHAR | NOT NULL |
| first_name | VARCHAR | |
| last_name | VARCHAR | |
| created_at | TIMESTAMP | AUTO DEFAULT |
| updated_at | TIMESTAMP | AUTO DEFAULT |
| is_active | BOOLEAN | DEFAULT TRUE |

---

## ⚙️ Configuration Options

### Default (H2 In-Memory)
```properties
# application.properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
```
**Best for:** Testing, no setup needed

### MySQL
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/liquibase_db
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=your_password
```

### PostgreSQL
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/liquibase_db
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.username=postgres
spring.datasource.password=your_password
```

---

## 🆕 Add New Table (5-Step Process)

### 1. Create Changelog File
Create: `db/changelog/003-create-orders-table.xml`

### 2. Define Table in XML
```xml
<?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog
        xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
        http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-4.1.xsd">

    <changeSet id="003-create-orders-table" author="developer">
        <description>Create orders table</description>
        
        <createTable tableName="orders">
            <column name="id" type="BIGINT" autoIncrement="true">
                <constraints primaryKey="true" nullable="false"/>
            </column>
            <column name="order_number" type="VARCHAR(50)">
                <constraints nullable="false" unique="true"/>
            </column>
            <column name="created_at" type="TIMESTAMP" defaultValueComputed="CURRENT_TIMESTAMP">
                <constraints nullable="false"/>
            </column>
        </createTable>
    </changeSet>

</databaseChangeLog>
```

### 3. Include in Master File
Edit: `db/changelog/db.changelog-master.xml`
```xml
<include file="db/changelog/003-create-orders-table.xml"/>
```

### 4. Create Entity
Create: `entity/Order.java` (copy from User.java and modify)

### 5. Restart App
```bash
mvn spring-boot:run
```

**Done!** Your new table is created!

---

## ⚠️ Golden Rules

### Rule #1: Never Modify Executed Changesets
❌ DON'T:
```xml
<!-- BAD: Don't modify after execution -->
<changeSet id="001-create-users-table">
    <createTable tableName="users">
        <column name="username" type="VARCHAR(100)"/>  <!-- Changed -->
    </createTable>
</changeSet>
```

✅ DO:
```xml
<!-- GOOD: Create new changeset instead -->
<changeSet id="004-alter-username-column">
    <modifyDataType tableName="users" columnName="username" newDataType="VARCHAR(255)"/>
</changeSet>
```

### Rule #2: Always Create New Files
✅ Follow this pattern:
- `001-create-users-table.xml`
- `002-create-products-table.xml`
- `003-create-orders-table.xml`

### Rule #3: Keep Changesets Focused
✅ One logical change per changeset
❌ Don't put multiple unrelated changes in one changeset

---

## 🐛 Quick Troubleshooting

| Problem | Solution |
|---------|----------|
| App won't start | Check database connection in application.properties |
| Tables not created | Check logs for Liquibase errors |
| "Changeset already executed" | Never modify old changesets, create new ones |
| Can't connect to database | Ensure DB is running and credentials are correct |
| H2 console not accessible | Add `spring.h2.console.enabled=true` |

---

## 📚 What to Read First

1. **README_INDEX.md** - Navigation guide (read this!)
2. **SETUP_SUMMARY.md** - 5-minute overview
3. **COMPLETE_SETUP_GUIDE.md** - Detailed setup
4. **ARCHITECTURE_DIAGRAM.md** - Visual diagrams
5. **LIQUIBASE_README.md** - Deep reference

---

## 🎯 Next Steps

1. ✅ Run the app: `mvn spring-boot:run`
2. ✅ Test the API: Create a user via `/api/users`
3. ✅ Add your first table following the 5-step process
4. ✅ Read LIQUIBASE_README.md for best practices
5. ✅ Deploy to production database when ready

---

## 💾 DATABASECHANGELOG Table

Liquibase automatically tracks all executed changes:

| ID | AUTHOR | FILENAME | DATEEXECUTED | ORDEREXECUTED |
|-------|--------|----------|--------------|---------------|
| 001-create-users-table | developer | 001-create... | 2024-12-08... | 1 |

This table ensures:
- ✅ Changes run only once
- ✅ Consistency across environments
- ✅ Ability to track all changes

---

## 🔗 Useful Commands

```bash
# Build project
mvn clean install

# Run project
mvn spring-boot:run

# Run tests
mvn test

# Build without running tests
mvn clean package -DskipTests

# View all dependencies
mvn dependency:tree
```

---

## 🌐 API Testing Examples

### Using curl:
```bash
# Create user
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username":"john","email":"john@example.com","password":"pass123"}'

# Get all users
curl http://localhost:8080/api/users

# Get user by ID
curl http://localhost:8080/api/users/1
```

### Using Postman:
1. Create new request
2. Set method to POST
3. URL: http://localhost:8080/api/users
4. Body (JSON): `{"username":"john","email":"john@example.com","password":"pass123"}`
5. Click Send

---

## 📊 Architecture at a Glance

```
HTTP Request
    ↓
Controller (@RestController)
    ↓
Service (@Service) - Business Logic
    ↓
Repository (Spring Data JPA) - Database Access
    ↓
Entity (JPA) - Database Mapping
    ↓
Database (Liquibase Manages Schema)
```

---

## ✨ Key Features

✅ Version control for database schema
✅ Automatic table creation on startup
✅ Cross-platform database support (H2, MySQL, PostgreSQL)
✅ JPA/Hibernate integration
✅ Spring Data repositories
✅ REST API ready
✅ Change tracking and history
✅ Rollback capability

---

## 🎓 Core Concepts

| Concept | Explanation |
|---------|-------------|
| **Changeset** | Single database change (create table, add column, etc.) |
| **Changelog** | Collection of changesets (one file) |
| **Master File** | Includes all changelogs (entry point) |
| **Entity** | Java class representing database table |
| **Repository** | Interface for database operations |
| **Service** | Business logic layer |
| **Controller** | REST API endpoints |

---

## 🚀 You're Ready!

Your Liquibase project is fully set up with:
- ✅ Master changelog file
- ✅ Sample table migrations
- ✅ Complete Java application stack
- ✅ REST API endpoints
- ✅ Comprehensive documentation
- ✅ Multiple database support

**Now go build something awesome! 🎉**

---

## 📞 Quick Help Links

- **Can't get started?** → Read SETUP_SUMMARY.md
- **Need architecture details?** → Read ARCHITECTURE_DIAGRAM.md
- **Want to add a table?** → Follow "Add New Table" section above
- **Need deep reference?** → Read LIQUIBASE_README.md
- **Confused about files?** → Check FILE_INVENTORY.md
- **Lost?** → Start with README_INDEX.md

**Happy coding! 🚀**
