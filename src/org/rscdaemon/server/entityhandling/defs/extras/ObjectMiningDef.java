package org.rscdaemon.server.entityhandling.defs.extras;

/**
 * The definition wrapper for rocks
 * 
 * @author Devin
 */
public class ObjectMiningDef {

	/**
	 * Herblaw level required to identify
	 */
	public int requiredLvl;
	/**
	 * The id of the ore this turns into
	 */
	private int oreId;
	/**
	 * How much experience identifying gives
	 */
	public int exp;
	/**
	 * How long the rock takes to respawn afterwards
	 */
	public int respawnTime;

	public int getExp() {
		return exp;
	}

	public int getOreId() {
		return oreId;
	}

	public int getReqLevel() {
		return requiredLvl;
	}

	public int getRespawnTime() {
		return respawnTime;
	}

}
