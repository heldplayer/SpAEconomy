
package me.heldplayer.SpAEconomy.cache;

public class CachedAccount {

    public final String owner;
    public final String account;
    public double balance = 0;
    public boolean hidden = false;

    public CachedAccount(String owner, String account) {
        this.owner = owner.toLowerCase();
        this.account = account.toLowerCase();
    }

    public CachedAccount(String owner, String account, double balance) {
        this.owner = owner.toLowerCase();
        this.account = account.toLowerCase();
        this.balance = balance;
    }

    public CachedAccount(String owner, String account, double balance, boolean hidden) {
        this.owner = owner.toLowerCase();
        this.account = account.toLowerCase();
        this.balance = balance;
        this.hidden = hidden;
    }

    public void destroy() {
        try {
            this.finalize();
        }
        catch (Throwable ex) {}
    }
}
