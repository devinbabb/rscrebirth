package org.rscdaemon.ls.packetbuilder.loginserver;

import org.rscdaemon.ls.packetbuilder.LSPacketBuilder;
import org.rscdaemon.ls.net.LSPacket;

/**
 * Handles registration with world
 * 
 * @author Devin
 * 
 */

public class WorldRegisteredPacketBuilder {
	/**
	 * Packets uID
	 */
	private long uID;
	/**
	 * Was the registering successful?
	 */
	private boolean success;

	/**
	 * Sets the packet to reply to
	 */
	public void setUID(long uID) {
		this.uID = uID;
	}

	/**
	 * Sets whether or not we were successful
	 */
	public void setSuccess(boolean success) {
		this.success = success;
	}

	public LSPacket getPacket() {
		LSPacketBuilder packet = new LSPacketBuilder();
		packet.setUID(uID);
		packet.addByte((byte) (success ? 1 : 0));
		return packet.toPacket();
	}
}
