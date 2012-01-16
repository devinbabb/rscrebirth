package org.rscdaemon.server;

import org.rscdaemon.server.net.PacketQueue;
import org.rscdaemon.server.net.RSCPacket;
import org.rscdaemon.server.packethandler.PacketHandler;
import org.rscdaemon.server.packethandler.PacketHandlerDef;
import org.rscdaemon.server.util.PersistenceManager;
import org.rscdaemon.server.model.Player;
import org.rscdaemon.server.model.World;
import org.rscdaemon.server.model.Shop;
import org.rscdaemon.server.util.Logger;
import org.rscdaemon.server.event.DelayedEvent;
import org.rscdaemon.server.quest.*;
import org.rscdaemon.server.entityhandling.EntityHandler;
import org.rscdaemon.server.util.Config;
//import org.rscdaemon.server.net.DBConnection;
//import java.sql.ResultSet;
//import java.sql.SQLException;
import org.rscdaemon.server.entityhandling.defs.extras.AdvertDef;

import org.apache.mina.common.IoSession;

import java.util.TreeMap;

/**
 * The central motor of the game. This class is responsible for the primary
 * operation of the entire game.
 * 
 * @author Devin
 */
public final class GameEngine extends Thread {
	/**
	 * Connection to the MySQL database
	 */

	/*
	 * public static DBConnection db; public static String news = ""; public
	 * static String getNews() { return news; }
	 */

	/**
	 * World instance
	 */
	int curAdvert = -1;
	private static final World world = World.getWorld();
	/**
	 * The packet queue to be processed
	 */
	private PacketQueue<RSCPacket> packetQueue;
	/**
	 * Whether the engine's thread is running
	 */
	private boolean running = true;
	/**
	 * The mapping of packet IDs to their handler
	 */
	private TreeMap<Integer, PacketHandler> packetHandlers = new TreeMap<Integer, PacketHandler>();
	/**
	 * Responsible for updating all connected clients
	 */
	private ClientUpdater clientUpdater = new ClientUpdater();
	/**
	 * Handles delayed events rather than events to be ran every iteration
	 */
	private DelayedEventHandler eventHandler = new DelayedEventHandler();
	/**
	 * Adverts
	 */
	private long lastAdvert = 0;
	/**
	 * When the update loop was last ran, required for throttle
	 */
	private long lastSentClientUpdate = 0;

	/**
	 * Constructs a new game engine with an empty packet queue.
	 */
	public GameEngine() {
		packetQueue = new PacketQueue<RSCPacket>();
		QuestLoader.initClasses();
		loadPacketHandlers();
		for (Shop shop : world.getShops()) {
			shop.initRestock();
		}
		lastAdvert = System.currentTimeMillis();
	}

	/**
	 * The thread execution process.
	 */
	public void run() {
		Logger.print("GameEngine now running");
		int curAdvert = -1;
		// Get news
		/*
		 * try { db = new DBConnection(); ResultSet result; result =
		 * db.getQuery("SELECT news from news ORDER BY RAND() LIMIT 1");
		 * if(result.next()) { news = result.getString("news"); } db.close(); }
		 * catch(SQLException e){
		 * System.out.println("MySQL error while retriving news:");
		 * System.err.println(e); }
		 */

		eventHandler.add(new DelayedEvent(null, 900000) { // 15mins
					public void run() {
						// Update news
						/*
						 * try { db = new DBConnection(); ResultSet result;
						 * result = db.getQuery(
						 * "SELECT news from news ORDER BY RAND() LIMIT 1");
						 * if(result.next()) { news = result.getString("news");
						 * } db.close(); } catch(SQLException e){
						 * System.out.println
						 * ("MySQL error while retriving news:");
						 * System.err.println(e); }
						 */

						for (Player p : world.getPlayers()) {
							p.save();
						}
						world.getServer().getLoginConnector().getActionSender()
								.saveProfiles();
					}
				});
		while (running) {
			try {
				long curTime = System.currentTimeMillis();
				if (curTime - lastAdvert >= 93000) // Display an advert every
													// 3.5 mins.
				{
					lastAdvert = curTime;

					curAdvert++;

					if (curAdvert >= EntityHandler.getAdverts().length
							|| curAdvert < 0)
						curAdvert = 0;

					AdvertDef advertDef = EntityHandler.getAdverts()[curAdvert];
					String advert = advertDef.getMessage();

					for (Player p : world.getPlayers()) {
						p.getActionSender().sendMessage(
								"@whi@" + processAdvert(advert, p));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// }

			try {
				Thread.sleep(50);
			} catch (InterruptedException ie) {
			}
			processLoginServer();
			processIncomingPackets();
			processEvents();
			processClients();
		}
	}

	private static String processAdvert(String advert, Player p) {
		advert = advert.replaceAll("%name", p.getUsername());
		advert = advert.replaceAll("%version",
				String.valueOf(Config.SERVER_VERSION));
		advert = advert.replaceAll("%online",
				String.valueOf(world.getPlayers().size()));

		return advert;
	}

	public void emptyWorld() {
		for (Player p : world.getPlayers()) {
			p.save();
			p.getActionSender().sendLogout();
		}
		world.getServer().getLoginConnector().getActionSender().saveProfiles();
	}

	public void kill() {
		Logger.print("Terminating GameEngine");
		running = false;
	}

	/*
	 * public static TreeMap<Integer, QuestDef> getQuestList() { return
	 * questList; }
	 * 
	 * protected void loadQuests() { QuestDef[] questDefs =
	 * (QuestDef[])PersistenceManager.load("Quests.xml");
	 * 
	 * for(QuestDef questDef : questDefs) { try { String className =
	 * questDef.getClassName(); Class c = Class.forName(className); if(c !=
	 * null) { String[] args = { "none" }; QuestDef quest =
	 * (QuestDef)c.getConstructor(new Class[]{GameEngine.class}).newInstance(new
	 * Object[]{this}); questList.put(questDef.getID(), questDef);
	 * System.out.println("Loaded quest(" + questDef.getID() + "): " +
	 * questDef.getName()); } } catch(Exception e) { e.printStackTrace();
	 * Logger.err(e.getMessage()); } } }
	 */
	public void processLoginServer() {
		LoginConnector connector = world.getServer().getLoginConnector();
		if (connector != null) {
			connector.processIncomingPackets();
			connector.sendQueuedPackets();
		}
	}

	/**
	 * Processes incoming packets.
	 */
	private void processIncomingPackets() {
		for (RSCPacket p : packetQueue.getPackets()) {
			IoSession session = p.getSession();
			Player player = (Player) session.getAttachment();
			player.ping();
			PacketHandler handler = packetHandlers.get(p.getID());
			if (handler != null) {
				try {
					handler.handlePacket(p, session);
				} catch (Exception e) {
					Logger.error("Exception with p[" + p.getID() + "] from "
							+ player.getUsername() + " ["
							+ player.getCurrentIP() + "]: " + e.getMessage());
					player.getActionSender().sendLogout();
					player.destroy(false);
				}
			} else {
				Logger.error("Unhandled packet from " + player.getCurrentIP()
						+ ": " + p.getID());
			}
		}
	}

	private void processEvents() {
		eventHandler.doEvents();
	}

	private void processClients() {
		clientUpdater.sendQueuedPackets();

		long now = System.currentTimeMillis();
		if (now - lastSentClientUpdate >= 600) {
			lastSentClientUpdate = now;
			clientUpdater.updateClients();
		}
	}

	/**
	 * Returns the current packet queue.
	 * 
	 * @return A <code>PacketQueue</code>
	 */
	public PacketQueue getPacketQueue() {
		return packetQueue;
	}

	/**
	 * Loads the packet handling classes from the persistence manager.
	 */
	protected void loadPacketHandlers() {
		PacketHandlerDef[] handlerDefs = (PacketHandlerDef[]) PersistenceManager
				.load("PacketHandlers.xml");
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

}
