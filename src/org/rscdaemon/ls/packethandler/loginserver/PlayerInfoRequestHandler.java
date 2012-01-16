package org.rscdaemon.ls.packethandler.loginserver;

import org.rscdaemon.ls.packethandler.PacketHandler;
import org.rscdaemon.ls.Server;
import org.rscdaemon.ls.model.World;
import org.rscdaemon.ls.net.LSPacket;
import org.rscdaemon.ls.net.Packet;
import org.rscdaemon.ls.packetbuilder.LSPacketBuilder;

import org.apache.mina.common.IoSession;

/**
 * Generic game entity packet handler
 * 
 * @author Devin
 */

public class PlayerInfoRequestHandler implements PacketHandler {

	public void handlePacket(Packet p, final IoSession session)
			throws Exception {
		final long uID = ((LSPacket) p).getUID();
		final long user = p.readLong();
		final World w = Server.getServer().findWorld(user);
		if (w == null) {
			LSPacketBuilder builder = new LSPacketBuilder();
			builder.setUID(uID);
			builder.addByte((byte) 0);
			session.write(builder.toPacket());
			return;
		}
		w.getActionSender().requestPlayerInfo(user, new PacketHandler() {
			public void handlePacket(Packet p, IoSession s) throws Exception {
				LSPacketBuilder builder = new LSPacketBuilder();
				builder.setUID(uID);
				if (p.readByte() == 0) {
					builder.addByte((byte) 0);
				} else {
					builder.addByte((byte) 1);
					builder.addShort(w == null ? 0 : w.getID());
					builder.addBytes(p.getRemainingData());
				}
				session.write(builder.toPacket());
			}
		});

	}

}