
package me.heldplayer.SpAEconomy.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.heldplayer.SpAEconomy.SpAEconomy;
import me.heldplayer.SpAEconomy.system.Accounts;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RemoveSubCommand extends SubCommand {

    public RemoveSubCommand(String name, String permissions, Map<String, SubCommand> commandsMap, Map<String, SubCommand> aliasesMap, String... aliases) {
        super(name, permissions, commandsMap, aliasesMap, aliases);
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

            if (sender instanceof Player) {
                account = SpAEconomy.getAccountForWorld(((Player) sender).getWorld());
            }

            if (account == null) {
                sender.sendMessage(ChatColor.RED + "Bank accounts are not enabled in this world.");
                return;
            }

            if (!accounts.exists(name, account, false)) {
                sender.sendMessage(ChatColor.RED + "An account with that name doesn't exists.");
            }
            else {
                accounts.remove(name, account, false);

                sender.sendMessage(ChatColor.DARK_GREEN + "Deleted account");
            }
        }
        else if (args.length == 2) {
            String name = args[0];

            if (!this.hasPermission(sender, "differentbank")) {
                sender.sendMessage(ChatColor.RED + "You do not have permissions to use this command.");
                return;
            }

            String account = args[1];

            if (!SpAEconomy.accountNames.contains(account)) {
                sender.sendMessage(ChatColor.RED + "Bank accounts are not enabled in this world.");
                return;
            }

            if (sender instanceof Player) {
                account = SpAEconomy.getAccountForWorld(((Player) sender).getWorld());
            }

            if (!accounts.exists(name, account, false)) {
                sender.sendMessage(ChatColor.RED + "An account with that name doesn't exists.");
            }
            else {
                accounts.remove(name, account, false);

                sender.sendMessage(ChatColor.DARK_GREEN + "Deleted account");
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
        return new String[] { this.name + " account [bank]" };
    }

}
