
package me.heldplayer.SpAEconomy.cache;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import me.heldplayer.SpAEconomy.SpAEconomy;

public class CacheQeue implements Runnable {

    private HashMap<Integer, CacheQeueObject> qeue;
    @SuppressWarnings("unused")
    private final SpAEconomy main;
    private int number = 0;

    public CacheQeue(SpAEconomy plugin) {
        this.main = plugin;
        this.qeue = new HashMap<Integer, CacheQeueObject>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void run() {
        SpAEconomy.debug("Running through cache qeue...");

        Set<Entry<Integer, CacheQeueObject>> entries = this.qeue.entrySet();
        Object[] objects = entries.toArray();

        for (Object object : objects) {
            SpAEconomy.debug("object id: " + ((Entry<Integer, CacheQeueObject>) object).getKey());
            if (((Entry<Integer, CacheQeueObject>) object).getValue().run()) {
                this.qeue.remove(((Entry<Integer, CacheQeueObject>) object).getKey());
            }
            else {
                SpAEconomy.warning("Tried to excecute a cached qeue entry but failed!");
            }
        }

        SpAEconomy.debug("Done running through cache qeue!");
    }

    public void add(CacheQeueObject object) {
        this.qeue.put(this.number++, object);
    }
}
