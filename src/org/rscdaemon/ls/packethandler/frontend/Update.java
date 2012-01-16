package org.rscdaemon.ls.packethandler.frontend;

import org.rscdaemon.ls.packetbuilder.FPacketBuilder;
import org.rscdaemon.ls.packethandler.PacketHandler;
import org.rscdaemon.ls.Server;
import org.rscdaemon.ls.model.World;
import org.rscdaemon.ls.net.FPacket;
import org.rscdaemon.ls.net.Packet;

import org.apache.mina.common.IoSession;

/**
 * Handles the update packets
 * 
 * @author Devin
 */

public class Update implements PacketHandler {
	private static final FPacketBuilder builder = new FPacketBuilder();

	public void handlePacket(Packet p, IoSession session) throws Exception {
		String[] params = ((FPacket) p).getParameters();
		try {
			String reason = params[0];
			for (World w : Server.getServer().getWorlds()) {
				w.getActionSender().update(reason);
			}
			builder.setID(1);
		} catch (Exception e) {
			builder.setID(0);
		}
		FPacket packet = builder.toPacket();
		if (packet != null) {
			session.write(packet);
		}
	}

}