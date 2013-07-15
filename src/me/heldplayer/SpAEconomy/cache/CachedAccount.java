
package me.heldplayer.SpAEconomy.cache;

import me.heldplayer.SpAEconomy.SpAEconomy;
import me.heldplayer.SpAEconomy.tables.Account;

public class CachedAccount {

    private Account accountRow;

    public CachedAccount(Account accountRow) {
        this.accountRow = accountRow;
    }

    public Account getAccountRow() {
        return this.accountRow;
    }

    public String getOwner() {
        return this.accountRow.getOwner();
    }

    public String getAccount() {
        return this.accountRow.getAccount();
    }

    public double getBalance() {
        return this.accountRow.getBalance();
    }

    public void setBalance(double balance) {
        this.accountRow.setBalance(balance);
    }

    public boolean isHidden() {
        return this.accountRow.isHidden();
    }

    public void setHidden(boolean hidden) {
        this.accountRow.setHidden(hidden);
    }

    public void destroy() {
        this.accountRow = null;
    }

    @Override
    public String toString() {
        return "[CachedAccount \"" + this.getOwner() + "\":\"" + this.getAccount() + "\"@\"" + SpAEconomy.formatMoney(this.getBalance()) + "\" " + (this.isHidden() ? "hidden" : "visible") + "]";
    }

}
