import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;


/**
 * BookingRegistration.java
 * Module 3: Booking Registration
 * Class for adding new bookings.
 * @author Ankit Srivastava, Usman Khan
 */
public class BookingRegistration {
	DatabaseManager databaseManager;
	
	public BookingRegistration(DatabaseManager databaseManager) {
		this.databaseManager = databaseManager;
	}
	
	/**
	 * Add a new booking for a hotel room.
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
}
