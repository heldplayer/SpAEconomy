
package me.heldplayer.SpAEconomy.listeners;

import me.heldplayer.SpAEconomy.SpAEconomy;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

    private SpAEconomy main;

    public PlayerListener(SpAEconomy instance) {
        this.main = instance;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (SpAEconomy.createOnFirstJoin && event.getPlayer().hasPermission("spaeconomy.money")) {
            if (event.getPlayer() != null && !this.main.accounts.exists(event.getPlayer().getName(), false)) {
                this.main.accounts.create(event.getPlayer().getName(), SpAEconomy.startingMoney);
            }
        }
    }
}
