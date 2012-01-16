package org.rscdaemon.ls.packethandler.loginserver;

import org.rscdaemon.ls.packethandler.PacketHandler;
import org.rscdaemon.ls.Server;
import org.rscdaemon.ls.model.World;
import org.rscdaemon.ls.net.LSPacket;
import org.rscdaemon.ls.net.Packet;
import org.rscdaemon.ls.util.DataConversions;
import org.rscdaemon.ls.packetbuilder.loginserver.PlayerLoginPacketBuilder;
import org.rscdaemon.ls.net.Ipban;

import org.apache.mina.common.IoSession;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map.Entry;

/**
 * Generic game entity packet handler
 * 
 * @author Devin
 */

public class PlayerLoginHandler implements PacketHandler {
	private PlayerLoginPacketBuilder builder = new PlayerLoginPacketBuilder();

	public void handlePacket(Packet p, IoSession session) throws Exception {
		final long uID = ((LSPacket) p).getUID();
		World world = (World) session.getAttachment();
		long user = p.readLong();
		String ip = DataConversions.IPToString(p.readLong());
		String pass = p.readString(32).trim();
		String className = p.readString();
		byte loginCode = validatePlayer(user, pass, ip);

		builder.setUID(uID);
		if (loginCode == 0 || loginCode == 1 || loginCode == 99) {
			builder.setPlayer(Server.getServer().findSave(user, world),
					loginCode);
			world.registerPlayer(user, ip);
		} else {
			builder.setPlayer(null, loginCode);
		}

		LSPacket packet = builder.getPacket();
		if (packet != null) {
			session.write(packet);
		}
	}

	private byte validatePlayer(long user, String pass, String ip) {
		Server server = Server.getServer();
		if (Ipban.ipbans.contains(ip)) {
			return 9;
		}
		byte returnVal = 0;

		try {
			ResultSet result = Server.db
					.getQuery("SELECT pass, banned, group_id, username FROM `players` WHERE `username`=\""
							+ DataConversions.hashToUsername(user) + "\"");
			if (!result.next()
					|| !pass.equalsIgnoreCase(result.getString("pass"))) {
				return 2;
			}

			if (result.getInt("banned") == 1) {
				return 6;
			}

			if (result.getInt("group_id") == 1
					|| result.getInt("group_id") == 2) {
				returnVal = 99;
			}
			for (World w : server.getWorlds()) {
				for (Entry<Long, Integer> player : w.getPlayers()) {
					if (player.getKey() == user) {
						return 3;
					}
				}
				if (w.hasPlayer(user)) {
					return 3;
				}
			}
			return returnVal;
		} catch (SQLException e) {
			System.out.println("Exception in PlayerLoginHandler      " + e);
			return 7;
		}
	}
}