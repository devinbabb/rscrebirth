package org.rscdaemon.server;

import org.rscdaemon.server.net.LSPacket;
import org.rscdaemon.server.net.PacketQueue;
import org.rscdaemon.server.packethandler.PacketHandlerDef;
import org.rscdaemon.server.packethandler.PacketHandler;
import org.rscdaemon.server.util.Config;
import org.rscdaemon.server.util.Logger;
import org.rscdaemon.server.util.PersistenceManager;
import org.rscdaemon.server.packetbuilder.loginserver.MiscPacketBuilder;
import org.rscdaemon.server.net.LSConnectionHandler;

import org.apache.mina.common.ConnectFuture;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.IoHandler;
import org.apache.mina.transport.socket.nio.*;

import java.net.InetSocketAddress;
import java.util.TreeMap;
import java.util.List;

/**
 * Control connection to login server
 * 
 * @author Devin
 */

public class LoginConnector {
	/**
	 * The packet queue to be processed
	 */
	private PacketQueue<LSPacket> packetQueue;
	/**
	 * The mapping of packet IDs to their handler
	 */
	private TreeMap<Integer, PacketHandler> packetHandlers = new TreeMap<Integer, PacketHandler>();
	/**
	 * IoSession
	 */
	private IoSession session;
	/**
	 * Should we be running?
	 */
	private boolean running = true;
	/**
	 * World registered
	 */
	private boolean registered = false;
	/**
	 * The mapping of packet UIDs to their handler
	 */
	private TreeMap<Long, PacketHandler> uniqueHandlers = new TreeMap<Long, PacketHandler>();
	/**
	 * A packet builder
	 */
	private MiscPacketBuilder actionSender = new MiscPacketBuilder(this);
	/**
	 * Connection Handler
	 */
	private IoHandler connectionHandler = new LSConnectionHandler(this);
	/**
	 * Connection attempts
	 */
	private int connectionAttempts = 0;

	public LoginConnector() {
		packetQueue = new PacketQueue<LSPacket>();
		loadPacketHandlers();
		reconnect();
	}

	public boolean reconnect() {
		try {
			Logger.print("Attempting to connect to LS");
			SocketConnector conn = new SocketConnector();
			SocketConnectorConfig config = new SocketConnectorConfig();
			((SocketSessionConfig) config.getSessionConfig())
					.setKeepAlive(true);
			((SocketSessionConfig) config.getSessionConfig())
					.setTcpNoDelay(true);
			ConnectFuture future = conn.connect(new InetSocketAddress(
					Config.LS_IP, Config.LS_PORT), connectionHandler, config);
			future.join(3000);
			if (future.isConnected()) {
				session = future.getSession();
				Logger.print("Registering world (" + Config.SERVER_NUM
						+ ") with LS");
				actionSender.registerWorld();
				connectionAttempts = 0;
				return true;
			}
			if (connectionAttempts++ >= 100) {
				Logger.print("Unable to connect to LS, giving up after "
						+ connectionAttempts + " tries");
				System.exit(1);
				return false;
			}
			return reconnect();

		} catch (Exception e) {
			Logger.print("Error connecting to LS: " + e.getMessage());
			return false;
		}
	}

	public IoSession getSession() {
		return session;
	}

	public void kill() {
		running = false;
		Logger.print("Unregistering world (" + Config.SERVER_NUM + ") with LS");
		actionSender.unregisterWorld();
	}

	public MiscPacketBuilder getActionSender() {
		return actionSender;
	}

	public boolean running() {
		return running;
	}

	public boolean isRegistered() {
		return registered;
	}

	public PacketQueue getPacketQueue() {
		return packetQueue;
	}

	public void setRegistered(boolean registered) {
		if (registered) {
			this.registered = true;
			Logger.print("World successfully registered with LS");
		} else {
			Logger.error(new Exception("Error registering world"));
		}
	}

	private void loadPacketHandlers() {
		PacketHandlerDef[] handlerDefs = (PacketHandlerDef[]) PersistenceManager
				.load("LSPacketHandlers.xml");
		for (PacketHandlerDef handlerDef : handlerDefs) {
			try {
				String className = handlerDef.getClassName();
				Class c = Class.forName(className);
				if (c != null) {
					PacketHandler handler = (PacketHandler) c.newInstance();
					for (int packetID : handlerDef.getAssociatedPackets()) {
						packetHandlers.put(packetID, handler);
					}
				}
			} catch (Exception e) {
				Logger.error(e);
			}
		}
	}

	public void setHandler(long uID, PacketHandler handler) {
		uniqueHandlers.put(uID, handler);
	}

	public void processIncomingPackets() {
		for (LSPacket p : packetQueue.getPackets()) {
			PacketHandler handler;
			if (((handler = uniqueHandlers.get(p.getUID())) != null)
					|| ((handler = packetHandlers.get(p.getID())) != null)) {
				try {
					handler.handlePacket(p, session);
					uniqueHandlers.remove(p.getUID());
				} catch (Exception e) {
					Logger.error("Exception with p[" + p.getID()
							+ "] from LOGIN_SERVER: " + e.getMessage());
				}
			} else {
				Logger.error("Unhandled packet from LS: " + p.getID());
			}
		}
	}

	public void sendQueuedPackets() {
		List<LSPacket> packets = actionSender.getPackets();
		for (LSPacket packet : packets) {
			session.write(packet);
		}
		actionSender.clearPackets();
	}

}