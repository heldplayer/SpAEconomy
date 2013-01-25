
package me.heldplayer.SpAEconomy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

import me.heldplayer.SpAEconomy.cache.CacheManager;
import me.heldplayer.SpAEconomy.cache.CacheQeue;
import me.heldplayer.SpAEconomy.listeners.PlayerListener;
import me.heldplayer.SpAEconomy.system.Accounts;
import me.heldplayer.SpAEconomy.system.Database;
import me.heldplayer.SpAEconomy.system.DbUtils;
import me.heldplayer.SpAEconomy.system.MissingDriver;
import me.heldplayer.SpAEconomy.system.QueryRunner;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SpAEconomy extends JavaPlugin {

    // Plugin specific variables
    public PluginManager pm;
    public PluginDescriptionFile pdf;
    public String dataPath = "";
    private final String separator = System.getProperty("line.separator");
    public Accounts accounts;
    public Database Database;
    public CacheManager cmg;
    public CacheQeue cq;
    // Logger
    private static Logger log;
    // Listeners
    private final PlayerListener pListener = new PlayerListener(this);
    // Config
    public File configFile = null;
    public Config cfg;
    public static boolean createOnFirstJoin = false;
    public static double startingMoney = 0.0;
    public static String moneyName = "dollars";
    public static boolean debug = false;

    @Override
    public void onDisable() {
        this.cq.run();
        info("Disabled!");
    }

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();

        log = this.getLogger();

        this.dataPath = this.getDataFolder().getAbsolutePath();
        this.pm = this.getServer().getPluginManager();
        this.pdf = this.getDescription();

        File dataFolder = this.getDataFolder();

        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        this.configFile = new File(this.dataPath + "/config.txt");

        this.cfg = new Config(this, this.configFile);
        this.cfg.load();

        createOnFirstJoin = this.cfg.getBoolean("create-on-first-join", true);
        startingMoney = this.cfg.getDouble("starting-money", 0);
        moneyName = this.cfg.getString("money-name", "dollars");
        debug = this.cfg.getBoolean("debug", false);

        this.cfg.save();

        if (!new File("lib", "mysql.jar").exists()) {
            info("Downloading mysql.jar...");
            fetch("http://mirror.nexua.org/Dependencies/mysql-connector-java-bin.jar", "mysql.jar");
            info("Finished Downloading.");
        }

        try {
            this.Database = new Database(this.cfg.getString("db-url", ""), this.cfg.getString("db-username", ""), this.cfg.getString("db-password", ""));

            if (!this.Database.tableExists("SpAEconomy")) {
                String SQL = "CREATE TABLE IF NOT EXISTS `SpAEconomy` (" + this.separator + "  `id` int(11) NOT NULL AUTO_INCREMENT," + this.separator + "  `playername` varchar(32) NOT NULL," + this.separator + "  `balance` double NOT NULL," + this.separator + "  `hidden` boolean DEFAULT false," + this.separator + "  UNIQUE KEY `playername` (`playername`)," + this.separator + "  PRIMARY KEY `id` (`id`)" + this.separator + ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
                try {
                    QueryRunner run = new QueryRunner();
                    Connection c = this.Database.getConnection();
                    try {
                        run.update(c, SQL);
                    }
                    catch (SQLException ex) {
                        info("Error creating database: " + ex);
                    }
                    finally {
                        DbUtils.close(c);
                    }
                }
                catch (SQLException ex) {
                    info("Database Error: " + ex);
                }
            }
        }
        catch (MissingDriver ex) {
            info(ex.getMessage());
        }

        this.accounts = new Accounts(this);
        this.cmg = new CacheManager(this);
        this.cq = new CacheQeue(this);

        this.getCommand("money").setExecutor(new MoneyCommand(this));

        this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, this.cq, 1200, 1200);

        this.getServer().getPluginManager().registerEvents(this.pListener, this);

        long stop = System.currentTimeMillis();
        info("Enabled!");
        debug("Took (" + (stop - start) + "ms)");
    }

    public String getMoneyName() {
        return moneyName;
    }

    public static double roundToDecimals(double value, int decimals) {
        int temp = (int) ((value * Math.pow(10, decimals)));
        return (((double) temp) / Math.pow(10, decimals));
    }

    public static void fetch(String location, String filename) {
        try {
            info("   + " + filename + " (" + getFileSize(location) + ")");
            download(location, filename);
            info("   - " + filename + " finished.");
        }
        catch (IOException ex) {
            info("Error Downloading File: " + ex);
        }
    }

    @SuppressWarnings("unused")
    protected static synchronized void download(String location, String filename) throws IOException {
        URLConnection connection = new URL(location).openConnection();
        connection.setUseCaches(false);
        String destination = "lib" + File.separator + filename;
        File parentDirectory = new File(destination).getParentFile();
        if (parentDirectory != null) {
            parentDirectory.mkdirs();
        }
        InputStream in = connection.getInputStream();
        OutputStream out = new FileOutputStream(destination);
        byte[] buffer = new byte[65536];
        int currentCount = 0;

        while (true) {
            int count = in.read(buffer);
            if (count < 0) {
                break;
            }
            out.write(buffer, 0, count);
            currentCount += count;
        }

        in.close();
        out.close();
    }

    private static String getFileSize(String location) throws IOException {
        URLConnection connection = new URL(location).openConnection();
        return readableSize(connection.getContentLength());
    }

    public static String readableSize(long size) {
        String[] units = { "B", "KB", "MB", "GB", "TB", "PB" };
        int mod = 1024;

        int i;
        for (i = 0; size > mod; i++) {
            size /= mod;
        }
        return Math.round((float) size) + " " + units[i];
    }

    public static void warning(String message) {
        log.warning(message);
    }

    public static void info(String message) {
        log.info(message);
    }

    public static void debug(String message) {
        if (debug) {
            info(message);
        }
    }
}