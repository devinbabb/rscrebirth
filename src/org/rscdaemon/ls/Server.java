package org.rscdaemon.ls;

import org.rscdaemon.ls.model.World;
import org.rscdaemon.ls.model.PlayerSave;
import org.rscdaemon.ls.net.DatabaseConnection;
import org.rscdaemon.ls.net.LSConnectionHandler;
import org.rscdaemon.ls.net.FConnectionHandler;
import org.rscdaemon.ls.util.Config;
import org.rscdaemon.ls.util.DataConversions;
import org.rscdaemon.ls.net.Ipban;

import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoAcceptor;
import org.apache.mina.common.IoAcceptorConfig;
import org.apache.mina.transport.socket.nio.*;

import java.io.File;
import java.util.Collection;
import java.util.TreeMap;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Starts the login engine
 * 
 * @author Devin
 */

public class Server {
	/**
	 * Connection to the MySQL database
	 */
	public static DatabaseConnection db;
	/**
	 * The login engine
	 */
	private LoginEngine engine;
	/**
	 * The Server SocketAcceptor
	 */
	private IoAcceptor serverAcceptor;
	/**
	 * The Server SocketAcceptor
	 */
	private IoAcceptor frontendAcceptor;

	private static Server server;

	private TreeMap<Integer, World> worlds = new TreeMap<Integer, World>();
	private TreeMap<Integer, World> idleWorlds = new TreeMap<Integer, World>();

	public static Server getServer() {
		if (server == null) {
			server = new Server();
		}
		return server;
	}

	private Server() {
		try {
			engine = new LoginEngine(this);
			engine.start();
			serverAcceptor = createListener(Config.LS_IP, Config.LS_PORT,
					new LSConnectionHandler(engine));
			frontendAcceptor = createListener(Config.QUERY_IP,
					Config.QUERY_PORT, new FConnectionHandler(engine));
		} catch (IOException e) {
			Server.error(e);
		}
	}

	private IoAcceptor createListener(String ip, int port, IoHandler handler)
			throws IOException {
		IoAcceptor acceptor = new SocketAcceptor();
		IoAcceptorConfig config = new SocketAcceptorConfig();
		config.setDisconnectOnUnbind(true);
		((SocketSessionConfig) config.getSessionConfig()).setReuseAddress(true);
		acceptor.bind(new InetSocketAddress(ip, port), handler, config);
		return acceptor;
	}

	public PlayerSave findSave(long user, World world) {
		PlayerSave save = null;
		// for(World w : getWorlds()) {
		// PlayerSave s = w.getSave(user);
		// if(s != null) {
		// w.unassosiateSave(s);
		// save = s;
		// System.out.println("Found cached save for " +
		// DataConversions.hashToUsername(user));
		// break;
		// }
		// }
		// if(save == null) {
		// System.out.println("No save found for " +
		// DataConversions.hashToUsername(user) + ", loading fresh");
		save = PlayerSave.loadPlayer(user);
		// }
		// world.assosiateSave(save);
		return save;
	}

	public World findWorld(long user) {
		for (World w : getWorlds()) {
			if (w.hasPlayer(user)) {
				return w;
			}
		}
		return null;
	}

	public World getIdleWorld(int id) {
		return idleWorlds.get(id);
	}

	public void setIdle(World world, boolean idle) {
		if (idle) {
			worlds.remove(world.getID());
			idleWorlds.put(world.getID(), world);
		} else {
			idleWorlds.remove(world.getID());
			worlds.put(world.getID(), world);
		}
	}

	public Collection<World> getWorlds() {
		return worlds.values();
	}

	public World getWorld(int id) {
		if (id < 0) {
			return null;
		}
		return worlds.get(id);
	}

	public boolean isRegistered(World world) {
		return getWorld(world.getID()) != null;
	}

	public boolean registerWorld(World world) {
		int id = world.getID();
		if (id < 0 || getWorld(id) != null) {
			return false;
		}
		worlds.put(id, world);
		return true;
	}

	public boolean unregisterWorld(World world) {
		int id = world.getID();
		if (id < 0) {
			return false;
		}
		if (getWorld(id) != null) {
			worlds.remove(id);
			return true;
		}
		if (getIdleWorld(id) != null) {
			idleWorlds.remove(id);
			return true;
		}
		return false;
	}

	public LoginEngine getEngine() {
		return engine;
	}

	public void kill() {
		try {
			serverAcceptor.unbindAll();
			frontendAcceptor.unbindAll();
			db.close();
		} catch (Exception e) {
			Server.error(e);
		}
	}

	public static void error(Object o) {
		if (o instanceof Exception) {
			Exception e = (Exception) o;
			e.printStackTrace();
			System.exit(1);
			return;
		}
		System.err.println(o.toString());
	}

	public static void main(String[] args) throws IOException {
		Ipban.loadIPBans();
		String configFile = "conf/ls/Conf.xml";
		if (args.length > 0) {
			File f = new File(args[0]);
			if (f.exists()) {
				configFile = f.getName();
			}
		}
		System.out.println("Login Server starting up...");
		Config.initConfig(configFile);
		db = new DatabaseConnection();
		System.out.println("Connected to MySQL");
		Server.getServer();
	}
}