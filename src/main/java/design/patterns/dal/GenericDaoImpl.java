package design.patterns.dal;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import design.patterns.utils.MapUtils;

public class GenericDaoImpl<T extends Persistable, U> implements IGenericDao<T, U> {

	private Connection connection;

	public GenericDaoImpl() {
		connection = MariaDbConnection.getConnection();
	}

	@Override
	public boolean add(T o) {
		try {
			int numFields = o.getClass().getDeclaredFields().length;
			StringBuilder sql = new StringBuilder("INSERT INTO " + o.tableName() + " (");
			for (int i = 0; i < numFields; i++) {
				sql.append(o.columns().get(o.getClass().getDeclaredFields()[i].getName()));
				if (i < numFields - 1) {
					sql.append(", ");
				}
			}
			sql.append(") VALUES (" + "?, ".repeat(numFields - 1) + "?)");
			PreparedStatement statement = connection.prepareStatement(sql.toString());
			for (int i = 0; i < numFields; i++) {
				statement.setObject(i + 1, o.getClass().getDeclaredFields()[i].get(o));
			}
			// System.out.println(statement.toString());
			statement.executeUpdate();
			return true;
		} catch (SQLException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public T findById(Class<T> c, U id) {
		try {
			T t = (T) c.getDeclaredConstructor().newInstance();
			PreparedStatement statement = connection
					.prepareStatement("SELECT * FROM " + t.tableName() + " WHERE " + t.idColumn() + " = ?");
			statement.setObject(1, id);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				T entity = (T) c.getDeclaredConstructor().newInstance();
				for (Field field : c.getDeclaredFields()) {
					String sqlColumn = t.columns().get(field.getName());
					if (sqlColumn != null) {
						field.set(entity, resultSet.getObject(sqlColumn));
					}
				}
				return entity;
			} else {
				return null;
			}
		} catch (SQLException | InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<T> findByKeyword(Class<T> c, String param, String value) {
		try {
			T t = (T) c.getDeclaredConstructor().newInstance();
			boolean paramFound = false;
			String columnName = "";
			for (String s : t.columns().keySet()) {
				if (s.equals(param)) {
					paramFound = true;
					columnName = t.columns().get(param);
					break;
				}
			}
			if (!paramFound) {
				throw new IllegalArgumentException("Parameter " + param + " not found in class " + c.getName());
			}
			PreparedStatement statement = connection
					.prepareStatement("SELECT * FROM " + t.tableName() + " WHERE " + columnName + " = ?");
			statement.setObject(1, value);
			ResultSet resultSet = statement.executeQuery();
			List<T> list = new ArrayList<>();
			while (resultSet.next()) {
				T entity = (T) c.getDeclaredConstructor().newInstance();
				for (Field field : c.getDeclaredFields()) {
					String sqlColumn = t.columns().get(field.getName());
					if (sqlColumn != null) {
						field.set(entity, resultSet.getObject(sqlColumn));
					}
				}
				list.add(entity);
			}
			return list;
		} catch (SQLException | InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean update(T o) {
		try {
			StringBuilder sql = new StringBuilder("UPDATE " + o.tableName() + " SET");
			for (int i = 0; i < o.getClass().getDeclaredFields().length; i++) {
				sql.append(" " + o.columns().get(o.getClass().getDeclaredFields()[i].getName())).append(" = ?,");
			}
			sql.deleteCharAt(sql.length() - 1);
			sql.append(" WHERE " + o.idColumn() + " = ?");
			PreparedStatement statement = connection.prepareStatement(sql.toString());
			// get id field name to get id field value
			for (int i = 0; i < o.getClass().getDeclaredFields().length; i++) {
				statement.setObject(i + 1, o.getClass().getDeclaredFields()[i].get(o));
			}
			Field idField = null;
			for (Field field : o.getClass().getDeclaredFields()) {
				if (field.getName().equals(MapUtils.getKeyByValue(o.columns(), o.idColumn()))) {
					idField = field;
					break;
				}
			}
			statement.setObject(o.getClass().getDeclaredFields().length + 1, idField.get(o));
			statement.executeUpdate();
			return true;
		} catch (SQLException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean updateById(T o, U id) {
		try {
			StringBuilder sql = new StringBuilder("UPDATE " + o.tableName() + " SET");
			for (int i = 0; i < o.getClass().getDeclaredFields().length; i++) {
				sql.append(" " + o.columns().get(o.getClass().getDeclaredFields()[i].getName())).append(" = ?,");
			}
			sql.deleteCharAt(sql.length() - 1);
			sql.append(" WHERE " + o.idColumn() + " = ?");
			PreparedStatement statement = connection.prepareStatement(sql.toString());
			for (int i = 0; i < o.getClass().getDeclaredFields().length; i++) {
				statement.setObject(i + 1, o.getClass().getDeclaredFields()[i].get(o));
			}
			statement.setObject(o.getClass().getDeclaredFields().length + 1, id);
			statement.executeUpdate();
			return true;
		} catch (SQLException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean patch(T o) {
		try {
			StringBuilder sql = new StringBuilder("UPDATE " + o.tableName() + " SET");
			int parameterIndex = 1;
			for (int i = 0; i < o.getClass().getDeclaredFields().length; i++) {
				if (o.getClass().getDeclaredFields()[i].get(o) == null) {
					continue;
				}
				sql.append(" " + o.columns().get(o.getClass().getDeclaredFields()[i].getName())).append(" = ?,");
				parameterIndex++;
			}
			if (parameterIndex == 1) {
				return false;
			}
			sql.deleteCharAt(sql.length() - 1);
			sql.append(" WHERE " + o.idColumn() + " = ?");
			PreparedStatement statement = connection.prepareStatement(sql.toString());
			parameterIndex = 1;
			for (int i = 0; i < o.getClass().getDeclaredFields().length; i++) {
				if (o.getClass().getDeclaredFields()[i].get(o) == null) {
					continue;
				}
				statement.setObject(parameterIndex++, o.getClass().getDeclaredFields()[i].get(o));
			}
			Field idField = null;
			for (Field field : o.getClass().getDeclaredFields()) {
				if (field.getName().equals(MapUtils.getKeyByValue(o.columns(), o.idColumn()))) {
					idField = field;
					break;
				}
			}
			statement.setObject(parameterIndex, idField.get(o));
			statement.executeUpdate();
			return true;
		} catch (SQLException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean patchById(T o, U id) {
		try {
			StringBuilder sql = new StringBuilder("UPDATE " + o.tableName() + " SET");
			int parameterIndex = 1;
			for (int i = 0; i < o.getClass().getDeclaredFields().length; i++) {
				if (o.getClass().getDeclaredFields()[i].get(o) == null) {
					continue;
				}
				sql.append(" " + o.columns().get(o.getClass().getDeclaredFields()[i].getName())).append(" = ?,");
				parameterIndex++;
			}
			if (parameterIndex == 1) {
				return false;
			}
			sql.deleteCharAt(sql.length() - 1);
			sql.append(" WHERE " + o.idColumn() + " = ?");
			PreparedStatement statement = connection.prepareStatement(sql.toString());
			parameterIndex = 1;
			for (int i = 0; i < o.getClass().getDeclaredFields().length; i++) {
				if (o.getClass().getDeclaredFields()[i].get(o) == null) {
					continue;
				}
				statement.setObject(parameterIndex++, o.getClass().getDeclaredFields()[i].get(o));
			}
			statement.setObject(parameterIndex, id);
			statement.executeUpdate();
			return true;
		} catch (SQLException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean delete(T o) {
		try {
			PreparedStatement statement = connection
					.prepareStatement("DELETE FROM " + o.tableName() + " WHERE " + o.idColumn() + " = ?");
			Field idField = null;
			for (Field field : o.getClass().getDeclaredFields()) {
				if (field.getName().equals(MapUtils.getKeyByValue(o.columns(), o.idColumn()))) {
					idField = field;
					break;
				}
			}
			idField.get(o);
			statement.setObject(1, idField.get(o));
			statement.executeUpdate();
			return true;
		} catch (SQLException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean deleteById(T o, U id) {
		try {
			PreparedStatement statement = connection
					.prepareStatement("DELETE FROM " + o.tableName() + " WHERE " + o.idColumn() + " = ?");
			statement.setObject(1, id);
			statement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
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
				for (Field field : c.getDeclaredFields()) {
					String sqlColumn = t.columns().get(field.getName());
					if (sqlColumn != null) {
						field.set(entity, resultSet.getObject(sqlColumn));
					}
				}
				list.add(entity);
			}
			return list;
		} catch (SQLException | InstantiationException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	private Object getIdValue(T o) {
		try {
			Field idField = null;
			for (Field field : o.getClass().getDeclaredFields()) {
				if (field.getName().equals(MapUtils.getKeyByValue(o.columns(), o.idColumn()))) {
					idField = field;
					break;
				}
			}
			return idField.get(o);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	// getColumnValue helper method to get the value of a column by its name
	private Object getColumnValue(T o, String columnName) {
		try {
			Field field = o.getClass().getDeclaredField(MapUtils.getKeyByValue(o.columns(), columnName));
			return field.get(o);
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}
}