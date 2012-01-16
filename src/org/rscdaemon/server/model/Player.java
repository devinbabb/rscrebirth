package org.rscdaemon.server.model;

import org.rscdaemon.server.util.Formulae;
import org.rscdaemon.server.util.DataConversions;
import org.rscdaemon.server.util.StatefulEntityCollection;
import org.rscdaemon.server.net.LSPacket;
import org.rscdaemon.server.net.RSCPacket;
import org.rscdaemon.server.packetbuilder.client.MiscPacketBuilder;
import org.rscdaemon.server.packetbuilder.loginserver.SavePacketBuilder;
import org.rscdaemon.server.entityhandling.EntityHandler;
import org.rscdaemon.server.entityhandling.defs.PrayerDef;
import org.rscdaemon.server.event.*;
import org.rscdaemon.server.states.Action;
import org.rscdaemon.server.states.CombatState;
import org.rscdaemon.server.util.Logger;
import org.rscdaemon.server.quest.QuestManager;

import org.apache.mina.common.IoSession;

import java.util.Map.Entry;
import java.util.*;
import java.net.InetSocketAddress;
import java.io.*;

/**
 * A single player.
 */
public final class Player extends Mob {

	private ShortEvent sEvent = null;

	public void setSEvent(ShortEvent sEvent) {
		this.sEvent = sEvent;
		world.getDelayedEventHandler().add(sEvent);
	}

	/**
	 * Quests
	 */
	private QuestManager questManager;

	public QuestManager getQuestManager() {
		return questManager;
	}

	/**
	 * The player's username
	 */
	private String username;
	/**
	 * The player's username hash
	 */
	private long usernameHash;
	/**
	 * The player's password
	 */
	private String password;
	/**
	 * The main accounts group is
	 */
	private int groupID = 4;
	public boolean inClanChat;
	public boolean madeClan;
	public String clanChatJoined;
	public String[] motd;
	public ArrayList<String> clanAdmins = new ArrayList<String>();
	public ArrayList<String> peopleInClan = new ArrayList<String>();
	public ArrayList<String> bannedUsers = new ArrayList<String>();
	/**
	 * Whether the player is currently logged in
	 */
	private boolean loggedIn = false;
	/**
	 * The IO session of this player
	 */
	private IoSession ioSession;
	/**
	 * Last time a 'ping' was received
	 */
	private long lastPing = System.currentTimeMillis();
	/**
	 * The Players appearance
	 */
	private PlayerAppearance appearance;
	/**
	 * The items being worn by the player
	 */
	private int[] wornItems = new int[12];
	/**
	 * The current stat array
	 */
	private int[] curStat = new int[18];
	/**
	 * The max stat array
	 */
	private int[] maxStat = new int[18];
	/**
	 * The exp level array
	 */
	private int[] exp = new int[18];
	/**
	 * If the player has been sending suscicious packets
	 */
	private boolean suspicious = false;
	/**
	 * List of players this player 'knows' (recieved from the client) about
	 */
	private HashMap<Integer, Integer> knownPlayersAppearanceIDs = new HashMap<Integer, Integer>();
	/**
	 * Nearby players that we should be aware of
	 */
	private StatefulEntityCollection<Player> watchedPlayers = new StatefulEntityCollection<Player>();
	/**
	 * Nearby game objects that we should be aware of
	 */
	private StatefulEntityCollection<GameObject> watchedObjects = new StatefulEntityCollection<GameObject>();
	/**
	 * Nearby items that we should be aware of
	 */
	private StatefulEntityCollection<Item> watchedItems = new StatefulEntityCollection<Item>();
	/**
	 * Nearby npcs that we should be aware of
	 */
	private StatefulEntityCollection<Npc> watchedNpcs = new StatefulEntityCollection<Npc>();
	/**
	 * Inventory to hold items
	 */
	private Inventory inventory;
	/**
	 * Bank for banked items
	 */
	private Bank bank;
	/**
	 * Users privacy settings, chat block etc.
	 */
	private boolean[] privacySettings = new boolean[4];
	/**
	 * Users game settings, camera rotation preference etc
	 */
	private boolean[] gameSettings = new boolean[7]; // Why is 1 empty?
	/**
	 * Methods to send packets related to actions
	 */
	private MiscPacketBuilder actionSender;
	/**
	 * Unix time when the player last logged in
	 */
	private long lastLogin = 0;
	/**
	 * Unix time when the player logged in
	 */
	private long currentLogin = 0;
	/**
	 * Stores the last IP address used
	 */
	private String lastIP = "0.0.0.0";
	/**
	 * Stores the current IP address used
	 */
	private String currentIP = "0.0.0.0";
	/**
	 * If the player is reconnecting after connection loss
	 */
	private boolean reconnecting = false;
	/**
	 * Controls if were allowed to accept appearance updates
	 */
	private boolean changingAppearance = false;
	/**
	 * Is the character male?
	 */
	private boolean maleGender;
	/**
	 * The player we last requested to trade with, or null for none
	 */
	private Player wishToTrade = null;
	/**
	 * The player we last requested to duel with, or null for none
	 */
	private Player wishToDuel = null;
	/**
	 * If the player is currently in a trade
	 */
	private boolean isTrading = false;
	/**
	 * If the player is currently in a duel
	 */
	private boolean isDueling = false;
	/**
	 * List of items offered in the current trade
	 */
	private ArrayList<InvItem> tradeOffer = new ArrayList<InvItem>();
	/**
	 * List of items offered in the current duel
	 */
	private ArrayList<InvItem> duelOffer = new ArrayList<InvItem>();
	/**
	 * If the first trade screen has been accepted
	 */
	private boolean tradeOfferAccepted = false;
	/**
	 * If the first duel screen has been accepted
	 */
	private boolean duelOfferAccepted = false;
	/**
	 * If the second trade screen has been accepted
	 */
	private boolean tradeConfirmAccepted = false;
	/**
	 * If the second duel screen has been accepted
	 */
	private boolean duelConfirmAccepted = false;
	/**
	 * Map of players on players friend list
	 */
	private TreeMap<Long, Integer> friendList = new TreeMap<Long, Integer>();
	/**
	 * List of usernameHash's of players on players ignore list
	 */
	private ArrayList<Long> ignoreList = new ArrayList<Long>();
	/**
	 * List of all projectiles needing displayed
	 */
	private ArrayList<Projectile> projectilesNeedingDisplayed = new ArrayList<Projectile>();
	/**
	 * List of players who have been hit
	 */
	private ArrayList<Player> playersNeedingHitsUpdate = new ArrayList<Player>();
	/**
	 * List of players who have been hit
	 */
	private ArrayList<Npc> npcsNeedingHitsUpdate = new ArrayList<Npc>();
	/**
	 * Chat messages needing displayed
	 */
	private ArrayList<ChatMessage> chatMessagesNeedingDisplayed = new ArrayList<ChatMessage>();
	/**
	 * NPC messages needing displayed
	 */
	private ArrayList<ChatMessage> npcMessagesNeedingDisplayed = new ArrayList<ChatMessage>();
	/**
	 * Bubbles needing displayed
	 */
	private ArrayList<Bubble> bubblesNeedingDisplayed = new ArrayList<Bubble>();
	/**
	 * The time of the last spell cast, used as a throttle
	 */
	private long lastSpellCast = 0;
	/**
	 * Players we have been attacked by signed login, used to check if we should
	 * get a skull for attacking back
	 */
	private HashMap<Long, Long> attackedBy = new HashMap<Long, Long>();
	/**
	 * Time last report was sent, used to throttle reports
	 */
	private long lastReport = 0;
	/**
	 * Time of last charge spell
	 */
	private long lastCharge = 0;
	/**
	 * Combat style: 0 - all, 1 - str, 2 - att, 3 - def
	 */
	private int combatStyle = 0;
	/**
	 * Should we destroy this player?
	 */
	private boolean destroy = false;
	/**
	 * Session keys for the players session
	 */
	private int[] sessionKeys = new int[4];
	/**
	 * Is the player accessing their bank?
	 */
	private boolean inBank = false;
	/**
	 * A handler for any menu we are currently in
	 */
	private MenuHandler menuHandler = null;
	/**
	 * DelayedEvent responsible for handling prayer drains
	 */
	private DelayedEvent drainer;
	/**
	 * The drain rate of the prayers currently enabled
	 */
	private int drainRate = 0;
	/**
	 * DelayedEvent used for removing players skull after 20mins
	 */
	private DelayedEvent skullEvent = null;
	/**
	 * Amount of fatigue - 0 to 100
	 */
	private int fatigue = 0;
	/**
	 * Has the player been registered into the world?
	 */
	private boolean initialized = false;
	/**
	 * The shop (if any) the player is currently accessing
	 */
	private Shop shop = null;
	/**
	 * The npc we are currently interacting with
	 */
	private Npc interactingNpc = null;
	/**
	 * The ID of the owning account
	 */
	private int owner = 1;
	/**
	 * Queue of last 100 packets, used for auto detection purposes
	 */
	private LinkedList<RSCPacket> lastPackets = new LinkedList<RSCPacket>();
	/**
	 * When the users subscription expires (or 0 if they don't have one)
	 */
	private long subscriptionExpires = 0;
	/**
	 * Who we are currently following (if anyone)
	 */
	private Mob following;
	/**
	 * Event to handle following
	 */
	private DelayedEvent followEvent;
	/**
	 * Ranging event
	 */
	private RangeEvent rangeEvent;
	/**
	 * Last arrow fired
	 */
	private long lastArrow = 0;
	/**
	 * Last packet count time
	 */
	private long lastCount = 0;
	/**
	 * Amount of packets since last count
	 */
	private int packetCount = 0;
	/**
	 * List of chat messages to send
	 */
	private LinkedList<ChatMessage> chatQueue = new LinkedList<ChatMessage>();
	/**
	 * Time of last trade/duel request
	 */
	private long lastTradeDuelRequest = 0;
	/**
	 * The name of the client class they are connecting from
	 */
	private String className = "NOT_SET";
	/**
	 * The current status of the player
	 */
	private Action status = Action.IDLE;
	/**
	 * Duel options
	 */
	private boolean[] duelOptions = new boolean[4];
	/**
	 * Is a trade/duel update required?
	 */
	private boolean requiresOfferUpdate = false;

	/**
	 * Quest points
	 */
	private int questPoints = 0;

	public void setQuestPoints(int i) {
		questPoints = i;
		actionSender.sendQP();
	}

	public void npcautospawn(int npcid, int getx, int getX2, int gety, int getY2) {
		try {
			getActionSender().sendMessage(
					"Writing npcid: " + npcid + " at X: " + getX() + " Y: "
							+ getY());
			BufferedWriter out = new BufferedWriter(new FileWriter(
					"npc_autospawn.txt", true));
			out.write("<NPCLoc>");
			out.newLine();
			out.write("  <id>" + npcid + "</id>");
			out.newLine();
			out.write("  <startX>" + getX() + "</startX>");
			out.newLine();
			out.write("  <startY>" + getY() + "</startY>");
			out.newLine();
			out.write("  <minX>" + getx + "</minX>");
			out.newLine();
			out.write("  <maxX>" + getX2 + "</maxX>");
			out.newLine();
			out.write("  <minY>" + gety + "</minY>");
			out.newLine();
			out.write("  <maxY>" + getY2 + "</maxY>");
			out.newLine();
			out.write("</NPCLoc>");
			out.newLine();
			out.close();

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

	}

	public void objectautospawn(int objectid, int getx, int gety, int dir2) {
		try {
			getActionSender().sendMessage(
					"Writing objectid: " + objectid + " at X: " + getX()
							+ " Y: " + getY());
			BufferedWriter out = new BufferedWriter(new FileWriter(
					"object_autospawn.txt", true));
			out.write("<GameObjectLoc>");
			out.newLine();
			out.write("  <id>" + objectid + "</id>");
			out.newLine();
			out.write("  <x>" + getX() + "</x>");
			out.newLine();
			out.write("  <y>" + getY() + "</y>");
			out.newLine();
			out.write("  <direction>" + dir2 + "</direction>");
			out.newLine();
			out.write("  <type>0</type>");
			out.newLine();
			out.write("</GameObjectLoc>");
			out.newLine();
			out.close();

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

	}

	/**
	 * Kills and Deaths
	 */
	private int kills = 0;
	private int deaths = 0;

	public int getKills() {
		return kills;
	}

	public int getDeaths() {
		return deaths;
	}

	public void setKills(int kills) {
		kills = kills;
		actionSender.sendKills();
	}

	public void setDeaths(int deaths) {
		deaths = deaths;
		actionSender.sendDeaths();
	}

	public void incKills() {
		kills++;
	}

	public void incDeaths() {
		deaths++;
	}

	public int getQuestPoints() {
		return questPoints;
	}

	public void setRequiresOfferUpdate(boolean b) {
		requiresOfferUpdate = b;
	}

	public boolean requiresOfferUpdate() {
		return requiresOfferUpdate;
	}

	public void setStatus(Action a) {
		status = a;
	}

	public Action getStatus() {
		return status;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getClassName() {
		return className;
	}

	public boolean[] npcThief = { false, false, false, false, false, false }; // Baker,
																				// Silver,
																				// Spices,
																				// Gem.
	private boolean packetSpam = false;

	public void setSpam(boolean spam) {
		packetSpam = spam;
	}

	public boolean getSpam() {
		return packetSpam;
	}

	public boolean tradeDuelThrottling() {
		long now = System.currentTimeMillis();
		if (now - lastTradeDuelRequest > 1000) {
			lastTradeDuelRequest = now;
			return false;
		}
		return true;
	}

	public void addMessageToChatQueue(byte[] messageData) {
		chatQueue.add(new ChatMessage(this, messageData));
		if (chatQueue.size() > 2) {
			destroy(false);
		}
	}

	public ChatMessage getNextChatMessage() {
		return chatQueue.poll();
	}

	public void setArrowFired() {
		lastArrow = System.currentTimeMillis();
	}

	public void setRangeEvent(RangeEvent event) {
		if (isRanging()) {
			resetRange();
		}
		rangeEvent = event;
		rangeEvent.setLastRun(lastArrow);
		world.getDelayedEventHandler().add(rangeEvent);
	}

	public boolean isRanging() {
		return rangeEvent != null;
	}

	public void resetRange() {
		if (rangeEvent != null) {
			rangeEvent.stop();
			rangeEvent = null;
		}
		setStatus(Action.IDLE);
	}

	public boolean canLogout() {
		return !isBusy()
				&& System.currentTimeMillis() - getCombatTimer() > 10000
				&& System.currentTimeMillis() - getLastMoved() > 5000;
	}

	public boolean isFollowing() {
		return followEvent != null && following != null;
	}

	public boolean isFollowing(Mob mob) {
		return isFollowing() && mob.equals(following);
	}

	public void setFollowing(Mob mob) {
		setFollowing(mob, 0);
	}

	public void setFollowing(final Mob mob, final int radius) {
		if (isFollowing()) {
			resetFollowing();
		}
		following = mob;
		followEvent = new DelayedEvent(this, 500) {
			public void run() {
				if (!owner.withinRange(mob) || mob.isRemoved()
						|| (owner.isBusy() && !owner.isDueling())) {
					resetFollowing();
				} else if (!owner.finishedPath()
						&& owner.withinRange(mob, radius)) {
					owner.resetPath();
				} else if (owner.finishedPath()
						&& !owner.withinRange(mob, radius + 1)) {
					owner.setPath(new Path(owner.getX(), owner.getY(), mob
							.getX(), mob.getY()));
				}
			}
		};
		world.getDelayedEventHandler().add(followEvent);
	}

	public Npc getClosestNpc() {
		List<Npc> npcs = world.getNpcs(534, 559, 576, 611);
		double ourX = getLocation().getX();
		double ourY = getLocation().getY();
		double smallest = 0;
		Npc closestNpc = null;
		boolean bool = false;
		for (Npc npc : npcs) {
			double npc1X = npc.getX();

			double npc1Y = npc.getY();

			double xDist1 = Math.pow(ourX - npc1X, 2);

			double yDist1 = Math.pow(ourY - npc1Y, 2);

			double dist1 = Math.sqrt(xDist1 + yDist1);
			if (!bool) {
				smallest = dist1;
				closestNpc = npc;
				bool = true;
				// System.out.println("HELLO I'M SETTING CLOSEST NPC TO FIRST NPC ITERATED THROUGH");
			}
			if (dist1 < smallest) {
				smallest = dist1;
				closestNpc = npc;
				// System.out.println("HEY LOOK I CHANGED CLOSEST NPC!");
			}
			// System.out.println("Npc name: "+npc.getDef().getName());
			// System.out.println("i="+i);
			// System.out.println(smallest);
			// System.out.println("Distance: "+dist1);
			// System.out.println(closestNpc.getDef().getName()+"\tMinX:"+closestNpc.getLoc().minX+"\tMinY:"+closestNpc.getLoc().minY);
			// return closestNpc;
		}
		bool = false;
		// System.out.println("Smallest distance found:  "+smallest);
		// System.out.println(closestNpc.getDef().getName()+"\tMinX:"+closestNpc.getLoc().minX+"\tMinY:"+closestNpc.getLoc().minY);
		return closestNpc;
	}

	public void resetFollowing() {
		following = null;
		if (followEvent != null) {
			followEvent.stop();
			followEvent = null;
		}
		resetPath();
	}

	public void setSkulledOn(Player player) {
		player.addAttackedBy(this);
		if (System.currentTimeMillis() - lastAttackedBy(player) > 1200000) {
			addSkull(1200000);
		}
	}

	public void setSubscriptionExpires(long expires) {
		subscriptionExpires = expires;
	}

	public int getDaysSubscriptionLeft() {
		long now = (System.currentTimeMillis() / 1000);
		if (subscriptionExpires == 0 || now >= subscriptionExpires) {
			return 0;
		}
		return (int) ((subscriptionExpires - now) / 86400);
	}

	public void addPacket(RSCPacket p) {
		long now = System.currentTimeMillis();
		if (now - lastCount > 3000) {
			lastCount = now;
			packetCount = 0;
		}
		if (!DataConversions.inArray(Formulae.safePacketIDs, p.getID())
				&& packetCount++ >= 60) {
			destroy(false);
		}
		if (lastPackets.size() >= 60) {
			lastPackets.remove();
		}
		lastPackets.addLast(p);
	}

	public List<RSCPacket> getPackets() {
		return lastPackets;
	}

	public boolean isSuspicious() {
		return suspicious;
	}

	public void setOwner(int owner) {
		this.owner = owner;
	}

	public int getOwner() {
		return owner;
	}

	public Npc getNpc() {
		return interactingNpc;
	}

	public void setNpc(Npc npc) {
		// System.out.println("setNpc(npc)");
		interactingNpc = npc;
	}

	public void remove() {
		removed = true;
	}

	public boolean initialized() {
		return initialized;
	}

	public void setInitialized() {
		initialized = true;
	}

	public int getDrainRate() {
		return drainRate;
	}

	public void setDrainRate(int rate) {
		drainRate = rate;
	}

	public double[] getBowStats() {
		double[] longBowStats = { 2000, 1.55, 5 };
		double[] shortBowStats = { 1550, 1.2, 4 };
		double[] xbowStats = { 1800, 1.4, 4 };
		Inventory i = getInventory();
		for (InvItem it : i.getItems()) {
			if (!it.isWielded())
				continue;
			for (int i1 : longBowIds)
				if (it.getID() == i1)
					return longBowStats;
			for (int i1 : shortBowIds)
				if (it.getID() == i1)
					return shortBowStats;
			for (int i1 : xbowIds)
				if (it.getID() == i1)
					return xbowStats;
		}
		return null;
	}

	private static final int[] longBowIds = { 188, 648, 650, 652, 654, 656 };
	private static final int[] shortBowIds = { 189, 649, 651, 653, 655, 657 };
	private static final int[] xbowIds = { 59, 60 };
	private static final int[][] arrows = {
			{ 45, 40, 35, 30, 25, 20, 10, 5, 5, 1, 3, 1 },
			{ 647, 646, 645, 644, 643, 642, 641, 640, 639, 638, 574, 11 } };
	private static final int[][] bolts = { { 50, 1 }, { 786, 190 } };

	public int getRangeEquip() {
		for (InvItem item : inventory.getItems()) {
			if (item.isWielded()
					&& (DataConversions.inArray(Formulae.bowIDs, item.getID()) || DataConversions
							.inArray(Formulae.xbowIDs, item.getID()))) {
				return item.getID();
			}
		}
		return -1;
	}

	public void resetAll() {
		resetAllExceptTradeOrDuel();
		resetTrade();
		resetDuel();
	}

	public void resetTrade() {
		Player opponent = getWishToTrade();
		if (opponent != null) {
			opponent.resetTrading();
		}
		resetTrading();
	}

	public void resetDuel() {
		Player opponent = getWishToDuel();
		if (opponent != null) {
			opponent.resetDueling();
		}
		resetDueling();
	}

	public void resetAllExceptTrading() {
		resetAllExceptTradeOrDuel();
		resetDuel();
	}

	public void resetAllExceptDueling() {
		resetAllExceptTradeOrDuel();
		resetTrade();
	}

	private void resetAllExceptTradeOrDuel() {
		if (getMenuHandler() != null) {
			resetMenuHandler();
		}
		if (accessingBank()) {
			resetBank();
		}
		if (accessingShop()) {
			resetShop();
		}
		if (interactingNpc != null) {
			interactingNpc.unblock();
		}
		if (isFollowing()) {
			resetFollowing();
		}
		if (isRanging()) {
			resetRange();
		}
		setStatus(Action.IDLE);
	}

	public void setMenuHandler(MenuHandler menuHandler) {
		menuHandler.setOwner(this);
		this.menuHandler = menuHandler;
	}

	public void setQuestMenuHandler(MenuHandler menuHandler) {
		this.menuHandler = menuHandler;
		menuHandler.setOwner(this);
		actionSender.sendMenu(menuHandler.getOptions());
	}

	public void resetMenuHandler() {
		menuHandler = null;
		actionSender.hideMenu();
	}

	public MenuHandler getMenuHandler() {
		return menuHandler;
	}

	public boolean accessingShop() {
		return shop != null;
	}

	public void setAccessingShop(Shop shop) {
		this.shop = shop;
		if (shop != null) {
			shop.addPlayer(this);
		}
	}

	public void resetShop() {
		if (shop != null) {
			shop.removePlayer(this);
			shop = null;
			actionSender.hideShop();
		}
	}

	public boolean accessingBank() {
		return inBank;
	}

	public double[] getModifiers() {
		double[] result = { 1, 1, 1 };
		double prayerModifier[][] = {
				{ 1, 1, 1.05, 1, 1, 1.1, 1, 1, 1, 1, 1, 1.15, 1, 1 },
				{ 1.05, 1, 1, 1.1, 1, 1, 1, 1, 1, 1.15, 1, 1, 1, 1 },
				{ 1, 1.05, 1, 1, 1.1, 1, 1, 1, 1, 1, 1.15, 1, 1, 1 } };
		for (int i = 0; i < 3; i++) {
			for (int i1 = 0; i1 < activatedPrayers.length; i1++)
				if (activatedPrayers[i1] && prayerModifier[i][i1] > result[i])
					result[i] = prayerModifier[i][i1];
		}
		return result;
	}

	public Shop getShop() {
		return shop;
	}

	public void setAccessingBank(boolean b) {
		inBank = b;
	}

	public void resetBank() {
		setAccessingBank(false);
		actionSender.hideBank();
	}

	public Player(IoSession ios) {
		ioSession = ios;
		currentIP = ((InetSocketAddress) ios.getRemoteAddress()).getAddress()
				.getHostAddress();
		currentLogin = System.currentTimeMillis();
		actionSender = new MiscPacketBuilder(this);
		setBusy(true);
		questManager = new QuestManager(this);
	}

	public void setServerKey(long key) {
		sessionKeys[2] = (int) (key >> 32);
		sessionKeys[3] = (int) key;
	}

	public boolean setSessionKeys(int[] keys) {
		boolean valid = (sessionKeys[2] == keys[2] && sessionKeys[3] == keys[3]);
		sessionKeys = keys;
		return valid;
	}

	public boolean destroyed() {
		return destroy;
	}

	public void destroy(boolean force) {
		if (destroy) {
			return;
		}
		if (force || canLogout()) {
			destroy = true;
			actionSender.sendLogout();
		} else {
			final long startDestroy = System.currentTimeMillis();
			world.getDelayedEventHandler().add(new DelayedEvent(this, 3000) {
				public void run() {
					if (owner.canLogout()
							|| (!(owner.inCombat() && owner.isDueling()) && System
									.currentTimeMillis() - startDestroy > 60000)) {
						owner.destroy(true);
						running = false;
					}
				}
			});
		}
	}

	public int getCombatStyle() {
		return combatStyle;
	}

	public void setCombatStyle(int style) {
		combatStyle = style;
	}

	public void load(String username, String password, int uid,
			boolean reconnecting) {
		setID(uid);
		this.password = password;
		this.reconnecting = reconnecting;
		usernameHash = DataConversions.usernameToHash(username);
		this.username = DataConversions.hashToUsername(usernameHash);

		world.getServer().getLoginConnector().getActionSender()
				.playerLogin(this);

		world.getDelayedEventHandler().add(new DelayedEvent(this, 60000) {
			public void run() {
				for (int statIndex = 0; statIndex < 18; statIndex++) {
					if (statIndex == 5) {
						continue;
					}
					int curStat = getCurStat(statIndex);
					int maxStat = getMaxStat(statIndex);
					if (curStat > maxStat) {
						setCurStat(statIndex, curStat - 1);
						getActionSender().sendStat(statIndex);
						checkStat(statIndex);
					} else if (curStat < maxStat) {
						setCurStat(statIndex, curStat + 1);
						getActionSender().sendStat(statIndex);
						checkStat(statIndex);
					}
				}
			}

			private void checkStat(int statIndex) {
				if (statIndex != 3
						&& owner.getCurStat(statIndex) == owner
								.getMaxStat(statIndex)) {
					owner.getActionSender().sendMessage(
							"Your " + Formulae.statArray[statIndex]
									+ " ability has returned to normal.");
				}
			}
		});
		drainer = new DelayedEvent(this, Integer.MAX_VALUE) {
			public void run() {
				int curPrayer = getCurStat(5);
				if (getDrainRate() > 0 && curPrayer > 0) {
					incCurStat(5, -1);
					getActionSender().sendStat(5);
					if (curPrayer <= 1) {
						for (int prayerID = 0; prayerID < 14; prayerID++) {
							setPrayer(prayerID, false);
						}
						setDrainRate(0);
						setDelay(Integer.MAX_VALUE);
						getActionSender()
								.sendMessage(
										"You have run out of prayer points. Return to a church to recharge");
						getActionSender().sendPrayers();
					}
				}
			}
		};
		world.getDelayedEventHandler().add(drainer);
	}

	public boolean isWielded(int itemId) {
		for (InvItem item : inventory.getItems()) {
			if (item.isWielded() && item.getID() == itemId) {
				return true;
			}
		}
		return false;
	}

	public void resetTrading() {
		if (isTrading()) {
			actionSender.sendTradeWindowClose();
			setStatus(Action.IDLE);
		}
		setWishToTrade(null);
		setTrading(false);
		setTradeOfferAccepted(false);
		setTradeConfirmAccepted(false);
		resetTradeOffer();
	}

	public void resetDueling() {
		if (isDueling()) {
			actionSender.sendDuelWindowClose();
			setStatus(Action.IDLE);
		}
		setWishToDuel(null);
		setDueling(false);
		setDuelOfferAccepted(false);
		setDuelConfirmAccepted(false);
		resetDuelOffer();
		clearDuelOptions();
	}

	public void clearDuelOptions() {
		for (int i = 0; i < 4; i++) {
			duelOptions[i] = false;
		}
	}

	public void save() {
		SavePacketBuilder builder = new SavePacketBuilder();
		builder.setPlayer(this);
		LSPacket temp = builder.getPacket();
		if (temp != null) {
			world.getServer().getLoginConnector().getSession().write(temp);
		}
	}

	public void setCharged() {
		lastCharge = System.currentTimeMillis();
	}

	public boolean isCharged() {
		return System.currentTimeMillis() - lastCharge < 600000;
	}

	public boolean canReport() {
		return System.currentTimeMillis() - lastReport > 60000;
	}

	public void setLastReport() {
		lastReport = System.currentTimeMillis();
	}

	public void killedBy(Mob mob) {
		killedBy(mob, false);
	}

	public void killedBy(Mob mob, boolean stake) {
		boolean drop = true;
		if (!loggedIn) {
			Logger.error(username + " not logged in, but killed!");
			return;
		}
		if (mob instanceof Player) {
			Player player = (Player) mob;
			player.getActionSender().sendMessage(
					"You have defeated " + getUsername() + "!");
			player.incKills();
			player.actionSender.sendKills();
			player.getActionSender().sendSound("victory");
			world.getDelayedEventHandler().add(new MiniEvent(player) {
				public void action() {
					owner.getActionSender().sendScreenshot();
				}
			});
			world.getServer().getLoginConnector().getActionSender()
					.logKill(player.getUsernameHash(), usernameHash, stake);
			if (world.getServer().pvpIsRunning() && world.getPvpSize() == 2) {
				world.setWinner(player);
				world.removePvpEntry(player);
				world.removePvpEntry(this);
				player.getInventory().add(new InvItem(10, world.getJackPot()));
				player.getActionSender().sendInventory();
				world.getServer().stopDuel();
				world.clearJackPot();
				player.teleport(220, 445, false);
				drop = false;
			} else if (world.getServer().pvpIsRunning()) {
				world.removePvpEntry(this);
				drop = false;
			}
		}
		Mob opponent = super.getOpponent();
		if (opponent != null) {
			opponent.resetCombat(CombatState.WON);
		}
		actionSender.sendDied();
		for (int i = 0; i < 18; i++) {
			curStat[i] = maxStat[i];
			actionSender.sendStat(i);
		}

		Player player = mob instanceof Player ? (Player) mob : null;
		if (drop) {
			if (stake) {
				for (InvItem item : duelOffer) {
					InvItem affectedItem = getInventory().get(item);
					if (affectedItem == null) {
						setSuspiciousPlayer(true);
						Logger.error("Missing staked item [" + item.getID()
								+ ", " + item.getAmount() + "] from = "
								+ usernameHash + "; to = "
								+ player.getUsernameHash() + ";");
						continue;
					}
					if (affectedItem.isWielded()) {
						affectedItem.setWield(false);
						updateWornItems(
								affectedItem.getWieldableDef().getWieldPos(),
								getPlayerAppearance().getSprite(
										affectedItem.getWieldableDef()
												.getWieldPos()));
					}
					getInventory().remove(item);
					world.registerItem(new Item(item.getID(), getX(), getY(),
							item.getAmount(), player));
				}
			} else {
				inventory.sort();
				ListIterator<InvItem> iterator = inventory.iterator();
				if (!isSkulled()) {
					for (int i = 0; i < 3 && iterator.hasNext(); i++) {
						if ((iterator.next()).getDef().isStackable()) {
							iterator.previous();
							break;
						}
					}
				}
				if (activatedPrayers[8] && iterator.hasNext()) {
					if (((InvItem) iterator.next()).getDef().isStackable()) {
						iterator.previous();
					}
				}
				for (int slot = 0; iterator.hasNext(); slot++) {
					InvItem item = (InvItem) iterator.next();
					if (item.isWielded()) {
						item.setWield(false);
						updateWornItems(item.getWieldableDef().getWieldPos(),
								appearance.getSprite(item.getWieldableDef()
										.getWieldPos()));
					}
					iterator.remove();
					world.registerItem(new Item(item.getID(), getX(), getY(),
							item.getAmount(), player));
				}
				removeSkull();
			}
		}
		world.registerItem(new Item(20, getX(), getY(), 1, player));

		for (int x = 0; x < activatedPrayers.length; x++) {
			if (activatedPrayers[x]) {
				removePrayerDrain(x);
				activatedPrayers[x] = false;
			}
		}
		actionSender.sendPrayers();

		setLocation(Point.location(122, 647), true);
		Player affectedPlayer = world.getPlayer(usernameHash);
		Collection<Player> allWatched = watchedPlayers.getAllEntities();
		for (Player p : allWatched) {
			p.removeWatchedPlayer(this);
		}

		resetPath();
		resetCombat(CombatState.LOST);
		actionSender.sendWorldInfo();
		actionSender.sendEquipmentStats();
		actionSender.sendInventory();
		affectedPlayer.incDeaths();
		affectedPlayer.actionSender.sendDeaths();
	}

	public void addAttackedBy(Player p) {
		attackedBy.put(p.getUsernameHash(), System.currentTimeMillis());
	}

	public long lastAttackedBy(Player p) {
		Long time = attackedBy.get(p.getUsernameHash());
		if (time != null) {
			return time;
		}
		return 0;
	}

	public void setCastTimer() {
		lastSpellCast = System.currentTimeMillis();
	}

	public void setSpellFail() {
		lastSpellCast = System.currentTimeMillis() + 20000;
	}

	public int getSpellWait() {
		return DataConversions.roundUp((double) (1200 - (System
				.currentTimeMillis() - lastSpellCast)) / 1000D);
	}

	public long getCastTimer() {
		return lastSpellCast;
	}

	public boolean castTimer() {
		return System.currentTimeMillis() - lastSpellCast > 1200;
	}

	public boolean checkAttack(Mob mob, boolean missile) {
		if (mob instanceof Player) {
			Player victim = (Player) mob;
			if ((inCombat() && isDueling())
					&& (victim.inCombat() && victim.isDueling())) {
				Player opponent = (Player) getOpponent();
				if (opponent != null && victim.equals(opponent)) {
					return true;
				}
			}
			if (getLocation().inDuelArena()
					&& victim.getLocation().inDuelArena()) {
				return true;
			}
			if (System.currentTimeMillis() - mob.getCombatTimer() < (mob
					.getCombatState() == CombatState.RUNNING
					|| mob.getCombatState() == CombatState.WAITING ? 3000 : 500)
					&& !mob.inCombat()) {
				return false;
			}
			int myWildLvl = getLocation().wildernessLevel();
			int victimWildLvl = victim.getLocation().wildernessLevel();
			if (myWildLvl < 1 || victimWildLvl < 1) {
				actionSender
						.sendMessage("You cannot attack other players outside of the wilderness!");
				return false;
			}
			int combDiff = Math.abs(getCombatLevel() - victim.getCombatLevel());
			if (combDiff > myWildLvl) {
				actionSender.sendMessage("You must move to at least level "
						+ combDiff + " wilderness to attack "
						+ victim.getUsername() + "!");
				return false;
			}
			if (combDiff > victimWildLvl) {
				actionSender
						.sendMessage(victim.getUsername()
								+ " is not in high enough wilderness for you to attack!");
				return false;
			}
			return true;
		} else if (mob instanceof Npc) {
			Npc victim = (Npc) mob;
			if (!victim.getDef().isAttackable()) {
				setSuspiciousPlayer(true);
				return false;
			}
			return true;
		}
		return true;
	}

	public void informOfBubble(Bubble b) {
		bubblesNeedingDisplayed.add(b);
	}

	public List<Bubble> getBubblesNeedingDisplayed() {
		return bubblesNeedingDisplayed;
	}

	public void clearBubblesNeedingDisplayed() {
		bubblesNeedingDisplayed.clear();
	}

	public void informOfChatMessage(ChatMessage cm) {
		chatMessagesNeedingDisplayed.add(cm);
	}

	public void sayMessage(String msg, Mob to) {
		ChatMessage cm = new ChatMessage(this, msg, to);
		chatMessagesNeedingDisplayed.add(cm);
	}

	public void informOfNpcMessage(ChatMessage cm) {
		npcMessagesNeedingDisplayed.add(cm);
	}

	public List<ChatMessage> getNpcMessagesNeedingDisplayed() {
		return npcMessagesNeedingDisplayed;
	}

	public List<ChatMessage> getChatMessagesNeedingDisplayed() {
		return chatMessagesNeedingDisplayed;
	}

	public void clearNpcMessagesNeedingDisplayed() {
		npcMessagesNeedingDisplayed.clear();
	}

	public void clearChatMessagesNeedingDisplayed() {
		chatMessagesNeedingDisplayed.clear();
	}

	public void informOfModifiedHits(Mob mob) {
		if (mob instanceof Player) {
			playersNeedingHitsUpdate.add((Player) mob);
		} else if (mob instanceof Npc) {
			npcsNeedingHitsUpdate.add((Npc) mob);
		}
	}

	public List<Player> getPlayersRequiringHitsUpdate() {
		return playersNeedingHitsUpdate;
	}

	public List<Npc> getNpcsRequiringHitsUpdate() {
		return npcsNeedingHitsUpdate;
	}

	public void clearPlayersNeedingHitsUpdate() {
		playersNeedingHitsUpdate.clear();
	}

	public void clearNpcsNeedingHitsUpdate() {
		npcsNeedingHitsUpdate.clear();
	}

	public void informOfProjectile(Projectile p) {
		projectilesNeedingDisplayed.add(p);
	}

	public List<Projectile> getProjectilesNeedingDisplayed() {
		return projectilesNeedingDisplayed;
	}

	public void clearProjectilesNeedingDisplayed() {
		projectilesNeedingDisplayed.clear();
	}

	public void addPrayerDrain(int prayerID) {
		PrayerDef prayer = EntityHandler.getPrayerDef(prayerID);
		drainRate += prayer.getDrainRate();
		drainer.setDelay((int) (240000 / drainRate));
	}

	public void removePrayerDrain(int prayerID) {
		PrayerDef prayer = EntityHandler.getPrayerDef(prayerID);
		drainRate -= prayer.getDrainRate();
		if (drainRate <= 0) {
			drainRate = 0;
			drainer.setDelay(Integer.MAX_VALUE);
		} else {
			drainer.setDelay((int) (240000 / drainRate));
		}
	}

	public boolean isFriendsWith(long usernameHash) {
		return friendList.containsKey(usernameHash);
	}

	public boolean isIgnoring(long usernameHash) {
		return ignoreList.contains(usernameHash);
	}

	public Collection<Entry<Long, Integer>> getFriendList() {
		return friendList.entrySet();
	}

	public ArrayList<Long> getIgnoreList() {
		return ignoreList;
	}

	public void removeFriend(long id) {
		friendList.remove(id);
	}

	public void removeIgnore(long id) {
		ignoreList.remove(id);
	}

	public void addFriend(long id, int world) {
		friendList.put(id, world);
	}

	public void addIgnore(long id) {
		ignoreList.add(id);
	}

	public int friendCount() {
		return friendList.size();
	}

	public int ignoreCount() {
		return ignoreList.size();
	}

	public void setTradeConfirmAccepted(boolean b) {
		tradeConfirmAccepted = b;
	}

	public void setDuelConfirmAccepted(boolean b) {
		duelConfirmAccepted = b;
	}

	public boolean isTradeConfirmAccepted() {
		return tradeConfirmAccepted;
	}

	public boolean isDuelConfirmAccepted() {
		return duelConfirmAccepted;
	}

	public void setTradeOfferAccepted(boolean b) {
		tradeOfferAccepted = b;
	}

	public void setDuelOfferAccepted(boolean b) {
		duelOfferAccepted = b;
	}

	public boolean isTradeOfferAccepted() {
		return tradeOfferAccepted;
	}

	public boolean isDuelOfferAccepted() {
		return duelOfferAccepted;
	}

	public void resetTradeOffer() {
		tradeOffer.clear();
	}

	public void resetDuelOffer() {
		duelOffer.clear();
	}

	public void addToTradeOffer(InvItem item) {
		tradeOffer.add(item);
	}

	public void addToDuelOffer(InvItem item) {
		duelOffer.add(item);
	}

	public ArrayList<InvItem> getTradeOffer() {
		return tradeOffer;
	}

	public ArrayList<InvItem> getDuelOffer() {
		return duelOffer;
	}

	public void setTrading(boolean b) {
		isTrading = b;
	}

	public void setDueling(boolean b) {
		isDueling = b;
	}

	public boolean isTrading() {
		return isTrading;
	}

	public boolean isDueling() {
		return isDueling;
	}

	public void setWishToTrade(Player p) {
		wishToTrade = p;
	}

	public void setWishToDuel(Player p) {
		wishToDuel = p;
	}

	public Player getWishToTrade() {
		return wishToTrade;
	}

	public Player getWishToDuel() {
		return wishToDuel;
	}

	public void setDuelSetting(int i, boolean b) {
		duelOptions[i] = b;
	}

	public boolean getDuelSetting(int i) {
		try {
			for (InvItem item : duelOffer) {
				if (DataConversions.inArray(Formulae.runeIDs, item.getID())) {
					setDuelSetting(1, true);
					break;
				}
			}
			for (InvItem item : wishToDuel.getDuelOffer()) {
				if (DataConversions.inArray(Formulae.runeIDs, item.getID())) {
					setDuelSetting(1, true);
					break;
				}
			}
		} catch (Exception e) {
		}
		return duelOptions[i];
	}

	public void setMale(boolean male) {
		maleGender = male;
	}

	public boolean isMale() {
		return maleGender;
	}

	public void setChangingAppearance(boolean b) {
		changingAppearance = b;
	}

	public boolean isChangingAppearance() {
		return changingAppearance;
	}

	public boolean isReconnecting() {
		return reconnecting;
	}

	public void setLastLogin(long l) {
		lastLogin = l;
	}

	public long getLastLogin() {
		return lastLogin;
	}

	public int getDaysSinceLastLogin() {
		long now = Calendar.getInstance().getTimeInMillis() / 1000;
		return (int) ((now - lastLogin) / 86400);
	}

	public long getCurrentLogin() {
		return currentLogin;
	}

	public void setLastIP(String ip) {
		lastIP = ip;
	}

	public String getCurrentIP() {
		return currentIP;
	}

	public String getLastIP() {
		return lastIP;
	}

	public void setGroupID(int id) {
		groupID = id;
	}

	public int getGroupID() {
		return groupID;
	}

	public boolean isSubscriber() {
		return groupID == 5 || isPMod() || isMod() || isAdmin();
	}

	public boolean isPMod() {
		return groupID == 6 || isMod() || isAdmin();
	}

	public boolean isMod() {
		return groupID == 2 || isAdmin();
	}

	public boolean isAdmin() {
		return groupID == 1;
	}

	public int getArmourPoints() {
		int points = 1;
		for (InvItem item : inventory.getItems()) {
			if (item.isWielded()) {
				points += item.getWieldableDef().getArmourPoints();
			}
		}
		return points < 1 ? 1 : points;
	}

	public int getWeaponAimPoints() {
		int points = 1;
		for (InvItem item : inventory.getItems()) {
			if (item.isWielded()) {
				points += item.getWieldableDef().getWeaponAimPoints();
			}
		}
		return points < 1 ? 1 : points;
	}

	public int getWeaponPowerPoints() {
		int points = 1;
		for (InvItem item : inventory.getItems()) {
			if (item.isWielded()) {
				points += item.getWieldableDef().getWeaponPowerPoints();
			}
		}
		return points < 1 ? 1 : points;
	}

	public int getMagicPoints() {
		int points = 1;
		for (InvItem item : inventory.getItems()) {
			if (item.isWielded()) {
				points += item.getWieldableDef().getMagicPoints();
			}
		}
		return points < 1 ? 1 : points;
	}

	public int getPrayerPoints() {
		int points = 1;
		for (InvItem item : inventory.getItems()) {
			if (item.isWielded()) {
				points += item.getWieldableDef().getPrayerPoints();
			}
		}
		return points < 1 ? 1 : points;
	}

	public int getRangePoints() {
		int points = 1;
		for (InvItem item : inventory.getItems()) {
			if (item.isWielded()) {
				points += item.getWieldableDef().getRangePoints();
			}
		}
		return points < 1 ? 1 : points;
	}

	public MiscPacketBuilder getActionSender() {
		return actionSender;
	}

	public int[] getWornItems() {
		return wornItems;
	}

	public void updateWornItems(int index, int id) {
		wornItems[index] = id;
		super.ourAppearanceChanged = true;
	}

	public void setWornItems(int[] worn) {
		wornItems = worn;
		super.ourAppearanceChanged = true;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public void setInventory(Inventory i) {
		inventory = i;
	}

	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank b) {
		bank = b;
	}

	public void setGameSetting(int i, boolean b) {
		gameSettings[i] = b;
	}

	public boolean getGameSetting(int i) {
		return gameSettings[i];
	}

	public void setPrivacySetting(int i, boolean b) {
		privacySettings[i] = b;
	}

	public boolean getPrivacySetting(int i) {
		return privacySettings[i];
	}

	public long getLastPing() {
		return lastPing;
	}

	public IoSession getSession() {
		return ioSession;
	}

	public boolean loggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(boolean loggedIn) {
		if (loggedIn) {
			currentLogin = System.currentTimeMillis();
		}
		this.loggedIn = loggedIn;
	}

	public String getUsername() {
		return username;
	}

	public long getUsernameHash() {
		return usernameHash;
	}

	public String getPassword() {
		return password;
	}

	public void ping() {
		lastPing = System.currentTimeMillis();
	}

	public boolean isSkulled() {
		return skullEvent != null;
	}

	public PlayerAppearance getPlayerAppearance() {
		return appearance;
	}

	public void setAppearance(PlayerAppearance appearance) {
		this.appearance = appearance;
	}

	public int getSkullTime() {
		if (isSkulled()) {
			return skullEvent.timeTillNextRun();
		}
		return 0;
	}

	public void addSkull(long timeLeft) {
		if (!getLocation().inDuelArena()) {
			if (!isSkulled()) {
				skullEvent = new DelayedEvent(this, 1200000) {
					public void run() {
						removeSkull();
					}
				};
				world.getDelayedEventHandler().add(skullEvent);
				super.setAppearnceChanged(true);
			}
			skullEvent.setLastRun(System.currentTimeMillis()
					- (1200000 - timeLeft));
		}
	}

	public void removeSkull() {
		if (!isSkulled()) {
			return;
		}
		super.setAppearnceChanged(true);
		skullEvent.stop();
		skullEvent = null;
	}

	public void setSuspiciousPlayer(boolean suspicious) {
		this.suspicious = suspicious;
	}

	public void addPlayersAppearanceIDs(int[] indicies, int[] appearanceIDs) {
		for (int x = 0; x < indicies.length; x++) {
			knownPlayersAppearanceIDs.put(indicies[x], appearanceIDs[x]);
		}
	}

	public List<Player> getPlayersRequiringAppearanceUpdate() {
		List<Player> needingUpdates = new ArrayList<Player>();
		needingUpdates.addAll(watchedPlayers.getNewEntities());
		if (super.ourAppearanceChanged) {
			needingUpdates.add(this);
		}
		for (Player p : watchedPlayers.getKnownEntities()) {
			if (needsAppearanceUpdateFor(p)) {
				needingUpdates.add(p);
			}
		}
		return needingUpdates;
	}

	private boolean needsAppearanceUpdateFor(Player p) {
		int playerServerIndex = p.getIndex();
		if (knownPlayersAppearanceIDs.containsKey(playerServerIndex)) {
			int knownPlayerAppearanceID = knownPlayersAppearanceIDs
					.get(playerServerIndex);
			if (knownPlayerAppearanceID != p.getAppearanceID()) {
				return true;
			}
		} else {
			return true;
		}
		return false;
	}

	public void updateViewedPlayers() {
		List<Player> playersInView = viewArea.getPlayersInView();
		for (Player p : playersInView) {
			if (p.getIndex() != getIndex() && p.loggedIn()) {
				this.informOfPlayer(p);
				p.informOfPlayer(this);
			}
		}
	}

	public void updateViewedObjects() {
		List<GameObject> objectsInView = viewArea.getGameObjectsInView();
		for (GameObject o : objectsInView) {
			if (!watchedObjects.contains(o) && !o.isRemoved() && withinRange(o)) {
				watchedObjects.add(o);
			}
		}
	}

	public void updateViewedItems() {
		List<Item> itemsInView = viewArea.getItemsInView();
		for (Item i : itemsInView) {
			if (!watchedItems.contains(i) && !i.isRemoved() && withinRange(i)
					&& i.visibleTo(this)) {
				watchedItems.add(i);
			}
		}
	}

	public void updateViewedNpcs() {
		List<Npc> npcsInView = viewArea.getNpcsInView();
		for (Npc n : npcsInView) {
			if ((!watchedNpcs.contains(n) || watchedNpcs.isRemoving(n))
					&& withinRange(n)) {
				watchedNpcs.add(n);
			}
		}
	}

	public void teleport(int x, int y, boolean bubble) {
		Mob opponent = super.getOpponent();
		if (inCombat()) {
			resetCombat(CombatState.ERROR);
		}
		if (opponent != null) {
			opponent.resetCombat(CombatState.ERROR);
		}
		for (Object o : getWatchedPlayers().getAllEntities()) {
			Player p = ((Player) o);
			if (bubble) {
				p.getActionSender().sendTeleBubble(getX(), getY(), false);
			}
			p.removeWatchedPlayer(this);
		}
		if (bubble) {
			actionSender.sendTeleBubble(getX(), getY(), false);
		}
		setLocation(Point.location(x, y), true);
		resetPath();
		actionSender.sendWorldInfo();
	}

	/**
	 * This is a 'another player has tapped us on the shoulder' method.
	 * 
	 * If we are in another players viewArea, they should in theory be in ours.
	 * So they will call this method to let the player know they should probably
	 * be informed of them.
	 */
	public void informOfPlayer(Player p) {
		if ((!watchedPlayers.contains(p) || watchedPlayers.isRemoving(p))
				&& withinRange(p)) {
			watchedPlayers.add(p);
		}
	}

	public StatefulEntityCollection<Player> getWatchedPlayers() {
		return watchedPlayers;
	}

	public StatefulEntityCollection<GameObject> getWatchedObjects() {
		return watchedObjects;
	}

	public StatefulEntityCollection<Item> getWatchedItems() {
		return watchedItems;
	}

	public StatefulEntityCollection<Npc> getWatchedNpcs() {
		return watchedNpcs;
	}

	public void removeWatchedNpc(Npc n) {
		watchedNpcs.remove(n);
	}

	public void removeWatchedPlayer(Player p) {
		watchedPlayers.remove(p);
	}

	public void revalidateWatchedPlayers() {
		for (Player p : watchedPlayers.getKnownEntities()) {
			if (!withinRange(p) || !p.loggedIn()) {
				watchedPlayers.remove(p);
				knownPlayersAppearanceIDs.remove(p.getIndex());
			}
		}
	}

	public void revalidateWatchedObjects() {
		for (GameObject o : watchedObjects.getKnownEntities()) {
			if (!withinRange(o) || o.isRemoved()) {
				watchedObjects.remove(o);
			}
		}
	}

	public void revalidateWatchedItems() {
		for (Item i : watchedItems.getKnownEntities()) {
			if (!withinRange(i) || i.isRemoved() || !i.visibleTo(this)) {
				watchedItems.remove(i);
			}
		}
	}

	public void revalidateWatchedNpcs() {
		for (Npc n : watchedNpcs.getKnownEntities()) {
			if (!withinRange(n) || n.isRemoved()) {
				watchedNpcs.remove(n);
			}
		}
	}

	public boolean withinRange(Entity e) {
		int xDiff = location.getX() - e.getLocation().getX();
		int yDiff = location.getY() - e.getLocation().getY();
		return xDiff <= 16 && xDiff >= -15 && yDiff <= 16 && yDiff >= -15;
	}

	public int[] getCurStats() {
		return curStat;
	}

	public int getCurStat(int id) {
		return curStat[id];
	}

	public int getHits() {
		return getCurStat(3);
	}

	public int getAttack() {
		return getCurStat(0);
	}

	public int getDefense() {
		return getCurStat(1);
	}

	public int getStrength() {
		return getCurStat(2);
	}

	public void setHits(int lvl) {
		setCurStat(3, lvl);
	}

	public void setCurStat(int id, int lvl) {
		if (lvl <= 0) {
			lvl = 0;
		}
		curStat[id] = lvl;
	}

	public int getMaxStat(int id) {
		return maxStat[id];
	}

	public void setMaxStat(int id, int lvl) {
		if (lvl < 0) {
			lvl = 0;
		}
		maxStat[id] = lvl;
	}

	public int[] getMaxStats() {
		return maxStat;
	}

	public int getSkillTotal() {
		int total = 0;
		for (int stat : maxStat) {
			total += stat;
		}
		return total;
	}

	public void incCurStat(int i, int amount) {
		curStat[i] += amount;
		if (curStat[i] < 0) {
			curStat[i] = 0;
		}
	}

	public void incMaxStat(int i, int amount) {
		maxStat[i] += amount;
		if (maxStat[i] < 0) {
			maxStat[i] = 0;
		}
	}

	public void setFatigue(int fatigue) {
		this.fatigue = fatigue;
	}

	public int getFatigue() {
		return fatigue;
	}

	public void incExp(int i, int amount, boolean useFatigue) {
		useFatigue = true;
		if (useFatigue) {
			if (fatigue >= 100) {
				actionSender
						.sendMessage("@gre@You are too tired to gain experience, get some rest!");
				return;
			}
			if (fatigue >= 96) {
				actionSender
						.sendMessage("@gre@You start to feel tired, maybe you should rest soon.");
			}
			fatigue++;
			actionSender.sendFatigue();
		}
		if (isSubscriber()) {
			amount *= 1.5D;
		}
		if (getLocation().inWilderness() && i != 9 && i != 11 && i != 16
				&& i != 8) {
			amount *= 1.1D;
		}
		exp[i] += amount;
		if (exp[i] < 0) {
			exp[i] = 0;
		}
		int level = Formulae.experienceToLevel(exp[i]);
		if (level != maxStat[i]) {
			int advanced = level - maxStat[i];
			incCurStat(i, advanced);
			incMaxStat(i, advanced);
			actionSender.sendStat(i);
			actionSender.sendMessage("@gre@You just advanced " + advanced + " "
					+ Formulae.statArray[i] + " level!");
			actionSender.sendSound("advance");
			world.getDelayedEventHandler().add(new MiniEvent(this) {
				public void action() {
					owner.getActionSender().sendScreenshot();
					owner.actionSender.sendKills();
				}
			});
			int comb = Formulae.getCombatlevel(maxStat);
			if (comb != getCombatLevel()) {
				setCombatLevel(comb);
			}
		}
	}

	public int[] getExps() {
		return exp;
	}

	public int getExp(int id) {
		return exp[id];
	}

	public void setExp(int id, int lvl) {
		if (lvl < 0) {
			lvl = 0;
		}
		exp[id] = lvl;
	}

	public void setExp(int[] lvls) {
		exp = lvls;
	}

	public boolean equals(Object o) {
		if (o instanceof Player) {
			Player p = (Player) o;
			return usernameHash == p.getUsernameHash();
		}
		return false;
	}

}
