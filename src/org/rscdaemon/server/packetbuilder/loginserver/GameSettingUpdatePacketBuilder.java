package org.rscdaemon.server.packetbuilder.loginserver;

import org.rscdaemon.server.packetbuilder.LSPacketBuilder;
import org.rscdaemon.server.model.Player;
import org.rscdaemon.server.net.LSPacket;

public class GameSettingUpdatePacketBuilder {
	/**
	 * Player to update
	 */
	private Player player;
	/**
	 * The settings index
	 */
	private int index;
	/**
	 * Has the setting been turned on or off?
	 */
	private boolean on;

	/**
	 * Sets the player to update
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setOn(boolean on) {
		this.on = on;
	}

	public LSPacket getPacket() {
		LSPacketBuilder packet = new LSPacketBuilder();
		packet.setID(7);
		packet.addLong(player.getUsernameHash());
		packet.addByte((byte) (on ? 1 : 0));
		packet.addByte((byte) index);
		return packet.toPacket();
	}

}
