package org.rscdaemon.server.entityhandling.defs.extras;

/**
 * The definition wrapper for Certificate items
 * 
 * @author Devin
 */

public class CertDef {

	public String name;
	/**
	 * The ID of the certificate
	 */
	public int certID;
	/**
	 * The ID of the assosiated item
	 */
	public int itemID;

	public String getName() {
		return name;
	}

	public int getCertID() {
		return certID;
	}

	public int getItemID() {
		return itemID;
	}
}