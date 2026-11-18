package design.patterns.dal;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GenericDaoImpl<T extends Persistable, U> implements IGenericDao<T, U> {
	
	private Connection connection;
	
	public GenericDaoImpl() {
		connection = MariaDbConnection.getConnection();
	}

	@Override
	public void add(T o) {
		try {
			int numFields = o.getClass().getDeclaredFields().length;
			String sql = "INSERT INTO "+ o.tableName() +" VALUES (" + "?, ".repeat(numFields - 1) + "?" + ")";
			PreparedStatement statement = connection.prepareStatement(sql);
		for (int i = 0; i < numFields; i++) {
			statement.setObject(i + 1, o.getClass().getDeclaredFields()[i].get(o));
		}
		System.out.println(statement.toString());
		statement.executeUpdate();
		} catch (SQLException | IllegalArgumentException | IllegalAccessException  e) {
			e.printStackTrace();
		}
	}

	@Override
	public T findById(Class<T> c, U id) {
		try {
		T t = (T) c.getDeclaredConstructor().newInstance();
		PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + t.tableName() + " WHERE " + t.idColumn() + " = ?");
		statement.setObject(1, id);
		ResultSet resultSet = statement.executeQuery();
		if (resultSet.next()) {
			T entity = (T) c.getDeclaredConstructor().newInstance();
			for (int i = 0; i < c.getDeclaredFields().length; i++) {
				c.getDeclaredFields()[i].set(entity, resultSet.getObject(i + 1));
			}
			return entity;
		} else {
			return null;
		}
		//flatten the catch blocks to reduce code duplication
		} catch (SQLException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<T> findByKeyword(Class<T> c, String param, String value) {
		try {
		T t = (T) c.getDeclaredConstructor().newInstance();
		PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + t.tableName() + " WHERE " + param + " = ?");
		statement.setObject(1, value);
		ResultSet resultSet = statement.executeQuery();
		List<T> list = new ArrayList<>();
		while (resultSet.next()) {
			T entity = (T) c.getDeclaredConstructor().newInstance();
			for (int i = 0; i < c.getDeclaredFields().length; i++) {
				c.getDeclaredFields()[i].set(entity, resultSet.getObject(i + 1));
			}
			list.add(entity);
		}
		return list;
		} catch (SQLException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
			return null;
		}
	}


	@Override
	public void update(T o) {
		try {
			StringBuilder sql = new StringBuilder("UPDATE " + o.tableName() + " SET ");
			for (int i = 0; i < o.getClass().getDeclaredFields().length; i++) {
				sql.append(o.getClass().getDeclaredFields()[i].getName()).append(" = ?");
				if (i < o.getClass().getDeclaredFields().length - 1) {
					sql.append(", ");
				}
			}
			sql.append(" WHERE " + o.idColumn() + " = ?");
			PreparedStatement statement = connection.prepareStatement(sql.toString());
			for (int i = 0; i < o.getClass().getDeclaredFields().length; i++) {
				statement.setObject(i + 1, o.getClass().getDeclaredFields()[i].get(o));
			}
			statement.setObject(o.getClass().getDeclaredFields().length + 1, o.getClass().getDeclaredFields()[0].get(o));
			statement.executeUpdate();
		} catch (SQLException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}		
	}

	@Override
	public void update(T o, U id) {
		try {
			StringBuilder sql = new StringBuilder("UPDATE " + o.tableName() + " SET ");
			for (int i = 0; i < o.getClass().getDeclaredFields().length; i++) {
				sql.append(o.getClass().getDeclaredFields()[i].getName()).append(" = ?");
				if (i < o.getClass().getDeclaredFields().length - 1) {
					sql.append(", ");
				}
			}
			sql.append(" WHERE " + o.idColumn() + " = ?");
			PreparedStatement statement = connection.prepareStatement(sql.toString());
			for (int i = 0; i < o.getClass().getDeclaredFields().length; i++) {
				statement.setObject(i + 1, o.getClass().getDeclaredFields()[i].get(o));
			}
			statement.setObject(o.getClass().getDeclaredFields().length + 1, id);
			statement.executeUpdate();

		} catch (SQLException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		} 
	}

	@Override
	public void merge(T o) {
		try {
			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + o.tableName() + " WHERE " + o.idColumn() + " = ?");
			statement.setObject(1, o.getClass().getDeclaredFields()[0].get(o));
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				update(o);
			} else {
				add(o);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void merge(T o, U id) {
		try {
			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + o.tableName() + " WHERE " + o.idColumn() + " = ?");
			statement.setObject(1, id);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				update(o, id);
			} else {
				add(o);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void delete(T o) {
		try {
			PreparedStatement statement = connection.prepareStatement("DELETE FROM " + o.tableName() + " WHERE " + o.idColumn() + " = ?");
			statement.setObject(1, o.getClass().getDeclaredFields()[0].get(o));
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void delete(T o, U id) {
		try {
			PreparedStatement statement = connection.prepareStatement("DELETE FROM " + o.tableName() + " WHERE " + o.idColumn() + " = ?");
			statement.setObject(1, id);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<T> getAll(Class<T> c) {
		try {
			T t = (T) c.getDeclaredConstructor().newInstance();
			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + t.tableName());
			ResultSet resultSet = statement.executeQuery();
			List<T> list = new java.util.ArrayList<>();
			while (resultSet.next()) {
				T entity = (T) c.getDeclaredConstructor().newInstance();
				for (int i = 0; i < c.getDeclaredFields().length; i++) {
					c.getDeclaredFields()[i].set(entity, resultSet.getObject(i + 1));
				}
				list.add(entity);
			}
			return list;
		} catch (SQLException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
			return null;
		}
	}
}