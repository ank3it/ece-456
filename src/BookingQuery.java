import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BookingQuery.java
 * Module 2: Booking Query
 * Class for querying for available rooms.
 * 
 * @author Ankit Srivastava, Usman Khan
 */
public class BookingQuery {
	private DatabaseManager dbManager;

	public BookingQuery(DatabaseManager databaseManager) {
		this.dbManager = databaseManager;
	}
	
	public List<Map<String, Object>> getAvailableRooms(String startDate,
			String endDate,
			String hotelName,
			String city,
			Double price,
			String type)
			throws SQLException {
		Connection connection = null;
		Statement satement = null;
		ResultSet resultSet = null;
		List<Map<String, Object>> results = null;
		
		// Construct query
		StringBuilder query = new StringBuilder("SELECT hotel.hotelID, ");
		query.append("hotel.hotelName, hotel.city, room.roomNo, room.price, ");
		query.append("room.type ");
		query.append("FROM room ");
		query.append("INNER JOIN hotel ON (hotel.hotelID = room.hotelID) ");
		query.append("WHERE NOT EXISTS (SELECT * ");
		query.append("FROM booking ");
		query.append("WHERE booking.hotelID = hotel.hotelID ");
		query.append("AND booking.roomNo = room.roomNo ");
		
		if (startDate != null && endDate != null) {
			query.append("AND ((booking.startDate BETWEEN '");
			query.append(startDate);
			query.append("' AND '");
			query.append(endDate);
			query.append("' OR booking.endDate BETWEEN '");
			query.append(startDate);
			query.append("' AND '");
			query.append(endDate);
			query.append("') OR (");
			query.append("booking.startDate <= '");
			query.append(startDate);
			query.append("' AND endDate >= '");
			query.append(endDate);
			query.append("')))");
		} else {
			query.append(") ");
		}
		
		if (hotelName != null) {
			query.append("AND hotel.hotelName = '");
			query.append(hotelName);
			query.append("' ");
		}
		
		if (city != null) {
			query.append("AND hotel.city = '");
			query.append(city);
			query.append("' ");
		}
		
		if (price != null) {
			query.append("AND room.price = ");
			query.append(price);
			query.append(" ");
		}
		
		if (type != null) {
			query.append("AND room.type = '");
			query.append(type);
			query.append("' ");
		}
		
		System.out.println(query.toString());
		
		try {
			connection = dbManager.getConnection();
			satement = connection.createStatement();
			resultSet = satement.executeQuery(query.toString());
			results = convertResultSetToList(resultSet);
		} finally {
			dbManager.closeQuietly(resultSet);
			dbManager.closeQuietly(satement);
			dbManager.closeQuietly(connection);
		}
		
		return results;
	}
	
	/**
	 * Converts a ResultSet into a List so that the ResultSet can be closed
	 * safely.
	 * @param rs The ResultSet object.
	 * @return A List<Map<String, Object>> which contains the same data as the 
	 * ResultSet.
	 * @throws SQLException
	 */
	private List<Map<String, Object>> convertResultSetToList(ResultSet rs)
			throws SQLException {
		List<Map<String, Object>> results = new ArrayList<Map<String,Object>>();
		ResultSetMetaData metaData = rs.getMetaData();
		int numColumns = metaData.getColumnCount();
		
		while (rs.next()) {
			HashMap<String, Object> row = new HashMap<String, Object>();
			
			for (int i = 1; i <= numColumns; i++) {
				System.out.println(metaData.getColumnLabel(i));
				row.put(metaData.getColumnLabel(i), rs.getObject(i));
			}
			
			results.add(row);
		}
		
		return results;
	}
}