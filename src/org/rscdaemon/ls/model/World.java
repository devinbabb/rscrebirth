package org.rscdaemon.ls.model;

import org.rscdaemon.ls.Server;
import org.rscdaemon.ls.packetbuilder.loginserver.MiscPacketBuilder;
import org.rscdaemon.ls.util.DataConversions;

import org.apache.mina.common.IoSession;

import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.Collection;
import java.sql.ResultSet;

/**
 * Handles the communications between the world and ls
 * 
 * @author Devin
 */

public class World {
	private IoSession session;
	private int id = -1;
	private MiscPacketBuilder actionSender = new MiscPacketBuilder();
	private TreeMap<Long, Integer> players = new TreeMap<Long, Integer>();
	private TreeMap<Long, PlayerSave> saves = new TreeMap<Long, PlayerSave>();

	public Collection<Entry<Long, PlayerSave>> getAssosiatedSaves() {
		return saves.entrySet();
	}

	public void assosiateSave(PlayerSave save) {
		saves.put(save.getUser(), save);
	}

	public void unassosiateSave(PlayerSave save) {
		saves.remove(save.getUser());
	}

	public PlayerSave getSave(long user) {
		return saves.get(user);
	}

	public World(int id, IoSession session) {
		this.id = id;
		setSession(session);
	}

	public void setSession(IoSession session) {
		this.session = session;
	}

	public IoSession getSession() {
		return session;
	}

	public MiscPacketBuilder getActionSender() {
		return actionSender;
	}

	public int getID() {
		return id;
	}

	public void registerPlayer(long user, String ip) {
		Server server = Server.getServer();
		ResultSet result;
		try {
			result = Server.db
					.getQuery("SELECT block_private FROM `players` WHERE `username`=\""
							+ DataConversions.hashToUsername(user) + "\"");
			if (!result.next()) {
				return;
			}
			// int owner = result.getInt("owner");
			boolean blockPrivate = result.getInt("block_private") == 1;

			result = Server.db
					.getQuery("SELECT user FROM `friends` WHERE `friend`="
							+ user
							+ (blockPrivate ? " AND user IN (SELECT friend FROM `friends` WHERE `user`="
									+ user + ")"
									: ""));
			while (result.next()) {
				long friend = result.getLong("user");
				World w = server.findWorld(friend);
				if (w != null) {
					w.getActionSender().friendLogin(friend, user, id);
				}
			}
			long now = (int) (System.currentTimeMillis() / 1000);
			Server.db
					.updateQuery("INSERT INTO `logins`(`user`, `time`, `ip`) VALUES('"
							+ user + "', '" + now + "', '" + ip + "')");
			Server.db.updateQuery("UPDATE `players` SET login_date=" + now
					+ ", login_ip='" + ip + "' WHERE username=\""
					+ DataConversions.hashToUsername(user) + "\"");
			Server.db.updateQuery("UPDATE `players` SET loggedin=" + 1
					+ ", world='" + id + "' WHERE username=\""
					+ DataConversions.hashToUsername(user) + "\"");

			players.put(user, 1);
			System.out.println("Added " + DataConversions.hashToUsername(user)
					+ " to world " + id);
		} catch (Exception e) {
			Server.error(e);
		}
	}

	public void unregisterPlayer(long user) {
		for (World w : Server.getServer().getWorlds()) {
			w.getActionSender().friendLogout(user);
		}
		try {
			Server.db.updateQuery("UPDATE `players` SET loggedin=" + 0
					+ ", world='" + 0 + "' WHERE username=\""
					+ DataConversions.hashToUsername(user) + "\"");
			players.remove(user);
			System.out.println("Removed "
					+ DataConversions.hashToUsername(user) + " from world "
					+ id);
		} catch (Exception e) {
			Server.error(e);
		}
	}

	public void clearPlayers() {
		for (Entry<Long, Integer> player : getPlayers()) {
			long user = player.getKey();
			for (World w : Server.getServer().getWorlds()) {
				w.getActionSender().friendLogout(user);
			}
			System.out.println("Removed "
					+ DataConversions.hashToUsername(user) + " from world "
					+ id);
		}
		players.clear();
	}

	public Collection<Entry<Long, Integer>> getPlayers() {
		return players.entrySet();
	}

	public boolean hasPlayer(long user) {
		return players.containsKey(user);
	}

}