package org.rscdaemon.server.packethandler.loginserver;

import org.rscdaemon.server.packethandler.PacketHandler;
import org.rscdaemon.server.packetbuilder.loginserver.PlayerInfoRequestPacketBuilder;
import org.rscdaemon.server.model.World;
import org.rscdaemon.server.model.Player;
import org.rscdaemon.server.net.Packet;
import org.rscdaemon.server.net.LSPacket;
import org.rscdaemon.server.util.Logger;

import org.apache.mina.common.IoSession;

public class PlayerInfoRequestHandler implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();
	private PlayerInfoRequestPacketBuilder builder = new PlayerInfoRequestPacketBuilder();

	public void handlePacket(Packet p, IoSession session) throws Exception {
		long uID = ((LSPacket) p).getUID();
		Logger.event("LOGIN_SERVER requested player information (uID: " + uID
				+ ")");
		builder.setUID(uID);
		builder.setPlayer(world.getPlayer(p.readLong()));
		LSPacket temp = builder.getPacket();
		if (temp != null) {
			session.write(temp);
		}
	}

}