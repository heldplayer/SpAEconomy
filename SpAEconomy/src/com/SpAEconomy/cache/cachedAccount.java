package com.SpAEconomy.cache;

public class cachedAccount {

	public final String owner;
	public double balance = 0;
	public boolean hidden = false;

	public cachedAccount(String owner) {
		this.owner = owner.toLowerCase();
	}

	public cachedAccount(String owner, double balance) {
		this.owner = owner.toLowerCase();
		this.balance = balance;
	}

	public cachedAccount(String owner, double balance, boolean hidden) {
		this.owner = owner.toLowerCase();
		this.balance = balance;
		this.hidden = hidden;
	}

	public void destroy() {
		try {
			finalize();
		} catch (Throwable ex) {
		}
	}
}