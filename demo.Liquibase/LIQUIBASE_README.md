# Liquibase Database Migration Guide

## Overview
This project uses Liquibase for database schema version control and migrations. Liquibase allows you to track, manage, and deploy database changes in a structured way.

## Project Structure

```
src/main/resources/
├── db/
│   └── changelog/
│       ├── db.changelog-master.xml    (Master file - includes all changelogs)
│       ├── 001-create-users-table.xml (Sample: Users table)
│       └── 002-create-products-table.xml (Sample template: Products table)
└── application.properties              (Configuration file)
```

## Files Explained

### 1. **db.changelog-master.xml** (Master File)
This is the main changelog file that serves as an entry point. It includes all individual changelog files in the correct order.

```xml
<include file="db/changelog/001-create-users-table.xml"/>
```

When adding new migrations:
- Create a new XML file with a descriptive name (e.g., `003-add-orders-table.xml`)
- Add an `<include>` statement in the master file

### 2. **001-create-users-table.xml** (Sample Implementation)
This changelog creates a `users` table with the following features:

- **Auto-increment ID**: Primary key
- **Unique Constraints**: On `username` and `email`
- **Timestamps**: `created_at` and `updated_at` with defaults
- **Indexes**: For faster queries on `email` and `username`
- **Active Status**: Boolean flag with default value `true`

### 3. **002-create-products-table.xml** (Sample Template)
A template file showing how to create another table. Currently not included in the master file. To use it:

1. Uncomment the include statement in `db.changelog-master.xml`:
   ```xml
   <include file="db/changelog/002-create-products-table.xml"/>
   ```
2. Restart the application

## Configuration (application.properties)

```properties
spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.xml
spring.liquibase.enabled=true
spring.liquibase.drop-first=false
```

- **change-log**: Path to the master changelog file
- **enabled**: Enable/disable Liquibase on startup
- **drop-first**: Set to `true` only for development if you want to drop all tables on startup

## Database Configuration

The project includes example configurations for different databases. Uncomment and configure based on your needs:

### H2 (In-Memory - Default for Testing)
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
```

### MySQL
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/liquibase_db
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

### PostgreSQL
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/liquibase_db
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.postgresql.Driver
```

## How to Add New Migrations

### Step 1: Create a New Changelog File
Create a new XML file in `src/main/resources/db/changelog/` with the naming convention `XXX-description.xml`

Example: `003-create-orders-table.xml`

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
            <column name="user_id" type="BIGINT">
                <constraints nullable="false"/>
            </column>
            <column name="total_amount" type="DECIMAL(10, 2)">
                <constraints nullable="false"/>
            </column>
            <column name="created_at" type="TIMESTAMP" defaultValueComputed="CURRENT_TIMESTAMP">
                <constraints nullable="false"/>
            </column>
        </createTable>

        <!-- Add foreign key constraint -->
        <addForeignKeyConstraint
                constraintName="fk_orders_user_id"
                baseTableName="orders"
                baseColumnNames="user_id"
                referencedTableName="users"
                referencedColumnNames="id"
                onDelete="CASCADE"/>
    </changeSet>

</databaseChangeLog>
```

### Step 2: Include in Master File
Add the new file to `db.changelog-master.xml`:

```xml
<include file="db/changelog/003-create-orders-table.xml"/>
```

### Step 3: Run the Application
Start your Spring Boot application. Liquibase will automatically:
1. Read the master changelog
2. Detect new changesets
3. Execute them in order
4. Track execution in the `DATABASECHANGELOG` table

## Key Liquibase Elements

### createTable
```xml
<createTable tableName="table_name">
    <column name="id" type="BIGINT" autoIncrement="true">
        <constraints primaryKey="true" nullable="false"/>
    </column>
</createTable>
```

### createIndex
```xml
<createIndex indexName="idx_name" tableName="table_name">
    <column name="column_name"/>
</createIndex>
```

### addForeignKeyConstraint
```xml
<addForeignKeyConstraint
        constraintName="fk_name"
        baseTableName="orders"
        baseColumnNames="user_id"
        referencedTableName="users"
        referencedColumnNames="id"/>
```

### addColumn
```xml
<addColumn tableName="users">
    <column name="phone" type="VARCHAR(20)"/>
</addColumn>
```

### dropTable
```xml
<dropTable tableName="old_table"/>
```

## Best Practices

1. **Naming Convention**: Use numeric prefixes (001, 002, 003) for sequential ordering
2. **Descriptive Names**: Use clear names like `001-create-users-table.xml`
3. **One Purpose Per ChangeSet**: Each `<changeSet>` should do one logical change
4. **Never Modify Old ChangeSets**: Always create new files for additional changes
5. **Use Rollback Tags**: Include `<rollback>` statements for reversibility (optional but recommended)
6. **Track Author**: Always specify the `author` attribute in `<changeSet>`
7. **Add Descriptions**: Use `<description>` for clarity on what the changeset does

## Database Tracking

Liquibase automatically creates a `DATABASECHANGELOG` table that tracks:
- `ID`: ChangeSet ID
- `AUTHOR`: Author name
- `FILENAME`: Changelog file name
- `DATEEXECUTED`: When it was executed
- `ORDEREXECUTED`: Execution order
- `EXECTYPE`: Type of execution (EXECUTED, FAILED, etc.)
- `MD5SUM`: Hash of the changeset (for integrity)
- `DESCRIPTION`: Description of the change

## Troubleshooting

### Issue: ChangeSet already executed
**Solution**: Never modify a changeset that has already been executed. Create a new changeset instead.

### Issue: Database state mismatch
**Solution**: Check the `DATABASECHANGELOG` table to see what has been executed. If needed, manually fix the database or rollback changes.

### Issue: Liquibase not detecting changes
**Solution**: Ensure the file is properly included in the master changelog and the file path is correct.

## Additional Resources

- [Liquibase Official Documentation](https://docs.liquibase.com)
- [Liquibase XML Format](https://docs.liquibase.com/concepts/changelogs/xml-format.html)
- [Spring Boot Liquibase Integration](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-initialization.migration-tool.liquibase)
