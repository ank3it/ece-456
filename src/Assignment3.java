import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

/**
 * Contains all required queries.
 * @author Ankit Srivastave, Usman Khan
 */
public class Assignment3 {
	DatabaseManager databaseManager;
	
	public Assignment3(DatabaseManager databaseManager) {
		this.databaseManager = databaseManager;
	}
	
	// ----- Module 1: Guest Registration -----
	/**
	 * Adds a new guest.
	 * @param guestName The full name of the guest.
	 * @param guestAddress The full address.
	 * @param guestAffiliation A guest's affiliation.
	 * @throws SQLException 
	 */
	public void addGuest(String guestName, String guestAddress, 
			String guestAffiliation) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		
		try {
			connection = databaseManager.getConnection();
			preparedStatement = connection.prepareStatement(
					"INSERT INTO guest " +
					"(guestName, guestAddress, guestAffiliation) " +
					"VALUES (?, ?, ?)");
			
			preparedStatement.setString(1, guestName);
			preparedStatement.setString(2, guestName);
			preparedStatement.setString(3, guestName);
			
			preparedStatement.executeUpdate();
		} finally {
			databaseManager.closeQuietly(preparedStatement);
			databaseManager.closeQuietly(connection);
		}
	}
	
	/**
	 * Updates the name, address, affiliation of a guest.
	 * @param guestID The ID of the guest to update.
	 * @param guestName The new name of the guest.
	 * @param guestAddress The new address of the guest.
	 * @param guestAffiliation The new affiliation of the guest.
	 * @return The number of guests updated.
	 * @throws SQLException
	 */
	public int updateGuest(int guestID, String guestName, String guestAddress,
			String guestAffiliation) throws SQLException {
		int numUpdated = 0;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		
		try {
			connection = databaseManager.getConnection();
			preparedStatement = connection.prepareStatement(
					"UPDATE guest SET " +
					"guestName = ?, " +
					"guestAddress = ?, " +
					"guestAffiliation = ?");
			
			preparedStatement.setString(1, guestName);
			preparedStatement.setString(2, guestAddress);
			preparedStatement.setString(3, guestAffiliation);
			
			numUpdated = preparedStatement.executeUpdate();
		} finally {
			databaseManager.closeQuietly(preparedStatement);
			databaseManager.closeQuietly(connection);
		}
		
		return numUpdated;
	}
	
	/**
	 * Deletes a guest.
	 * @param guestID The ID of the guest to be deleted.
	 * @return The number of guests deleted.
	 * @throws SQLException 
	 */
	public int deleteGuest(int guestID) throws SQLException {
		int numDeleted = 0;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		
		try {
			connection = databaseManager.getConnection();
			preparedStatement = connection.prepareStatement(
					"DELETE FROM guest WHERE guestID = ?");
			
			preparedStatement.setInt(1, guestID);
			numDeleted = preparedStatement.executeUpdate();
		} finally {
			databaseManager.closeQuietly(preparedStatement);
			databaseManager.closeQuietly(connection);
		}
		
		return numDeleted;
	}	
	
	// ----- Module 2: Booking Query -----
	/**
	 * Returns available rooms. Paramters set to null will not be considered
	 * when retrieving the list of available rooms.
	 * @param startDate The start date of the desired time interval.
	 * @param endDate The end date of the desired time interval.
	 * @param hotelName The name of the hotel to look for rooms in.
	 * @param city The city within which to limit the search.
	 * @param price The price of available rooms.
	 * @param type The type of the available rooms ("King", "Queen", "Double", 
	 * "Single")
	 * @return A List of Map objects, where the map key value pairs are the
	 * column name and column value.
	 * @throws SQLException
	 */
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
		List<Map<String, Object>> resultList = null;
		
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
		
		try {
			connection = databaseManager.getConnection();
			satement = connection.createStatement();
			resultSet = satement.executeQuery(query.toString());
			resultList = Util.convertResultSetToList(resultSet);
		} finally {
			databaseManager.closeQuietly(resultSet);
			databaseManager.closeQuietly(satement);
			databaseManager.closeQuietly(connection);
		}
		
		return resultList;
	}
	
	// ----- Module 3: Booking Registration -----
	/**
	 * Adds a new booking for a hotel room.
	 * @param hotelID The ID of the hotel.
	 * @param roomNo The room number of the room being booked.
	 * @param guestID The ID of the guest.
	 * @param startDate The start date of the booking.
	 * @param endDate The end date of the booking.
	 * @return The bookingID of the newly created booking.
	 * @throws SQLException
	 */
	public int addBooking(int hotelID, String roomNo, int guestID,
			Date startDate, Date endDate) throws SQLException {
		int newBookingID;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		
		try {
			connection = databaseManager.getConnection();
			preparedStatement = connection.prepareStatement(
					"INSERT INTO booking2 " +
					"(hotelID, roomNo, guestID, startDate, endDate) " +
					"VALUES (?, ?, ?, ?, ?)",
					PreparedStatement.RETURN_GENERATED_KEYS);
			
			preparedStatement.setInt(1, hotelID);
			preparedStatement.setString(2, roomNo);
			preparedStatement.setInt(3, guestID);
			preparedStatement.setDate(4, startDate);
			preparedStatement.setDate(5, endDate);
			
			preparedStatement.executeUpdate();
			
			// Get bookingID of newly inserted row
			rs = preparedStatement.getGeneratedKeys();
			rs.next();
			newBookingID = rs.getInt(1);
		} finally {
			databaseManager.closeQuietly(rs);
			databaseManager.closeQuietly(preparedStatement);
			databaseManager.closeQuietly(connection);
		}
		
		return newBookingID;
	}
	
	// ----- Module 3: Booking Registration -----
	/**
	 * Returns a list of arrivals for the given date.
	 * @param hotelID The ID of the hotel.
	 * @param date The date we are checking arrivals for.
	 * @return A List of Map object containing the arrivals.
	 * @throws SQLException
	 */
	public List<Map<String, Object>> getArrivals(int hotelID, Date date) 
			throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Map<String, Object>> resultList = null;
		
		try {
			connection = databaseManager.getConnection();
			preparedStatement = connection.prepareStatement(
					"SELECT guest.guestID, hotel.hotelID, hotel.hotelName, " +
					"booking.roomNo, guest.guestName " +
					"FROM booking " +
					"INNER JOIN hotel ON hotel.hotelID = booking.hotelID " +
					"INNER JOIN guest on guest.guestID = booking.guestID " +
					"WHERE booking.hotelID = ? AND booking.startDate = ?");
			preparedStatement.setInt(1, hotelID);
			preparedStatement.setDate(2, date);
			
			resultSet = preparedStatement.executeQuery();
			resultList = Util.convertResultSetToList(resultSet);
		} finally {
			databaseManager.closeQuietly(resultSet);
			databaseManager.closeQuietly(preparedStatement);
			databaseManager.closeQuietly(connection);
		}
		
		return resultList;
	}
	
	/**
	 * Returns a list of departures for the given date.
	 * @param hotelID The ID of the hotel.
	 * @param date The date we are checking departures for.
	 * @return A List of Map object containing the departures.
	 * @throws SQLException
	 */
	public List<Map<String, Object>> getDepartures(int hotelID, Date date)
			throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Map<String, Object>> resultList = null;
		
		try {
			connection = databaseManager.getConnection();
			preparedStatement = connection.prepareStatement(
					"SELECT guest.guestID, hotel.hotelID, hotel.hotelName, " +
					"booking.roomNo, guest.guestName " +
					"FROM booking " +
					"INNER JOIN hotel ON hotel.hotelID = booking.hotelID " +
					"INNER JOIN guest on guest.guestID = booking.guestID " +
					"WHERE booking.hotelID = ? AND booking.endDate = ?");
			preparedStatement.setInt(1, hotelID);
			preparedStatement.setDate(2, date);
			
			resultSet = preparedStatement.executeQuery();
			resultList = Util.convertResultSetToList(resultSet);
		} finally {
			databaseManager.closeQuietly(resultSet);
			databaseManager.closeQuietly(preparedStatement);
			databaseManager.closeQuietly(connection);
		}
		
		return resultList;
	}
	
	public List<Map<String, Object>> getBill(int bookingID) 
			throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Map<String, Object>> resultList = null;
		
		try {
			connection = databaseManager.getConnection();
			
			// Retrieve billing information
			preparedStatement = connection.prepareStatement(
					"SELECT booking.bookingID, hotel.hotelName, hotel.city, " +
					"room.roomNo, guest.guestName, guest.guestAddress, " +
					"room.type, room.price, " +
					"booking.startDate, booking.endDate, " +
					"booking.endDate - booking.startDate AS numberOfDaysStayed, " +
					"(booking.endDate - booking.startDate) * price AS total " +
					"FROM booking " +
					"INNER JOIN hotel ON hotel.hotelID = booking.hotelID " +
					"INNER JOIN room ON room.hotelID = booking.hotelID AND " +
					"room.roomNo = booking.roomNo " +
					"INNER JOIN guest ON guest.guestID = booking.guestID " +
					"WHERE booking.bookingID = ?");
			preparedStatement.setInt(1, bookingID);
			resultSet = preparedStatement.executeQuery();
			resultList = Util.convertResultSetToList(resultSet);
			
			// Log billing information
			preparedStatement = connection.prepareStatement(
					"INSERT INTO billinglog (" +
					"SELECT booking.bookingID, hotel.hotelName, hotel.city, " +
					"room.roomNo, guest.guestName, guest.guestAddress, " +
					"room.type, room.price, " +
					"booking.startDate, booking.endDate, " +
					"booking.endDate - booking.startDate AS numberOfDaysStayed, " +
					"(booking.endDate - booking.startDate) * price AS total " +
					"FROM booking " +
					"INNER JOIN hotel ON hotel.hotelID = booking.hotelID " +
					"INNER JOIN room ON room.hotelID = booking.hotelID AND " +
					"room.roomNo = booking.roomNo " +
					"INNER JOIN guest ON guest.guestID = booking.guestID " +
					"WHERE booking.bookingID = ? " +
					")");
			preparedStatement.setInt(1, bookingID);
			preparedStatement.executeUpdate();
		} finally {
			databaseManager.closeQuietly(resultSet);
			databaseManager.closeQuietly(preparedStatement);
			databaseManager.closeQuietly(connection);
		}
		
		return resultList;
	}
	
	public static void main(String[] args) {
		DatabaseManager dm = new PostgresDatabaseManager(
				"jdbc:postgresql://localhost/ece456", "usman", "bad_password");
		
		Assignment3 a3 = new Assignment3(dm);
		List<Map<String, Object>> r;
		
		try {
			r = a3.getBill(1);
			
			for (Map<String, Object> row : r) {
				System.out.println(row);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
