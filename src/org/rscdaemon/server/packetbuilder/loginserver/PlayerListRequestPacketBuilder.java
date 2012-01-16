package org.rscdaemon.server.packetbuilder.loginserver;

import org.rscdaemon.server.packetbuilder.LSPacketBuilder;
import org.rscdaemon.server.model.World;
import org.rscdaemon.server.model.Player;
import org.rscdaemon.server.net.LSPacket;
import org.rscdaemon.server.util.EntityList;

public class PlayerListRequestPacketBuilder {
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();
	/**
	 * Packets uID
	 */
	private long uID;

	/**
	 * Sets the packet to reply to
	 */
	public void setUID(long uID) {
		this.uID = uID;
	}

	public LSPacket getPacket() {
		EntityList<Player> players = world.getPlayers();

		LSPacketBuilder packet = new LSPacketBuilder();
		packet.setUID(uID);
		packet.addInt(players.size());
		for (Player p : players) {
			packet.addLong(p.getUsernameHash());
			packet.addShort(p.getX());
			packet.addShort(p.getY());
		}
		return packet.toPacket();
	}
}
