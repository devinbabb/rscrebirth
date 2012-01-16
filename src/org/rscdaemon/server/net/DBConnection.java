package org.rscdaemon.server.net;

import org.rscdaemon.server.util.Logger;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Used to interact with the database.
 */
public class DBConnection {
	/**
	 * Define MySQL connection info.
	 */
	final String MYSQL_HOST = "";
	final String MYSQL_DB = "";
	final String MYSQL_USER = "";
	final String MYSQL_PASS = "";
	/**
	 * The database connection in use
	 */
	private Connection con;
	/**
	 * A statement for running queries on
	 */
	private Statement statement;
	/**
	 * The last query being executed
	 */
	private String lastQuery;

	static {
		testForDriver();
	}

	/**
	 * Tests we have a mysql Driver
	 */
	private static void testForDriver() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException cnfe) {
			Logger.error(cnfe);
		}
	}

	/**
	 * Instantiates a new database connection
	 */
	public DBConnection() {
		if (!createConnection()) {
			Logger.error(new Exception("Unable to connect to MySQL"));
		}
	}

	public boolean createConnection() {
		try {
			con = DriverManager.getConnection("jdbc:mysql://" + MYSQL_HOST
					+ "/" + MYSQL_DB, MYSQL_USER, MYSQL_PASS);
			statement = con.createStatement();
			statement.setEscapeProcessing(true);
			return isConnected();
		} catch (SQLException e) {
			Logger.error(e.getMessage());
			return false;
		}
	}

	public boolean isConnected() {
		try {
			statement.executeQuery("SELECT CURRENT_DATE");
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	/**
	 * Runs a select query on the current database connection
	 * 
	 * @param s
	 *            The query to be ran
	 */
	public synchronized ResultSet getQuery(String q) throws SQLException {
		try {
			lastQuery = q;
			return statement.executeQuery(q);
		} catch (SQLException e) {
			if (!isConnected() && createConnection()) {
				return getQuery(q);
			}
			throw new SQLException(e.getMessage() + ": '" + lastQuery + "'",
					e.getSQLState(), e.getErrorCode());
		}
	}

	/**
	 * Runs a update/insert/replace query on the current database connection
	 * 
	 * @param s
	 *            The query to be ran
	 */
	public synchronized int updateQuery(String q) throws SQLException {
		try {
			lastQuery = q;
			return statement.executeUpdate(q);
		} catch (SQLException e) {
			if (!isConnected() && createConnection()) {
				return updateQuery(q);
			}
			throw new SQLException(e.getMessage() + ": '" + lastQuery + "'",
					e.getSQLState(), e.getErrorCode());
		}
	}

	/**
	 * Closes the database conection.
	 * 
	 * @throws SQLException
	 *             if there was an error when closing the connection
	 */
	public void close() throws SQLException {
		con.close();
		con = null;
	}
}
