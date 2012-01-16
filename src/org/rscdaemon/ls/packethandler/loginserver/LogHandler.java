package org.rscdaemon.ls.packethandler.loginserver;

import org.rscdaemon.ls.packethandler.PacketHandler;
import org.rscdaemon.ls.Server;
import org.rscdaemon.ls.net.LSPacket;
import org.rscdaemon.ls.net.Packet;
import org.rscdaemon.ls.util.Config;

import org.apache.mina.common.IoSession;

import java.text.SimpleDateFormat;
import java.io.File;
import java.io.PrintWriter;

/**
 * Generic game entity packet handler
 * 
 * @author Devin
 */

public class LogHandler implements PacketHandler {
	private static final SimpleDateFormat formatter = new SimpleDateFormat(
			"HH:mm:ss dd-MM-yy");

	private static PrintWriter event;
	private static PrintWriter error;
	private static PrintWriter mod;

	static {
		try {
			event = new PrintWriter(new File(Config.LOG_DIR, "event.log"));
			error = new PrintWriter(new File(Config.LOG_DIR, "error.log"));
			mod = new PrintWriter(new File(Config.LOG_DIR, "mod.log"));
		} catch (Exception e) {
			Server.error(e);
		}
	}

	private static String getDate() {
		return formatter.format(System.currentTimeMillis());
	}

	public void handlePacket(Packet p, IoSession session) throws Exception {
		byte type = p.readByte();
		String message = getDate() + ": " + p.readString();
		switch (type) {
		case 1:
			event.println(message);
			event.flush();
			break;
		case 2:
			error.println(message);
			error.flush();
			break;
		case 3:
			mod.println(message);
			mod.flush();
			break;
		}
	}

}