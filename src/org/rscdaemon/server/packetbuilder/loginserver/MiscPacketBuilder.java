package org.rscdaemon.server.packetbuilder.loginserver;

import org.rscdaemon.server.model.*;
import org.rscdaemon.server.LoginConnector;
import org.rscdaemon.server.packetbuilder.LSPacketBuilder;
import org.rscdaemon.server.net.Packet;
import org.rscdaemon.server.net.LSPacket;
import org.rscdaemon.server.packethandler.PlayerLogin;
import org.rscdaemon.server.packethandler.PacketHandler;
import org.rscdaemon.server.util.*;

import org.apache.mina.common.IoSession;

import java.util.List;
import java.util.ArrayList;

public class MiscPacketBuilder {
	/**
	 * World instance
	 */
	private World world = World.getWorld();
	/**
	 * Connector instance
	 */
	private final LoginConnector connector;
	/**
	 * List of packets waiting to be sent to the user
	 */
	private List<LSPacket> packets = new ArrayList<LSPacket>();

	public MiscPacketBuilder(LoginConnector connector) {
		this.connector = connector;
	}

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

	/**
	 * Tells the login server we are registered and lists players connected
	 * (should be 0 at startup)
	 */
	public void registerWorld() {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(1);
		s.setHandler(connector, new PacketHandler() {
			public void handlePacket(Packet p, IoSession session)
					throws Exception {
				connector.setRegistered(p.readByte() == 1);
			}
		});
		s.addShort(Config.SERVER_NUM);
		s.addBytes(Config.LS_PASSWORD.getBytes());
		EntityList<Player> players = world.getPlayers();
		s.addShort(players.size());
		for (Player player : players) {
			s.addLong(player.getUsernameHash());
			s.addLong(DataConversions.IPToLong(player.getCurrentIP()));
		}
		packets.add(s.toPacket());
	}

	public void unregisterWorld() {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(2);
		s.setHandler(connector, new PacketHandler() {
			public void handlePacket(Packet p, IoSession session)
					throws Exception {
				session.close().join();
				world.getServer().unbind();
				world.getServer().getEngine().kill();
			}
		});
		packets.add(s.toPacket());
	}

	public void logKill(long user, long killed, boolean stake) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(3);
		s.addLong(user);
		s.addLong(killed);
		s.addByte((byte) (stake ? 2 : 1));
		packets.add(s.toPacket());
	}

	public void banPlayer(final Player mod, final long user, final boolean ban) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(ban ? 4 : 5);
		s.addLong(user);
		s.setHandler(connector, new PacketHandler() {
			public void handlePacket(Packet p, IoSession session)
					throws Exception {
				if (p.readByte() == 1) {
					Logger.mod(mod.getUsername() + " "
							+ (ban ? "banned" : "unbanned") + " "
							+ DataConversions.hashToUsername(user));
				}
				mod.getActionSender().sendMessage(p.readString());
			}
		});
		packets.add(s.toPacket());
	}

	public void setStage(final int questId, final int stage, final long user) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(16);
		s.addLong(user);
		s.addShort(questId);
		s.addShort(stage);
		packets.add(s.toPacket());
	}

	public void saveQuest(final int questId, final int stage, final long user) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(17);
		s.addLong(user);
		s.addShort(questId);
		s.addShort(stage);
		packets.add(s.toPacket());
	}

	public void loadQuest(final Player owner, final int questId, final long user) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(18);
		s.addLong(user);
		s.addShort(questId);
		s.setHandler(connector, new PacketHandler() {
			public void handlePacket(Packet p, IoSession session)
					throws Exception {
				int stage = p.readShort();
				// System.out.println("Calling quest load method..."+stage);
				owner.getQuestManager().setStages(stage, questId);
			}
		});
		packets.add(s.toPacket());
		// System.out.println("Packet sent to Login Server.");
	}

	/*
	 * public void loadQuest(final int questId, final long user) {
	 * LSPacketBuilder s = new LSPacketBuilder(); s.setID(18); s.addLong(user);
	 * s.addShort(questId); s.addShort(stage); s.setHandler(connector, new
	 * PacketHandler() { public void handlePacket(Packet p, IoSession session)
	 * throws Exception { if(p.readByte() == 1) { Logger.mod(mod.getUsername() +
	 * " requested information on " + DataConversions.hashToUsername(user));
	 * 
	 * int world = p.readShort(); Point location = Point.location(p.readShort(),
	 * p.readShort()); long loginDate = p.readLong(); int lastMoved =
	 * (int)((System.currentTimeMillis() - p.readLong()) / 1000); boolean
	 * chatBlock = p.readByte() == 1; int fatigue = p.readShort(); String state
	 * = p.readString();
	 * 
	 * mod.getActionSender().sendAlert("@whi@" +
	 * DataConversions.hashToUsername(user) + " is currently on world @or1@" +
	 * world + "@whi@ at @or1@" + location.toString() + "@whi@ (@or1@" +
	 * location.getDescription() + "@whi@). State is @or1@" + state +
	 * "@whi@. Logged in @or1@" + DataConversions.timeSince(loginDate) +
	 * "@whi@ ago. Last moved @or1@" + lastMoved +
	 * " secs @whi@ ago. Chat block is @or1@" + (chatBlock ? "on" : "off") +
	 * "@whi@. Fatigue is at @or1@" + fatigue + "@whi@.", false); } else {
	 * mod.getActionSender
	 * ().sendMessage("Invalid player, maybe they aren't currently online?"); }
	 * } }); packets.add(s.toPacket()); }
	 */
	public void requestPlayerInfo(final Player mod, final long user) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(8);
		s.addLong(user);
		s.setHandler(connector, new PacketHandler() {
			public void handlePacket(Packet p, IoSession session)
					throws Exception {
				if (p.readByte() == 1) {
					Logger.mod(mod.getUsername() + " requested information on "
							+ DataConversions.hashToUsername(user));

					int world = p.readShort();
					Point location = Point.location(p.readShort(),
							p.readShort());
					long loginDate = p.readLong();
					int lastMoved = (int) ((System.currentTimeMillis() - p
							.readLong()) / 1000);
					boolean chatBlock = p.readByte() == 1;
					int fatigue = p.readShort();
					int kills = p.readShort();
					int deaths = p.readShort();
					String state = p.readString();

					mod.getActionSender().sendAlert(
							"@whi@" + DataConversions.hashToUsername(user)
									+ " is currently on world @or1@" + world
									+ "@whi@ at @or1@" + location.toString()
									+ "@whi@ (@or1@"
									+ location.getDescription()
									+ "@whi@). State is @or1@" + state
									+ "@whi@. Logged in @or1@"
									+ DataConversions.timeSince(loginDate)
									+ "@whi@ ago. Last moved @or1@" + lastMoved
									+ " secs @whi@ ago. Chat block is @or1@"
									+ (chatBlock ? "on" : "off")
									+ "@whi@. Fatigue is at @or1@" + fatigue
									+ "@whi@.", false);
				} else {
					mod.getActionSender()
							.sendMessage(
									"Invalid player, maybe they aren't currently online?");
				}
			}
		});
		packets.add(s.toPacket());
	}

	public void saveProfiles() {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(9);
		s.setHandler(connector, new PacketHandler() {
			public void handlePacket(Packet p, IoSession session)
					throws Exception {
				if (p.readByte() != 1) {
					Logger.error("Error saving all profiles!");
				}
			}
		});
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

	public void addFriend(long user, long friend) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(11);
		s.addLong(user);
		s.addLong(friend);
		packets.add(s.toPacket());
	}

	public void removeFriend(long user, long friend) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(12);
		s.addLong(user);
		s.addLong(friend);
		packets.add(s.toPacket());
	}

	public void addIgnore(long user, long friend) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(13);
		s.addLong(user);
		s.addLong(friend);
		packets.add(s.toPacket());
	}

	public void removeIgnore(long user, long friend) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(14);
		s.addLong(user);
		s.addLong(friend);
		packets.add(s.toPacket());
	}

	public void reportUser(long user, long reported, byte reason) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(15);
		s.addLong(user);
		s.addLong(reported);
		s.addByte(reason);
		packets.add(s.toPacket());
	}

	public void playerLogout(long user) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(30);
		s.addLong(user);
		packets.add(s.toPacket());
	}

	public void playerLogin(Player player) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(31);
		s.setHandler(connector, new PlayerLogin(player));
		s.addLong(player.getUsernameHash());
		s.addLong(DataConversions.IPToLong(player.getCurrentIP()));
		s.addBytes(DataConversions.md5(player.getPassword()).getBytes());
		s.addBytes(player.getClassName().getBytes());
		packets.add(s.toPacket());
	}

	public void logAction(String message, int type) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(32);
		s.addByte((byte) type);
		s.addBytes(message.getBytes());
		packets.add(s.toPacket());
	}
}