package db;

import java.sql.*;

public class Connect {

	public static Connection createConn() {
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/crawler", "root", "");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	public static PreparedStatement prepare(Connection conn, String sql) {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ps;
	}
	
	public static void close(Connection conn) {
		
		try {
			conn.close();
			conn = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public static void close(Statement stmt) {
		try {
			stmt.close();
			stmt = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public static void close(ResultSet rs) {
		try {
			rs.close();
			rs = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
