package org.rscdaemon.server.packethandler.client;

import org.rscdaemon.server.packethandler.PacketHandler;
import org.rscdaemon.server.packetbuilder.loginserver.MiscPacketBuilder;
import org.rscdaemon.server.model.*;
import org.rscdaemon.server.net.Packet;
import org.rscdaemon.server.util.DataConversions;
import org.rscdaemon.server.entityhandling.EntityHandler;
import org.rscdaemon.server.util.Logger;
import org.rscdaemon.server.util.Formulae;
import org.rscdaemon.server.event.SingleEvent;
import org.rscdaemon.server.states.CombatState;
import org.apache.mina.common.IoSession;
import java.io.*;
import java.util.*;

import org.apache.mina.common.IoSession;

public class CommandHandler implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();
	String lastplayer;
	public long lasttime;
	public long lastmessage;

	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player) session.getAttachment();
		if (player.isBusy()) {
			player.resetPath();
			return;
		}
		player.resetAll();
		String s = new String(p.getData()).trim();
		int firstSpace = s.indexOf(" ");
		String cmd = s;
		String[] args = new String[0];
		if (firstSpace != -1) {
			cmd = s.substring(0, firstSpace).trim();
			args = s.substring(firstSpace + 1).trim().split(" ");
		}
		try {
			handleCommand(cmd.toLowerCase(), args, player, s);
		} catch (Exception e) {
		}
	}

	public void handleCommand(String cmd, String[] args, Player player,
			String raw) throws Exception {
		MiscPacketBuilder loginServer = world.getServer().getLoginConnector()
				.getActionSender();
		if (cmd.equals("stuck")) {
			if (player.getLocation().inModRoom() && !player.isAdmin()) {
				player.getActionSender().sendMessage("Nice try.");
			} else if (player.getLocation().inJail() && !player.isAdmin()) {
				player.getActionSender().sendMessage("Nice try.");
			} else if (player.getLocation().inWilderness() && !player.isAdmin()) {
				player.getActionSender().sendMessage("Nice try.");
			} else if (!player.inCombat()
					&& System.currentTimeMillis() - player.getCombatTimer() > 7000) {
				Logger.mod(player.getUsername() + " used stuck");
				player.setCastTimer();
				player.teleport(220, 440, true);
			} else {
				player.getActionSender().sendMessage(
						"You cannot use ::stuck for 7 seconds after combat");
			}
			return;
		}
		if (cmd.equals("fatigue")) {
			player.setFatigue(100);
			player.getActionSender().sendFatigue();
			player.getActionSender().sendMessage(
					"Your fatigue has been set to 100.");
			return;
		}
		if (cmd.equals("buy")) {
			if (player.getLocation().inWilderness()) {
				player.getActionSender().sendMessage(
						"Cannot use ::buy in wilderness");
				return;
			}
			if (args.length < 1 || args.length > 2) {
				player.getActionSender().sendMessage(
						"Invalid args. Syntax: BUY id [amount]");
				return;
			}
			int id = Integer.parseInt(args[0]);
			int amount = 1;
			int[] ids = { 10, 133, 504, 503, 502, 422, 193, 25, 26, 27, 47, 48,
					49, 235, 218, 1237, 51, 53, 54, 56, 57, 58, 61, 134, 139,
					175, 176, 177, 178, 180, 181, 201, 208, 212, 213, 217, 231,
					232, 233, 234, 237, 238, 239, 240, 242, 244, 245, 246, 247,
					260, 264, 266, 271, 272, 273, 274, 275, 282, 318, 322, 331,
					347, 353, 360, 368, 371, 368, 371, 374, 382, 387, 390, 391,
					392, 393, 394, 395, 410, 411, 412, 413, 414, 415, 416, 417,
					418, 421, 444, 445, 475, 476, 477, 479, 481, 482, 484, 485,
					487, 488, 490, 491, 493, 494, 496, 497, 499, 500, 505, 506,
					507, 508, 510, 515, 516, 517, 518, 519, 520, 521, 522, 523,
					524, 525, 526, 527, 528, 529, 530, 531, 532, 533, 534, 535,
					536, 538, 539, 540, 541, 547, 557, 558, 566, 567, 568, 569,
					570, 571, 572, 573, 574, 575, 576, 577, 578, 579, 580, 581,
					582, 583, 584, 585, 586, 587, 588, 595, 596, 598, 599, 600,
					601, 602, 603, 604, 605, 610, 620, 621, 622, 623, 624, 625,
					628, 629, 630, 631, 647, 677, 678, 679, 680, 681, 686, 687,
					688, 689, 694, 695, 696, 697, 704, 705, 706, 710, 711, 712,
					713, 714, 715, 716, 721, 722, 723, 724, 737, 747, 752, 753,
					755, 756, 758, 759, 762, 763, 764, 766, 767, 768, 769, 770,
					771, 772, 773, 774, 775, 776, 777, 778, 779, 780, 781, 783,
					784, 785, 786, 787, 788, 789, 790, 791, 792, 793, 794, 796,
					797, 798, 799, 800, 803, 804, 805, 806, 809, 810, 811, 812,
					813, 815, 816, 817, 818, 819, 820, 821, 822, 823, 824, 828,
					831, 832, 833, 834, 835, 851, 852, 853, 854, 866, 867, 868,
					869, 870, 871, 873, 874, 875, 876, 877, 878, 879, 880, 881,
					882, 883, 884, 885, 886, 887, 888, 889, 890, 891, 892, 893,
					894, 895, 896, 897, 898, 899, 900, 901, 902, 903, 904, 905,
					906, 907, 908, 909, 910, 912, 913, 914, 915, 916, 917, 918,
					919, 920, 921, 922, 925, 926, 927, 928, 929, 930, 931, 932,
					933, 934, 935, 937, 938, 939, 940, 941, 942, 943, 944, 945,
					946, 947, 948, 949, 950, 951, 952, 953, 954, 955, 956, 957,
					958, 959, 960, 961, 962, 963, 964, 965, 971, 972, 973, 974,
					975, 976, 977, 978, 979, 980, 981, 982, 983, 984, 985, 986,
					987, 988, 989, 990, 991, 992, 993, 994, 995, 996, 997, 998,
					999, 1001, 1002, 1003, 1004, 1005, 1006, 1007, 1008, 1012,
					1017, 1018, 1021, 1025, 1030, 1032, 1033, 1034, 1035, 1036,
					1037, 1038, 1039, 1040, 1041, 1042, 1043, 1044, 1045, 1046,
					1047, 1048, 1049, 1050, 1051, 1052, 1053, 1054, 1055, 1056,
					1057, 1058, 1059, 1060, 1061, 1070, 1071, 1072, 1073, 1074,
					1083, 1084, 1085, 1086, 1093, 1093, 1094, 1095, 1096, 1097,
					1098, 1099, 1100, 1115, 1116, 1117, 1118, 1119, 1120, 1121,
					1141, 1142, 1143, 1144, 1145, 1146, 1147, 1148, 1149, 1150,
					1151, 1152, 1153, 1154, 1155, 1156, 1157, 1158, 1159, 1160,
					1161, 1162, 1163, 1164, 1165, 1166, 1167, 1168, 1169, 1170,
					1171, 1172, 1173, 1174, 1175, 1176, 1177, 1178, 1179, 1180,
					1181, 1182, 1183, 1184, 1185, 1186, 1187, 1188, 1195, 1196,
					1197, 1198, 1199, 1204, 1205, 1206, 1207, 1208, 1209, 1210,
					1211, 1212, 1219, 1220, 1221, 1222, 1223, 1224, 1225, 1226,
					1227, 1228, 1229, 1230, 1231, 1232, 1233, 1235, 1238, 1239,
					1240, 1241, 1242, 1243, 1244, 1245, 1246, 1247, 1248, 1249,
					1250, 1251, 1252, 1253, 1254, 1265, 1266, 1267, 1270, 1271,
					1272, 1273, 1274, 1275, 1276, 1277, 1278, 1279, 1283, 1284,
					1285, 1286, 1287, 1289, 1290, 1291, 1292, 1293, 1294, 1295,
					1296, 1297, 1298, 1299, 1300, 1301, 1302, 1303, 1304, 1305,
					1306, 1307, 1308, 1309, 1310, 1311, 1312, 1313, 1314, 1315,
					1316, 1317, 1318, 1319, 1320, 1321, 1322, 1323, 1324, 1325,
					1326, 1327, 1328, 1329, 1330, 1331, 1332, 1333, 1334, 369,
					372, 454, 455, 456, 457, 458, 458, 459, 460, 461, 462, 463,
					545, 1190, 1192, 287, 286, 285, 284, 283, 408, 409, 541 };
			for (int i = 0; i < ids.length; i++) {
				if (ids[i] == id) {
					player.getActionSender().sendMessage(
							"Sorry, you cannot buy that item.");
					return;
				}
			}
			if (args.length == 2 && EntityHandler.getItemDef(id).isStackable()) {
				amount = Integer.parseInt(args[1]);
			}
			if (amount <= 0) {
				player.getActionSender().sendMessage("Invalid amount");
				return;
			}
			if (player.getInventory().countId(10) < EntityHandler
					.getItemDef(id).getBasePrice() * amount) {
				player.getActionSender().sendMessage(
						"You don't have enough money to buy this!");
				return;
			} else {

				InvItem item = new InvItem(id, amount);
				player.getInventory().remove(10,
						(EntityHandler.getItemDef(id).getBasePrice() * amount));
				player.getInventory().add(item);
				player.getActionSender().sendInventory();
				player.getActionSender().sendMessage(
						"You have just purchased " + amount + " "
								+ item.getDef().getName() + "(s)");
			}
			return;
		}
		if (cmd.equals("appearance")) {
			player.setChangingAppearance(true);
			player.getActionSender().sendAppearanceScreen();
			return;
		}
		if (cmd.equals("skull")) {
			int length = 20;
			// try { length = Integer.parseInt(args[0]); } catch(Exception e) {
			// }
			player.addSkull(length * 60000);
			return;
		}
		if (cmd.equals("say")) {
			boolean waittime = false;
			if (lasttime == 0L) {
				lasttime = System.currentTimeMillis();
			}
			ArrayList informOfChatMessage = new ArrayList();
			Player p;
			for (Iterator i$ = world.getPlayers().iterator(); i$.hasNext(); informOfChatMessage
					.add(p)) {
				p = (Player) i$.next();
			}
			String newStr = "@gre@";
			for (int i = 0; i < args.length; i++) {
				newStr = (new StringBuilder()).append(newStr).append(args[i])
						.append(" ").toString();
			}
			for (int msg = 0; msg < ChatMessage.abusiveWords.length; msg++) {
				String abusiveReplaceString = "";
				for (int msgs = 0; msgs < ChatMessage.abusiveWords[msg]
						.length(); msgs++) {
					abusiveReplaceString += "*";
				}
				newStr = newStr.replaceAll("(?i)"
						+ ChatMessage.abusiveWords[msg], abusiveReplaceString);
			}
			if (player.isAdmin()) {
				newStr = (new StringBuilder()).append("@que@@dre@[ADMIN]")
						.append(player.getUsername()).append(": ")
						.append(newStr).toString();
			} else if (player.isMod()) {
				newStr = (new StringBuilder()).append("@que@")
						.append(player.getUsername()).append(": ")
						.append(newStr).toString();
			} else if (player.isPMod()) {
				newStr = (new StringBuilder()).append("@que@")
						.append(player.getUsername()).append(": ")
						.append(newStr).toString();
			} else if (System.currentTimeMillis() - lasttime > 120000L
					|| lastplayer != player.getUsername()) {
				newStr = (new StringBuilder()).append("@que@")
						.append(player.getUsername()).append(": ")
						.append(newStr).toString();
			} else {
				long timeremaining = 120L - (System.currentTimeMillis() - lasttime) / 1000L;
				player.getActionSender().sendMessage(
						(new StringBuilder()).append("You need to wait ")
								.append(timeremaining)
								.append(" seconds before using ::say again.")
								.toString());
				waittime = true;
			}
			if (!waittime) {
				lasttime = System.currentTimeMillis();
				lastplayer = player.getUsername();
				Player pl;
				for (Iterator i$ = informOfChatMessage.iterator(); i$.hasNext(); pl
						.getActionSender().sendMessage(newStr)) {
					pl = (Player) i$.next();
				}

			}
			return;
		}
		if (cmd.equals("online")) {
			int playersInWilderness = 0;
			for (Player p : world.getPlayers()) {
				if (p.getLocation().inWilderness()) {
					playersInWilderness++;
				}
			}
			player.getActionSender().sendMessage(
					"@yel@Players online:@whi@ " + world.countPlayers()
							+ ". @red@Players in the Wilderness@whi@: "
							+ playersInWilderness + "");
			return;
		}

		if (cmd.equals("commands") && player.isAdmin()) {
			String commands = "";
			commands = "online, onlinelist, stuck, fatigue, buy, appearance, skull, modroom, info, jail, free, mute, kick, ban, ipban, info, say, yell, timeout, teleport, npc, item, object, door, dropall, shutdown, update, summon, goto, adminlocationlist";
			player.getActionSender().sendAlert("Commands: " + commands, false);
			return;
		}
		if (cmd.equals("commands") && player.isPMod()) {
			String commands = "";
			commands = "::online, ::onlinelist, ::stuck, ::say, ::fatigue, ::buy ITEMNUMBER QUANTITY, appearance, say";
			player.getActionSender().sendAlert("Commands: " + commands, false);
			return;
		}
		if (cmd.equals("commands") && player.isMod()) {
			String commands = "";
			commands = "online, onlinelist, stuck, fatigue, buy, appearance, modroom, info, jail, free, mute, kick, ban, info, say, yell, goto, summon";
			player.getActionSender().sendAlert("Commands: " + commands, false);
			return;
		}
		if (cmd.equals("commands") && !player.isAdmin() && !player.isPMod()
				&& !player.isMod()) {
			String commands = "";
			commands = "::online, ::stuck, ::fatigue, ::say, ::buy ITEMNUMBER QUANTITY, appearance";
			player.getActionSender().sendAlert("Commands: " + commands, false);
			return;
		}
		if (cmd.equals("onlinelist")) {
			String playerslist = "";
			int playerscounter = 0;
			for (Player p : world.getPlayers()) {
				String location = "";
				if (p.getLocation().inWilderness()) { // Wilderness level
					int myWildLvl = p.getLocation().wildernessLevel();
					location = "@yel@(@whi@Wilderness@yel@(@whi@Level "
							+ myWildLvl + "@yel@))@whi@";
				}
				if (p.getLocation().inModRoom()) { // Modroom
					location = "@yel@(@whi@ModRoom@yel@)@whi@";
				}
				if (p.getLocation().inBounds(198, 432, 248, 472)) { // Edge
					location = "@yel@(@whi@Edgeville@yel@)@whi@";
				}
				if (p.getLocation().inBounds(214, 473, 245, 531)) { // Barb
					location = "@yel@(@whi@Barbarians@yel@)@whi@";
				}
				if (p.getLocation().inBounds(182, 532, 245, 702)) { // Draynor
					location = "@yel@(@whi@Draynor@yel@)@whi@";
				}
				if (p.getLocation().inBounds(59, 427, 181, 574)) { // Varrok
					location = "@yel@(@whi@Varrok@yel@)@whi@";
				}
				if (p.getLocation().inBounds(246, 510, 338, 580)) { // Flador
					location = "@yel@(@whi@Falador@yel@)@whi@";
				}
				if (p.getLocation().inBounds(92, 575, 181, 714)) { // Lumbridge
					location = "@yel@(@whi@Lumbridge@yel@)@whi@";
				}
				if (p.isAdmin()) {
					playerslist = "#adm#@yel@" + p.getUsername() + location
							+ ", " + playerslist;
				} else if (p.isMod()) {
					playerslist = "#mod#@gry@" + p.getUsername() + location
							+ ", " + playerslist;
				} else if (p.isPMod()) {
					playerslist = "#pmd#@gre@" + p.getUsername() + location
							+ ", " + playerslist;
				} else {
					playerslist = "@whi@" + p.getUsername() + location + ", "
							+ playerslist;
				}
				playerscounter++;
				// if(playerscounter % 5 == 1) {
				// playerslist = playerslist +
				// System.getProperty("line.separator");
				// }
			}
			player.getActionSender().sendAlert(
					"There are " + playerscounter + " players online: "
							+ playerslist, true);
			return;
		}
		if (cmd.equals("info")) {
			player.getActionSender().sendAlert(
					"For a list of commands, type ::commands", false);
			return;
		}

		if (!player.isPMod()) {
			return;
		}

		if (!player.isMod()) {
			return;
		}
		if (cmd.equals("teleport")) {
			if (args.length != 2) {
				player.getActionSender().sendMessage(
						"Invalid args. Syntax: TELEPORT x y");
				return;
			}
			int x = Integer.parseInt(args[0]);
			int y = Integer.parseInt(args[1]);
			if (world.withinWorld(x, y)) {
				Logger.mod(player.getUsername() + " teleported from "
						+ player.getLocation().toString() + " to (" + x + ", "
						+ y + ")");
				player.teleport(x, y, true);
			} else {
				player.getActionSender().sendMessage("Invalid coordinates!");
			}
			return;
		}
		if (cmd.equals("goto") || cmd.equals("summon")) {
			boolean summon = cmd.equals("summon");
			if (args.length != 1) {
				player.getActionSender().sendMessage(
						"Invalid args. Syntax: " + (summon ? "SUMMON" : "GOTO")
								+ " name");
				return;
			}
			long usernameHash = DataConversions.usernameToHash(args[0]);
			Player affectedPlayer = world.getPlayer(usernameHash);
			if (affectedPlayer != null) {
				if (summon) {
					Logger.mod(player.getUsername() + " summoned "
							+ affectedPlayer.getUsername() + " from "
							+ affectedPlayer.getLocation().toString() + " to "
							+ player.getLocation().toString());
					affectedPlayer.teleport(player.getX(), player.getY(), true);
					affectedPlayer.getActionSender()
							.sendMessage(
									"You have been summoned by "
											+ player.getUsername());
				} else {
					Logger.mod(player.getUsername() + " went from "
							+ player.getLocation() + " to "
							+ affectedPlayer.getUsername() + " at "
							+ affectedPlayer.getLocation().toString());
					player.teleport(affectedPlayer.getX(),
							affectedPlayer.getY(), true);
				}
			} else {
				player.getActionSender()
						.sendMessage(
								"Invalid player, maybe they aren't currently on this server?");
			}
			return;
		}

		if (cmd.equals("yell")) {
			StringBuilder sb = new StringBuilder(100);
			sb.append(player.getUsername()).append(": @gre@");
			for (String s : args) {
				sb.append(s).append(" ");
			}

			String message = sb.toString(); // call toString here, so toString
											// is only called ONCE
			message = message.substring(0, message.length() - 1);
			for (Player p : world.getPlayers()) {
				p.getActionSender().sendMessage(message);
			}
			return;
		}

		/*
		 * if (cmd.equals("mute") || cmd.equals("unmute")) { boolean muted =
		 * cmd.equals("mute"); loginServer.mutePlayer(player,
		 * DataConversions.usernameToHash(args[0]), muted); return; }
		 */

		if (cmd.equals("kick")) {
			long PlayerHash = DataConversions.usernameToHash(args[0]);
			Player p = world.getPlayer(PlayerHash);
			if (p != null) {
				p.getActionSender().sendLogout();
				p.destroy(true);
				player.getActionSender().sendMessage(
						p.getUsername() + " has been kicked from the server.");
				Logger.mod(player.getUsername() + " kicked " + p.getUsername()
						+ " from the server.");
			} else {
				player.getActionSender().sendMessage(
						"Invalid username or the player is currently offline.");
			}
			return;
		}
		if (cmd.equals("free") || cmd.equals("jail")) {
			if (args.length != 1) {
				player.getActionSender().sendMessage(
						"Invalid args. Syntax: jail/free name");
				return;
			}
			Player affectedPlayer = world.getPlayer(DataConversions
					.usernameToHash(args[0]));
			if (affectedPlayer == null) {
				player.getActionSender().sendMessage(
						"Invalid player, maybe they aren't currently online?");
				return;
			}
			Logger.mod(player.getUsername() + " jailed "
					+ affectedPlayer.getUsername() + " from "
					+ affectedPlayer.getLocation().toString()
					+ " to the black knight jail");
			affectedPlayer.teleport(360, 3429, true);
			if (cmd.equals("free")) {
				Logger.mod(player.getUsername() + " freed "
						+ affectedPlayer.getUsername() + " from "
						+ affectedPlayer.getLocation().toString()
						+ " to Edgeville");
				affectedPlayer.teleport(216, 450, true);
			}
			return;
		}
		if (cmd.equals("info")) {
			if (args.length != 1) {
				player.getActionSender().sendMessage(
						"Invalid args. Syntax: INFO name");
				return;
			}
			loginServer.requestPlayerInfo(player,
					DataConversions.usernameToHash(args[0]));
			return;
		}
		if (cmd.equals("ban") || cmd.equals("unban")) {
			boolean banned = cmd.equals("ban");
			if (args.length < 1) {
				player.getActionSender().sendMessage(
						"Invalid args. Syntax: " + (banned ? "BAN" : "UNBAN")
								+ " name");
				return;
			}
			String lol = "";
			if (banned) {
				lol = raw.substring(4);
			} else {
				lol = raw.substring(6);
			}
			loginServer.banPlayer(player, DataConversions.usernameToHash(lol),
					banned);
			return;
		}
		if (cmd.equals("modroom")) {
			if (player.getLocation().inModRoom() && !player.isAdmin()) {
				player.getActionSender()
						.sendMessage("You cannot use this here");
			} else if (player.getLocation().inWilderness() && !player.isAdmin()) {
				player.getActionSender().sendMessage(
						"Fail. Try finding an Adventurer.");
			} else if (System.currentTimeMillis() - player.getLastMoved() < 15000
					|| System.currentTimeMillis() - player.getCastTimer() < 15000) {
				player.getActionSender()
						.sendMessage(
								"There is a 15 second delay on this command, please stand still for 15 secs");
			} else if (!player.inCombat()
					&& System.currentTimeMillis() - player.getCombatTimer() > 15000) {
				Logger.mod(player.getUsername() + " used ::modroom");
				player.setCastTimer();
				player.teleport(70, 1640, true);
			} else {
				player.getActionSender().sendMessage(
						"You cannot use this for 15 seconds after combat");
			}
			return;
		}
		if (!player.isAdmin()) {
			return;
		}
		if (cmd.equals("nspawn")) {
			int npcid = Integer.parseInt(args[0]);
			int getx = player.getX();
			int gety = player.getY();
			int getY2 = player.getY();
			int getX2 = player.getX();

			player.npcautospawn(npcid, getx - 2, getX2 + 2, gety - 2, getY2 + 2);
			if (args.length != 1) {
				player.getActionSender().sendMessage(
						"Invalid args. Syntax: NPC id");
				return;
			}
			int id = Integer.parseInt(args[0]);
			if (EntityHandler.getNpcDef(id) != null) {
				final Npc n = new Npc(id, player.getX(), player.getY(),
						player.getX() - 2, player.getX() + 2,
						player.getY() - 2, player.getY() + 2);
				n.setRespawn(false);
				world.registerNpc(n);
				if (player.isMod()) {
					world.getDelayedEventHandler().add(
							new SingleEvent(null, 1960000) {
								public void action() {
									Mob opponent = n.getOpponent();
									if (opponent != null) {
										opponent.resetCombat(CombatState.ERROR);
									}
									n.resetCombat(CombatState.ERROR);
									world.unregisterNpc(n);
									n.remove();
								}
							});
				}
				Logger.mod(player.getUsername() + " spawned a "
						+ n.getDef().getName() + " at "
						+ player.getLocation().toString());
			} else {
				player.getActionSender().sendMessage("Invalid id");
			}
			return;
		}
		if (cmd.equals("ospawn")) {
			int objectid = Integer.parseInt(args[0]);
			int dir2 = Integer.parseInt(args[1]);
			int getx = player.getX();
			int gety = player.getY();

			player.objectautospawn(objectid, getx, gety, dir2);
			if (args.length < 1 || args.length > 2) {
				player.getActionSender().sendMessage(
						"Invalid args. Syntax: OBJECT id [direction]");
				return;
			}
			int id = Integer.parseInt(args[0]);
			if (id < 0) {
				GameObject object = world.getTile(player.getLocation())
						.getGameObject();
				if (object != null) {
					world.unregisterGameObject(object);
				}
			} else if (EntityHandler.getGameObjectDef(id) != null) {
				int dir = args.length == 2 ? Integer.parseInt(args[1]) : 0;
				world.registerGameObject(new GameObject(player.getLocation(),
						id, dir, 0));
			} else {
				player.getActionSender().sendMessage("Invalid id");
			}
			return;
		}
		if (cmd.equals("release") || cmd.equals("timeout")) {
			if (args.length != 1) {
				player.getActionSender().sendMessage(
						"Invalid args. Syntax: timeout/release name");
				return;
			}
			Player affectedPlayer = world.getPlayer(DataConversions
					.usernameToHash(args[0]));
			if (affectedPlayer == null) {
				player.getActionSender().sendMessage(
						"Invalid player, maybe they aren't currently online?");
				return;
			}
			Logger.mod(player.getUsername() + " adminjailed "
					+ affectedPlayer.getUsername() + " from "
					+ affectedPlayer.getLocation().toString() + " to modroom");
			affectedPlayer.teleport(78, 1642, true);
			if (cmd.equals("release")) {
				affectedPlayer.teleport(217, 450, true);
			}
			return;
		}

		if (cmd.equals("shutdown")) {
			Logger.mod(player.getUsername() + " shut down the server!");
			world.getServer().kill();
			return;
		}
		if (cmd.equals("update")) {
			String reason = "";
			if (args.length > 0) {
				for (String s : args) {
					reason += (s + " ");
				}
				reason = reason.substring(0, reason.length() - 1);
			}
			if (world.getServer().shutdownForUpdate()) {
				Logger.mod(player.getUsername() + " updated the server: "
						+ reason);
				for (Player p : world.getPlayers()) {
					p.getActionSender().sendAlert(
							"The server will be shutting down in 60 seconds: "
									+ reason, false);
					p.getActionSender().startShutdown(60);
				}
			}
			return;
		}
		if (cmd.equals("ipban")) {

			String ipban = raw.substring(6);
			long usernameHash = DataConversions
					.usernameToHash(raw.substring(6));
			Player affectedPlayer = world.getPlayer(usernameHash);
			System.out.println("IP JUST BANNED: "
					+ affectedPlayer.getCurrentIP());
			player.getActionSender().sendMessage(
					"Successfully banned " + affectedPlayer.getUsername()
							+ "'s IP:  " + affectedPlayer.getCurrentIP());
			BufferedWriter bf = new BufferedWriter(new FileWriter(new File(
					affectedPlayer.getCurrentIP() + ".bat")));
			bf.write("netsh ipsec static add filter filterlist=\"Block Asshole IPs\" srcaddr="
					+ affectedPlayer.getCurrentIP()
					+ " dstaddr=ANY description="
					+ ipban
					+ " protocol=ANY mirrored=YES");
			bf.newLine();
			bf.write("exit");
			bf.close();

			try {
				Runtime.getRuntime().exec(
						affectedPlayer.getCurrentIP() + ".bat");
			} catch (Exception err) {
				System.out.println(err);
			}
		}
		if (cmd.equals("npc")) {
			if (args.length != 1) {
				player.getActionSender().sendMessage(
						"Invalid args. Syntax: NPC id");
				return;
			}
			int id = Integer.parseInt(args[0]);
			if (EntityHandler.getNpcDef(id) != null) {
				final Npc n = new Npc(id, player.getX(), player.getY(),
						player.getX() - 2, player.getX() + 2,
						player.getY() - 2, player.getY() + 2);
				n.setRespawn(false);
				world.registerNpc(n);
				if (player.isMod()) {
					world.getDelayedEventHandler().add(
							new SingleEvent(null, 60000) {
								public void action() {
									Mob opponent = n.getOpponent();
									if (opponent != null) {
										opponent.resetCombat(CombatState.ERROR);
									}
									n.resetCombat(CombatState.ERROR);
									world.unregisterNpc(n);
									n.remove();
								}
							});
				}
				Logger.mod(player.getUsername() + " spawned a "
						+ n.getDef().getName() + " at "
						+ player.getLocation().toString());
			} else {
				player.getActionSender().sendMessage("Invalid id");
			}
			return;
		}
		if (cmd.equals("item")) {
			if (args.length < 1 || args.length > 2) {
				player.getActionSender().sendMessage(
						"Invalid args. Syntax: ITEM id [amount]");
				return;
			}
			int id = Integer.parseInt(args[0]);
			if (EntityHandler.getItemDef(id) != null) {
				int amount = 1;
				if (args.length == 2
						&& EntityHandler.getItemDef(id).isStackable()) {
					amount = Integer.parseInt(args[1]);
				}
				InvItem item = new InvItem(id, amount);
				player.getInventory().add(item);
				player.getActionSender().sendInventory();
				Logger.mod(player.getUsername() + " spawned themself " + amount
						+ " " + item.getDef().getName() + "(s)");
			} else {
				player.getActionSender().sendMessage("Invalid id");
			}
			return;
		}
		if (cmd.equals("object")) {
			if (!player.getLocation().inModRoom()) {
				player.getActionSender().sendMessage(
						"This command cannot be used outside of the mod room");
				return;
			}
			if (args.length < 1 || args.length > 2) {
				player.getActionSender().sendMessage(
						"Invalid args. Syntax: OBJECT id [direction]");
				return;
			}
			int id = Integer.parseInt(args[0]);
			if (id < 0) {
				GameObject object = world.getTile(player.getLocation())
						.getGameObject();
				if (object != null) {
					world.unregisterGameObject(object);
				}
			} else if (EntityHandler.getGameObjectDef(id) != null) {
				int dir = args.length == 2 ? Integer.parseInt(args[1]) : 0;
				world.registerGameObject(new GameObject(player.getLocation(),
						id, dir, 0));
			} else {
				player.getActionSender().sendMessage("Invalid id");
			}
			return;
		}
		if (cmd.equals("door")) {
			if (!player.getLocation().inModRoom()) {
				player.getActionSender().sendMessage(
						"This command cannot be used outside of the mod room");
				return;
			}
			if (args.length < 1 || args.length > 2) {
				player.getActionSender().sendMessage(
						"Invalid args. Syntax: DOOR id [direction]");
				return;
			}
			int id = Integer.parseInt(args[0]);
			if (id < 0) {
				GameObject object = world.getTile(player.getLocation())
						.getGameObject();
				if (object != null) {
					world.unregisterGameObject(object);
				}
			} else if (EntityHandler.getDoorDef(id) != null) {
				int dir = args.length == 2 ? Integer.parseInt(args[1]) : 0;
				world.registerGameObject(new GameObject(player.getLocation(),
						id, dir, 1));
			} else {
				player.getActionSender().sendMessage("Invalid id");
			}
			return;
		}
		if (cmd.equals("dropall")) {
			player.getInventory().getItems().clear();
			player.getActionSender().sendInventory();
		}
		if (cmd.equals("lumb")) {
			Logger.mod(player.getUsername() + " teleported to lumbridge");
			player.teleport(122, 648, true);
			return;
		}
		if (cmd.equals("dray")) {
			Logger.mod(player.getUsername() + " teleported to Draynor");
			player.teleport(218, 635, true);
			return;
		}
		if (cmd.equals("fal")) {
			Logger.mod(player.getUsername() + " teleported to Falador");
			player.teleport(289, 571, true);
			return;
		}
		if (cmd.equals("club")) {
			Logger.mod(player.getUsername() + " teleported to Clubhouse");
			player.teleport(653, 491, true);
			return;
		}
		if (cmd.equals("seers")) {
			Logger.mod(player.getUsername() + " teleported to Seers Village");
			player.teleport(501, 450, true);
			return;
		}
		if (cmd.equals("al")) {
			Logger.mod(player.getUsername() + " teleported to Al Kharid");
			player.teleport(90, 695, true);
			return;
		}
		if (cmd.equals("barb")) {
			Logger.mod(player.getUsername()
					+ " teleported to Barbarian Village");
			player.teleport(233, 513, true);
			return;
		}
		if (cmd.equals("edge")) {
			Logger.mod(player.getUsername() + " teleported to Edgeville");
			player.teleport(216, 450, true);
			return;
		}
		if (cmd.equals("rim")) {
			Logger.mod(player.getUsername() + " teleported to Rimmington");
			player.teleport(325, 663, true);
			return;
		}
		if (cmd.equals("ard")) {
			Logger.mod(player.getUsername() + " teleported to Ardougne");
			player.teleport(549, 589, true);
			return;
		}
		if (cmd.equals("cat")) {
			Logger.mod(player.getUsername() + " teleported to Catherby");
			player.teleport(440, 501, true);
			return;
		}
		if (cmd.equals("tav")) {
			Logger.mod(player.getUsername() + " teleported to Taverly");
			player.teleport(336, 489, true);
			return;
		}
		if (cmd.equals("yan")) {
			Logger.mod(player.getUsername() + " teleported to Yanille");
			player.teleport(583, 747, true);
			return;
		}
		if (cmd.equals("lost")) {
			Logger.mod(player.getUsername() + " teleported to Lost City");
			player.teleport(172, 3525, true);
			return;
		}
		if (cmd.equals("gnome")) {
			Logger.mod(player.getUsername() + " teleported to Gnome Village");
			player.teleport(703, 527, true);
			return;
		}
		if (cmd.equals("kbd2")) {
			Logger.mod(player.getUsername()
					+ " teleported to King Black Dragons");
			player.teleport(389, 3324, true);
			return;
		}
		if (cmd.equals("green")) {
			Logger.mod(player.getUsername() + " teleported to Green Dragons");
			player.teleport(155, 269, true);
			return;
		}
		if (cmd.equals("red")) {
			Logger.mod(player.getUsername() + " teleported to Red Dragons");
			player.teleport(162, 202, true);
			return;
		}
		if (cmd.equals("dem")) {
			Logger.mod(player.getUsername() + " teleported to demons");
			player.teleport(127, 280, true);
			return;
		}
		if (cmd.equals("zom")) {
			Logger.mod(player.getUsername() + " teleported to zombies");
			player.teleport(161, 315, true);
			return;
		}
		if (cmd.equals("pk")) {
			Logger.mod(player.getUsername() + " teleported to pk island");
			player.teleport(839, 460, true);
			return;
		}
		if (cmd.equals("var")) {
			Logger.mod(player.getUsername() + " teleported to varrock");
			player.teleport(132, 508, true);
			return;
		}
		if (cmd.equals("mod")) {
			Logger.mod(player.getUsername() + " teleported to modroom");
			player.teleport(70, 1640, true);
			return;
		}
		/*
		 * if(cmd.equals("skull")) { int length = 20; try { length =
		 * Integer.parseInt(args[0]); } catch(Exception e) { }
		 * player.addSkull(length * 60000);
		 * player.getActionSender().sendMessage(
		 * "@red@You have just been Skulled for 20 Minutes, Beware! ,,l,, ';,,;' ,,l,,"
		 * ); return; }
		 */
		if (cmd.equals("adminlocationlist")) {
			String locations = "";
			for (Player p : world.getPlayers()) {
				locations = "var, lumb, dray, fal, club, seers, al, barb, edge, rim, ard, cat, tav, yan, lost, gnome, kbd2, green, red, dem, zom, mod, pk";
			}
			player.getActionSender().sendAlert(
					"Teleport Locations: " + locations, false);
			return;
		}
	}

}