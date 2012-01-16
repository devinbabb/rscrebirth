package org.rscdaemon.ls.packethandler.frontend;

import org.rscdaemon.ls.packetbuilder.FPacketBuilder;
import org.rscdaemon.ls.packethandler.PacketHandler;
import org.rscdaemon.ls.Server;
import org.rscdaemon.ls.model.World;
import org.rscdaemon.ls.net.FPacket;
import org.rscdaemon.ls.net.Packet;

import org.apache.mina.common.IoSession;

/**
 * Handles actions globally
 * 
 * @author Devin
 */

public class Global implements PacketHandler {
	private static final FPacketBuilder builder = new FPacketBuilder();

	public void handlePacket(Packet p, IoSession session) throws Exception {
		String[] params = ((FPacket) p).getParameters();
		try {
			String message = params[0];
			for (World w : Server.getServer().getWorlds()) {
				w.getActionSender().alert(message);
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