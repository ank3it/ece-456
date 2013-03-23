import java.sql.SQLException;
import java.util.List;
import java.util.Map;


public class Assignment3 {
	public static void main(String[] args) throws SQLException {
		DatabaseManager dm = new PostgresDatabaseManager(
				"jdbc:postgresql://localhost/ece456", "usman", "bad_password");
		BookingQuery bq = new BookingQuery(dm);
		List<Map<String, Object>> results = bq.getAvailableRooms(
				"2013-03-27",
				"2013-03-28", 
				null,
				null,
				null,
				null);
		
		for (Map<String, Object> row : results) {
			for(Map.Entry<String, Object> value : row.entrySet()) {
				System.out.println(value.getValue());
			}
		}
	}
}
