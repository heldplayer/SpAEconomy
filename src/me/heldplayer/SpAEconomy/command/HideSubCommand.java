
package me.heldplayer.SpAEconomy.command;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import me.heldplayer.SpAEconomy.SpAEconomy;
import me.heldplayer.SpAEconomy.system.Accounts;
import net.specialattack.core.command.AbstractMultiCommand;
import net.specialattack.core.command.AbstractSubCommand;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HideSubCommand extends AbstractSubCommand {

    public HideSubCommand(AbstractMultiCommand command, String name, String permissions, String... aliases) {
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
                boolean hide = false;
                if (args[0].equalsIgnoreCase("true")) {
                    hide = true;
                }
                else if (!args[0].equalsIgnoreCase("false")) {
                    sender.sendMessage(ChatColor.RED + "Invalid value entered.");
                    return;
                }

                if (!accounts.exists(name, account)) {
                    sender.sendMessage(ChatColor.RED + "You do not have a bank account.");
                }
                else {
                    accounts.setHidden(name, account, hide);

                    if (hide) {
                        sender.sendMessage(ChatColor.DARK_GREEN + "You have been hidden");
                    }
                    else {
                        sender.sendMessage(ChatColor.DARK_GREEN + "You are now visible to the world again");
                    }
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
            boolean hide = false;
            if (args[0].equalsIgnoreCase("true")) {
                hide = true;
            }
            else if (!args[0].equalsIgnoreCase("false")) {
                sender.sendMessage(ChatColor.RED + "Invalid value entered.");
                return;
            }

            if (sender instanceof Player) {
                account = SpAEconomy.getAccountForWorld(((Player) sender).getWorld());
            }

            if (!accounts.exists(name, account)) {
                sender.sendMessage(ChatColor.RED + name + " has no bank account.");
            }
            else {
                accounts.setHidden(name, account, hide);

                if (hide) {
                    sender.sendMessage(ChatColor.DARK_GREEN + name + " is now hidden");
                }
                else {
                    sender.sendMessage(ChatColor.DARK_GREEN + name + " is now visible to the world again");
                }
            }
        }
        else if (args.length == 3) {
            String name = args[1];

            if (!name.equalsIgnoreCase(sender.getName()) && !this.hasPermission(sender, "others")) {
                sender.sendMessage(ChatColor.RED + "You do not have permissions to use this command.");
                return;
            }

            String account = args[2];
            boolean hide = false;
            if (args[0].equalsIgnoreCase("true")) {
                hide = true;
            }
            else if (!args[0].equalsIgnoreCase("false")) {
                sender.sendMessage(ChatColor.RED + "Invalid value entered.");
                return;
            }

            if (!accounts.exists(name, account)) {
                sender.sendMessage(ChatColor.RED + name + " has no bank account.");
            }
            else {
                accounts.setHidden(name, account, hide);

                if (hide) {
                    sender.sendMessage(ChatColor.DARK_GREEN + name + " is now hidden");
                }
                else {
                    sender.sendMessage(ChatColor.DARK_GREEN + name + " is now visible to the world again");
                }
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
            ArrayList<String> result = new ArrayList<String>();
            result.add("true");
            result.add("false");
            return result;
        }
        else if (args.length == 2) {
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
        return new String[] { this.name + " true/false [account [bank]]" };
    }

}
