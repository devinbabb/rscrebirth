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

public class BanHandler implements PacketHandler {
	private ReplyPacketBuilder builder = new ReplyPacketBuilder();

	public void handlePacket(Packet p, IoSession session) throws Exception {
		final long uID = ((LSPacket) p).getUID();
		boolean banned = ((LSPacket) p).getID() == 4;
		long user = p.readLong();
		String username = DataConversions.hashToUsername(user);
		ResultSet result = Server.db
				.getQuery("SELECT playermod FROM `players` WHERE `user`="
						+ user);
		if (!result.next()) {
			builder.setSuccess(false);
			builder.setReply("There is not an account by that username");
		} else if (Server.db.updateQuery("UPDATE `players` SET `banned`='"
				+ (banned ? "1" : "0") + "' WHERE `username`=\""
				+ DataConversions.hashToUsername(user) + "\"") == 0) {
			builder.setSuccess(false);
			builder.setReply("There is not an account by that username");
		} else {
			World w = Server.getServer().findWorld(user);
			if (w != null) {
				w.getActionSender().logoutUser(user);
			}
			builder.setSuccess(true);
			builder.setReply(DataConversions.hashToUsername(user)
					+ " has been " + (banned ? "banned" : "unbanned"));
		}
		builder.setUID(uID);

		LSPacket temp = builder.getPacket();
		if (temp != null) {
			session.write(temp);
		}

	}

}