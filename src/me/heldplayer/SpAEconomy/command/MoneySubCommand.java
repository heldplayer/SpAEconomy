
package me.heldplayer.SpAEconomy.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.heldplayer.SpAEconomy.SpAEconomy;
import me.heldplayer.SpAEconomy.system.Accounts;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MoneySubCommand extends SubCommand {

    public MoneySubCommand(String name, String permissions, Map<String, SubCommand> commandsMap, Map<String, SubCommand> aliasesMap, String... aliases) {
        super(name, permissions, commandsMap, aliasesMap, aliases);
    }

    @Override
    public void runCommand(CommandSender sender, String alias, String... args) {
        Accounts accounts = SpAEconomy.instance.accounts;

        if (args.length == 0) {
            if (sender instanceof Player) {
                String name = sender.getName();
                String account = SpAEconomy.getAccountForWorld(((Player) sender).getWorld());

                if (!accounts.exists(name, account, false)) {
                    sender.sendMessage(ChatColor.RED + "You do not have a bank account.");
                }
                else {
                    sender.sendMessage(ChatColor.DARK_GREEN + "You have " + SpAEconomy.roundToDecimals(accounts.getBalance(name, account), 2) + " " + SpAEconomy.moneyName);
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

            String account = SpAEconomy.defaultAccountName;

            if (sender instanceof Player) {
                account = SpAEconomy.getAccountForWorld(((Player) sender).getWorld());
            }

            if (!accounts.exists(name, account, false)) {
                sender.sendMessage(ChatColor.RED + name + " has no bank account.");
            }
            else {
                sender.sendMessage(ChatColor.DARK_GREEN + name + " has " + SpAEconomy.roundToDecimals(accounts.getBalance(name, account), 2) + " " + SpAEconomy.moneyName);
            }
        }
        else if (args.length == 2) {
            String name = args[0];

            if (!name.equalsIgnoreCase(sender.getName()) && !this.hasPermission(sender, "others")) {
                sender.sendMessage(ChatColor.RED + "You do not have permissions to use this command.");
                return;
            }

            String account = args[1];

            if (!accounts.exists(name, account, false)) {
                sender.sendMessage(ChatColor.RED + name + " has no bank account.");
            }
            else {
                sender.sendMessage(ChatColor.DARK_GREEN + name + " has " + SpAEconomy.roundToDecimals(accounts.getBalance(name, account), 2) + " " + SpAEconomy.moneyName);
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
        else if (args.length == 2) {
            ArrayList<String> accountNames = SpAEconomy.accountNames;

            for (int i = 0; i < accountNames.size(); i++) {
                String accountName = accountNames.get(i);

                if (accountName.startsWith(args[1])) {
                    result.add(accountName);
                }
            }
        }

        return result;
    }

    @Override
    public String[] getHelpMessage() {
        return new String[] { this.name + " [account [bank]]" };
    }

}
