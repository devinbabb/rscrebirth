package org.rscdaemon.server.packethandler.loginserver;

import org.rscdaemon.server.packethandler.PacketHandler;
import org.rscdaemon.server.model.World;
import org.rscdaemon.server.model.Player;
import org.rscdaemon.server.net.Packet;
import org.rscdaemon.server.net.LSPacket;
import org.apache.mina.common.IoSession;

public class ReceivePM implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();

	public void handlePacket(Packet p, IoSession session) throws Exception {
		long uID = ((LSPacket) p).getUID();
		long sender = p.readLong();
		Player recipient = world.getPlayer(p.readLong());
		boolean avoidBlock = p.readByte() == 1;
		if (recipient == null || !recipient.loggedIn()) {
			return;
		}
		if (recipient.getPrivacySetting(1) && !recipient.isFriendsWith(sender)
				&& !avoidBlock) {
			return;
		}
		if (recipient.isIgnoring(sender) && !avoidBlock) {
			return;
		}
		recipient.getActionSender().sendPrivateMessage(sender,
				p.getRemainingData());
	}

}