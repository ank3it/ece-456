import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class Util {
	
	/**
	 * Concatenates a list of string with the word 'AND' between them.
	 * @param list A List of String objects.
	 * @return A string of all the words concatenated with 'AND'.
	 */
	public static String concatWithAnd(List<String> list) {
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < list.size(); i++) {
			sb.append(list.get(i));
			
			if (i != list.size() - 1) 
				sb.append(" AND ");
		}
		
		return sb.toString();
	}
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
			LinkedHashMap<String, Object> row = 
					new LinkedHashMap<String, Object>();
			
			for (int i = 1; i <= numColumns; i++) {
				row.put(metaData.getColumnLabel(i), rs.getObject(i));
			}
			
			resultList.add(row);
		}
		
		return resultList;
	}
	
	/**
	 * Prints the give List of Map objects to the console.
	 * @param resultList A List of Map objects containing String, Object pairs.
	 */
	public static void printResultList(List<Map<String, Object>> resultList) {
		int i = 1;
		for (Map<String, Object> row: resultList) {
			System.out.println(i++ + ") ");
			for (Entry<String, Object> column : row.entrySet()) {
				System.out.print("\t" + column.getKey() + ": ");
				System.out.println(column.getValue());
			}
		}
	}
}
