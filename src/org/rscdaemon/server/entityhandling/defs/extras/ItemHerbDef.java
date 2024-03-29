package org.rscdaemon.server.entityhandling.defs.extras;

/**
 * The definition wrapper for herblaw items.
 * 
 * @author Devin
 */
public class ItemHerbDef {

	/**
	 * The exp smelting this item gives
	 */
	public int exp;
	/**
	 * The id of the related potion
	 */
	public int potionId;
	/**
	 * The level required to make this
	 */
	public int requiredLvl;

	public int getExp() {
		return exp;
	}

	public int getPotionId() {
		return potionId;
	}

	public int getReqLevel() {
		return requiredLvl;
	}

}
