package com.SpAEconomy.system;

import com.SpAEconomy.SpAEconomy;
import com.SpAEconomy.cache.cacheQeueObject;
import com.SpAEconomy.cache.cachedAccount;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Accounts {

	private final SpAEconomy main;
	public static Connection c;
	static ResultSetHandler<Double> returnBalance = new ResultSetHandler<Double>() {

		@Override
		public Double handle(ResultSet rs) throws SQLException {
			if (rs.next()) {
				return Double.valueOf(rs.getDouble("balance"));
			}
			return null;
		}
	};
	static ResultSetHandler<Boolean> returnHidden = new ResultSetHandler<Boolean>() {

		@Override
		public Boolean handle(ResultSet rs) throws SQLException {
			if (rs.next()) {
				return rs.getBoolean("hidden");
			}
			return null;
		}
	};

	public Accounts(SpAEconomy plugin) {
		main = plugin;
	}

	public void pingConnection() {
		try {
			if (c == null) {
				SpAEconomy.debug("Creating new database connection...");
				c = main.Database.getConnection();
				SpAEconomy.debug("Done creating new database connection!");
			} else if (c.isClosed()) {
				SpAEconomy.debug("Creating new database connection...");
				c = main.Database.getConnection();
				SpAEconomy.debug("Done creating new database connection!");
			}
		} catch (SQLException ex) {
			SpAEconomy.warning("Database Error: " + ex);
		}
	}

	public void create(String name, double balance) {
		create(name, balance, true);
	}

	public void create(final String name, final double balance, boolean checkCache) {
		if (checkCache) {
			if (exists(name, false, false)) {
				SpAEconomy.warning("Tried to create a new bank account for " + name + " but one already exists!");
				return;
			}
		}

		main.cmg.addToCache(new cachedAccount(name, balance));

		cacheQeueObject toRun = new cacheQeueObject() {

			@Override
			public boolean run() {
				try {
					SpAEconomy.debug("Creating an account for " + name + "...");

					QueryRunner run = new QueryRunner();
					pingConnection();
					run.update(c, "INSERT INTO SpAEconomy (playername, balance) " + "VALUES (?, ?)", new Object[] { name, balance });

					SpAEconomy.debug("Done creating an account for " + name + "!");
					return true;
				} catch (SQLException ex) {
					SpAEconomy.warning("Database Error: " + ex);
					return false;
				}
			}
		};

		main.cq.add(toRun);
	}

	public void remove(final String name, boolean checkCache) {
		if (checkCache) {
			if (!exists(name, false, false)) {
				SpAEconomy.warning("Tried to remove " + name + "'s account but it doesn't exist yet!");
				return;
			}
		}

		main.cmg.unCache(name);

		try {
			SpAEconomy.debug("Removing " + name + "'s account...");

			QueryRunner run = new QueryRunner();
			pingConnection();
			run.update(c, "DELETE FROM SpAEconomy WHERE playername=?", new Object[] { name });

			SpAEconomy.debug("Done removing " + name + "'s account!");
		} catch (SQLException ex) {
			SpAEconomy.warning("Database Error: " + ex);
		}
	}

	public boolean exists(String name, boolean fake) {
		return exists(name, fake, true);
	}

	public boolean exists(String name, boolean fake, boolean checkCache) {
		if (fake) {
			return true;
		}

		if (checkCache) {
			if (main.cmg.isCached(name)) {
				return true;
			} else {
				if (main.cmg.constructCache(name)) {
					return true;
				} else {
					return false;
				}

			}
		} else {
			return main.cmg.isCached(name);
		}
	}

	public void giveMoney(String name, double amount) {
		double newAmount = getBalance(name) + amount;

		setBalance(name, newAmount);
	}

	public void takeMoney(String name, double amount) {
		double newAmount = getBalance(name) - amount;

		setBalance(name, newAmount);
	}

	public void setBalance(final String name, final double balance) {
		if (main.cmg.isCached(name)) {
			main.cmg.setBalance(name, balance);
		} else {
			main.cmg.constructCache(name);
			main.cmg.setBalance(name, balance);
		}

		cacheQeueObject toRun = new cacheQeueObject() {

			@Override
			public boolean run() {
				try {
					SpAEconomy.debug("Setting a player's balance...");

					QueryRunner run = new QueryRunner();
					pingConnection();
					run.update(c, "UPDATE SpAEconomy SET balance=? WHERE playername=?", new Object[] { balance, name });

					SpAEconomy.debug("Done setting a player's balance!");
					return true;
				} catch (SQLException ex) {
					SpAEconomy.warning("Database Error: " + ex);
					return false;
				}
			}
		};

		main.cq.add(toRun);
	}

	public double getBalance(String name) {
		if (main.cmg.isCached(name)) {
			return main.cmg.getBalance(name);
		} else {
			main.cmg.constructCache(name);
			return main.cmg.getBalance(name);
		}
	}

	public void setHidden(final String name, final boolean hidden) {
		if (main.cmg.isCached(name)) {
			main.cmg.setHidden(name, hidden);
		} else {
			main.cmg.constructCache(name);
			main.cmg.setHidden(name, hidden);
		}

		cacheQeueObject toRun = new cacheQeueObject() {

			@Override
			public boolean run() {
				try {
					SpAEconomy.debug("Hiding a player...");

					QueryRunner run = new QueryRunner();
					pingConnection();
					run.update(c, "UPDATE SpAEconomy SET hidden=? WHERE playername=?", new Object[] { hidden, name });

					SpAEconomy.debug("Done hiding a player!");
					return true;
				} catch (SQLException ex) {
					SpAEconomy.warning("Database Error: " + ex);
					return false;
				}
			}
		};

		main.cq.add(toRun);
	}

	public boolean getHidden(String name) {
		if (main.cmg.isCached(name)) {
			return main.cmg.getHidden(name);
		} else {
			main.cmg.constructCache(name);
			return main.cmg.getHidden(name);
		}
	}

	public Account[] getTopAccounts(int amount) {
		SpAEconomy.debug("Retrieving the " + amount + " topmost ranked players...");

		return null;
	}

	public class Account {

		public final String playerName;
		public final double balance;
		public final boolean hidden;

		protected Account(String playerName, double balance, boolean hidden) {
			this.playerName = playerName;
			this.balance = balance;
			this.hidden = hidden;
		}
	}
}