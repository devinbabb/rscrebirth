package org.rscdaemon.server.packethandler;

import org.rscdaemon.server.net.Packet;
import org.apache.mina.common.IoSession;

/**
 * Generic Packet handler
 * 
 * @author Devin
 */

public interface PacketHandler {
	public void handlePacket(Packet p, IoSession session) throws Exception;
}
