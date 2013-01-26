
package me.heldplayer.SpAEconomy.system;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import me.heldplayer.SpAEconomy.SpAEconomy;
import me.heldplayer.SpAEconomy.cache.CacheQeueObject;
import me.heldplayer.SpAEconomy.cache.CachedAccount;

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
        this.main = plugin;
    }

    public void pingConnection() {
        try {
            if (c == null) {
                SpAEconomy.debug("Creating new database connection...");
                c = this.main.Database.getConnection();
                SpAEconomy.debug("Done creating new database connection!");
            }
            else if (c.isClosed()) {
                SpAEconomy.debug("Creating new database connection...");
                c = this.main.Database.getConnection();
                SpAEconomy.debug("Done creating new database connection!");
            }
        }
        catch (SQLException ex) {
            SpAEconomy.warning("Database Error: " + ex);
        }
    }

    public void create(String name, String account, double balance) {
        this.create(name, account, balance, true);
    }

    public void create(final String name, final String account, final double balance, boolean checkCache) {
        if (checkCache) {
            if (this.exists(name, account, false, false)) {
                SpAEconomy.warning("Tried to create a new bank account for " + name + " but one already exists!");
                return;
            }
        }

        this.main.cacheManager.addToCache(new CachedAccount(name, account, balance));

        CacheQeueObject toRun = new CacheQeueObject() {

            @Override
            public boolean run() {
                try {
                    SpAEconomy.debug("Creating an account for " + name + "...");

                    QueryRunner run = new QueryRunner();
                    Accounts.this.pingConnection();
                    run.update(c, "INSERT INTO SpAEconomy (playername, balance, account) " + "VALUES (?, ?, ?)", new Object[] { name, balance, account });

                    SpAEconomy.debug("Done creating an account for " + name + "!");
                    return true;
                }
                catch (SQLException ex) {
                    SpAEconomy.warning("Database Error: " + ex);
                    return false;
                }
            }
        };

        this.main.cacheQue.add(toRun);
    }

    public void remove(final String name, String account, boolean checkCache) {
        if (checkCache) {
            if (!this.exists(name, account, false, false)) {
                SpAEconomy.warning("Tried to remove " + name + "'s account but it doesn't exist yet!");
                return;
            }
        }

        this.main.cacheManager.unCache(name, account);

        try {
            SpAEconomy.debug("Removing " + name + "'s account...");

            QueryRunner run = new QueryRunner();
            this.pingConnection();
            run.update(c, "DELETE FROM SpAEconomy WHERE playername=? AND account=?", new Object[] { name, account });

            SpAEconomy.debug("Done removing " + name + "'s account!");
        }
        catch (SQLException ex) {
            SpAEconomy.warning("Database Error: " + ex);
        }
    }

    public boolean exists(String name, String account, boolean fake) {
        return this.exists(name, account, fake, true);
    }

    public boolean exists(String name, String account, boolean fake, boolean checkCache) {
        if (fake) {
            return true;
        }

        if (checkCache) {
            if (this.main.cacheManager.isCached(name, account)) {
                return true;
            }
            else {
                if (this.main.cacheManager.constructCache(name, account)) {
                    return true;
                }
                else {
                    return false;
                }

            }
        }
        else {
            return this.main.cacheManager.isCached(name, account);
        }
    }

    public void giveMoney(String name, String account, double amount) {
        double balance = this.getBalance(name, account);

        if (balance == Double.NaN) {
            return;
        }

        double newAmount = balance + amount;

        this.setBalance(name, account, newAmount);
    }

    public void takeMoney(String name, String account, double amount) {
        double balance = this.getBalance(name, account);

        if (balance == Double.NaN) {
            return;
        }

        double newAmount = balance - amount;

        this.setBalance(name, account, newAmount);
    }

    public void setBalance(final String name, final String account, final double balance) {
        if (this.main.cacheManager.isCached(name, account)) {
            this.main.cacheManager.setBalance(name, account, balance);
        }
        else {
            this.main.cacheManager.constructCache(name, account);
            this.main.cacheManager.setBalance(name, account, balance);
        }

        CacheQeueObject toRun = new CacheQeueObject() {

            @Override
            public boolean run() {
                try {
                    SpAEconomy.debug("Setting a player's balance...");

                    QueryRunner run = new QueryRunner();
                    Accounts.this.pingConnection();
                    run.update(c, "UPDATE SpAEconomy SET balance=? WHERE playername=? AND account=?", new Object[] { balance, name, account });

                    SpAEconomy.debug("Done setting a player's balance!");
                    return true;
                }
                catch (SQLException ex) {
                    SpAEconomy.warning("Database Error: " + ex);
                    return false;
                }
            }
        };

        this.main.cacheQue.add(toRun);
    }

    public Double getBalance(String name, String account) {
        if (this.main.cacheManager.isCached(name, account)) {
            return this.main.cacheManager.getBalance(name, account);
        }
        else {
            this.main.cacheManager.constructCache(name, account);
            return this.main.cacheManager.getBalance(name, account);
        }
    }

    public void setHidden(final String name, final String account, final boolean hidden) {
        if (this.main.cacheManager.isCached(name, account)) {
            this.main.cacheManager.setHidden(name, account, hidden);
        }
        else {
            this.main.cacheManager.constructCache(name, account);
            this.main.cacheManager.setHidden(name, account, hidden);
        }

        CacheQeueObject toRun = new CacheQeueObject() {

            @Override
            public boolean run() {
                try {
                    SpAEconomy.debug("Hiding a player...");

                    QueryRunner run = new QueryRunner();
                    Accounts.this.pingConnection();
                    run.update(c, "UPDATE SpAEconomy SET hidden=? WHERE playername=? AND account=?", new Object[] { hidden, name, account });

                    SpAEconomy.debug("Done hiding a player!");
                    return true;
                }
                catch (SQLException ex) {
                    SpAEconomy.warning("Database Error: " + ex);
                    return false;
                }
            }
        };

        this.main.cacheQue.add(toRun);
    }

    public boolean getHidden(String name, String account) {
        if (this.main.cacheManager.isCached(name, account)) {
            return this.main.cacheManager.getHidden(name, account);
        }
        else {
            this.main.cacheManager.constructCache(name, account);
            return this.main.cacheManager.getHidden(name, account);
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
