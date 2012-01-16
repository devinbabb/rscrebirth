package org.rscdaemon.server.packethandler.client;

import org.rscdaemon.server.packethandler.PacketHandler;
import org.rscdaemon.server.packetbuilder.loginserver.PrivacySettingUpdatePacketBuilder;
import org.rscdaemon.server.model.*;
import org.rscdaemon.server.net.Packet;
import org.rscdaemon.server.net.LSPacket;

import org.apache.mina.common.IoSession;

public class PrivacySettingHandler implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();
	private PrivacySettingUpdatePacketBuilder builder = new PrivacySettingUpdatePacketBuilder();

	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player) session.getAttachment();

		boolean[] newSettings = new boolean[4];
		for (int i = 0; i < 4; i++) {
			newSettings[i] = p.readByte() == 1;
		}

		builder.setPlayer(player);
		for (int i = 0; i < 4; i++) {
			builder.setIndex(i);
			if (newSettings[i] && !player.getPrivacySetting(i)) {
				builder.setOn(true);
			} else if (!newSettings[i] && player.getPrivacySetting(i)) {
				builder.setOn(false);
			} else {
				continue;
			}
			LSPacket packet = builder.getPacket();
			if (packet != null) {
				world.getServer().getLoginConnector().getSession()
						.write(packet);
			}
		}

		for (int i = 0; i < 4; i++) {
			player.setPrivacySetting(i, newSettings[i]);
		}
	}

}
