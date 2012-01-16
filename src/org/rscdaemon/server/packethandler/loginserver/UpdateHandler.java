package org.rscdaemon.server.packethandler.loginserver;

import org.rscdaemon.server.packethandler.PacketHandler;
import org.rscdaemon.server.model.World;
import org.rscdaemon.server.model.Player;
import org.rscdaemon.server.net.Packet;
import org.rscdaemon.server.net.LSPacket;
import org.rscdaemon.server.util.Logger;

import org.apache.mina.common.IoSession;

public class UpdateHandler implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();

	public void handlePacket(Packet p, IoSession session) throws Exception {
		long uID = ((LSPacket) p).getUID();
		Logger.event("LOGIN_SERVER sent update (uID: " + uID + ")");
		String reason = p.readString();
		if (world.getServer().shutdownForUpdate()) {
			for (Player player : world.getPlayers()) {
				player.getActionSender().sendAlert(
						"The server will be shutting down in 60 seconds: "
								+ reason, false);
				player.getActionSender().startShutdown(60);
			}
		}
	}

}