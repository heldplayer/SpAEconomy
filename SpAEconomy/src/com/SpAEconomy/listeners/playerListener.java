package com.SpAEconomy.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.SpAEconomy.SpAEconomy;

public class playerListener implements Listener {

	private SpAEconomy main;

	public playerListener(SpAEconomy instance) {
		main = instance;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (SpAEconomy.createOnFirstJoin && event.getPlayer().hasPermission("spaeconomy.money")) {
			if (event.getPlayer() != null && !main.accounts.exists(event.getPlayer().getName(), false)) {
				main.accounts.create(event.getPlayer().getName(), SpAEconomy.startingMoney);
			}
		}
	}
}