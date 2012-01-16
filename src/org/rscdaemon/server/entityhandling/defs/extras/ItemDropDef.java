package org.rscdaemon.server.entityhandling.defs.extras;

/**
 * The definition wrapper for monster drop instances
 * 
 * @author Devin
 */

public class ItemDropDef {
	public int id;
	public int amount;
	public int weight;

	public int getID() {
		return id;
	}

	public int getAmount() {
		return amount;
	}

	public int getWeight() {
		return weight;
	}
}