package org.rscdaemon.server.packethandler.client;

import org.rscdaemon.server.packethandler.PacketHandler;
import org.rscdaemon.server.model.World;
import org.rscdaemon.server.net.Packet;
import org.rscdaemon.server.model.Player;
import org.apache.mina.common.IoSession;

public class PlayerLogoutRequest implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();

	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player) session.getAttachment();
		if (player.getLocation().inWaitingRoom()
				|| player.getLocation().inDuelArena()) {
			player.getActionSender().sendMessage(
					"You must leave the PvP area's to logout.");
		}
		if (player.canLogout() && !player.getLocation().inWaitingRoom()
				&& !player.getLocation().inDuelArena()) {
			player.destroy(true);
		} else {
			player.getActionSender().sendCantLogout();
		}
	}
}
