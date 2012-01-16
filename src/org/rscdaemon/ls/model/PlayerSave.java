package org.rscdaemon.ls.model;

import org.rscdaemon.ls.Server;
import org.rscdaemon.ls.util.DataConversions;

import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Saves the players current data.
 * 
 * @author Devin
 */

public class PlayerSave {
	public static final String[] statArray = { "attack", "defense", "strength",
			"hits", "ranged", "prayer", "magic", "cooking", "woodcut",
			"fletching", "fishing", "firemaking", "crafting", "smithing",
			"mining", "herblaw", "agility", "thieving" };

	private long user;
	private int group;
	private long subExpires;
	private long[] exp = new long[18];
	private int[] lvl = new int[18];
	private ArrayList<InvItem> invItems = new ArrayList<InvItem>();
	private ArrayList<BankItem> bankItems = new ArrayList<BankItem>();
	private long lastUpdate = 0;
	private ArrayList<Long> friendList = new ArrayList<Long>();
	private ArrayList<Long> ignoreList = new ArrayList<Long>();

	private byte hairColour, topColour, trouserColour, skinColour, headSprite,
			bodySprite;
	private boolean male;
	private long skulled;
	private int x, y;
	private int fatigue, qp, kills, deaths;
	private byte combatStyle;
	private int combat, skillTotal;
	private long loginDate, loginIP;
	private boolean cameraAuto, oneMouse, soundOff, showRoof, autoScreenshot,
			combatWindow;
	private boolean blockChat, blockPrivate, blockTrade, blockDuel;

	public static PlayerSave loadPlayer(long user) {
		PlayerSave save = new PlayerSave(user);
		ResultSet result;

		try {
			result = Server.db
					.getQuery("SELECT * FROM `players` WHERE `username`=\""
							+ DataConversions.hashToUsername(save.getUser())
							+ "\"");
			if (!result.next()) {
				return save;
			}

			save.setOwner(result.getInt("group_id"),
					result.getLong("sub_expires"));

			save.setLogin(result.getLong("login_date"),
					DataConversions.IPToLong(result.getString("login_ip")));
			save.setLocation(result.getInt("x"), result.getInt("y"));

			save.setFatigue(result.getInt("fatigue"));
			save.setKills(result.getInt("kills"));
			save.setDeaths(result.getInt("deaths"));
			save.setQuestPoints(result.getInt("qp"));
			save.setCombatStyle((byte) result.getInt("combatstyle"));

			save.setPrivacy(result.getInt("block_chat") == 1,
					result.getInt("block_private") == 1,
					result.getInt("block_trade") == 1,
					result.getInt("block_duel") == 1);
			save.setSettings(result.getInt("cameraauto") == 1,
					result.getInt("onemouse") == 1,
					result.getInt("soundoff") == 1,
					result.getInt("showroof") == 1,
					result.getInt("autoscreenshot") == 1,
					result.getInt("combatwindow") == 1);

			save.setAppearance((byte) result.getInt("haircolour"),
					(byte) result.getInt("topcolour"),
					(byte) result.getInt("trousercolour"),
					(byte) result.getInt("skincolour"),
					(byte) result.getInt("headsprite"),
					(byte) result.getInt("bodysprite"),
					result.getInt("male") == 1, result.getInt("skulled"));

			result = Server.db
					.getQuery("SELECT * FROM `experience` WHERE `user`=\""
							+ DataConversions.hashToUsername(save.getUser())
							+ "\"");
			if (!result.next()) {
				return save;
			}
			for (int i = 0; i < 18; i++) {
				save.setExp(i, result.getInt("exp_" + statArray[i]));
			}

			result = Server.db
					.getQuery("SELECT * FROM `curstats` WHERE `user`=\""
							+ DataConversions.hashToUsername(save.getUser())
							+ "\"");
			if (!result.next()) {
				return save;
			}
			for (int i = 0; i < 18; i++) {
				save.setLvl(i, result.getInt("cur_" + statArray[i]));
			}

			result = Server.db
					.getQuery("SELECT * FROM `invitems` WHERE `user`=\""
							+ DataConversions.hashToUsername(save.getUser())
							+ "\"" + " ORDER BY `slot` ASC");
			while (result.next()) {
				save.addInvItem(result.getInt("id"), result.getInt("amount"),
						result.getInt("wielded") == 1);
			}

			result = Server.db.getQuery("SELECT * FROM `bank` WHERE `user`=\""
					+ DataConversions.hashToUsername(save.getUser()) + "\""
					+ " ORDER BY `slot` ASC");
			while (result.next()) {
				save.addBankItem(result.getInt("id"), result.getInt("amount"));
			}

			result = Server.db.getQuery("SELECT * FROM `friends` WHERE `user`="
					+ save.getUser());
			while (result.next()) {
				save.addFriend(result.getLong("friend"));
			}

			result = Server.db.getQuery("SELECT * FROM `ignores` WHERE `user`="
					+ save.getUser());
			while (result.next()) {
				save.addIgnore(result.getLong("ignore"));
			}
		} catch (SQLException e) {
			Server.error("SQL Exception Loading "
					+ DataConversions.hashToUsername(user) + ": "
					+ e.getMessage());
		}

		return save;
	}

	private PlayerSave(long user) {
		this.user = user;
	}

	/*
	 * public void setOwner(int owner) { // this.owner = owner; }
	 */
	public void setOwner(int group, long subExpires) {
		// this.owner = owner;
		this.group = group;
		this.subExpires = subExpires;
	}

	public long getUser() {
		return user;
	}

	public String getUsername() {
		return DataConversions.hashToUsername(user);
	}

	/*
	 * public int getOwner() { return owner; }
	 */
	public int getGroup() {
		return group;
	}

	public long getSubscriptionExpires() {
		return subExpires;
	}

	public long getLastIP() {
		return loginIP;
	}

	public long getLastLogin() {
		return loginDate;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getFatigue() {
		return fatigue;
	}

	public int getQuestPoints() {
		return qp;
	}

	public int getDeaths() {
		return deaths;
	}

	public int getKills() {
		return kills;
	}

	public byte getCombatStyle() {
		return combatStyle;
	}

	public boolean blockChat() {
		return blockChat;
	}

	public boolean blockPrivate() {
		return blockPrivate;
	}

	public boolean blockTrade() {
		return blockTrade;
	}

	public boolean blockDuel() {
		return blockDuel;
	}

	public boolean cameraAuto() {
		return cameraAuto;
	}

	public boolean oneMouse() {
		return oneMouse;
	}

	public boolean soundOff() {
		return soundOff;
	}

	public boolean showRoof() {
		return showRoof;
	}

	public boolean autoScreenshot() {
		return autoScreenshot;
	}

	public boolean combatWindow() {
		return combatWindow;
	}

	public int getHairColour() {
		return hairColour;
	}

	public int getTopColour() {
		return topColour;
	}

	public int getTrouserColour() {
		return trouserColour;
	}

	public int getSkinColour() {
		return skinColour;
	}

	public int getHeadSprite() {
		return headSprite;
	}

	public int getBodySprite() {
		return bodySprite;
	}

	public boolean isMale() {
		return male;
	}

	public long getSkullTime() {
		return skulled;
	}

	public long getExp(int i) {
		return exp[i];
	}

	public int getStat(int i) {
		return lvl[i];
	}

	public int getInvCount() {
		return invItems.size();
	}

	public InvItem getInvItem(int i) {
		return invItems.get(i);
	}

	public int getBankCount() {
		return bankItems.size();
	}

	public BankItem getBankItem(int i) {
		return bankItems.get(i);
	}

	public int getFriendCount() {
		return friendList.size();
	}

	public long getFriend(int i) {
		return friendList.get(i);
	}

	public void addFriend(long friend) {
		friendList.add(friend);
	}

	public void removeFriend(long friend) {
		friendList.remove(friend);
	}

	public int getIgnoreCount() {
		return ignoreList.size();
	}

	public long getIgnore(int i) {
		return ignoreList.get(i);
	}

	public void addIgnore(long friend) {
		ignoreList.add(friend);
	}

	public void removeIgnore(long friend) {
		ignoreList.remove(friend);
	}

	public void setPrivacy(boolean blockChat, boolean blockPrivate,
			boolean blockTrade, boolean blockDuel) {
		this.blockChat = blockChat;
		this.blockPrivate = blockPrivate;
		this.blockTrade = blockTrade;
		this.blockDuel = blockDuel;
	}

	public void setPrivacySetting(int idx, boolean on) {
		switch (idx) {
		case 0:
			blockChat = on;
			break;
		case 1:
			blockPrivate = on;
			break;
		case 2:
			blockTrade = on;
			break;
		case 3:
			blockDuel = on;
			break;
		}
	}

	public void setSettings(boolean cameraAuto, boolean oneMouse,
			boolean soundOff, boolean showRoof, boolean autoScreenshot,
			boolean combatWindow) {
		this.cameraAuto = cameraAuto;
		this.oneMouse = oneMouse;
		this.soundOff = soundOff;
		this.showRoof = showRoof;
		this.autoScreenshot = autoScreenshot;
		this.combatWindow = combatWindow;
	}

	public void setGameSetting(int idx, boolean on) {
		switch (idx) {
		case 0:
			cameraAuto = on;
			break;
		case 2:
			oneMouse = on;
			break;
		case 3:
			soundOff = on;
			break;
		case 4:
			showRoof = on;
			break;
		case 5:
			autoScreenshot = on;
			break;
		case 6:
			combatWindow = on;
			break;
		}
	}

	public void setLogin(long loginDate, long loginIP) {
		this.loginDate = loginDate;
		this.loginIP = loginIP;
	}

	public void setTotals(int combat, int skillTotal) {
		this.combat = combat;
		this.skillTotal = skillTotal;
	}

	public void setCombatStyle(byte combatStyle) {
		this.combatStyle = combatStyle;
	}

	public void setFatigue(int fatigue) {
		this.fatigue = fatigue;
	}

	public void setQuestPoints(int qp) {
		this.qp = qp;
	}

	public void setKills(int kills) {
		this.kills = kills;
	}

	public void setDeaths(int deaths) {
		this.deaths = deaths;
	}

	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void setAppearance(byte hairColour, byte topColour,
			byte trouserColour, byte skinColour, byte headSprite,
			byte bodySprite, boolean male, long skulled) {
		this.hairColour = hairColour;
		this.topColour = topColour;
		this.trouserColour = trouserColour;
		this.skinColour = skinColour;
		this.headSprite = headSprite;
		this.bodySprite = bodySprite;
		this.male = male;
		this.skulled = skulled;
	}

	public void setExp(int stat, long exp) {
		this.exp[stat] = exp;
	}

	public void setLvl(int stat, int lvl) {
		this.lvl[stat] = lvl;
	}

	public void setStat(int stat, long exp, int lvl) {
		this.exp[stat] = exp;
		this.lvl[stat] = lvl;
	}

	public void clearInvItems() {
		invItems.clear();
	}

	public void addInvItem(int id, int amount, boolean wielded) {
		invItems.add(new InvItem(id, amount, wielded));
	}

	public void clearBankItems() {
		bankItems.clear();
	}

	public void addBankItem(int id, int amount) {
		bankItems.add(new BankItem(id, amount));
	}

	public void setLastUpdate(long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public long getLastUpdate() {
		return lastUpdate;
	}

	public boolean save() {
		try {
			String query;

			Server.db.updateQuery("DELETE FROM `bank` WHERE `user`=\""
					+ DataConversions.hashToUsername(user) + "\"");
			if (bankItems.size() > 0) {
				query = "INSERT INTO `bank`(`user`, `id`, `amount`, `slot`) VALUES";
				int slot = 0;
				for (BankItem item : bankItems) {
					query += "(\"" + DataConversions.hashToUsername(user)
							+ "\", '" + item.getID() + "', '"
							+ item.getAmount() + "', '" + (slot++) + "'),";
				}
				Server.db.updateQuery(query.substring(0, query.length() - 1));
			}

			Server.db.updateQuery("DELETE FROM `invitems` WHERE `user`=\""
					+ DataConversions.hashToUsername(user) + "\"");

			ResultSet result = Server.db
					.getQuery("Select 1 FROM `players` WHERE `username`=\""
							+ DataConversions.hashToUsername(user) + "\"");
			if (!result.next()) {
				return false;
			}
			Server.db.updateQuery("UPDATE `players` SET `combat`=" + combat
					+ ", skill_total=" + skillTotal + ", `x`=" + x + ", `y`='"
					+ y + "', `qp`='" + qp + "', `kills`='" + kills
					+ "', `deaths`='" + deaths + "', `fatigue`='" + fatigue
					+ "', `haircolour`=" + hairColour + ", `topcolour`="
					+ topColour + ", `trousercolour`=" + trouserColour
					+ ", `skincolour`=" + skinColour + ", `headsprite`="
					+ headSprite + ", `bodysprite`=" + bodySprite + ", `male`="
					+ (male ? 1 : 0) + ", `skulled`=" + skulled
					+ ", `combatstyle`=" + combatStyle + " WHERE `username`=\""
					+ DataConversions.hashToUsername(user) + "\"");

			query = "UPDATE `experience` SET ";
			for (int i = 0; i < 18; i++) {
				query += "`exp_" + statArray[i] + "`=" + exp[i] + ",";
			}
			Server.db.updateQuery(query.substring(0, query.length() - 1)
					+ " WHERE `user`=\"" + DataConversions.hashToUsername(user)
					+ "\"");

			query = "UPDATE `curstats` SET ";
			for (int i = 0; i < 18; i++) {
				query += "`cur_" + statArray[i] + "`=" + lvl[i] + ",";
			}
			Server.db.updateQuery(query.substring(0, query.length() - 1)
					+ " WHERE `user`=\"" + DataConversions.hashToUsername(user)
					+ "\"");

			if (invItems.size() > 0) {
				query = "INSERT INTO `invitems`(`user`, `id`, `amount`, `wielded`, `slot`) VALUES";
				int slot = 0;
				for (InvItem item : invItems) {
					query += "(\"" + DataConversions.hashToUsername(user)
							+ "\", '" + item.getID() + "', '"
							+ item.getAmount() + "', '"
							+ (item.isWielded() ? 1 : 0) + "', '" + (slot++)
							+ "'),";
				}
				Server.db.updateQuery(query.substring(0, query.length() - 1));
			}

			return true;
		} catch (SQLException e) {
			Server.error(e);
			return false;
		}
	}
}