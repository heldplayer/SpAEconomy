
package me.heldplayer.SpAEconomy.command;

import java.util.List;

import me.heldplayer.SpAEconomy.SpAEconomy;
import me.heldplayer.SpAEconomy.system.Accounts;
import net.specialattack.bukkit.core.command.AbstractMultiCommand;
import net.specialattack.bukkit.core.command.AbstractSubCommand;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PaySubCommand extends AbstractSubCommand {

    public PaySubCommand(AbstractMultiCommand command, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
    }

    @Override
    public void runCommand(CommandSender sender, String alias, String... args) {
        Accounts accounts = SpAEconomy.instance.accounts;

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Too little arguments were entered.");
        }
        else if (args.length == 2) {
            String name = args[0];

            String account = SpAEconomy.getAccountForWorld(((Player) sender).getWorld());
            Double amount = 0.0D;

            try {
                amount = Double.valueOf(args[1]);
            }
            catch (NumberFormatException ex) {
                sender.sendMessage(ChatColor.RED + "Invalid amount entered.");
                return;
            }

            if (amount <= 0.0D) {
                sender.sendMessage(ChatColor.RED + "Invalid amount entered.");
                return;
            }

            if (!accounts.exists(sender.getName(), account)) {
                sender.sendMessage(ChatColor.RED + "You do not have a bank account.");
            }
            else if (!accounts.exists(name, account)) {
                sender.sendMessage(ChatColor.RED + name + " has no bank account.");
            }
            else {
                if (accounts.getBalance(sender.getName(), account) - amount < 0.0D) {
                    sender.sendMessage(ChatColor.RED + "You do not have enough money.");
                }
                else {
                    accounts.takeMoney(sender.getName(), account, amount);
                    accounts.giveMoney(name, account, amount);

                    Player player = Bukkit.getPlayerExact(name);

                    if (player != null) {
                        player.sendMessage(ChatColor.DARK_GREEN + sender.getName() + " has payed you " + SpAEconomy.roundToDecimals(amount, 2) + " " + SpAEconomy.moneyName);
                    }

                    sender.sendMessage(ChatColor.DARK_GREEN + name + " has been payed " + SpAEconomy.roundToDecimals(amount, 2) + " " + SpAEconomy.moneyName);
                }
            }
        }
        else {
            sender.sendMessage(ChatColor.RED + "Too many arguments were entered.");
        }
    }

    @Override
    public boolean canUseCommand(CommandSender sender) {
        return sender instanceof Player;
    }

    @Override
    public List<String> getTabCompleteResults(CommandSender sender, String alias, String... args) {
        if (args.length == 1) {
            return null;
        }

        return emptyTabResult;
    }

    @Override
    public String[] getHelpMessage() {
        return new String[] { this.name + " account amount" };
    }

}
