import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresDatabaseManager extends DatabaseManager {
	private static String JDBC_driver_class = "org.postgresql.Driver";
	private String url;
	private String user;
	private String password;
	
	public PostgresDatabaseManager(String url, String user, String password) {
		this.url = url;
		this.user = user;
		this.password = password;
	}

	/**
	 * Connects to a Postgres database and returns the corresponding connection
	 * object.
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 */
	@Override
	public Connection getConnection() throws SQLException {
		try {
			Class.forName(JDBC_driver_class);
		} catch (ClassNotFoundException e) {
			System.out.println("JDBC driver not found");
			e.printStackTrace();
		}
		
		return DriverManager.getConnection(this.url, this.user, this.password);
	}
}
