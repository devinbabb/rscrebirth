package org.rscdaemon.ls.model;

/**
 * Creates the instance of a inventory item.
 * 
 * @author Devin
 */

public class InvItem extends Item {
	private boolean wielded;

	public InvItem(int id, int amount, boolean wielded) {
		super(id, amount);
		this.wielded = wielded;
	}

	public boolean isWielded() {
		return wielded;
	}
}