package com.SpAEconomy.cache;

import com.SpAEconomy.SpAEconomy;
import com.SpAEconomy.system.Accounts;
import com.SpAEconomy.system.QueryRunner;
import com.SpAEconomy.system.ResultSetHandler;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class cacheManager {

	private ArrayList<cachedAccount> cache;
	private final SpAEconomy main;
	static ResultSetHandler<Double> returnBalance = new ResultSetHandler<Double>() {

		public Double handle(ResultSet rs) throws SQLException {
			if (rs.next()) {
				return Double.valueOf(rs.getDouble("balance"));
			}
			return null;
		}
	};
	static ResultSetHandler<Boolean> returnHidden = new ResultSetHandler<Boolean>() {

		public Boolean handle(ResultSet rs) throws SQLException {
			if (rs.next()) {
				return rs.getBoolean("hidden");
			}
			return null;
		}
	};

	public cacheManager(SpAEconomy plugin) {
		main = plugin;
		cache = new ArrayList<cachedAccount>();
	}

	public void addToCache(cachedAccount account) {
		cache.add(account);
	}

	public void unCache(String name) {
		Object[] accounts = cache.toArray();

		for (Object accountObj : accounts) {
			cachedAccount account = (cachedAccount) accountObj;
			if (account.owner.equalsIgnoreCase(name)) {
				cache.remove(cache.indexOf(account));
				SpAEconomy.debug("Removed " + name + " from cache memory");
				account.destroy();
			}
		}
	}

	public boolean constructCache(String name) {
		name = name.toLowerCase();

		main.accounts.pingConnection();

		Double balance = null;
		Boolean hidden = null;

		try {
			QueryRunner run = new QueryRunner();
			balance = (Double) run.query(Accounts.c, "SELECT balance FROM SpAEconomy WHERE playername=?", returnBalance, new Object[] { name });
			balance = SpAEconomy.roundToDecimals(balance, 2);
		} catch (SQLException ex) {
			SpAEconomy.warning("Database Error: " + ex);
			return false;
		} catch (NullPointerException ex) {
			SpAEconomy.info("Can't construct cache for " + name);
			SpAEconomy.info("The player needs a bank account!");
			return false;
		}

		try {
			QueryRunner run = new QueryRunner();
			hidden = (Boolean) run.query(Accounts.c, "SELECT hidden FROM SpAEconomy WHERE playername=?", returnHidden, new Object[] { name });
		} catch (SQLException ex) {
			SpAEconomy.warning("Database Error: " + ex);
			return false;
		} catch (NullPointerException ex) {
			SpAEconomy.warning("Can't construct cache for " + name);
			SpAEconomy.warning("The player needs a bank account!");
			return false;
		}

		cachedAccount account = new cachedAccount(name, balance, hidden);
		addToCache(account);
		return true;
	}

	public boolean isCached(String name) {
		name = name.toLowerCase();

		Object[] objects = cache.toArray();

		for (Object object : objects) {
			if (((cachedAccount) object).owner.equals(name)) {
				return true;
			}
		}

		return false;
	}

	public Double getBalance(String name) {
		name = name.toLowerCase();

		Object[] objects = cache.toArray();

		for (Object object : objects) {
			if (((cachedAccount) object).owner.equals(name)) {
				return ((cachedAccount) object).balance;
			}
		}

		return null;
	}

	public void setBalance(String name, double balance) {
		name = name.toLowerCase();

		Object[] objects = cache.toArray();

		for (Object object : objects) {
			if (((cachedAccount) object).owner.equals(name)) {
				((cachedAccount) object).balance = balance;
				return;
			}
		}
	}

	public Boolean getHidden(String name) {
		name = name.toLowerCase();

		Object[] objects = cache.toArray();

		for (Object object : objects) {
			if (((cachedAccount) object).owner.equals(name)) {
				return ((cachedAccount) object).hidden;
			}
		}

		return null;
	}

	public void setHidden(String name, boolean hidden) {
		name = name.toLowerCase();

		Object[] objects = cache.toArray();

		for (Object object : objects) {
			if (((cachedAccount) object).owner.equals(name)) {
				((cachedAccount) object).hidden = hidden;
				return;
			}
		}
	}
}