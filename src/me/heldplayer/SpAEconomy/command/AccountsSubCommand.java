
package me.heldplayer.SpAEconomy.command;

import java.util.List;

import me.heldplayer.SpAEconomy.SpAEconomy;
import me.heldplayer.SpAEconomy.system.Accounts;
import net.specialattack.bukkit.core.command.AbstractMultiCommand;
import net.specialattack.bukkit.core.command.AbstractSubCommand;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AccountsSubCommand extends AbstractSubCommand {

    public AccountsSubCommand(AbstractMultiCommand command, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
    }

    @Override
    public void runCommand(CommandSender sender, String alias, String... args) {
        Accounts accounts = SpAEconomy.instance.accounts;

        if (args.length == 0) {
            if (sender instanceof Player) {
                String name = sender.getName();

                boolean hasAccounts = false;

                for (int i = 0; i < SpAEconomy.accountNames.size(); i++) {
                    String account = SpAEconomy.accountNames.get(i);

                    if (accounts.exists(name, account)) {
                        sender.sendMessage(ChatColor.GRAY + account + ": " + ChatColor.DARK_GREEN + SpAEconomy.roundToDecimals(accounts.getBalance(name, account), 2) + " " + SpAEconomy.moneyName);

                        hasAccounts = true;
                    }
                }

                if (!hasAccounts) {
                    sender.sendMessage(ChatColor.RED + name + " has no bank accounts.");
                }
            }
            else {
                sender.sendMessage(ChatColor.RED + "This kind of user cannot have a bank account.");
            }
        }
        else if (args.length == 1) {
            String name = args[0];

            if (!name.equalsIgnoreCase(sender.getName()) && !this.hasPermission(sender, "others")) {
                sender.sendMessage(ChatColor.RED + "You do not have permissions to use this command.");
                return;
            }

            boolean hasAccounts = false;

            for (int i = 0; i < SpAEconomy.accountNames.size(); i++) {
                String account = SpAEconomy.accountNames.get(i);

                if (accounts.exists(name, account)) {
                    sender.sendMessage(ChatColor.GRAY + account + ": " + ChatColor.DARK_GREEN + SpAEconomy.roundToDecimals(accounts.getBalance(name, account), 2) + " " + SpAEconomy.moneyName);

                    hasAccounts = true;
                }
            }

            if (!hasAccounts) {
                sender.sendMessage(ChatColor.RED + name + " has no bank accounts.");
            }
        }
        else {
            sender.sendMessage(ChatColor.RED + "Too many arguments were entered.");
        }
    }

    @Override
    public boolean canUseCommand(CommandSender sender) {
        return true;
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
        return new String[] { this.name + " [account [bank]]" };
    }

}
