
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

    public void create(String name, double balance) {
        this.create(name, balance, true);
    }

    public void create(final String name, final double balance, boolean checkCache) {
        if (checkCache) {
            if (this.exists(name, false, false)) {
                SpAEconomy.warning("Tried to create a new bank account for " + name + " but one already exists!");
                return;
            }
        }

        this.main.cmg.addToCache(new CachedAccount(name, balance));

        CacheQeueObject toRun = new CacheQeueObject() {

            @Override
            public boolean run() {
                try {
                    SpAEconomy.debug("Creating an account for " + name + "...");

                    QueryRunner run = new QueryRunner();
                    Accounts.this.pingConnection();
                    run.update(c, "INSERT INTO SpAEconomy (playername, balance) " + "VALUES (?, ?)", new Object[] { name, balance });

                    SpAEconomy.debug("Done creating an account for " + name + "!");
                    return true;
                }
                catch (SQLException ex) {
                    SpAEconomy.warning("Database Error: " + ex);
                    return false;
                }
            }
        };

        this.main.cq.add(toRun);
    }

    public void remove(final String name, boolean checkCache) {
        if (checkCache) {
            if (!this.exists(name, false, false)) {
                SpAEconomy.warning("Tried to remove " + name + "'s account but it doesn't exist yet!");
                return;
            }
        }

        this.main.cmg.unCache(name);

        try {
            SpAEconomy.debug("Removing " + name + "'s account...");

            QueryRunner run = new QueryRunner();
            this.pingConnection();
            run.update(c, "DELETE FROM SpAEconomy WHERE playername=?", new Object[] { name });

            SpAEconomy.debug("Done removing " + name + "'s account!");
        }
        catch (SQLException ex) {
            SpAEconomy.warning("Database Error: " + ex);
        }
    }

    public boolean exists(String name, boolean fake) {
        return this.exists(name, fake, true);
    }

    public boolean exists(String name, boolean fake, boolean checkCache) {
        if (fake) {
            return true;
        }

        if (checkCache) {
            if (this.main.cmg.isCached(name)) {
                return true;
            }
            else {
                if (this.main.cmg.constructCache(name)) {
                    return true;
                }
                else {
                    return false;
                }

            }
        }
        else {
            return this.main.cmg.isCached(name);
        }
    }

    public void giveMoney(String name, double amount) {
        double newAmount = this.getBalance(name) + amount;

        this.setBalance(name, newAmount);
    }

    public void takeMoney(String name, double amount) {
        double newAmount = this.getBalance(name) - amount;

        this.setBalance(name, newAmount);
    }

    public void setBalance(final String name, final double balance) {
        if (this.main.cmg.isCached(name)) {
            this.main.cmg.setBalance(name, balance);
        }
        else {
            this.main.cmg.constructCache(name);
            this.main.cmg.setBalance(name, balance);
        }

        CacheQeueObject toRun = new CacheQeueObject() {

            @Override
            public boolean run() {
                try {
                    SpAEconomy.debug("Setting a player's balance...");

                    QueryRunner run = new QueryRunner();
                    Accounts.this.pingConnection();
                    run.update(c, "UPDATE SpAEconomy SET balance=? WHERE playername=?", new Object[] { balance, name });

                    SpAEconomy.debug("Done setting a player's balance!");
                    return true;
                }
                catch (SQLException ex) {
                    SpAEconomy.warning("Database Error: " + ex);
                    return false;
                }
            }
        };

        this.main.cq.add(toRun);
    }

    public double getBalance(String name) {
        if (this.main.cmg.isCached(name)) {
            return this.main.cmg.getBalance(name);
        }
        else {
            this.main.cmg.constructCache(name);
            return this.main.cmg.getBalance(name);
        }
    }

    public void setHidden(final String name, final boolean hidden) {
        if (this.main.cmg.isCached(name)) {
            this.main.cmg.setHidden(name, hidden);
        }
        else {
            this.main.cmg.constructCache(name);
            this.main.cmg.setHidden(name, hidden);
        }

        CacheQeueObject toRun = new CacheQeueObject() {

            @Override
            public boolean run() {
                try {
                    SpAEconomy.debug("Hiding a player...");

                    QueryRunner run = new QueryRunner();
                    Accounts.this.pingConnection();
                    run.update(c, "UPDATE SpAEconomy SET hidden=? WHERE playername=?", new Object[] { hidden, name });

                    SpAEconomy.debug("Done hiding a player!");
                    return true;
                }
                catch (SQLException ex) {
                    SpAEconomy.warning("Database Error: " + ex);
                    return false;
                }
            }
        };

        this.main.cq.add(toRun);
    }

    public boolean getHidden(String name) {
        if (this.main.cmg.isCached(name)) {
            return this.main.cmg.getHidden(name);
        }
        else {
            this.main.cmg.constructCache(name);
            return this.main.cmg.getHidden(name);
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
