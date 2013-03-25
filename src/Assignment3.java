import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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
			preparedStatement.setString(2, guestAddress);
			preparedStatement.setString(3, guestAffiliation);
			
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
					"guestAffiliation = ? " +
					"WHERE guestID = ?");
			
			preparedStatement.setString(1, guestName);
			preparedStatement.setString(2, guestAddress);
			preparedStatement.setString(3, guestAffiliation);
			preparedStatement.setInt(4, guestID);
			
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
		
		// List of conditions that make up the WHERE clause of query
		List<String> clauseList = new ArrayList<String>();
		
		// Construct query
		StringBuilder query = new StringBuilder("SELECT hotel.hotelID, ");
		query.append("hotel.hotelName, hotel.city, room.roomNo, room.price, ");
		query.append("room.type ");
		query.append("FROM room ");
		query.append("INNER JOIN hotel ON (hotel.hotelID = room.hotelID) ");
		
		if (!startDate.isEmpty() && !endDate.isEmpty()) {
			StringBuilder subquery = new StringBuilder();
			subquery.append("NOT EXISTS (SELECT * ");
			subquery.append("FROM booking ");
			subquery.append("WHERE booking.hotelID = hotel.hotelID ");
			subquery.append("AND booking.roomNo = room.roomNo ");
			subquery.append("AND ((booking.startDate BETWEEN '");
			subquery.append(startDate);
			subquery.append("' AND '");
			subquery.append(endDate);
			subquery.append("' OR booking.endDate BETWEEN '");
			subquery.append(startDate);
			subquery.append("' AND '");
			subquery.append(endDate);
			subquery.append("') OR (");
			subquery.append("booking.startDate <= '");
			subquery.append(startDate);
			subquery.append("' AND endDate >= '");
			subquery.append(endDate);
			subquery.append("')))");
			
			clauseList.add(subquery.toString());
		} 
		
		if (!hotelName.isEmpty())
			clauseList.add("hotel.hotelName = '" + hotelName + "'");
		
		if (!city.isEmpty())
			clauseList.add("hotel.city = '" + city + "'");
		
		if (price != null)
			clauseList.add("room.price = " + price);
		
		if (!type.isEmpty())
			clauseList.add("room.type = '" + type + "'");
		
		if (clauseList.size() > 0) {
			query.append("WHERE ");
			query.append(Util.concatWithAnd(clauseList));
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
					"INSERT INTO booking " +
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
		Scanner in = new Scanner(System.in);
		
		while (true) {
			System.out.println("Available options:");
			System.out.println("\t1. Add guest");
			System.out.println("\t2. Update guest");
			System.out.println("\t3. Delete guest");
			System.out.println("\t4. Query bookings");
			System.out.println("\t5. Add booking");
			System.out.println("\t6. List arrivals");
			System.out.println("\t7. List departures");
			System.out.println("\t8. Print bill");
			
			System.out.print("Choose an option: ");
			String input = in.nextLine();
			
			if (input.equalsIgnoreCase("q")) {
				break;
			} else if (input.equalsIgnoreCase("1")) {
				// Add guest
				System.out.print("Guest name: ");
				String guestName = in.nextLine();
				System.out.print("Guest address: ");
				String guestAddress = in.nextLine();
				System.out.print("Guest affiliation: ");
				String guestAffiliation = in.nextLine();
				
				try {
					a3.addGuest(guestName, guestAddress, guestAffiliation);
				} catch (SQLException e) {
					System.out.println("ERROR: Unable to complete operation");
				}
			} else if (input.equalsIgnoreCase("2")) {
				// Update guest
				System.out.print("Guest ID: ");
				int guestID = Integer.parseInt(in.nextLine());
				System.out.print("Guest name: ");
				String guestName = in.nextLine();
				System.out.print("Guest address: ");
				String guestAddress = in.nextLine();
				System.out.print("Guest affiliation: ");
				String guestAffiliation = in.nextLine();
				
				int result;
				try {
					result = a3.updateGuest(guestID, guestName, guestAddress, 
							guestAffiliation);
					
					if (result > 0)
						System.out.println("Update successful");
					else
						System.out.println("Update failed");
				} catch (SQLException e) {
					System.out.println("ERROR: Unable to complete operation");
				}
				
			} else if (input.equalsIgnoreCase("3")) {
				// Delete guest
				System.out.print("Guest ID: ");
				int guestID = Integer.parseInt(in.nextLine());
				
				int result;
				try {
					result = a3.deleteGuest(guestID);
					
					if (result > 0)
						System.out.println("Delete successful");
					else
						System.out.println("Delete failed");
				} catch (SQLException e) {
					System.out.println("ERROR: Unable to complete operation");
				}
			} else if (input.equalsIgnoreCase("4")) {
				// Query bookings
				System.out.print("Start date: ");
				String startDate = in.nextLine();
				System.out.print("End date: ");
				String endDate = in.nextLine();
				System.out.print("Hotel Name: ");
				String hotelName = in.nextLine();
				System.out.print("City: ");
				String city = in.nextLine();
				System.out.print("Price: ");
				String priceString = in.nextLine();
				Double price = priceString.isEmpty() ? 
						null : Double.parseDouble(priceString);
				System.out.print("Type: ");
				String type = in.nextLine();
				
				List<Map<String, Object>> resultList;
				try {
					resultList = a3.getAvailableRooms(
							startDate, endDate, hotelName, city, price, type);
					
					System.out.println("Booking query results: ");
					Util.printResultList(resultList);
				} catch (SQLException e) {
					System.out.println("ERROR: Unable to complete operation");
				}
			} else if (input.equalsIgnoreCase("5")) {
				// Add booking
				System.out.print("Hotel ID: ");
				int hotelID = Integer.parseInt(in.nextLine());
				System.out.print("Room no.: ");
				String roomNo = in.nextLine();
				System.out.print("Guest ID: ");
				int guestID = Integer.parseInt(in.nextLine());
				System.out.print("Start date: ");
				Date startDate = Date.valueOf(in.nextLine());
				System.out.print("End date: ");
				Date endDate = Date.valueOf(in.nextLine());
				
				int bookingID;
				try {
					bookingID = a3.addBooking(hotelID, roomNo, guestID,
							startDate, endDate);
					
					System.out.println(
							"Booking ID of new booking: " + bookingID);
				} catch (SQLException e) {
					System.out.println("ERROR: Unable to complete operation");
				}
			} else if (input.equalsIgnoreCase("6")) {
				// List arrivals
				System.out.print("Hotel ID: ");
				int hotelID = Integer.parseInt(in.nextLine());
				System.out.print("Date: ");
				Date date = Date.valueOf(in.nextLine());
				
				List<Map<String, Object>> resultList;
				try {
					resultList = a3.getArrivals(hotelID, date);
					
					System.out.println("Arrivals:");
					Util.printResultList(resultList);
				} catch (SQLException e) {
					System.out.println("ERROR: Unable to complete operation");
				}
			} else if (input.equalsIgnoreCase("7")) {
				// List departures
				System.out.print("Hotel ID: ");
				int hotelID = Integer.parseInt(in.nextLine());
				System.out.print("Date: ");
				Date date = Date.valueOf(in.nextLine());
				
				List<Map<String, Object>> resultList;
				try {
					resultList = a3.getDepartures(hotelID, date);
					
					System.out.println("Departures:");
					Util.printResultList(resultList);
				} catch (SQLException e) {
					System.out.println("ERROR: Unable to complete operation");
				}
			} else if (input.equalsIgnoreCase("8")) {
				// Print bill
				System.out.print("Booking ID: ");
				int bookingID = Integer.parseInt(in.nextLine());
				
				List<Map<String, Object>> resultList;
				try {
					resultList = a3.getBill(bookingID);
					
					System.out.println("Bill:");
					Util.printResultList(resultList);
				} catch (SQLException e) {
					System.out.println("ERROR: Unable to complete operation");
				}
			}
		}
		
		in.close();
	}
}
