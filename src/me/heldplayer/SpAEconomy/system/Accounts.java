
package me.heldplayer.SpAEconomy.system;

import java.util.List;

import me.heldplayer.SpAEconomy.SpAEconomy;
import me.heldplayer.SpAEconomy.tables.Account;

public class Accounts {

    private final SpAEconomy main;

    public Accounts(SpAEconomy plugin) {
        this.main = plugin;
    }

    public void create(String owner, String account, double balance) {
        Account accountRow = this.main.getDatabase().find(Account.class).where().eq("owner", owner).eq("account", account).findUnique();

        if (accountRow != null) {
            return;
        }

        accountRow = new Account();
        accountRow.setOwner(owner);
        accountRow.setAccount(account);
        accountRow.setBalance(balance);
        accountRow.setHidden(false);

        this.main.getDatabase().insert(accountRow);
    }

    public void remove(String owner, String account) {
        Account accountRow = this.main.getDatabase().find(Account.class).where().eq("owner", owner).eq("account", account).findUnique();

        if (accountRow != null) {
            this.main.getDatabase().delete(accountRow);
        }
    }

    public boolean exists(String owner, String account) {
        return this.main.getDatabase().find(Account.class).where().eq("owner", owner).eq("account", account).findUnique() != null;
    }

    public void giveMoney(String owner, String account, double amount) {
        Account accountRow = this.main.getDatabase().find(Account.class).where().eq("owner", owner).eq("account", account).findUnique();

        if (accountRow != null) {
            accountRow.setBalance(accountRow.getBalance() + amount);

            this.updateAccount(accountRow);
        }
    }

    public void takeMoney(String owner, String account, double amount) {
        Account accountRow = this.main.getDatabase().find(Account.class).where().eq("owner", owner).eq("account", account).findUnique();

        if (accountRow != null) {
            accountRow.setBalance(accountRow.getBalance() - amount);

            this.updateAccount(accountRow);
        }
    }

    public void setBalance(String owner, String account, double balance) {
        Account accountRow = this.main.getDatabase().find(Account.class).where().eq("owner", owner).eq("account", account).findUnique();

        if (accountRow != null) {
            accountRow.setBalance(balance);

            this.updateAccount(accountRow);
        }
    }

    public double getBalance(String owner, String account) {
        Account accountRow = this.main.getDatabase().find(Account.class).where().eq("owner", owner).eq("account", account).findUnique();

        if (accountRow != null) {
            return accountRow.getBalance();
        }

        return 0.0D;
    }

    public void setHidden(String owner, String account, boolean hidden) {
        Account accountRow = this.main.getDatabase().find(Account.class).where().eq("owner", owner).eq("account", account).findUnique();

        if (accountRow != null) {
            accountRow.setHidden(hidden);

            this.updateAccount(accountRow);
        }
    }

    public boolean getHidden(String owner, String account) {
        Account accountRow = this.main.getDatabase().find(Account.class).where().eq("owner", owner).eq("account", account).findUnique();

        if (accountRow != null) {
            return accountRow.isHidden();
        }

        return false;
    }

    public Account[] getTopAccounts(int amount, String account) {
        SpAEconomy.debug("Retrieving the " + amount + " topmost ranked players...");

        Account[] result = new Account[amount];

        List<Account> list = this.main.getDatabase().find(Account.class).setMaxRows(amount).orderBy("balance DESC").where().eq("account", account).eq("hidden", false).findList();

        if (list.size() <= 0) {
            return null;
        }

        for (int i = 0; i < list.size() && i < result.length; i++) {
            result[i] = list.get(i);
        }

        return result;
    }

    private void updateAccount(Account accountRow) {
        this.main.getDatabase().update(accountRow);
    }

}
