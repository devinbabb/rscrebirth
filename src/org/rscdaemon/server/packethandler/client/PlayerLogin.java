package org.rscdaemon.server.packethandler.client;

import org.rscdaemon.server.packethandler.PacketHandler;
import org.rscdaemon.server.model.Player;
import org.rscdaemon.server.model.World;
import org.rscdaemon.server.net.Packet;
import org.rscdaemon.server.net.RSCPacket;
import org.rscdaemon.server.packetbuilder.RSCPacketBuilder;
import org.rscdaemon.server.util.DataConversions;
import org.rscdaemon.server.util.Config;
import org.apache.mina.common.IoSession;

public class PlayerLogin implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();

	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player) session.getAttachment();
		byte loginCode;
		try {
			boolean reconnecting = (p.readByte() == 1);
			int clientVersion = p.readShort();
			RSCPacket loginPacket = DataConversions.decryptRSA(p.readBytes(p
					.readByte()));
			int[] sessionKeys = new int[4];
			for (int key = 0; key < sessionKeys.length; key++) {
				sessionKeys[key] = loginPacket.readInt();
			}
			int uid = loginPacket.readInt();
			String username = loginPacket.readString(20).trim();
			loginPacket.skip(1);
			String password = loginPacket.readString(20).trim();
			loginPacket.skip(1);
			if (world.countPlayers() >= Config.MAX_PLAYERS) {
				loginCode = 10;
			} else if (clientVersion != Config.SERVER_VERSION) {
				loginCode = 4;
			} else if (!player.setSessionKeys(sessionKeys)) {
				loginCode = 5;
			} else {
				player.load(username, password, uid, reconnecting);
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			loginCode = 7;
		}
		RSCPacketBuilder pb = new RSCPacketBuilder();
		pb.setBare(true);
		pb.addByte((byte) loginCode);
		session.write(pb.toPacket());
		player.destroy(true);
	}

}
