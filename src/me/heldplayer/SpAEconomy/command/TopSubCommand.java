
package me.heldplayer.SpAEconomy.command;

import java.util.List;

import me.heldplayer.SpAEconomy.SpAEconomy;
import me.heldplayer.SpAEconomy.system.Accounts;
import me.heldplayer.SpAEconomy.tables.Account;
import net.specialattack.core.command.AbstractMultiCommand;
import net.specialattack.core.command.AbstractSubCommand;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class TopSubCommand extends AbstractSubCommand {

    public TopSubCommand(AbstractMultiCommand command, String name, String permissions, String... aliases) {
        super(command, name, permissions, aliases);
    }

    @Override
    public void runCommand(CommandSender sender, String alias, String... args) {
        Accounts accounts = SpAEconomy.instance.accounts;

        int count = 5;
        String account = SpAEconomy.defaultAccountName;

        if (args.length > 0) {
            int value = Integer.parseInt(args[0]);
            if (value > 0 && value <= 20) {
                count = value;
            }
        }
        if (args.length > 1) {
            account = args[1];
        }

        Account[] list = accounts.getTopAccounts(count, account);

        if (list == null) {
            sender.sendMessage(ChatColor.RED + "Nobody has such a bank account or any money");
        }
        else {
            for (int i = 0; i < list.length; i++) {
                Account accountRow = list[i];
                if (accountRow != null) {
                    sender.sendMessage(ChatColor.DARK_GREEN.toString() + (i + 1) + ". " + ChatColor.WHITE + accountRow.getOwner() + ChatColor.DARK_GREEN + " has " + ChatColor.WHITE + SpAEconomy.formatMoney(accountRow.getBalance()));
                }
            }
        }
    }

    @Override
    public boolean canUseCommand(CommandSender sender) {
        return true;
    }

    @Override
    public List<String> getTabCompleteResults(CommandSender sender, String alias, String... args) {
        return emptyTabResult;
    }

    @Override
    public String[] getHelpMessage() {
        return new String[] { this.name + " [limit [bank]]" };
    }

}
