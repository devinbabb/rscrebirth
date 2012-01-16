package org.rscdaemon.server.packethandler.loginserver;

import org.rscdaemon.server.packethandler.PacketHandler;
import org.rscdaemon.server.model.World;
import org.rscdaemon.server.model.Player;
import org.rscdaemon.server.net.Packet;
import org.rscdaemon.server.net.LSPacket;
import org.apache.mina.common.IoSession;

public class FriendLogout implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();

	public void handlePacket(Packet p, IoSession session) throws Exception {
		long uID = ((LSPacket) p).getUID();
		long friend = p.readLong();

		switch (((LSPacket) p).getID()) {
		case 12:
			for (Player player : world.getPlayers()) {
				if (player.isFriendsWith(friend)) {
					player.getActionSender().sendFriendUpdate(friend, 0);
				}
			}
			break;
		case 13:
			Player player = world.getPlayer(p.readLong());
			if (player != null) {
				player.getActionSender().sendFriendUpdate(friend, 0);
			}
			break;
		}
	}

}