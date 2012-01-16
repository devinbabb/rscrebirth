package org.rscdaemon.server.entityhandling.defs.extras;

/**
 * The definition wrapper for crafting items
 * 
 * @author Devin
 */

public class ItemCraftingDef {
	/**
	 * The crafting level required to make this item
	 */
	public int requiredLvl;
	/**
	 * The ID of the item produced
	 */
	public int itemID;
	/**
	 * The exp given
	 */
	public int exp;

	public int getReqLevel() {
		return requiredLvl;
	}

	public int getItemID() {
		return itemID;
	}

	public int getExp() {
		return exp;
	}
}