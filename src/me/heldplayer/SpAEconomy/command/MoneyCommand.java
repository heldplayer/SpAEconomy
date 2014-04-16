
package me.heldplayer.SpAEconomy.command;

import net.specialattack.bukkit.core.command.AbstractMultiCommand;
import net.specialattack.bukkit.core.command.HelpSubCommand;

public class MoneyCommand extends AbstractMultiCommand {

    public MoneyCommand() {
        new MoneySubCommand(this, "get", "spaeconomy.command.view", "count", "amount", "money", "view");
        new GiveSubCommand(this, "give", "spaeconomy.command.give", "increase", "increment", "add", "+", "++");
        new TakeSubCommand(this, "take", "spaeconomy.command.take", "decrease", "decrement", "-", "--");
        new AccountsSubCommand(this, "accounts", "spaeconomy.command.accounts", "list", "acs", "moneys");
        new PaySubCommand(this, "pay", "spaeconomy.command.pay");
        new CreateSubCommand(this, "create", "spaeconomy.command.create", "new", "insert");
        new RemoveSubCommand(this, "remove", "spaeconomy.command.remove", "delete");
        new TopSubCommand(this, "top", "spaeconomy.command.top");
        new HideSubCommand(this, "hide", "spaeconomy.command.hide");
        new HelpSubCommand(this, "help", "spaeconomy.command.help", "?");
    }

    @Override
    public String getDefaultCommand() {
        return "get";
    }

}
