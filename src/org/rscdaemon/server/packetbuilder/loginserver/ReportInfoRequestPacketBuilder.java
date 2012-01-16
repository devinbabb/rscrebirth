package org.rscdaemon.server.packetbuilder.loginserver;

import org.rscdaemon.server.packetbuilder.LSPacketBuilder;
import org.rscdaemon.server.model.World;
import org.rscdaemon.server.model.Player;
import org.rscdaemon.server.net.LSPacket;

public class ReportInfoRequestPacketBuilder {
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();
	/**
	 * Packets uID
	 */
	private long uID;
	/**
	 * The player to provide information on
	 */
	private Player player;

	/**
	 * Sets the packet to reply to
	 */
	public void setUID(long uID) {
		this.uID = uID;
	}

	/**
	 * Sets the player to provide information on
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}

	public LSPacket getPacket() {
		LSPacketBuilder packet = new LSPacketBuilder();
		packet.setUID(uID);
		packet.addShort(player.getX());
		packet.addShort(player.getY());
		packet.addBytes(player.getStatus().toString().getBytes());
		return packet.toPacket();
	}
}
