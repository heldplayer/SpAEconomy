
package me.heldplayer.SpAEconomy.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class MoneyCommand implements CommandExecutor, TabCompleter {

    public final Map<String, SubCommand> commands = new HashMap<String, SubCommand>();
    public final Map<String, SubCommand> aliases = new HashMap<String, SubCommand>();

    public MoneyCommand() {
        new MoneySubCommand("get", "spaeconomy.command.view", this.commands, this.aliases, "count", "amount", "money", "view");
        new GiveSubCommand("give", "spaeconomy.command.give", this.commands, this.aliases, "increase", "increment", "add", "+", "++");
        new TakeSubCommand("take", "spaeconomy.command.take", this.commands, this.aliases, "decrease", "decrement", "-", "--");
        new AccountsSubCommand("accounts", "spaeconomy.command.accounts", this.commands, this.aliases, "list", "acs", "moneys");
        new PaySubCommand("pay", "spaeconomy.command.pay", this.commands, this.aliases);
        new CreateSubCommand("create", "spaeconomy.command.create", this.commands, this.aliases, "new", "insert");
        new RemoveSubCommand("remove", "spaeconomy.command.remove", this.commands, this.aliases, "delete");
        new TopSubCommand("top", "spaeconomy.command.top", this.commands, this.aliases);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (sender instanceof BlockCommandSender) {
            // We don't work with command blocks
            return false;
        }

        if (args.length == 0) {
            this.commands.get("get").runCommand(sender, "get");
        }
        else if (args[0].equalsIgnoreCase("help")) {
            Set<Entry<String, SubCommand>> commandSet = this.commands.entrySet();

            for (Entry<String, SubCommand> entry : commandSet) {
                SubCommand subCommand = entry.getValue();

                if (!subCommand.canUseCommand(sender)) {
                    continue;
                }
                if (!subCommand.hasPermission(sender)) {
                    continue;
                }

                String[] usages = subCommand.getHelpMessage();

                for (int i = 0; i < usages.length; i++) {
                    usages[i] = ChatColor.DARK_GRAY + "/" + alias + " " + ChatColor.GRAY + usages[i];
                }

                sender.sendMessage(usages);
            }
        }
        else {
            SubCommand subCommand = this.commands.get(args[0]);

            if (subCommand == null) {
                subCommand = this.aliases.get(args[0]);
            }

            if (subCommand == null) {
                sender.sendMessage(ChatColor.RED + "Unkown command, please type /" + alias + " help for a list of commands.");
                return true;
            }

            if (!subCommand.canUseCommand(sender)) {
                sender.sendMessage(ChatColor.RED + "You cannot use this command.");
                return true;
            }

            if (!subCommand.hasPermission(sender)) {
                sender.sendMessage(ChatColor.RED + "You do not have permissions to use this command.");
                return true;
            }

            String[] newArgs = new String[args.length - 1];

            System.arraycopy(args, 1, newArgs, 0, args.length - 1);

            subCommand.runCommand(sender, args[0], newArgs);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            ArrayList<String> complements = new ArrayList<String>();

            Set<Entry<String, SubCommand>> commandSet = this.commands.entrySet();

            for (Entry<String, SubCommand> entry : commandSet) {
                SubCommand subCommand = entry.getValue();

                if (!subCommand.canUseCommand(sender)) {
                    continue;
                }
                if (!subCommand.hasPermission(sender)) {
                    continue;
                }

                if (subCommand.name.toLowerCase().startsWith(args[0].toLowerCase())) {
                    complements.add(subCommand.name);
                }

                for (String commandAlias : subCommand.aliases) {
                    if (args[0].length() != 0 && commandAlias.toLowerCase().startsWith(args[0].toLowerCase())) {
                        complements.add(commandAlias);
                    }
                }
            }

            return complements;
        }
        else {
            SubCommand subCommand = this.commands.get(args[0]);

            if (subCommand == null) {
                subCommand = this.aliases.get(args[0]);
            }

            if (subCommand == null) {
                return null;
            }

            if (!subCommand.canUseCommand(sender)) {
                return null;
            }

            if (!subCommand.hasPermission(sender)) {
                return null;
            }

            String[] newArgs = new String[args.length - 1];

            System.arraycopy(args, 1, newArgs, 0, args.length - 1);

            return subCommand.getTabCompleteResults(sender, alias, newArgs);
        }
    }

}
