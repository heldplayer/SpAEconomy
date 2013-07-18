
package me.heldplayer.SpAEconomy.system;

import java.util.List;

import me.heldplayer.SpAEconomy.SpAEconomy;
import me.heldplayer.SpAEconomy.cache.CachedAccount;
import me.heldplayer.SpAEconomy.tables.Account;

public class Accounts {

    private final SpAEconomy main;

    public Accounts(SpAEconomy plugin) {
        this.main = plugin;
    }

    public void create(String owner, String account, double balance) {
        CachedAccount cachedAccount = this.main.cacheManager.constructCache(owner, account);

        if (cachedAccount != null) {
            return;
        }

        Account accountRow = new Account();
        accountRow.setOwner(owner);
        accountRow.setAccount(account);
        accountRow.setBalance(balance);
        accountRow.setHidden(false);

        this.main.getDatabase().insert(accountRow);

        cachedAccount = new CachedAccount(accountRow);

        this.main.cacheManager.addToCache(cachedAccount);
    }

    public void remove(String owner, String account) {
        CachedAccount cachedAccount = this.main.cacheManager.constructCache(owner, account);

        if (cachedAccount != null) {
            this.main.getDatabase().delete(cachedAccount.getAccountRow());

            this.main.cacheManager.unCache(owner, account);
        }
    }

    public boolean exists(String owner, String account) {
        return this.main.cacheManager.isCached(owner, account, true);
    }

    public void giveMoney(String owner, String account, double amount) {
        CachedAccount cachedAccount = this.main.cacheManager.constructCache(owner, account);

        if (cachedAccount != null) {
            cachedAccount.setBalance(cachedAccount.getBalance() + amount);

            this.main.getDatabase().update(cachedAccount.getAccountRow());
        }
    }

    public void takeMoney(String owner, String account, double amount) {
        CachedAccount cachedAccount = this.main.cacheManager.constructCache(owner, account);

        if (cachedAccount != null) {
            cachedAccount.setBalance(cachedAccount.getBalance() - amount);

            this.main.getDatabase().update(cachedAccount.getAccountRow());
        }
    }

    public void setBalance(String owner, String account, double balance) {
        CachedAccount cachedAccount = this.main.cacheManager.constructCache(owner, account);

        if (cachedAccount != null) {
            cachedAccount.setBalance(balance);

            this.main.getDatabase().update(cachedAccount.getAccountRow());
        }
    }

    public double getBalance(String owner, String account) {
        CachedAccount cachedAccount = this.main.cacheManager.constructCache(owner, account);

        if (cachedAccount != null) {
            return cachedAccount.getBalance();
        }

        return 0.0D;
    }

    public void setHidden(String owner, String account, boolean hidden) {
        CachedAccount cachedAccount = this.main.cacheManager.constructCache(owner, account);

        if (cachedAccount != null) {
            cachedAccount.setHidden(hidden);

            this.main.getDatabase().update(cachedAccount.getAccountRow());
        }
    }

    public boolean getHidden(String owner, String account) {
        CachedAccount cachedAccount = this.main.cacheManager.constructCache(owner, account);

        if (cachedAccount != null) {
            return cachedAccount.isHidden();
        }

        return false;
    }

    public CachedAccount[] getTopAccounts(int amount, String account) {
        SpAEconomy.debug("Retrieving the " + amount + " topmost ranked players...");

        CachedAccount[] result = new CachedAccount[amount];

        List<Account> list = this.main.getDatabase().find(Account.class).setMaxRows(amount).orderBy("balance DESC").where().eq("account", account).eq("hidden", false).findList();

        if (list.size() <= 0) {
            return null;
        }

        for (int i = 0; i < list.size() && i < result.length; i++) {
            result[i] = new CachedAccount(list.get(i));
        }

        return result;
    }
}
