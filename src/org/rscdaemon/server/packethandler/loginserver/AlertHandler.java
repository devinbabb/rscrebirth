package org.rscdaemon.server.packethandler.loginserver;

import org.rscdaemon.server.packethandler.PacketHandler;
import org.rscdaemon.server.model.World;
import org.rscdaemon.server.model.Player;
import org.rscdaemon.server.net.Packet;
import org.rscdaemon.server.net.LSPacket;
import org.rscdaemon.server.util.Logger;

import org.apache.mina.common.IoSession;

public class AlertHandler implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();

	public void handlePacket(Packet p, IoSession session) throws Exception {
		long uID = ((LSPacket) p).getUID();
		Logger.event("LOGIN_SERVER sent alert (uID: " + uID + ")");
		Player player = world.getPlayer(p.readLong());
		if (player != null) {
			String message = p.readString();
			player.getActionSender().sendAlert(message, false);
		}
	}

}