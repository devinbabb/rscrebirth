package org.rscdaemon.server;

import org.rscdaemon.server.event.DelayedEvent;
import org.rscdaemon.server.event.SingleEvent;
import org.rscdaemon.server.util.*;
import org.rscdaemon.server.net.*;
import org.rscdaemon.server.model.World;
import org.rscdaemon.server.model.Player;
import org.rscdaemon.server.net.RSCConnectionHandler;

import org.apache.mina.common.IoAcceptor;
import org.apache.mina.common.IoAcceptorConfig;
import org.apache.mina.transport.socket.nio.*;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * The entry point for RSC server.
 * 
 * @author Devin
 */
public class Server {
	/**
	 * World instance
	 */
	private static final World world = World.getWorld();
	/**
	 * The game engine
	 */
	private GameEngine engine;
	/**
	 * The SocketAcceptor
	 */
	private IoAcceptor acceptor;
	/**
	 * Update event - if the server is shutting down
	 */
	private DelayedEvent updateEvent, pvpEvent, duelingEvent;
	/**
	 * The login server connection
	 */
	private LoginConnector connector;
	/**
	 * Is the server running still?
	 */
	private boolean running;

	public static long START_TIME;

	public ConnectionThrottleFilter throttleFilter;

	public ConnectionThrottleFilter getThrottleFilter() {
		return throttleFilter;
	}

	public LoginConnector getLoginConnector() {
		return connector;
	}

	public boolean running() {
		return running;
	}

	/**
	 * Shutdown the server in 60 seconds
	 */
	public boolean shutdownForUpdate() {
		if (updateEvent != null) {
			return false;
		}
		updateEvent = new SingleEvent(null, 65000) {
			public void action() {
				kill();
			}
		};
		world.getDelayedEventHandler().add(updateEvent);
		return true;
	}

	public boolean pvpTimerStart(int time) {
		if (pvpEvent != null) {
			return false;
		}
		pvpEvent = new SingleEvent(null, time * 1000) {
			public void action() {
				for (Player p : world.getPlayers()) {
					p.getActionSender().sendMessage(
							"The PvP tournament has started!");
					if (world.getPvpEntry(p) && p.getLocation().inWaitingRoom()) {
						p.teleport(607, 706, false);
					}
				}
				duelingEvent();
			}
		};
		world.getDelayedEventHandler().add(pvpEvent);
		return true;
	}

	public boolean duelingEvent() {
		if (duelingEvent != null) {
			return false;
		}
		stopPvp();
		duelingEvent = new SingleEvent(null, 666666666) {
			public void action() {
				System.out
						.println("Shouldn't have reached here...Duel arena hackers.");
			}
		};
		world.getDelayedEventHandler().add(duelingEvent);
		return true;
	}

	public void stopPvp() {
		if (pvpEvent != null) {
			pvpEvent.stop();
			pvpEvent = null;
		}
	}

	public boolean pvpIsRunning() {
		if (duelingEvent != null) {
			return duelingEvent.isRunning();
		} else {
			return false;
		}
	}

	public boolean waitingIsRunning() {
		if (pvpEvent != null) {
			return pvpEvent.isRunning();
		} else {
			return false;
		}
	}

	public void stopDuel() {
		if (duelingEvent != null) {
			duelingEvent.stop();
			duelingEvent = null;
		}
		for (Player p : world.getPlayers()) {
			p.getActionSender().sendMessage(
					"The winner of the PvP tournament was: @cya@"
							+ world.getWinner().getUsername());
			p.getActionSender().sendMessage(
					"He won @gre@" + world.getJackPot() + "GP");
		}
	}

	/**
	 * MS till the server shuts down
	 */
	public int timeTillShutdown() {
		if (updateEvent == null) {
			return -1;
		}
		return updateEvent.timeTillNextRun();
	}

	public int timeTillPvp() {
		if (pvpEvent == null) {
			return -1;
		}
		return pvpEvent.timeTillNextRun();
	}

	public int timeTillDuel() {
		if (duelingEvent == null) {
			return -1;
		}
		return duelingEvent.timeTillNextRun();
	}

	/**
	 * Creates a new server instance, which in turn creates a new engine and
	 * prepares the server socket to accept connections.
	 */
	public Server() {
		running = true;
		world.setServer(this);
		try {
			connector = new LoginConnector();
			engine = new GameEngine();
			engine.start();
			while (!connector.isRegistered()) {
				Thread.sleep(100);
			}
			acceptor = new SocketAcceptor();
			IoAcceptorConfig config = new SocketAcceptorConfig();
			config.setDisconnectOnUnbind(true);
			((SocketSessionConfig) config.getSessionConfig())
					.setReuseAddress(true);
			throttleFilter = new ConnectionThrottleFilter(1000);
			acceptor.getFilterChain()
					.addFirst("throttleFilter", throttleFilter);
			acceptor.bind(new InetSocketAddress(Config.SERVER_IP,
					Config.SERVER_PORT), new RSCConnectionHandler(engine),
					config);
		} catch (Exception e) {
			Logger.error(e);
		}
	}

	/**
	 * Returns the game engine for this server
	 */
	public GameEngine getEngine() {
		return engine;
	}

	public boolean isInitialized() {
		return engine != null && connector != null;
	}

	/**
	 * Kills the game engine and irc engine
	 */
	public void kill() {
		Logger.print("Server shutting down...");
		running = false;
		engine.emptyWorld();
		connector.kill();
	}

	/**
	 * Unbinds the socket acceptor
	 */
	public void unbind() {
		try {
			acceptor.unbindAll();
		} catch (Exception e) {
		}
	}

	public static void main(String[] args) throws IOException {
		String configFile = "conf/server/Conf.xml";
		if (args.length > 0) {
			File f = new File(args[0]);
			if (f.exists()) {
				configFile = f.getName();
			}
		}
		START_TIME = System.currentTimeMillis();
		Logger.print("Server starting up...");
		Config.initConfig(configFile);
		new Server();
	}
}
