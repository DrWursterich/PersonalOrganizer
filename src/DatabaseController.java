import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseController {
	private String jdbcDriver = "com.mysql.jdbc.Driver";
	private String dbName = "Appointments";
	
	public DatabaseController() {
		this.initiateDatabase();
	}
	
	public void initiateDatabase() {
		try {
			Class.forName(this.jdbcDriver);
			DriverManager.getConnection("jdbc:mySql://localhost/?user=root&password=")
					.createStatement().executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
}