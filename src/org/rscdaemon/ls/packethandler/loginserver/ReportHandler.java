package org.rscdaemon.ls.packethandler.loginserver;

import org.rscdaemon.ls.packethandler.PacketHandler;
import org.rscdaemon.ls.Server;
import org.rscdaemon.ls.model.World;
import org.rscdaemon.ls.net.LSPacket;
import org.rscdaemon.ls.net.Packet;

import org.apache.mina.common.IoSession;

import java.sql.SQLException;

/**
 * Generic game entity packet handler
 * 
 * @author Devin
 */

public class ReportHandler implements PacketHandler {

	public void handlePacket(Packet p, IoSession session) throws Exception {
		World world = (World) session.getAttachment();

		final long user = p.readLong();
		final long reported = p.readLong();
		final byte reason = p.readByte();
		world.getActionSender().requestReportInfo(reported,
				new PacketHandler() {
					public void handlePacket(Packet p, IoSession session)
							throws Exception {
						int x = p.readShort();
						int y = p.readShort();
						String status = p.readString();
						try {
							Server.db
									.updateQuery("INSERT INTO `reports`(`from`, `about`, `time`, `reason`, `x`, `y`, `status`) VALUES('"
											+ user
											+ "', '"
											+ reported
											+ "', '"
											+ (System.currentTimeMillis() / 1000)
											+ "', '"
											+ reason
											+ "', '"
											+ x
											+ "', '"
											+ y
											+ "', '"
											+ status
											+ "')");
						} catch (SQLException e) {
							Server.error(e);
						}
					}
				});
	}

}