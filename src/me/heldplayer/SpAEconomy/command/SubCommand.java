
package me.heldplayer.SpAEconomy.command;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;

public abstract class SubCommand {
    private String permission;
    protected final String name;
    protected final String[] aliases;

    public SubCommand(String name, String permission, Map<String, SubCommand> commandsMap, Map<String, SubCommand> aliasesMap, String... aliases) {
        this.permission = permission;
        this.name = name;
        this.aliases = aliases;

        commandsMap.put(name, this);

        for (String alias : aliases) {
            aliasesMap.put(alias, this);
        }
    }

    public boolean hasPermission(CommandSender sender) {
        if (sender.hasPermission("spacore.command.*")) {
            return true;
        }

        return sender.hasPermission(permission);
    }

    public boolean hasPermission(CommandSender sender, String permission) {
        return sender.hasPermission(this.permission + "." + permission);
    }

    public abstract void runCommand(CommandSender sender, String alias, String... args);

    public abstract boolean canUseCommand(CommandSender sender);

    public abstract List<String> getTabCompleteResults(CommandSender sender, String alias, String... args);

    public abstract String[] getHelpMessage();
}
