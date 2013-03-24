import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Util {
	/**
	 * Converts a ResultSet into a List so that the ResultSet can be closed
	 * safely.
	 * @param rs The ResultSet object.
	 * @return A List<Map<String, Object>> which contains the same data as the 
	 * ResultSet.
	 * @throws SQLException
	 */
	public static List<Map<String, Object>> convertResultSetToList(ResultSet rs)
			throws SQLException {
		List<Map<String, Object>> resultList = 
				new ArrayList<Map<String,Object>>();
		ResultSetMetaData metaData = rs.getMetaData();
		int numColumns = metaData.getColumnCount();
		
		while (rs.next()) {
			HashMap<String, Object> row = new HashMap<String, Object>();
			
			for (int i = 1; i <= numColumns; i++) {
				System.out.println(metaData.getColumnLabel(i));
				row.put(metaData.getColumnLabel(i), rs.getObject(i));
			}
			
			resultList.add(row);
		}
		
		return resultList;
	}	
}
