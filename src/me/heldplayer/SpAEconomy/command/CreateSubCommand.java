
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

public class CreateSubCommand extends AbstractSubCommand {

    public CreateSubCommand(AbstractMultiCommand command, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
    }

    @Override
    public void runCommand(CommandSender sender, String alias, String... args) {
        Accounts accounts = SpAEconomy.instance.accounts;

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Too little arguments were entered.");
        }
        else if (args.length == 1) {
            String name = args[0];
            String account = SpAEconomy.defaultAccountName;
            Double amount = SpAEconomy.startingMoney;

            if (sender instanceof Player) {
                account = SpAEconomy.getAccountForWorld(((Player) sender).getWorld());
            }

            if (account == null) {
                sender.sendMessage(ChatColor.RED + "Bank accounts are not enabled in this world.");
                return;
            }

            if (accounts.exists(name, account)) {
                sender.sendMessage(ChatColor.RED + "An account with that name already exists.");
            }
            else {
                accounts.create(name, account, amount);

                sender.sendMessage(ChatColor.DARK_GREEN + "Created an account with starting balance " + SpAEconomy.roundToDecimals(accounts.getBalance(name, account), 2) + " " + SpAEconomy.moneyName);
            }
        }
        else if (args.length == 2) {
            String name = args[0];

            if (!this.hasPermission(sender, "withmoney")) {
                sender.sendMessage(ChatColor.RED + "You do not have permissions to use this command.");
                return;
            }

            String account = SpAEconomy.defaultAccountName;
            Double amount = 0.0D;

            if (account == null) {
                sender.sendMessage(ChatColor.RED + "Bank accounts are not enabled in this world.");
                return;
            }

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

            if (sender instanceof Player) {
                account = SpAEconomy.getAccountForWorld(((Player) sender).getWorld());
            }

            if (accounts.exists(name, account)) {
                sender.sendMessage(ChatColor.RED + "An account with that name already exists.");
            }
            else {
                accounts.create(name, account, amount);

                sender.sendMessage(ChatColor.DARK_GREEN + "Created an account with starting balance " + SpAEconomy.roundToDecimals(accounts.getBalance(name, account), 2) + " " + SpAEconomy.moneyName);
            }
        }
        else if (args.length == 3) {
            String name = args[0];

            if (!this.hasPermission(sender, "withmoney") || !this.hasPermission(sender, "differentbank")) {
                sender.sendMessage(ChatColor.RED + "You do not have permissions to use this command.");
                return;
            }

            String account = args[2];
            Double amount = 0.0D;

            if (!SpAEconomy.accountNames.contains(account)) {
                sender.sendMessage(ChatColor.RED + "Bank accounts are not enabled in this world.");
                return;
            }

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

            if (sender instanceof Player) {
                account = SpAEconomy.getAccountForWorld(((Player) sender).getWorld());
            }

            if (accounts.exists(name, account)) {
                sender.sendMessage(ChatColor.RED + "An account with that name already exists.");
            }
            else {
                accounts.create(name, account, amount);

                sender.sendMessage(ChatColor.DARK_GREEN + "Created an account with starting balance " + SpAEconomy.roundToDecimals(accounts.getBalance(name, account), 2) + " " + SpAEconomy.moneyName);
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
        return new String[] { this.name + " account [balance [bank]]" };
    }

}
