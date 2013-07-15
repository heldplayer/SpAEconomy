
package me.heldplayer.SpAEconomy.cache;

import java.util.LinkedList;

import me.heldplayer.SpAEconomy.SpAEconomy;
import me.heldplayer.SpAEconomy.tables.Account;

public class CacheManager {

    private LinkedList<CachedAccount> cache;
    private final SpAEconomy main;

    public CacheManager(SpAEconomy plugin) {
        this.main = plugin;
        this.cache = new LinkedList<CachedAccount>();
    }

    public void addToCache(CachedAccount account) {
        this.cache.add(account);

        SpAEconomy.debug("Added account to cache");
        SpAEconomy.debug("Owner: " + account.getOwner() + "; Account: " + account.getAccount() + "; Balance: " + account.getBalance() + "; Hidden: " + account.isHidden());
    }

    public void unCache(String owner, String account) {
        CachedAccount cachedAccount = this.getCachedAccount(owner, account);

        if (cachedAccount != null) {
            this.cache.remove(cachedAccount);
            SpAEconomy.debug("Removed " + cachedAccount.toString() + " from cache memory");
            cachedAccount.destroy();
        }
    }

    public CachedAccount constructCache(String owner, String account) {
        CachedAccount cachedAccount = this.getCachedAccount(owner, account);

        if (cachedAccount != null) {
            return cachedAccount;
        }

        Account accountRow = this.main.getDatabase().find(Account.class).where().eq("owner", owner).eq("account", account).findUnique();

        if (accountRow == null) {
            return null;
        }

        cachedAccount = new CachedAccount(accountRow);
        this.addToCache(cachedAccount);
        return cachedAccount;
    }

    public boolean isCached(String owner, String account, boolean cacheIfDoesnt) {
        return cacheIfDoesnt ? this.constructCache(owner, account) != null : this.getCachedAccount(owner, account) != null;
    }

    public double getBalance(String owner, String account) {
        CachedAccount cachedAccount = this.constructCache(owner, account);

        if (cachedAccount != null) {
            return cachedAccount.getBalance();
        }

        return 0.0D;
    }

    public void setBalance(String owner, String account, double balance) {
        CachedAccount cachedAccount = this.constructCache(owner, account);

        if (cachedAccount != null) {
            cachedAccount.setBalance(balance);

            // TODO: send to database
        }
    }

    public boolean getHidden(String owner, String account) {
        CachedAccount cachedAccount = this.constructCache(owner, account);

        if (cachedAccount != null) {
            return cachedAccount.isHidden();
        }

        return false;
    }

    public void setHidden(String owner, String account, boolean hidden) {
        CachedAccount cachedAccount = this.constructCache(owner, account);

        if (cachedAccount != null) {
            cachedAccount.setHidden(hidden);

            // TODO: send to database
        }
    }

    private CachedAccount getCachedAccount(String owner, String account) {
        for (CachedAccount cachedAccount : this.cache) {
            if (cachedAccount.getOwner().equals(owner) && cachedAccount.getAccount().equals(account)) {
                return cachedAccount;
            }
        }

        return null;
    }

}
