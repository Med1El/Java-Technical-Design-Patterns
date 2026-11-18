package design.patterns.dal;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Map;
import java.util.Objects;

import design.patterns.config.ConfigReader;
import design.patterns.entities.User;

public class CreateDBandTables {

	public static void createDB() throws Exception {
		Class.forName("org.mariadb.jdbc.Driver");
		Connection connection = MariaDbConnection.getConnection();
		Statement stmt = connection.createStatement();
		stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + ConfigReader.getConfigProperty("db.name"));
	}

	public static void createTableFromPersistable(Persistable persistable) throws Exception {
		Class<?> clazz = persistable.getClass();
		Persistable instance = (Persistable) clazz.getDeclaredConstructor().newInstance();
		Field[] fields = clazz.getDeclaredFields();
		StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS " + instance.tableName() + " (");
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			if( field.getName().equals( getKeyByValue( instance.columns() ,instance.idColumn() ) ) ) {
				sql.append(instance.idColumn()).append(" ").append(getColumnType(field.getType())).append(" AUTO_INCREMENT PRIMARY KEY").append(",");
				continue;
			}
			sql.append(instance.columns().get(field.getName())).append(" ").append(getColumnType(field.getType())).append(",");
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(")");
		Statement stmt = MariaDbConnection.getConnection().createStatement();
		stmt.executeUpdate(sql.toString());
	}

	public static void createTables() {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		// Package entitiesPackage = Package.getPackage("entities");
		//Package entitiesPackage = CreateDBandTables.class.getClassLoader().getDefinedPackage("entities");
		try {
			
			Class<?>[] classes = classLoader.loadClass("entities").getDeclaredClasses();
			for (Class<?> clazz : classes) {
				if (Persistable.class.isAssignableFrom(clazz)) {
					createTableFromPersistable((Persistable) clazz.getDeclaredConstructor().newInstance());
				}
			}
			
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void createDBAndTables() throws Exception {
		createDB();
		//createTables(); 
		//tried creating all entities within the entities package :/
		createTableFromPersistable(new User());
	}

	public static void main(String[] args) throws Exception {
	}

	// helper method to map Java types to SQL column types
	private static String getColumnType(Class<?> type) {
		if (type == String.class) {
			return "VARCHAR(255)";
		} else if (type == int.class || type == Integer.class) {
			return "INT";
		} else if (type == long.class || type == Long.class) {
			return "BIGINT";
		} else if (type == boolean.class || type == Boolean.class) {
			return "BOOLEAN";
		} else if (type == double.class || type == Double.class) {
			return "DOUBLE";
		} else if (type == float.class || type == Float.class) {
			return "FLOAT";
		} else if (type == java.util.Date.class) {
			return "DATETIME";
		} else {
			throw new IllegalArgumentException("Unsupported field type: " + type.getName());
		}
	}
	
	public static <K, V> K getKeyByValue(Map<K, V> map, V targetValue) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (Objects.equals(targetValue, entry.getValue())) {
                return entry.getKey(); // Returns the first match
            }
        }
        return null; // Return null if not found
    }

}
