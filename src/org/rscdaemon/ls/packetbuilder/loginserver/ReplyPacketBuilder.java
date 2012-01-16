package org.rscdaemon.ls.packetbuilder.loginserver;

import org.rscdaemon.ls.packetbuilder.LSPacketBuilder;
import org.rscdaemon.ls.net.LSPacket;

/**
 * Handles the reply packets.
 * 
 * @author Devin
 */

public class ReplyPacketBuilder {
	/**
	 * Packets uID
	 */
	private long uID;
	/**
	 * Reply
	 */
	private String reply;
	/**
	 * Was the action successful
	 */
	private boolean success;

	/**
	 * Sets the packet to reply to
	 */
	public void setUID(long uID) {
		this.uID = uID;
	}

	/**
	 * Sets the status of the action
	 */
	public void setSuccess(boolean success) {
		this.success = success;
	}

	/**
	 * Sets the reply to send back
	 */
	public void setReply(String reply) {
		this.reply = reply;
	}

	public LSPacket getPacket() {
		LSPacketBuilder packet = new LSPacketBuilder();
		packet.setUID(uID);
		packet.addByte((byte) (success ? 1 : 0));
		if (reply != null) {
			packet.addBytes(reply.getBytes());
		}
		return packet.toPacket();
	}
}
