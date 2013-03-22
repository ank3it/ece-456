import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Test {

	/**
	 * @param args
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 */
	public static void main(String[] args)
			throws ClassNotFoundException,SQLException {
		
		Class.forName("org.postgresql.Driver");
		Connection connection = DriverManager.getConnection(
				"jdbc:postgresql://localhost/ece456", "usman", "bad_password");
		
		if (connection != null) {
			System.out.println("Successfully connected to database");
			
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM test_table");
			
			while (rs.next()) {
				System.out.print(rs.getString(1) + " ");
				System.out.println(rs.getString(2));
			}
		}
		
		connection.close();
	}

}
