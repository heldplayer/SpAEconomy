package com.SpAEconomy.cache;

import com.SpAEconomy.SpAEconomy;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public class cacheQeue implements Runnable {

	private HashMap<Integer, cacheQeueObject> qeue;
	@SuppressWarnings("unused")
	private final SpAEconomy main;
	private int number = 0;

	public cacheQeue(SpAEconomy plugin) {
		main = plugin;
		qeue = new HashMap<Integer, cacheQeueObject>();
	}

	@SuppressWarnings("unchecked")
	public void run() {
		SpAEconomy.debug("Running through cache qeue...");

		Set<Entry<Integer, cacheQeueObject>> entries = qeue.entrySet();
		Object[] objects = entries.toArray();

		for (Object object : objects) {
			SpAEconomy.debug("object id: " + ((Entry<Integer, cacheQeueObject>) object).getKey());
			if (((Entry<Integer, cacheQeueObject>) object).getValue().run()) {
				qeue.remove(((Entry<Integer, cacheQeueObject>) object).getKey());
			} else {
				SpAEconomy.warning("Tried to excecute a cached qeue entry but failed!");
			}
		}

		SpAEconomy.debug("Done running through cache qeue!");
	}

	public void add(cacheQeueObject object) {
		qeue.put(number++, object);
	}
}