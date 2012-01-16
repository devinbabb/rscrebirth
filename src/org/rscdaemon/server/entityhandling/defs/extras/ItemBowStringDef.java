package org.rscdaemon.server.entityhandling.defs.extras;

/**
 * The definition wrapper for bow stringing
 * 
 * @author Devin
 */
public class ItemBowStringDef {

	/**
	 * The level required to attach this bow string
	 */
	public int requiredLvl;
	/**
	 * The exp given by attaching this bow string
	 */
	public int exp;
	/**
	 * The ID of the bow created
	 */
	public int bowID;

	public int getBowID() {
		return bowID;
	}

	public int getReqLevel() {
		return requiredLvl;
	}

	public int getExp() {
		return exp;
	}
}
