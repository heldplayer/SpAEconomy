
package me.heldplayer.SpAEconomy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Config {

    @SuppressWarnings("unused")
    private final SpAEconomy main;
    private File cfg;
    ArrayList<ConfigKey> keys = new ArrayList<ConfigKey>();
    private final String separator = System.getProperty("line.separator");

    public Config(SpAEconomy plugin, File cfg) {
        this.main = plugin;
        this.cfg = cfg;
    }

    public void load() {
        this.keys.clear();

        if (this.cfg.isDirectory()) {
            this.cfg = new File(this.cfg.getAbsolutePath() + "config.txt");
        }

        if (!this.cfg.exists()) {
            try {
                this.cfg.createNewFile();
            }
            catch (IOException e) {
                SpAEconomy.warning("Error while creating config file.");
                SpAEconomy.warning("File path: " + this.cfg.getAbsolutePath());
                e.printStackTrace();
            }
        }
        else {
            try {
                BufferedReader in = new BufferedReader(new FileReader(this.cfg));
                String line;
                while ((line = in.readLine()) != null) {
                    line = line.trim();
                    if (line.charAt(0) != '#') {
                        if (line.contains("=")) {
                            String[] args = line.split("=");
                            String key = args[0].trim();
                            String value = args[1].trim();
                            this.keys.add(new ConfigKey(key, value));
                        }
                    }
                }
            }
            catch (Exception e) {
                SpAEconomy.warning("Error while updating config file.");
                e.printStackTrace();
            }
        }
    }

    public void save() {
        boolean changes = false;
        for (int i = 0; i < this.keys.size(); i++) {
            if (this.keys.get(i).isChanged()) {
                changes = true;
            }
        }
        if (changes) {
            try {
                PrintWriter out = new PrintWriter(new FileOutputStream(this.cfg));
                out.write("###" + this.separator);
                out.write("#SpAEconomy" + this.separator);
                out.write("#A plugin developed by Heldplayer and mbl111" + this.separator);
                out.write("#written for the SpecialAttack.net Minecraft server" + this.separator);
                out.write("###" + this.separator);
                for (int i = 0; i < this.keys.size(); i++) {
                    String line = "";
                    ConfigKey cc = this.keys.get(i);
                    line += cc.key;
                    line += " = ";
                    line += cc.value;
                    out.write(line + this.separator);
                }
                out.close();
            }
            catch (Exception e) {
                e.printStackTrace();
                SpAEconomy.warning("Error while updating config file.");
            }
        }
    }

    public String getString(String key, String defaultValue) {
        for (int i = 0; i < this.keys.size(); i++) {
            ConfigKey k = this.keys.get(i);
            if (key.equals(k.getKey())) {
                return k.getValue();
            }
        }
        ConfigKey nk = new ConfigKey(key, defaultValue);
        nk.dirty();
        this.keys.add(nk);
        return nk.getValue();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        String val = this.getString(key, Boolean.toString(defaultValue));
        return Boolean.parseBoolean(val);
    }

    public int getInt(String key, int defaultValue) {
        String val = this.getString(key, Integer.toString(defaultValue));
        return Integer.parseInt(val);
    }

    public double getDouble(String key, int defaultValue) {
        String val = this.getString(key, Integer.toString(defaultValue));
        return Double.parseDouble(val);
    }

    private static class ConfigKey {

        private final String key;
        private String value;
        private boolean changed;

        public ConfigKey(String key, String value) {
            this.key = key;
            this.value = value;
            this.changed = false;
        }

        public void dirty() {
            this.changed = true;
        }

        public String getValue() {
            return this.value;
        }

        public String getKey() {
            return this.key;
        }

        public boolean isChanged() {
            return this.changed;
        }
    }
}
