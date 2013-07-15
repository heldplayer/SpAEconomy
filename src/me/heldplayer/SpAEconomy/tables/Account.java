
package me.heldplayer.SpAEconomy.tables;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.validation.Length;
import com.avaje.ebean.validation.NotEmpty;

@Entity()
@Table(name = "spaecon_accounts")
public class Account {

    @Id
    private int id;

    @Length(max = 64)
    @NotEmpty
    private String owner;

    @Length(max = 64)
    @NotEmpty
    private String account;

    private double balance;

    private boolean hidden;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOwner() {
        return this.owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getAccount() {
        return this.account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public double getBalance() {
        return this.balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

}
