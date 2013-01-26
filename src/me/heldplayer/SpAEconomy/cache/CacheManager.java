
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
    private static ResultSetHandler<Double> returnBalance = new ResultSetHandler<Double>() {

        @Override
        public Double handle(ResultSet rs) throws SQLException {
            if (rs.next()) {
                return Double.valueOf(rs.getDouble("balance"));
            }
            return null;
        }
    };
    private static ResultSetHandler<Boolean> returnHidden = new ResultSetHandler<Boolean>() {

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

        SpAEconomy.debug("Added account to cache");
        SpAEconomy.debug("Username: " + account.owner + "; Account: " + account.account + "; Balance: " + account.balance);
    }

    public void unCache(String name, String account) {
        Object[] accounts = this.cache.toArray();

        for (Object accountObj : accounts) {
            CachedAccount cachedAccount = (CachedAccount) accountObj;
            if (cachedAccount.owner.equalsIgnoreCase(name) && cachedAccount.account.equals(account)) {
                this.cache.remove(this.cache.indexOf(cachedAccount));
                SpAEconomy.debug("Removed " + name + " from cache memory");
                cachedAccount.destroy();
            }
        }
    }

    public boolean constructCache(String name, String account) {
        name = name.toLowerCase();

        this.main.accounts.pingConnection();

        Double balance = null;
        Boolean hidden = null;

        try {
            QueryRunner run = new QueryRunner();
            balance = (Double) run.query(Accounts.c, "SELECT balance FROM SpAEconomy WHERE playername=? AND account=?", returnBalance, new Object[] { name, account });
            balance = SpAEconomy.roundToDecimals(balance, 2);
        }
        catch (SQLException ex) {
            SpAEconomy.warning("Database Error: " + ex);
            return false;
        }
        catch (NullPointerException ex) {
            SpAEconomy.debug("Can't construct cache for " + name);
            SpAEconomy.debug("The player needs a bank account!");
            return false;
        }

        try {
            QueryRunner run = new QueryRunner();
            hidden = (Boolean) run.query(Accounts.c, "SELECT hidden FROM SpAEconomy WHERE playername=? AND account=?", returnHidden, new Object[] { name, account });
        }
        catch (SQLException ex) {
            SpAEconomy.warning("Database Error: " + ex);
            return false;
        }
        catch (NullPointerException ex) {
            SpAEconomy.debug("Can't construct cache for " + name);
            SpAEconomy.debug("The player needs a bank account!");
            return false;
        }

        CachedAccount cachedAccount = new CachedAccount(name, account, balance, hidden);
        this.addToCache(cachedAccount);
        return true;
    }

    public boolean isCached(String name, String account) {
        name = name.toLowerCase();

        Object[] objects = this.cache.toArray();

        for (Object object : objects) {
            if (((CachedAccount) object).owner.equals(name) && ((CachedAccount) object).account.equals(account)) {
                return true;
            }
        }

        return false;
    }

    public Double getBalance(String name, String account) {
        name = name.toLowerCase();

        Object[] objects = this.cache.toArray();

        for (Object object : objects) {
            if (((CachedAccount) object).owner.equals(name) && ((CachedAccount) object).account.equals(account)) {
                return ((CachedAccount) object).balance;
            }
        }

        return null;
    }

    public void setBalance(String name, String account, double balance) {
        name = name.toLowerCase();

        Object[] objects = this.cache.toArray();

        for (Object object : objects) {
            if (((CachedAccount) object).owner.equals(name) && ((CachedAccount) object).account.equals(account)) {
                ((CachedAccount) object).balance = balance;
                return;
            }
        }
    }

    public Boolean getHidden(String name, String account) {
        name = name.toLowerCase();

        Object[] objects = this.cache.toArray();

        for (Object object : objects) {
            if (((CachedAccount) object).owner.equals(name) && ((CachedAccount) object).account.equals(account)) {
                return ((CachedAccount) object).hidden;
            }
        }

        return null;
    }

    public void setHidden(String name, String account, boolean hidden) {
        name = name.toLowerCase();

        Object[] objects = this.cache.toArray();

        for (Object object : objects) {
            if (((CachedAccount) object).owner.equals(name) && ((CachedAccount) object).account.equals(account)) {
                ((CachedAccount) object).hidden = hidden;
                return;
            }
        }
    }
}
