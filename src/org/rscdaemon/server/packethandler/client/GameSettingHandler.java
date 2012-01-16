package org.rscdaemon.server.packethandler.client;

import org.rscdaemon.server.packethandler.PacketHandler;
import org.rscdaemon.server.packetbuilder.loginserver.GameSettingUpdatePacketBuilder;
import org.rscdaemon.server.model.World;
import org.rscdaemon.server.model.Player;
import org.rscdaemon.server.net.Packet;
import org.rscdaemon.server.net.LSPacket;
import org.apache.mina.common.IoSession;

public class GameSettingHandler implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();
	private GameSettingUpdatePacketBuilder builder = new GameSettingUpdatePacketBuilder();

	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player) session.getAttachment();
		int idx = (int) p.readByte();
		if (idx < 0 || idx > 6) {
			player.setSuspiciousPlayer(true);
			return;
		}
		boolean on = p.readByte() == 1;
		player.setGameSetting(idx, on);

		builder.setPlayer(player);
		builder.setIndex(idx);
		builder.setOn(on);

		LSPacket packet = builder.getPacket();
		if (packet != null) {
			world.getServer().getLoginConnector().getSession().write(packet);
		}
	}

}
