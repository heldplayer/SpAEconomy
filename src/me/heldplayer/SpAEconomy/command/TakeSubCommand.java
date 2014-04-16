
package me.heldplayer.SpAEconomy.command;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import me.heldplayer.SpAEconomy.SpAEconomy;
import me.heldplayer.SpAEconomy.system.Accounts;
import net.specialattack.bukkit.core.command.AbstractMultiCommand;
import net.specialattack.bukkit.core.command.AbstractSubCommand;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TakeSubCommand extends AbstractSubCommand {

    public TakeSubCommand(AbstractMultiCommand command, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
    }

    @Override
    public void runCommand(CommandSender sender, String alias, String... args) {
        Accounts accounts = SpAEconomy.instance.accounts;

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Too little arguments were entered.");
        }
        else if (args.length == 1) {
            if (sender instanceof Player) {
                String name = sender.getName();
                String account = SpAEconomy.getAccountForWorld(((Player) sender).getWorld());
                Double amount = 0.0D;

                try {
                    amount = Double.valueOf(args[0]);
                }
                catch (NumberFormatException ex) {
                    sender.sendMessage(ChatColor.RED + "Invalid amount entered.");
                    return;
                }

                if (amount <= 0.0D) {
                    sender.sendMessage(ChatColor.RED + "Invalid amount entered.");
                    return;
                }

                if (!accounts.exists(name, account)) {
                    sender.sendMessage(ChatColor.RED + "You do not have a bank account.");
                }
                else {
                    accounts.takeMoney(name, account, amount);

                    sender.sendMessage(ChatColor.DARK_GREEN + "You now have " + SpAEconomy.roundToDecimals(accounts.getBalance(name, account), 2) + " " + SpAEconomy.moneyName);
                }
            }
            else {
                sender.sendMessage(ChatColor.RED + "This kind of user cannot have a bank account.");
            }
        }
        else if (args.length == 2) {
            String name = args[1];

            if (!name.equalsIgnoreCase(sender.getName()) && !this.hasPermission(sender, "others")) {
                sender.sendMessage(ChatColor.RED + "You do not have permissions to use this command.");
                return;
            }

            String account = SpAEconomy.defaultAccountName;
            Double amount = 0.0D;

            try {
                amount = Double.valueOf(args[0]);
            }
            catch (NumberFormatException ex) {
                sender.sendMessage(ChatColor.RED + "Invalid amount entered.");
                return;
            }

            if (amount <= 0.0D) {
                sender.sendMessage(ChatColor.RED + "Invalid amount entered.");
                return;
            }

            if (sender instanceof Player) {
                account = SpAEconomy.getAccountForWorld(((Player) sender).getWorld());
            }

            if (!accounts.exists(name, account)) {
                sender.sendMessage(ChatColor.RED + name + " has no bank account.");
            }
            else {
                accounts.takeMoney(name, account, amount);

                sender.sendMessage(ChatColor.DARK_GREEN + name + " now has " + SpAEconomy.roundToDecimals(accounts.getBalance(name, account), 2) + " " + SpAEconomy.moneyName);
            }
        }
        else if (args.length == 3) {
            String name = args[1];

            if (!name.equalsIgnoreCase(sender.getName()) && !this.hasPermission(sender, "others")) {
                sender.sendMessage(ChatColor.RED + "You do not have permissions to use this command.");
                return;
            }

            String account = args[2];
            Double amount = 0.0D;

            try {
                amount = Double.valueOf(args[0]);
            }
            catch (NumberFormatException ex) {
                sender.sendMessage(ChatColor.RED + "Invalid amount entered.");
                return;
            }

            if (amount <= 0.0D) {
                sender.sendMessage(ChatColor.RED + "Invalid amount entered.");
                return;
            }

            if (!accounts.exists(name, account)) {
                sender.sendMessage(ChatColor.RED + name + " has no bank account.");
            }
            else {
                accounts.takeMoney(name, account, amount);

                sender.sendMessage(ChatColor.DARK_GREEN + name + " now has " + SpAEconomy.roundToDecimals(accounts.getBalance(name, account), 2) + " " + SpAEconomy.moneyName);
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
        if (args.length == 2) {
            return null;
        }
        else if (args.length == 3) {
            ArrayList<String> result = new ArrayList<String>();

            LinkedList<String> accountNames = SpAEconomy.accountNames;

            for (int i = 0; i < accountNames.size(); i++) {
                String accountName = accountNames.get(i);

                if (accountName.startsWith(args[2])) {
                    result.add(accountName);
                }
            }

            return result;
        }

        return emptyTabResult;
    }

    @Override
    public String[] getHelpMessage() {
        return new String[] { this.name + " amount [account [bank]]" };
    }

}
