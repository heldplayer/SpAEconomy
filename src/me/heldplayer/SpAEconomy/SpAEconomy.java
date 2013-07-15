
package me.heldplayer.SpAEconomy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;

import me.heldplayer.SpAEconomy.cache.CacheManager;
import me.heldplayer.SpAEconomy.command.MoneyCommand;
import me.heldplayer.SpAEconomy.listeners.PlayerListener;
import me.heldplayer.SpAEconomy.system.Accounts;
import me.heldplayer.SpAEconomy.tables.Account;

import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SpAEconomy extends JavaPlugin {

    // Plugin specific variables
    public static SpAEconomy instance;

    public PluginManager pm;
    public PluginDescriptionFile pdf;

    public Accounts accounts;
    public CacheManager cacheManager;

    private static Logger log;

    public PlayerListener pListener;

    private FileConfiguration config;
    public static boolean createOnFirstJoin;
    public static double startingMoney;
    public static String moneyName;
    public static boolean debug;
    public static String defaultAccountName;
    public static HashMap<String, String> worlds = new HashMap<String, String>();
    public static LinkedList<String> accountNames = new LinkedList<String>();

    @Override
    public void onDisable() {
        this.pm = null;
        this.pdf = null;
        this.config = null;
        instance = null;
        info("Disabled!");
    }

    @Override
    public void onEnable() {
        instance = this;

        long start = System.currentTimeMillis();

        log = this.getLogger();

        this.pm = this.getServer().getPluginManager();
        this.pdf = this.getDescription();

        this.setupDatabase();

        this.config = this.getConfig();

        FileConfiguration defConfig = YamlConfiguration.loadConfiguration(this.getResource("config.yml"));
        this.config.setDefaults(defConfig);

        createOnFirstJoin = this.config.getBoolean("create-on-first-join");
        startingMoney = this.config.getDouble("starting-money");
        moneyName = this.config.getString("money-name");
        debug = this.config.getBoolean("debug");
        defaultAccountName = this.config.getString("default-account-name");
        ConfigurationSection section = this.config.getConfigurationSection("world-accounts");

        Set<String> keys = section.getKeys(false);
        Iterator<String> i = keys.iterator();

        while (i.hasNext()) {
            String world = i.next();

            String account = section.getString(world, "false");

            if (!account.equalsIgnoreCase("false")) {
                worlds.put(world, account);

                if (!accountNames.contains(account)) {
                    accountNames.add(account);
                }
            }
        }

        this.saveConfig();

        this.accounts = new Accounts(this);
        this.cacheManager = new CacheManager(this);

        this.getCommand("money").setExecutor(new MoneyCommand());

        this.getServer().getPluginManager().registerEvents(this.pListener = new PlayerListener(this), this);

        long stop = System.currentTimeMillis();
        info("Enabled!");
        debug("Took (" + (stop - start) + "ms)");
    }

    protected void setupDatabase() {
        try {
            this.getDatabase().find(Account.class).findRowCount();
        }
        catch (PersistenceException ex) {
            this.getLogger().info("Installing database...");

            this.installDDL();
        }
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {
        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(Account.class);
        return list;
    }

    public static double roundToDecimals(double value, int decimals) {
        int temp = (int) ((value * Math.pow(10, decimals)));
        return (((double) temp) / Math.pow(10, decimals));
    }

    public static void warning(String message) {
        log.warning(message);
    }

    public static void info(String message) {
        log.info(message);
    }

    public static void debug(String message) {
        if (debug) {
            info("[DEBUG] " + message);
        }
    }

    public static String getAccountForWorld(World world) {
        String account = worlds.get(world.getName());
        return account == null ? defaultAccountName : account;
    }

    public static String formatMoney(double amount) {
        return roundToDecimals(amount, 2) + " " + moneyName;
    }

}
