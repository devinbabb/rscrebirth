package org.rscdaemon.ls.packetbuilder.loginserver;

import org.rscdaemon.ls.packetbuilder.LSPacketBuilder;
import org.rscdaemon.ls.Server;
import org.rscdaemon.ls.LoginEngine;
import org.rscdaemon.ls.packethandler.PacketHandler;
import org.rscdaemon.ls.net.LSPacket;

import java.util.List;
import java.util.ArrayList;

public class MiscPacketBuilder {
	/**
	 * List of packets waiting to be sent to the world
	 */
	private List<LSPacket> packets = new ArrayList<LSPacket>();
	/**
	 * LoginEngine
	 */
	private LoginEngine engine = Server.getServer().getEngine();

	/**
	 * Gets a List of new packets since the last update
	 */
	public List<LSPacket> getPackets() {
		return packets;
	}

	/**
	 * Clears old packets that have already been sent
	 */
	public void clearPackets() {
		packets.clear();
	}

	public void requestStats(PacketHandler handler) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(1);
		s.setHandler(engine, handler);
		packets.add(s.toPacket());
	}

	public void playerListRequest(PacketHandler handler) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(2);
		s.setHandler(engine, handler);
		packets.add(s.toPacket());
	}

	public void shutdown() {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(3);
		packets.add(s.toPacket());
	}

	public void update(String reason) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(4);
		s.addBytes(reason.getBytes());
		packets.add(s.toPacket());
	}

	public void alert(String message) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(5);
		s.addBytes(message.getBytes());
		packets.add(s.toPacket());
	}

	public void alert(long user, String message) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(6);
		s.addLong(user);
		s.addBytes(message.getBytes());
		packets.add(s.toPacket());
	}

	public void logoutUser(long user) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(7);
		s.addLong(user);
		packets.add(s.toPacket());
	}

	public void requestReportInfo(long user, PacketHandler handler) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(8);
		s.addLong(user);
		s.setHandler(engine, handler);
		packets.add(s.toPacket());
	}

	public void requestPlayerInfo(long user, PacketHandler handler) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(9);
		s.addLong(user);
		s.setHandler(engine, handler);
		packets.add(s.toPacket());
	}

	public void sendPM(long user, long friend, boolean avoidBlock,
			byte[] message) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(10);
		s.addLong(user);
		s.addLong(friend);
		s.addByte((byte) (avoidBlock ? 1 : 0));
		s.addBytes(message);
		packets.add(s.toPacket());
	}

	public void friendLogin(long user, long friend, int w) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(11);
		s.addLong(user);
		s.addLong(friend);
		s.addShort(w);
		packets.add(s.toPacket());
	}

	public void friendLogout(long friend) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(12);
		s.addLong(friend);
		packets.add(s.toPacket());
	}

	public void friendLogout(long user, long friend) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(13);
		s.addLong(friend);
		s.addLong(user);
		packets.add(s.toPacket());
	}

}