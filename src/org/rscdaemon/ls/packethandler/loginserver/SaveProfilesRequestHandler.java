package org.rscdaemon.ls.packethandler.loginserver;

import org.rscdaemon.ls.packethandler.PacketHandler;
import org.rscdaemon.ls.Server;
import org.rscdaemon.ls.model.World;
import org.rscdaemon.ls.model.PlayerSave;
import org.rscdaemon.ls.net.LSPacket;
import org.rscdaemon.ls.net.Packet;
import org.rscdaemon.ls.packetbuilder.loginserver.ReplyPacketBuilder;
import org.rscdaemon.ls.util.DataConversions;

import org.apache.mina.common.IoSession;

import java.util.Map.Entry;
import java.util.Iterator;

/**
 * Generic game entity packet handler
 * 
 * @author Devin
 */

public class SaveProfilesRequestHandler implements PacketHandler {
	private ReplyPacketBuilder builder = new ReplyPacketBuilder();

	public void handlePacket(Packet p, final IoSession session)
			throws Exception {
		final long uID = ((LSPacket) p).getUID();
		World world = (World) session.getAttachment();
		System.out.println("World " + world.getID()
				+ " requested we save all profiles");

		boolean success = true;
		// Iterator iterator = world.getAssosiatedSaves().iterator();
		// while(iterator.hasNext()) {
		// PlayerSave profile = ((Entry<Long,
		// PlayerSave>)iterator.next()).getValue();
		// profile.save();
		// iterator.remove();
		// }

		builder.setUID(uID);
		builder.setSuccess(success);

		LSPacket packet = builder.getPacket();
		if (packet != null) {
			session.write(packet);
		}
	}

}