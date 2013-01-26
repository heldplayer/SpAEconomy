
package me.heldplayer.SpAEconomy.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.heldplayer.SpAEconomy.SpAEconomy;
import me.heldplayer.SpAEconomy.system.Accounts;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AccountsSubCommand extends SubCommand {

    public AccountsSubCommand(String name, String permissions, Map<String, SubCommand> commandsMap, Map<String, SubCommand> aliasesMap, String... aliases) {
        super(name, permissions, commandsMap, aliasesMap, aliases);
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

                    if (accounts.exists(name, account, false)) {
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

                if (accounts.exists(name, account, false)) {
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
        ArrayList<String> result = new ArrayList<String>();

        if (args.length == 1) {
            return null;
        }

        return result;
    }

    @Override
    public String[] getHelpMessage() {
        return new String[] { this.name + " [account [bank]]" };
    }

}
