import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * GuestRegistration.java
 * Module 1: Guest Registration
 * Class for adding, updating and deleting a guest.
 * 
 * @author Ankit Srivastava, Usman Khan
 */
public class GuestRegistration {
	private DatabaseManager dbManager;

	public GuestRegistration(DatabaseManager databaseManager) {
		this.dbManager = databaseManager;
	}
	
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
			connection = dbManager.getConnection();
			preparedStatement = connection.prepareStatement(
					"INSERT INTO guest " +
					"(guestName, guestAddress, guestAffiliation) " +
					"VALUES (?, ?, ?)");
			
			preparedStatement.setString(1, guestName);
			preparedStatement.setString(2, guestName);
			preparedStatement.setString(3, guestName);
			
			preparedStatement.executeUpdate();
		} finally {
			dbManager.closeQuietly(preparedStatement);
			dbManager.closeQuietly(connection);
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
			connection = dbManager.getConnection();
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
			dbManager.closeQuietly(preparedStatement);
			dbManager.closeQuietly(connection);
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
			connection = dbManager.getConnection();
			preparedStatement = connection.prepareStatement(
					"DELETE FROM guest WHERE guestID = ?");
			
			preparedStatement.setInt(1, guestID);
			numDeleted = preparedStatement.executeUpdate();
		} finally {
			dbManager.closeQuietly(preparedStatement);
			dbManager.closeQuietly(connection);
		}
		
		return numDeleted;
	}
}
