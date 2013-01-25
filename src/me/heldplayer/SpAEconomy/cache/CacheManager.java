
package me.heldplayer.SpAEconomy.cache;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import me.heldplayer.SpAEconomy.SpAEconomy;
import me.heldplayer.SpAEconomy.system.Accounts;
import me.heldplayer.SpAEconomy.system.QueryRunner;
import me.heldplayer.SpAEconomy.system.ResultSetHandler;

public class CacheManager {

    private ArrayList<CachedAccount> cache;
    private final SpAEconomy main;
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

    public CacheManager(SpAEconomy plugin) {
        this.main = plugin;
        this.cache = new ArrayList<CachedAccount>();
    }

    public void addToCache(CachedAccount account) {
        this.cache.add(account);
    }

    public void unCache(String name) {
        Object[] accounts = this.cache.toArray();

        for (Object accountObj : accounts) {
            CachedAccount account = (CachedAccount) accountObj;
            if (account.owner.equalsIgnoreCase(name)) {
                this.cache.remove(this.cache.indexOf(account));
                SpAEconomy.debug("Removed " + name + " from cache memory");
                account.destroy();
            }
        }
    }

    public boolean constructCache(String name) {
        name = name.toLowerCase();

        this.main.accounts.pingConnection();

        Double balance = null;
        Boolean hidden = null;

        try {
            QueryRunner run = new QueryRunner();
            balance = (Double) run.query(Accounts.c, "SELECT balance FROM SpAEconomy WHERE playername=?", returnBalance, new Object[] { name });
            balance = SpAEconomy.roundToDecimals(balance, 2);
        }
        catch (SQLException ex) {
            SpAEconomy.warning("Database Error: " + ex);
            return false;
        }
        catch (NullPointerException ex) {
            SpAEconomy.info("Can't construct cache for " + name);
            SpAEconomy.info("The player needs a bank account!");
            return false;
        }

        try {
            QueryRunner run = new QueryRunner();
            hidden = (Boolean) run.query(Accounts.c, "SELECT hidden FROM SpAEconomy WHERE playername=?", returnHidden, new Object[] { name });
        }
        catch (SQLException ex) {
            SpAEconomy.warning("Database Error: " + ex);
            return false;
        }
        catch (NullPointerException ex) {
            SpAEconomy.warning("Can't construct cache for " + name);
            SpAEconomy.warning("The player needs a bank account!");
            return false;
        }

        CachedAccount account = new CachedAccount(name, balance, hidden);
        this.addToCache(account);
        return true;
    }

    public boolean isCached(String name) {
        name = name.toLowerCase();

        Object[] objects = this.cache.toArray();

        for (Object object : objects) {
            if (((CachedAccount) object).owner.equals(name)) {
                return true;
            }
        }

        return false;
    }

    public Double getBalance(String name) {
        name = name.toLowerCase();

        Object[] objects = this.cache.toArray();

        for (Object object : objects) {
            if (((CachedAccount) object).owner.equals(name)) {
                return ((CachedAccount) object).balance;
            }
        }

        return null;
    }

    public void setBalance(String name, double balance) {
        name = name.toLowerCase();

        Object[] objects = this.cache.toArray();

        for (Object object : objects) {
            if (((CachedAccount) object).owner.equals(name)) {
                ((CachedAccount) object).balance = balance;
                return;
            }
        }
    }

    public Boolean getHidden(String name) {
        name = name.toLowerCase();

        Object[] objects = this.cache.toArray();

        for (Object object : objects) {
            if (((CachedAccount) object).owner.equals(name)) {
                return ((CachedAccount) object).hidden;
            }
        }

        return null;
    }

    public void setHidden(String name, boolean hidden) {
        name = name.toLowerCase();

        Object[] objects = this.cache.toArray();

        for (Object object : objects) {
            if (((CachedAccount) object).owner.equals(name)) {
                ((CachedAccount) object).hidden = hidden;
                return;
            }
        }
    }
}
