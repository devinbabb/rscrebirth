package org.rscdaemon.ls.packethandler.loginserver;

import org.rscdaemon.ls.packethandler.PacketHandler;
import org.rscdaemon.ls.Server;
import org.rscdaemon.ls.model.World;
import org.rscdaemon.ls.net.LSPacket;
import org.rscdaemon.ls.net.Packet;
import org.rscdaemon.ls.util.DataConversions;
import org.rscdaemon.ls.packetbuilder.loginserver.ReplyPacketBuilder;

import org.apache.mina.common.IoSession;

import java.sql.ResultSet;

/**
 * Generic game entity packet handler
 * 
 * @author Devin
 */

public class QuestHandler implements PacketHandler {
	private ReplyPacketBuilder builder = new ReplyPacketBuilder();

	public void handlePacket(Packet p, IoSession session) throws Exception {
		final long uID = ((LSPacket) p).getUID();
		long user = p.readLong();
		int questid = p.readShort();
		int stage = p.readShort();

		Server.db
				.updateQuery("INSERT INTO `quests`(`quest_id`, `stage`, `user`) VALUES('"
						+ questid + "', '" + stage + "', '" + user + "')");
		// System.out.println("SUCCESS!!!\tquestid="+questid+"\tstage="+stage+"\tusernamehash="+user);

	}

}