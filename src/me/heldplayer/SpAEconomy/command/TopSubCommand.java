
package me.heldplayer.SpAEconomy.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.heldplayer.SpAEconomy.SpAEconomy;
import me.heldplayer.SpAEconomy.cache.CachedAccount;
import me.heldplayer.SpAEconomy.system.Accounts;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class TopSubCommand extends SubCommand {

    public TopSubCommand(String name, String permissions, Map<String, SubCommand> commandsMap, Map<String, SubCommand> aliasesMap, String... aliases) {
        super(name, permissions, commandsMap, aliasesMap, aliases);
    }

    @Override
    public void runCommand(CommandSender sender, String alias, String... args) {
        Accounts accounts = SpAEconomy.instance.accounts;

        int count = 5;
        String account = SpAEconomy.defaultAccountName;

        if (args.length > 0) {
            int value = Integer.parseInt(args[0]);
            if (value > 0 && value < 20) {
                count = value;
            }
        }
        if (args.length > 1) {
            account = args[1];
        }

        CachedAccount[] list = accounts.getTopAccounts(count, account);

        if (list == null) {
            sender.sendMessage(ChatColor.RED + "Nobody has such a bank account or any money");
        }
        else {
            for (int i = 0; i < list.length; i++) {
                CachedAccount cachedAccount = list[i];
                if (cachedAccount != null) {
                    sender.sendMessage(ChatColor.DARK_GREEN.toString() + (i + 1) + ". " + ChatColor.WHITE + cachedAccount.getOwner() + ChatColor.DARK_GREEN + " has " + ChatColor.WHITE + SpAEconomy.formatMoney(cachedAccount.getBalance()));
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
        ArrayList<String> result = new ArrayList<String>();

        if (args.length == 1) {
            return null;
        }

        return result;
    }

    @Override
    public String[] getHelpMessage() {
        return new String[] { this.name + " [limit [bank]]" };
    }

}
