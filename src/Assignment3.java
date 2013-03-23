import java.sql.Date;
import java.sql.SQLException;


/**
 * Contains all required queries.
 * @author Ankit Srivastave, Usman Khan
 */
public class Assignment3 {
	public static void main(String[] args) throws SQLException {
		DatabaseManager dm = new PostgresDatabaseManager(
				"jdbc:postgresql://localhost/ece456", "usman", "bad_password");
//		BookingQuery bq = new BookingQuery(dm);
//		List<Map<String, Object>> results = bq.getAvailableRooms(
//				"2013-03-27",
//				"2013-03-28", 
//				null,
//				null,
//				null,
//				null);
//		
//		for (Map<String, Object> row : results) {
//			for(Map.Entry<String, Object> value : row.entrySet()) {
//				System.out.println(value.getValue());
//			}
//		}
		
		BookingRegistration br = new BookingRegistration(dm);
		int result = br.addBooking(1, "1", 1, Date.valueOf("2013-03-23"),
				Date.valueOf("2013-03-25"));
		System.out.println(result);
	}
}
