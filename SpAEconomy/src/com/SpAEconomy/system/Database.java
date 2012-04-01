package com.SpAEconomy.system;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {

	private String driver = "com.mysql.jdbc.Driver";
	private String url;
	private String username;
	private String password;
	ResultSetHandler<Boolean> returnBoolean = new ResultSetHandler<Boolean>() {

		@Override
		public Boolean handle(ResultSet rs) {
			try {
				rs.next();
			} catch (SQLException ex) {
				return Boolean.valueOf(false);
			}

			return Boolean.valueOf(true);
		}
	};

	public Database(String url, String username, String password) throws MissingDriver {
		this.url = ("jdbc:" + url);
		this.username = username;
		this.password = password;

		if (!DbUtils.loadDriver(this.driver)) {
			throw new MissingDriver("Please make sure the MySQL driver library jar exists.");
		}
	}

	public Connection getConnection() throws SQLException {
		return (this.username.isEmpty()) && (this.password.isEmpty()) ? DriverManager.getConnection(this.url) : DriverManager.getConnection(this.url, this.username, this.password);
	}

	public boolean tableExists(String table) {
		boolean exists = false;
		try {
			Connection conn = getConnection();
			QueryRunner run = new QueryRunner();
			try {
				exists = ((Boolean) run.query(conn, "SELECT id FROM " + table, this.returnBoolean)).booleanValue();
			} finally {
				DbUtils.close(conn);
			}
		} catch (SQLException e) {
			exists = false;
		}

		return exists;
	}
}