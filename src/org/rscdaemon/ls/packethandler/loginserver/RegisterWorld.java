package org.rscdaemon.ls.packethandler.loginserver;

import org.rscdaemon.ls.packethandler.PacketHandler;
import org.rscdaemon.ls.Server;
import org.rscdaemon.ls.packetbuilder.loginserver.WorldRegisteredPacketBuilder;
import org.rscdaemon.ls.model.World;
import org.rscdaemon.ls.net.LSPacket;
import org.rscdaemon.ls.net.Packet;
import org.rscdaemon.ls.util.DataConversions;
import org.rscdaemon.ls.util.Config;

import org.apache.mina.common.IoSession;

/**
 * Generic game entity packet handler
 * 
 * @author Devin
 */

public class RegisterWorld implements PacketHandler {
	private WorldRegisteredPacketBuilder builder = new WorldRegisteredPacketBuilder();

	public void handlePacket(Packet p, IoSession session) throws Exception {
		final long uID = ((LSPacket) p).getUID();
		builder.setUID(uID);
		builder.setSuccess(false);

		Server server = Server.getServer();
		if (((LSPacket) p).getID() == 1) {
			int id = p.readShort();
			String passrecv = p.readString().trim();
			if (server.getWorld(id) == null && passrecv != null) {
				if (!passrecv.equals(Config.LS_PASSWORD)) {
					System.out.println("Failed to authenticate world " + id);
					return;
				}
				World world = server.getIdleWorld(id);
				if (world == null) {
					world = new World(id, session);
					server.registerWorld(world);
					System.out.println("Registering world: " + id);
				} else {
					world.setSession(session);
					server.setIdle(world, false);
					System.out.println("Reattached to world " + id);
				}
				int playerCount = p.readShort();
				for (int i = 0; i < playerCount; i++) {
					world.registerPlayer(p.readLong(),
							DataConversions.IPToString(p.readLong()));
				}
				session.setAttachment(world);
				builder.setSuccess(true);
			}
		} else {
			World world = (World) session.getAttachment();

			server.unregisterWorld(world);
			System.out.println("UnRegistering world: " + world.getID());
			session.setAttachment(null);
			builder.setSuccess(true);
		}

		LSPacket temp = builder.getPacket();
		if (temp != null) {
			session.write(temp);
		}
	}

}