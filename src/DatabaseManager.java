/**
 * DatabaseManager.java
 * Interface to make using different databases easier.
 * @author Ankit Srivastava, Usman Khan1
 */

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class DatabaseManager {
	
	/**
	 * Retrieve a connection to the database.
	 * @return A Connection object representing the connection to the database.
	 */
	abstract Connection getConnection() throws SQLException;
	
	/**
	 * Attempts to quietly close the given Connection object.
	 * @param connection SQL Connection to be closed.
	 */
	public void closeQuietly(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Attempts to quietly close the given Statement object.
	 * @param statement SQL Statement to be closed.
	 */
	public void closeQuietly(Statement statement) {
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Attempts to quietly close the given ResultSet object.
	 * @param resultSet SQL ResultSet to be closed.
	 */
	public void closeQuietly(ResultSet resultSet) {
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}