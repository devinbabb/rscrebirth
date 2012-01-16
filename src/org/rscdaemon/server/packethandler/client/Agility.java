package org.rscdaemon.server.packethandler.client;

import org.rscdaemon.server.entityhandling.defs.GameObjectDef;
import org.rscdaemon.server.model.*;

import org.rscdaemon.server.entityhandling.EntityHandler;
import org.rscdaemon.server.entityhandling.defs.NPCDef;
import org.rscdaemon.server.entityhandling.defs.extras.ItemDropDef;
import org.rscdaemon.server.entityhandling.locs.NPCLoc;
import org.rscdaemon.server.model.Point;
import org.rscdaemon.server.util.Formulae;
import org.rscdaemon.server.util.DataConversions;
import org.rscdaemon.server.event.ShortEvent;
import org.rscdaemon.server.event.SingleEvent;
import org.rscdaemon.server.event.DelayedEvent;
import org.rscdaemon.server.event.FightEvent;
import org.rscdaemon.server.states.Action;
import org.rscdaemon.server.states.CombatState;
import org.rscdaemon.server.model.*;

/**
 * Handles the agility skill
 * 
 * @author Devin
 */

public class Agility {

	public Agility(Player p, GameObject obj) {
		this.player = p;
		this.object = obj;
		this.def = object.getGameObjectDef();
	}

	private Player player;
	private GameObject object;
	private GameObjectDef def;
	private static final World world = World.getWorld();
	public int ExpMultiplier = 5; // Modify this number to multiply the XP
									// given.
	private int curStall = -1;
	private int Exp = -1;
	private int exp = 0;
	private int lvl = 1;
	private int curDoor = -1;

	// Cur X, Cur Y, New X, New Y, Lvl, Exp
	private int[][] Doors = { { 655, 495, 692, 500, 1, 120 }, // Nat rune West
																// house (In)
			{ 585, 581, 586, 581, 10, 13 }, // Nat rune West house (Out)
			{ 539, 598, 539, 599, 10, 13 }, // Nat Rune East House (Out)
			{ 539, 599, 539, 598, 10, 13 }, // Nat Rune East House (Out)
			{ 609, 1547, 609, 1548, 65, 54 }, // Paladin Door (In)
			{ 609, 1548, 609, 1547, 65, 54 }, // Paladin Door (Out)
			{ 537, 3425, 536, 3425, 31, 25 }, // Ardy Door
			{ 536, 3425, 537, 3425, 31, 25 }, // Ardy Door
			{ 617, 556, 617, 555, 46, 37 }, // Blood rune door
			{ 617, 555, 617, 556, 46, 37 }, // Blood rune door
			{ 593, 3590, 593, 3589, 61, 84 }, // yanile door
			{ 593, 3589, 593, 3590, 61, 84 }, // yanile door
			{ 266, 100, 266, 99, 39, 35 }, // pirate doors wildy
			{ 266, 99, 266, 100, 39, 35 }, // pirate doors wildy
			{ 160, 103, 160, 102, 30, 28 }, // Axe hut wildy
			{ 160, 102, 160, 103, 30, 28 }, // Axe hut wildy
			{ 581, 580, 580, 580, 10, 30 }, { 580, 580, 581, 580, 10, 30 } };

	public void lockpick() {

		try {

			if (player.isBusy() || player.inCombat() || player == null
					|| object == null) {
				// player.packetSpam = false;
				player.setBusy(false);
				return;
			}
			player.setBusy(true);

			boolean cont = true;
			for (int i = 0; i < Doors.length; i++) {
				if (player.getX() == Doors[i][0]
						&& player.getY() == Doors[i][1]) {
					curDoor = i;
					cont = false;
				}
			}
			if (cont) {
				player.getActionSender()
						.sendMessage(
								"This has not been added, contact an admin with your coords");
				// player.packetSpam = false;
				player.setBusy(false);
				return;
			}
			if (player.getMaxStat(16) < Doors[curDoor][4]) {

				player.getActionSender().sendMessage(
						"Sorry, your agility level is not high enough");
				// player.packetSpam = false;
				player.setBusy(false);
				return;
			}

			// player.getActionSender().sendMessage("You attempt to pick the lock on the "
			// + object.getDoorDef().name);
			world.getDelayedEventHandler().add(new ShortEvent(player) {
				public void action() {
					// player.getActionSender().sendMessage("You sucessfully unlocked the "
					// + object.getDoorDef().name);
					// doDoor();
					player.incExp(16, Doors[curDoor][5] * ExpMultiplier, true);
					player.getActionSender().sendStat(16);
					// player.packetSpam = false;
					player.setBusy(false);
					player.teleport(Doors[curDoor][2], Doors[curDoor][3], false);

				}
			});
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
