
package me.heldplayer.SpAEconomy;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MoneyCommand implements CommandExecutor {

    private final SpAEconomy main;

    public MoneyCommand(SpAEconomy plugin) {
        this.main = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                if (sender.hasPermission("spaeconomy.money")) {
                    this.displayMoney(sender, sender.getName());
                    return true;
                }
            }
            else {
                sender.sendMessage("I'm sorry, but a console doesn't need a bank account right?");
                return true;
            }
        }
        else if (args.length == 1) {
            /*
             * if (args[0].equalsIgnoreCase("top") &&
             * sender.hasPermission("spaeconomy.top")) {
             * Account[] accounts = main.accounts.getTopAccounts(5);
             * sender.sendMessage(ChatColor.DARK_GREEN +
             * "Listing top 5 players with money.");
             * for (Account account : accounts) {
             * sender.sendMessage(ChatColor.DARK_GREEN + account.playerName +
             * ChatColor.GREEN + " - " + account.balance);
             * }
             * return true;
             * } else
             */if (args[0].equalsIgnoreCase("hide") && sender.hasPermission("spaeconomy.hide")) {
                if (sender instanceof Player) {
                    if (!this.main.accounts.exists(sender.getName(), false)) {
                        sender.sendMessage(ChatColor.RED + "You don't have a bank account!");
                    }
                    else if (this.main.accounts.getHidden(sender.getName())) {
                        this.main.accounts.setHidden(sender.getName(), false);
                        sender.sendMessage(ChatColor.DARK_GREEN + "Showing you to the world!");
                    }
                    else {
                        this.main.accounts.setHidden(sender.getName(), true);
                        sender.sendMessage(ChatColor.DARK_GREEN + "You're shy, I'll hide your ass...");
                    }
                }
                else {
                    sender.sendMessage("Your ass is hiden enough already!");
                }
                return true;
            }
            else if (args[0].equalsIgnoreCase("help")) {
                sender.sendMessage(ChatColor.GRAY + "/" + label + " - displays your money");
                sender.sendMessage(ChatColor.GRAY + "/" + label + " help - displays this help message");
                sender.sendMessage(ChatColor.GRAY + "/" + label + " hide - hides you from the top list");
                //sender.sendMessage(ChatColor.GRAY + "/" + label + " top - displays the top ranked players");
                sender.sendMessage(ChatColor.GRAY + "/" + label + " hide [player] - hides [player] from the top list");
                sender.sendMessage(ChatColor.GRAY + "/" + label + " remove [player] - removes [player]'s bank account");
                //sender.sendMessage(ChatColor.GRAY + "/" + label + " top [limit] - displays the [limit] topmost players");
                sender.sendMessage(ChatColor.GRAY + "/" + label + " give [player] [amount] - adds [amount] " + SpAEconomy.moneyName + " to [player]'s bank account");
                sender.sendMessage(ChatColor.GRAY + "/" + label + " pay [player] [amount] - pays [player] [amount] " + SpAEconomy.moneyName);
                sender.sendMessage(ChatColor.GRAY + "/" + label + " set [player] [balance] - sets " + SpAEconomy.moneyName + " [player]'s bank account to have [balance]");
                sender.sendMessage(ChatColor.GRAY + "/" + label + " take [player] [amount] - removes [amount] " + SpAEconomy.moneyName + " of [player]'s bank account");
                return true;
            }
            else if (sender.hasPermission("spaeconomy.money")) {
                this.displayMoney(sender, args[0]);
                return true;
            }
            else {
                sender.sendMessage(ChatColor.RED + "Not enough permissions.");
                return true;
            }
        }
        else if (args.length == 2) {
            /*
             * if(args[0].equalsIgnoreCase("top") &&
             * sender.hasPermission("spaeconomy.top")){
             * Integer amount;
             * try{
             * amount = Integer.parseInt(args[1]);
             * } catch(Exception ex) {
             * sender.sendMessage(ChatColor.RED +
             * "Parameter expected to be a number.");
             * return true;
             * }
             * if(amount <= 0 || amount > 20){
             * sender.sendMessage(ChatColor.RED +
             * "Parameter must be above 0 and below 20.");
             * return true;
             * }
             * Account[] accounts = main.accounts.getTopAccounts(amount);
             * sender.sendMessage(ChatColor.DARK_GREEN + "Listing top " + amount
             * + " players with money.");
             * for (Account account : accounts) {
             * sender.sendMessage(ChatColor.DARK_GREEN + account.playerName +
             * ChatColor.GREEN + " - " + account.balance);
             * }
             * return true;
             * } else
             */if (args[0].equalsIgnoreCase("hide") && sender.hasPermission("spaeconomy.hide")) {
                if (!this.main.accounts.exists(sender.getName(), false)) {
                    sender.sendMessage(ChatColor.RED + "You don't have a bank account!");
                }
                else if (this.main.accounts.getHidden(args[1])) {
                    this.main.accounts.setHidden(args[1], false);
                    sender.sendMessage(ChatColor.DARK_GREEN + "Unhiding " + args[1]);
                }
                else {
                    this.main.accounts.setHidden(args[1], true);
                    sender.sendMessage(ChatColor.DARK_GREEN + "Hiding " + args[1]);
                }
                return true;
            }
            else if (args[0].equalsIgnoreCase("remove") && sender.hasPermission("spaeconomy.remove")) {
                if (!this.main.accounts.exists(args[1], false)) {
                    sender.sendMessage(ChatColor.RED + "Player doesn't have a bank account!");
                    return true;
                }

                this.main.accounts.remove(args[1], false);
                sender.sendMessage(ChatColor.DARK_GREEN + "Removed " + args[1] + "'s account!");

                return true;
            }
            else if (args[0].equalsIgnoreCase("create") && sender.isOp()) {
                this.main.accounts.create(args[1], 10);
                sender.sendMessage(ChatColor.DARK_GREEN + "Created " + args[1] + "'s account!");

                return true;
            }
        }
        else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("pay") && sender.hasPermission("spaeconomy.pay")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("I'm sorry, but a console doesn't need a bank account right?");
                    return true;
                }

                Double amount;
                try {
                    amount = Double.parseDouble(args[2].replaceAll(",", "."));
                }
                catch (Exception ex) {
                    sender.sendMessage(ChatColor.RED + "Parameter expected to be a number.");
                    return true;
                }

                if (amount <= 0) {
                    sender.sendMessage(ChatColor.RED + "Can't pay negative amounts!");
                    return true;
                }

                if (!this.main.accounts.exists(args[1], false)) {
                    sender.sendMessage(ChatColor.RED + "Player doesn't have a bank account!");
                    return true;
                }

                if (this.main.accounts.getBalance(sender.getName()) - amount < 0) {
                    sender.sendMessage(ChatColor.RED + "You cannot pay for this!");
                    return true;
                }

                if (this.main.getServer().getPlayerExact(args[1]) == null) {
                    sender.sendMessage(ChatColor.RED + "Player has to be online for this!");
                    return true;
                }

                this.main.accounts.takeMoney(sender.getName(), amount);
                this.main.accounts.giveMoney(args[1], amount);

                this.main.getServer().getPlayerExact(args[1]).sendMessage(ChatColor.DARK_GREEN + sender.getName() + " payed you " + amount + " " + SpAEconomy.moneyName);
                sender.sendMessage(ChatColor.DARK_GREEN + "Paying " + args[1] + " " + amount + " " + SpAEconomy.moneyName);
                return true;
            }
            else if (args[0].equalsIgnoreCase("give") && sender.hasPermission("spaeconomy.give")) {
                Double amount;
                try {
                    amount = Double.parseDouble(args[2].replaceAll(",", "."));
                }
                catch (Exception ex) {
                    sender.sendMessage(ChatColor.RED + "Parameter expected to be a number.");
                    return true;
                }

                if (amount <= 0) {
                    sender.sendMessage(ChatColor.RED + "Can't give negative amounts!");
                    return true;
                }

                if (!this.main.accounts.exists(args[1], false)) {
                    sender.sendMessage(ChatColor.RED + "Player doesn't have a bank account!");
                    return true;
                }

                this.main.accounts.giveMoney(args[1], amount);

                sender.sendMessage(ChatColor.DARK_GREEN + "Giving " + args[1] + " " + amount + " " + SpAEconomy.moneyName);
                return true;
            }
            else if (args[0].equalsIgnoreCase("take") && sender.hasPermission("spaeconomy.take")) {
                Double amount;
                try {
                    amount = Double.parseDouble(args[2].replaceAll(",", "."));
                }
                catch (Exception ex) {
                    sender.sendMessage(ChatColor.RED + "Parameter expected to be a number.");
                    return true;
                }

                if (amount <= 0) {
                    sender.sendMessage(ChatColor.RED + "Can't take negative amounts!");
                    return true;
                }

                if (!this.main.accounts.exists(args[1], false)) {
                    sender.sendMessage(ChatColor.RED + "Player doesn't have a bank account!");
                    return true;
                }

                this.main.accounts.takeMoney(args[1], amount);

                sender.sendMessage(ChatColor.DARK_GREEN + "Taking " + amount + " " + SpAEconomy.moneyName + " from " + args[1]);
                return true;
            }
            else if (args[0].equalsIgnoreCase("set") && sender.hasPermission("spaeconomy.set")) {
                Double balance;
                try {
                    balance = Double.parseDouble(args[2].replaceAll(",", "."));
                }
                catch (Exception ex) {
                    sender.sendMessage(ChatColor.RED + "Parameter expected to be a number.");
                    return true;
                }

                if (!this.main.accounts.exists(args[1], false)) {
                    sender.sendMessage(ChatColor.RED + "Player doesn't have a bank account!");
                    return true;
                }

                this.main.accounts.setBalance(args[1], balance);

                sender.sendMessage(ChatColor.DARK_GREEN + "Setting " + args[1] + "'s balance to " + balance + " " + SpAEconomy.moneyName);
                return true;
            }
        }

        sender.sendMessage(ChatColor.RED + "Unknown command or no permissions, type \"/" + label + "\" help for more information.");
        return true;
    }

    private void displayMoney(CommandSender sender, String playerAccount) {
        if (!this.main.accounts.exists(playerAccount, false)) {
            sender.sendMessage(ChatColor.RED + playerAccount + " has no bank account or has no money.");
        }
        else {
            sender.sendMessage(ChatColor.DARK_GREEN + playerAccount + " has " + SpAEconomy.roundToDecimals(this.main.accounts.getBalance(playerAccount), 2) + " " + SpAEconomy.moneyName);
        }
    }
}
