
package me.heldplayer.SpAEconomy.cache;

public class CachedAccount {

    public final String owner;
    public double balance = 0;
    public boolean hidden = false;

    public CachedAccount(String owner) {
        this.owner = owner.toLowerCase();
    }

    public CachedAccount(String owner, double balance) {
        this.owner = owner.toLowerCase();
        this.balance = balance;
    }

    public CachedAccount(String owner, double balance, boolean hidden) {
        this.owner = owner.toLowerCase();
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
