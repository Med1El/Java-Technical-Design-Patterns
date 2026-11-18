package design.patterns.dal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import design.patterns.config.ConfigReader;

public final class MariaDbConnection {

	private static Connection connection;

	private MariaDbConnection() {
	}

	public static synchronized Connection getConnection() {
		try {
			if (connection == null || connection.isClosed()) {

				connection = DriverManager.getConnection(
						"jdbc:mariadb://localhost/" + ConfigReader.getConfigProperty("db.name") + "?sslMode=trust",
						"root", "");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connection;
	}

	public static synchronized void close() throws SQLException {
		if (connection != null && !connection.isClosed()) {
			connection.close();
		}
	}
}