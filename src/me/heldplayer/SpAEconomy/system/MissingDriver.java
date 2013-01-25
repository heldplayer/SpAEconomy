
package me.heldplayer.SpAEconomy.system;

public class MissingDriver extends Exception {

    private static final long serialVersionUID = -4137244394400234077L;

    public MissingDriver(String file) {
        super("Missing Driver: " + file);
    }
}
