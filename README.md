# Technical Design Patterns Implementation

Educational Java showcase of common application design patterns, including a generic DAO, dependency injection, and a front controller servlet.

## Generic DAO

`GenericDaoJpaImpl<T, U>` implements `IGenericDao<T, U>` with plain JDBC and MariaDB. It does not use JPA, Hibernate, or JDBC templates.

The implementation uses `MariaDbConnection` as a lazy, thread-safe singleton:

- the default connection is `jdbc:mariadb://localhost:3306/database`;
- the default user is `root` with an empty password;
- URL, user, and password can be overridden with the `mariadb.url`, `mariadb.user`, and `mariadb.password` system properties;
- all values are bound with prepared statements;
- entity table names come from the entity class name, and column names come from its non-static fields;
- identifier-based operations require an `id` field;
- `findByKeyword` searches the named column with `LIKE '%value%'` and returns the first match;
- entities need a no-argument constructor for result mapping.

Example entity:

```java
public class Product {
    private Long id;
    private String name;

    public Product() {
    }

    public Product(Long id, String name) {
        this.id = id;
        this.name = name;
    }

}
```

Example usage:

```java
IGenericDao<Product, Long> products = new GenericDaoJpaImpl<>();

products.add(new Product(1L, "Keyboard"));
Product product = products.findById(Product.class, 1L);
Product keyboard = products.findByKeyword(Product.class, "name", "board");
List<Product> allProducts = products.getAll(Product.class);
```

## Project Structure

- `dal`: generic DAO contract and implementation
- `entities`: sample domain entities
- `ioc`: simple enum-based dependency injection and configuration reader
- `servlet`: front controller example

Before running the example, create a matching table in the configured MariaDB database:

```sql
CREATE DATABASE database;
USE database;

CREATE TABLE Product (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);
```

The DAO maps Java field names directly to column names. Use system properties when the local MariaDB credentials differ:

```text
-Dmariadb.url=jdbc:mariadb://localhost:3306/database
-Dmariadb.user=root
-Dmariadb.password=secret
```

## Running

This is an Eclipse web project targeting Java 25 and Tomcat 11. Import the project into Eclipse, configure a Tomcat 11 server, and run the web application.

The servlet example contains a MariaDB database-creation demonstration. The MariaDB JDBC driver is included in `src/main/webapp/WEB-INF/lib`.

## Design Notes and Possible Improvements

The current interface reflects an older JPA-oriented design. For a production DAO, consider:

1. Rename `GenericDaoJpaImpl` to `GenericDaoJdbcImpl`, since its implementation is JDBC-based.
2. Replace raw `Class` parameters with `Class<T>` to remove unchecked type usage.
3. Use a typed identifier contract, such as an `Identifiable<U>` interface, instead of reflection for `id` lookup.
4. Return `Optional<T>` for single-object reads and define explicit behavior for missing updates and deletes.
5. Split `update` and `merge` semantics clearly and remove duplicate overloads if explicit IDs are not needed.
6. Add service-layer tests and DAO contract tests before introducing a real JDBC or other persistence implementation.
7. Prefer a connection pool for a real web application; this singleton is intentionally a simple educational pattern.
