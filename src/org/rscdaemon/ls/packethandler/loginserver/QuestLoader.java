package org.rscdaemon.ls.packethandler.loginserver;

import org.rscdaemon.ls.packethandler.PacketHandler;
import org.rscdaemon.ls.Server;
import org.rscdaemon.ls.model.World;
import org.rscdaemon.ls.net.LSPacket;
import org.rscdaemon.ls.net.Packet;
import org.rscdaemon.ls.util.DataConversions;
import org.rscdaemon.ls.packetbuilder.loginserver.ReplyPacketBuilder;
import org.rscdaemon.ls.packetbuilder.LSPacketBuilder;

import org.apache.mina.common.IoSession;

import java.sql.ResultSet;

/**
 * Generic game entity packet handler
 * 
 * @author Devin
 */

public class QuestLoader implements PacketHandler {
	private ReplyPacketBuilder builder = new ReplyPacketBuilder();

	public void handlePacket(Packet p, IoSession session) throws Exception {
		final long uID = ((LSPacket) p).getUID();
		long user = p.readLong();
		int questid = p.readShort();
		int stage = -1;
		boolean bool = false;

		// ResultSet result =
		// Server.db.getQuery("SELECT stage FROM `quests` WHERE `user`="+user +
		// " AND `quest_id`=" + questid);
		ResultSet result = Server.db
				.getQuery("SELECT stage FROM `quests` WHERE user=" + user
						+ " AND quest_id=" + questid);
		if (!result.next()) {
			// System.out.println(DataConversions.hashToUsername(user)+" did not do quest ID: "+questid);
			// System.out.println("No \"stage\" found in the MySQL...Making a faulty entry 8o|");
			stage = -1;
			bool = true;
		}
		// result.next(); // skip empty
		if (!bool) {
			stage = result.getInt("stage");
		}

		if (stage == -1 && bool) {
			LSPacketBuilder builder = new LSPacketBuilder();
			builder.setUID(uID);
			builder.addShort(stage);
			session.write(builder.toPacket());
			bool = false;
			// System.out.println("Hi.");
		}
		/*
		 * if(stage!=-1){ System.out.println(stage); }
		 */if (stage != -1) {
			LSPacketBuilder builder = new LSPacketBuilder();
			builder.setUID(uID);
			builder.addShort(stage);
			session.write(builder.toPacket());
			bool = false;
		}

	}

}