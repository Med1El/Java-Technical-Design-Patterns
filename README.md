# Technical Design Patterns Implementation

An educational Java web-project showcase for experimenting with application design patterns. The project combines a generic JDBC DAO, entity metadata, dependency injection, configuration reading, database initialization, and a servlet entry point.

This repository is not a production framework. It intentionally uses reflection and small direct implementations to make pattern mechanics easy to see and modify.

## Implemented Patterns and Components

### Generic DAO

`IGenericDao<T, U>` defines basic persistence operations:

- `add`
- `findById`
- `findByKeyword`
- `update` and `merge`
- `delete`
- `getAll`

`GenericDaoImpl<T, U>` implements the contract with JDBC and MariaDB. SQL values are passed through `PreparedStatement` where supported. Entity metadata comes from `Persistable`, so the DAO and table creator can use a table name, identifier column, and Java-field-to-SQL-column mapping.

### Persistable Entities

An entity implements `Persistable` to describe its persistence metadata:

```java
public interface Persistable {
    String tableName();
    String idColumn();
    Map<String, String> columns();
}
```

The current `User` entity maps Java fields such as `firstName` and `phoneNumber` to SQL columns such as `first_name` and `phone_number`.

Entities are expected to provide a no-argument constructor because reflection is used for result and table construction.

### Dependency Injection

`DependencyInject` is a singleton enum that reads `dao.class` from `config.properties` and constructs the configured DAO implementation with reflection.

For example:

```properties
dao.class=design.patterns.dal.GenericDaoImpl
db.name=ecom
```

The current implementation demonstrates service-location-style dependency injection with a small configuration file. It is not a general-purpose DI container.

### Configuration Reader

`ConfigReader` loads `config.properties` from the application classpath.

It is used by:

- `MariaDbConnection` for the database name;
- `DependencyInject` for the DAO implementation class;
- `CreateDBandTables` for database initialization.

### Database Connection

`MariaDbConnection` provides a shared JDBC connection. Its default connection is:

```text
jdbc:mariadb://localhost/ecom?sslMode=trust
```

The username is `root` and the password is empty in the current implementation.

### Database and Table Setup

`CreateDBandTables` demonstrates database and table creation:

- `createDB()` creates the configured database when it does not exist;
- `createTableFromPersistable()` generates `CREATE TABLE IF NOT EXISTS` SQL from entity metadata and reflected fields;
- `createDBAndTables()` combines database creation with entity table setup.

The code currently creates the `User` table explicitly. Automatic discovery of every class in the `entities` package is not yet reliable and is therefore not used by `createDBAndTables()`.

## Front Controller Status

A servlet named `FrontController` is registered at `/FrontController`, but a full Front Controller pattern is not implemented yet.

It currently acts as a demonstration endpoint:

1. Creates or selects the configured MariaDB database.
2. Creates the `users` table metadata for `User`.
3. Resolves `GenericDaoImpl` through `DependencyInject`.
4. Inserts one fixed `User` example.
5. Writes `User added successfully` to the HTTP response.

It does not yet provide request routing, command objects, action classes, centralized validation, or a view-selection mechanism.

## Project Structure

```text
src/main/java/design/patterns/
├── config/      Properties and configuration access
├── dal/         Generic DAO contract, JDBC implementation, connection, table setup
├── entities/    Persistable domain entities
├── ioc/         Reflection-based dependency injection
└── servlet/     Demonstration servlet entry point

src/main/resources/
└── config.properties

src/main/webapp/
├── WEB-INF/web.xml
└── WEB-INF/lib/ MariaDB JDBC driver
```

## User Example

`User` demonstrates the metadata contract:

```java
public String tableName() {
    return "users";
}

public String idColumn() {
    return "user_id";
}

public Map<String, String> columns() {
    return Map.of(
        "userId", "user_id",
        "username", "username",
        "email", "email",
        "firstName", "first_name",
        "lastName", "last_name",
        "password", "password",
        "phoneNumber", "phone_number",
        "address", "address"
    );
}
```

This mapping keeps Java names separate from database column names.

## Running

This is an Eclipse web project targeting Java 25 and Tomcat 11.

1. Install and start MariaDB.
2. Create or confirm access for the `root` user.
3. Import the project into Eclipse.
4. Configure Tomcat 11 for the project.
5. Run the web application.
6. Open:

```text
http://localhost:8080/TechnicalDesignPatternsImpl/FrontController
```

The MariaDB JDBC driver is in `src/main/webapp/WEB-INF/lib`.

## Current Limitations

This project is under active design and is intended for learning, so it has several known limitations:

- The full front controller routing workflow is not implemented.
- `GenericDaoImpl` should consistently use `Persistable.columns()` instead of Java field names in SQL.
- Dynamic entity-package discovery needs a reliable implementation for Eclipse and packaged deployments.
- JDBC exceptions are often printed and not converted into application-level outcomes.
- The MariaDB connection has no connection pool.
- Raw generic types and reflection should be narrowed for more type-safe code.
- CRUD behavior, table setup, and the servlet need integration tests.

## Possible Next Steps

- Complete front-controller request routing and action dispatch.
- Add `Product` and `Order` implementations of `Persistable`.
- Use an entity registry or component scanner for automatic table creation.
- Make DAO column mapping consistent across insert, update, find, and delete operations.
- Introduce service classes between the servlet and DAO.
- Add DAO and servlet tests.
- Improve error handling and HTTP responses.
