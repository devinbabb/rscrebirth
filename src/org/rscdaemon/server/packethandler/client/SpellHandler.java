package org.rscdaemon.server.packethandler.client;

import org.rscdaemon.server.packethandler.PacketHandler;
import org.rscdaemon.server.model.*;
import org.rscdaemon.server.net.Packet;
import org.rscdaemon.server.net.RSCPacket;
import org.rscdaemon.server.util.*;
import org.rscdaemon.server.event.WalkToMobEvent;
import org.rscdaemon.server.event.NpcWalkEvent;
import org.rscdaemon.server.event.FightEvent;
import org.rscdaemon.server.event.WalkToPointEvent;
import org.rscdaemon.server.states.Action;
import org.rscdaemon.server.entityhandling.EntityHandler;
import org.rscdaemon.server.entityhandling.defs.SpellDef;
import org.rscdaemon.server.entityhandling.defs.extras.ItemSmeltingDef;
import org.rscdaemon.server.entityhandling.defs.extras.ReqOreDef;

import org.apache.mina.common.IoSession;

import java.util.*;
import java.util.Map.Entry;

public class SpellHandler implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();

	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player) session.getAttachment();
		int pID = ((RSCPacket) p).getID();
		if ((player.isBusy() && !player.inCombat()) || player.isRanging()) {
			return;
		}
		if (player.isDueling() && player.getDuelSetting(1)) {
			player.getActionSender().sendMessage(
					"Magic is disabled in this duel");
			return;
		}
		player.resetAllExceptDueling();
		int idx = p.readShort();
		if (idx < 0 || idx >= 49) {
			player.setSuspiciousPlayer(true);
			return;
		}
		if (!canCast(player)) {
			return;
		}
		SpellDef spell = EntityHandler.getSpellDef(idx);
		if (player.getCurStat(6) < spell.getReqLevel()) {
			player.setSuspiciousPlayer(true);
			player.getActionSender().sendMessage(
					"Your magic ability is not high enough for this spell.");
			player.resetPath();
			return;
		}
		if (!Formulae.castSpell(spell, player.getCurStat(6),
				player.getMagicPoints())) {
			player.getActionSender().sendMessage(
					"The spell fails, you may try again in 20 seconds.");
			player.getActionSender().sendSound("spellfail");
			player.setSpellFail();
			player.resetPath();
			return;
		}
		switch (pID) {
		case 206: // Cast on self
			if (player.isDueling()) {
				player.getActionSender().sendMessage(
						"This type of spell cannot be used in a duel.");
				return;
			}
			if (spell.getSpellType() == 0) {
				handleTeleport(player, spell, idx);
			}
			break;
		case 55: // Cast on player
			if (spell.getSpellType() == 1 || spell.getSpellType() == 2) {
				Player affectedPlayer = world.getPlayer(p.readShort());
				if (affectedPlayer == null) { // This shouldn't happen
					player.resetPath();
					return;
				}
				if (player.withinRange(affectedPlayer, 5)) {
					player.resetPath();
				}
				handleMobCast(player, affectedPlayer, idx);
			}
			break;
		case 71: // Cast on npc
			if (spell.getSpellType() == 2) {
				Npc affectedNpc = world.getNpc(p.readShort());
				if (affectedNpc == null) { // This shouldn't happen
					player.resetPath();
					return;
				}
				if (player.withinRange(affectedNpc, 5)) {
					player.resetPath();
				}
				handleMobCast(player, affectedNpc, idx);
			}
			break;
		case 49: // Cast on inventory item
			if (player.isDueling()) {
				player.getActionSender().sendMessage(
						"This type of spell cannot be used in a duel.");
				return;
			}
			if (spell.getSpellType() == 3) {
				InvItem item = player.getInventory().get(p.readShort());
				if (item == null) { // This shoudln't happen
					player.resetPath();
					return;
				}
				handleInvItemCast(player, spell, idx, item);
			}
			break;
		case 67: // Cast on door - type 4
			if (player.isDueling()) {
				player.getActionSender().sendMessage(
						"This type of spell cannot be used in a duel.");
				return;
			}
			player.getActionSender().sendMessage(
					"@or1@This type of spell is not yet implemented.");
			break;
		case 17: // Cast on game object - type 5
			if (player.isDueling()) {
				player.getActionSender().sendMessage(
						"This type of spell cannot be used in a duel.");
				return;
			}
			player.getActionSender().sendMessage(
					"@or1@This type of spell is not yet implemented.");
			break;
		case 104: // Cast on ground item
			if (player.isDueling()) {
				player.getActionSender().sendMessage(
						"This type of spell cannot be used in a duel.");
				return;
			}
			ActiveTile t = world.getTile(p.readShort(), p.readShort());
			int itemId = p.readShort();
			Item affectedItem = null;
			for (Item i : t.getItems()) {
				if (i.getID() == itemId) {
					affectedItem = i;
					break;
				}
			}
			if (affectedItem == null) { // This shouldn't happen
				return;
			}
			handleItemCast(player, spell, idx, affectedItem);
			break;
		case 232: // Cast on ground - type 6
			if (player.isDueling()) {
				player.getActionSender().sendMessage(
						"This type of spell cannot be used in a duel.");
				return;
			}
			// if(spell.getSpellType() == 6) {
			handleGroundCast(player, spell, idx);
			// }
			break;
		}
		player.getActionSender().sendInventory();
		player.getActionSender().sendStat(6);
	}

	private void handleMobCast(Player player, Mob affectedMob, final int spellID) {
		if (!player.isBusy()) {
			player.setFollowing(affectedMob);
		}
		player.setStatus(Action.CASTING_MOB);
		world.getDelayedEventHandler().add(
				new WalkToMobEvent(player, affectedMob, 5) {
					public void arrived() {
						owner.resetPath();
						SpellDef spell = EntityHandler.getSpellDef(spellID);
						if (!canCast(owner) || affectedMob.getHits() <= 0
								|| !owner.checkAttack(affectedMob, true)
								|| owner.getStatus() != Action.CASTING_MOB) {
							return;
						}
						owner.resetAllExceptDueling();
						switch (spellID) {
						case 1: // Confuse
						case 5: // Weaken
						case 9: // Curse
						case 19: // Crumble undead
						case 25: // Iban blast
							/*
							 * case 33: // Claws of Guthix case 34: // Saradomin
							 * strike case 35: // Flames of Zamarak
							 */
						case 42: // vulnerability
						case 45: // Enfeeble
						case 47: // Stun
							owner.getActionSender().sendMessage(
									"@or1@This spell is not yet implemented.");
							break;
						default:
							if (affectedMob instanceof Npc
									&& owner.withinRange(affectedMob, 6)
									&& !affectedMob.isBusy() && !owner.isBusy()) {
								final Npc affectedNpc = (Npc) affectedMob;
								final Player victim = owner;
								if (victim != null) {
									world.getDelayedEventHandler().add(
											new NpcWalkEvent(owner,
													affectedNpc, 0) {
												public void arrived() {
													affectedNpc.resetPath();
													victim.resetPath();
													victim.resetAll();
													victim.setStatus(Action.FIGHTING_MOB);
													victim.getActionSender()
															.sendMessage(
																	"You are under attack!");

													for (Player p : affectedNpc
															.getViewArea()
															.getPlayersInView()) {
														p.removeWatchedNpc(affectedNpc);
													}

													victim.setBusy(true);
													victim.setSprite(9);
													victim.setOpponent(affectedNpc);
													victim.setCombatTimer();

													affectedNpc.setBusy(true);
													affectedNpc.setSprite(8);
													affectedNpc
															.setOpponent(victim);
													affectedNpc
															.setCombatTimer();
													FightEvent fighting = new FightEvent(
															victim,
															affectedNpc, true);
													fighting.setLastRun(0);
													world.getDelayedEventHandler()
															.add(fighting);
												}
											});
								}
							}
							int damage = Formulae.calcSpellHit(owner,
									affectedMob, EntityHandler
											.getSpellAggressiveLvl(spellID));
							if (spellID >= 33 && spellID <= 35) {
								int specialID = spellID - 33;
								int[] objSpawns = { 1142, 1031, 1036 };
								int[] capeIDs = { 1215, 1214, 1213 };
								int[] staffIDs = { 1217, 1218, 1216 };
								String[] gods = { "Guthix", "Saradomin",
										"Zamarok" };

								if (!owner.isWielded(staffIDs[specialID])) {
									owner.getActionSender().sendMessage(
											"You cannot cast this spell without the Staff of "
													+ gods[specialID] + ".");
									// setRunning(false);
									return;
								}
								if (!owner.isWielded(capeIDs[specialID])) {
									damage = (int) (Math.random() * 3);
									owner.getActionSender().sendMessage(
											"Without the Cape of "
													+ gods[specialID]
													+ " you feel weak..");
								}
								if (!owner.isCharged()) {
									damage = (int) (Math.random() * 10);
									owner.getActionSender().sendMessage(
											"You are not charged..");
								}
								ActiveTile t = World.getWorld().getTile(
										affectedMob.getLocation());
								if (!world.getTile(owner.getLocation())
										.hasGameObject()) {
									GameObject spawned = new GameObject(
											affectedMob.getLocation(),
											objSpawns[specialID], 0, 0);
									World.getWorld()
											.registerGameObject(spawned);
									// World.getWorld().getDelayedEventHandler().add(new
									// ObjectRemover(spawned, 500));
									world.delayedRemoveObject(spawned, 500);
								}
								if (specialID == 0
										&& affectedMob instanceof Player) {
									Player affectedPlayer = (Player) affectedMob;
									affectedPlayer
											.getActionSender()
											.sendMessage(
													"Your defense has been lowered by "
															+ owner.getUsername()
															+ "!");
									owner.getActionSender().sendMessage(
											"You have lowered "
													+ affectedPlayer
															.getUsername()
													+ "'s defense.");
									affectedPlayer.setCurStat(1,
											(affectedPlayer.getCurStat(1) - 1));
									affectedPlayer.getActionSender()
											.sendStat(1);
								}
								if (specialID == 1
										&& affectedMob instanceof Player) {
									Player affectedPlayer = (Player) affectedMob;
									affectedPlayer
											.getActionSender()
											.sendMessage(
													"Your prayer has been lowered by "
															+ owner.getUsername()
															+ "!");
									owner.getActionSender().sendMessage(
											"You have lowered "
													+ affectedPlayer
															.getUsername()
													+ "'s prayer.");
									affectedPlayer.setCurStat(5,
											(affectedPlayer.getCurStat(5) - 1));
									affectedPlayer.getActionSender()
											.sendStat(5);
								}
								if (specialID == 2
										&& affectedMob instanceof Player) {
									Player affectedPlayer = (Player) affectedMob;
									affectedPlayer
											.getActionSender()
											.sendMessage(
													"Your magic has been lowered by "
															+ owner.getUsername()
															+ "!");
									owner.getActionSender().sendMessage(
											"You have lowered "
													+ affectedPlayer
															.getUsername()
													+ "'s magic.");
									affectedPlayer.setCurStat(6,
											(affectedPlayer.getCurStat(6) - 1));
									affectedPlayer.getActionSender()
											.sendStat(6);
								}
							}
							if (!checkAndRemoveRunes(owner, spell)) {
								return;
							}
							if (affectedMob instanceof Player
									&& !owner.isDueling()) {
								Player affectedPlayer = (Player) affectedMob;
								owner.setSkulledOn(affectedPlayer);
							}
							if (affectedMob instanceof Player) {
								Player affectedPlayer = (Player) affectedMob;
								affectedPlayer.getActionSender().sendMessage(
										owner.getUsername()
												+ " is shooting at you!");
							}
							Projectile projectile = new Projectile(owner,
									affectedMob, 1);
							affectedMob.setLastDamage(damage);
							int newHp = affectedMob.getHits() - damage;
							affectedMob.setHits(newHp);
							ArrayList<Player> playersToInform = new ArrayList<Player>();
							playersToInform.addAll(owner.getViewArea()
									.getPlayersInView());
							playersToInform.addAll(affectedMob.getViewArea()
									.getPlayersInView());
							for (Player p : playersToInform) {
								p.informOfProjectile(projectile);
								p.informOfModifiedHits(affectedMob);
							}
							if (affectedMob instanceof Player) {
								Player affectedPlayer = (Player) affectedMob;
								affectedPlayer.getActionSender().sendStat(3);
							}
							if (newHp <= 0) {
								affectedMob.killedBy(owner, owner.isDueling());
							}
							finalizeSpell(owner, spell);
							break;
						}
						owner.getActionSender().sendInventory();
						owner.getActionSender().sendStat(6);
					}
				});
	}

	private void handleItemCast(Player player, final SpellDef spell,
			final int id, final Item affectedItem) {
		player.setStatus(Action.CASTING_GITEM);
		world.getDelayedEventHandler().add(
				new WalkToPointEvent(player, affectedItem.getLocation(), 5,
						true) {
					public void arrived() {
						owner.resetPath();
						ActiveTile tile = world.getTile(location);
						if (!canCast(owner) || !tile.hasItem(affectedItem)
								|| owner.getStatus() != Action.CASTING_GITEM) {
							return;
						}
						owner.resetAllExceptDueling();
						switch (id) {
						case 16: // Telekinetic grab
							if (affectedItem.getLocation().inBounds(490, 464,
									500, 471)
									|| affectedItem.getLocation().inBounds(490,
											1408, 500, 1415)) {
								owner.getActionSender()
										.sendMessage(
												"Telekinetic grab cannot be used in here");
								return;
							}
							if (!checkAndRemoveRunes(owner, spell)) {
								return;
							}
							owner.getActionSender().sendTeleBubble(
									location.getX(), location.getY(), true);
							for (Object o : owner.getWatchedPlayers()
									.getAllEntities()) {
								Player p = ((Player) o);
								p.getActionSender().sendTeleBubble(
										location.getX(), location.getY(), true);
							}
							world.unregisterItem(affectedItem);
							finalizeSpell(owner, spell);
							owner.getInventory().add(
									new InvItem(affectedItem.getID(),
											affectedItem.getAmount()));
							break;
						}
						owner.getActionSender().sendInventory();
						owner.getActionSender().sendStat(6);
					}
				});
	}

	private void handleInvItemCast(Player player, SpellDef spell, int id,
			InvItem affectedItem) {
		switch (id) {
		case 3: // Enchant lvl-1 Sapphire amulet
			if (affectedItem.getID() == 302) {
				if (!checkAndRemoveRunes(player, spell)) {
					return;
				}
				player.getInventory().remove(affectedItem);
				player.getInventory().add(new InvItem(314));
				finalizeSpell(player, spell);
			} else {
				player.getActionSender().sendMessage(
						"This spell cannot be used on this kind of item");
			}
			break;
		case 10: // Low level alchemy
			if (affectedItem.getID() == 10) {
				player.getActionSender().sendMessage("You cannot alchemy that");
				return;
			}
			if (!checkAndRemoveRunes(player, spell)) {
				return;
			}
			if (player.getInventory().remove(affectedItem) > 1) {
				int value = (int) (affectedItem.getDef().getBasePrice() * 0.4D * affectedItem
						.getAmount());
				player.getInventory().add(new InvItem(10, value)); // 40%
			}
			finalizeSpell(player, spell);
			break;
		case 13: // Enchant lvl-2 emerald amulet
			if (affectedItem.getID() == 303) {
				if (!checkAndRemoveRunes(player, spell)) {
					return;
				}
				player.getInventory().remove(affectedItem);
				player.getInventory().add(new InvItem(315));
				finalizeSpell(player, spell);
			} else {
				player.getActionSender().sendMessage(
						"This spell cannot be used on this kind of item");
			}
			break;
		case 21: // Superheat item
			ItemSmeltingDef smeltingDef = affectedItem.getSmeltingDef();
			if (smeltingDef == null) {
				player.getActionSender().sendMessage(
						"This spell cannot be used on this kind of item");
				return;
			}
			for (ReqOreDef reqOre : smeltingDef.getReqOres()) {
				if (player.getInventory().countId(reqOre.getId()) < reqOre
						.getAmount()) {
					if (affectedItem.getID() == 151) {
						smeltingDef = EntityHandler.getItemSmeltingDef(9999);
						break;
					}
					player.getActionSender().sendMessage(
							"You need "
									+ reqOre.getAmount()
									+ " "
									+ EntityHandler.getItemDef(reqOre.getId())
											.getName() + " to smelt a "
									+ affectedItem.getDef().getName() + ".");
					return;
				}
			}
			if (player.getCurStat(13) < smeltingDef.getReqLevel()) {
				player.getActionSender()
						.sendMessage(
								"You need a smithing level of "
										+ smeltingDef.getReqLevel()
										+ " to smelt this.");
				return;
			}
			if (!checkAndRemoveRunes(player, spell)) {
				return;
			}
			InvItem bar = new InvItem(smeltingDef.getBarId());
			if (player.getInventory().remove(affectedItem) > -1) {
				for (ReqOreDef reqOre : smeltingDef.getReqOres()) {
					for (int i = 0; i < reqOre.getAmount(); i++) {
						player.getInventory().remove(
								new InvItem(reqOre.getId()));
					}
				}
				player.getActionSender().sendMessage(
						"You make a " + bar.getDef().getName() + ".");
				player.getInventory().add(bar);
				player.incExp(13, smeltingDef.getExp() * 10, true);
				player.getActionSender().sendStat(13);
				player.getActionSender().sendInventory();
			}
			finalizeSpell(player, spell);
			break;
		case 24: // Enchant lvl-3 ruby amulet
			if (affectedItem.getID() == 304) {
				if (!checkAndRemoveRunes(player, spell)) {
					return;
				}
				player.getInventory().remove(affectedItem);
				player.getInventory().add(new InvItem(316));
				finalizeSpell(player, spell);
			} else {
				player.getActionSender().sendMessage(
						"This spell cannot be used on this kind of item");
			}
			break;
		case 28: // High level alchemy
			if (affectedItem.getID() == 10) {
				player.getActionSender().sendMessage("You cannot alchemy that");
				return;
			}
			if (!checkAndRemoveRunes(player, spell)) {
				return;
			}
			if (player.getInventory().remove(affectedItem) > -1) {
				int value = (int) (affectedItem.getDef().getBasePrice() * 0.6D * affectedItem
						.getAmount());
				player.getInventory().add(new InvItem(10, value)); // 60%
			}
			finalizeSpell(player, spell);
			break;
		case 30: // Enchant lvl-4 diamond amulet
			if (affectedItem.getID() == 305) {
				if (!checkAndRemoveRunes(player, spell)) {
					return;
				}
				player.getInventory().remove(affectedItem);
				player.getInventory().add(new InvItem(317));
				finalizeSpell(player, spell);
			} else {
				player.getActionSender().sendMessage(
						"This spell cannot be used on this kind of item");
			}
			break;
		case 43: // Enchant lvl-5 dragonstone amulet
			if (affectedItem.getID() == 610) {
				if (!checkAndRemoveRunes(player, spell)) {
					return;
				}
				player.getInventory().remove(affectedItem);
				player.getInventory().add(new InvItem(522));
				finalizeSpell(player, spell);
			} else {
				player.getActionSender().sendMessage(
						"This spell cannot be used on this kind of item");
			}
			break;
		}
		if (affectedItem.isWielded()) {
			player.getActionSender().sendSound("click");
			affectedItem.setWield(false);
			player.updateWornItems(
					affectedItem.getWieldableDef().getWieldPos(),
					player.getPlayerAppearance().getSprite(
							affectedItem.getWieldableDef().getWieldPos()));
			player.getActionSender().sendEquipmentStats();
		}
	}

	private void handleGroundCast(Player player, SpellDef spell, int id) {
		switch (id) {
		case 7: // Bones to bananas
			if (!checkAndRemoveRunes(player, spell)) {
				return;
			}
			Iterator<InvItem> inventory = player.getInventory().iterator();
			int boneCount = 0;
			while (inventory.hasNext()) {
				InvItem i = inventory.next();
				if (i.getID() == 20) {
					inventory.remove();
					boneCount++;
				}
			}
			for (int i = 0; i < boneCount; i++) {
				player.getInventory().add(new InvItem(249));
			}
			finalizeSpell(player, spell);
			break;
		case 48: // Charge
			if (world.getTile(player.getLocation()).hasGameObject()) {
				player.getActionSender()
						.sendMessage(
								"You cannot charge here, please move to a different area.");
				return;
			}
			if (!checkAndRemoveRunes(player, spell)) {
				return;
			}
			GameObject charge = new GameObject(player.getLocation(), 1147, 0, 0);
			world.registerGameObject(charge);
			world.delayedRemoveObject(charge, 500);
			player.setCharged();
			player.resetPath();
			finalizeSpell(player, spell);
			break;
		}
	}

	private void handleTeleport(Player player, SpellDef spell, int id) {
		if (player.inCombat()) {
			player.getActionSender().sendMessage(
					"You cannot teleport while in combat.");
			return;
		}
		if (player.getLocation().wildernessLevel() >= 20
				|| (player.getLocation().inModRoom() && !player.isMod())) {
			player.getActionSender().sendMessage(
					"A magical force stops you from teleporting.");
			return;
		}
		if (!checkAndRemoveRunes(player, spell)) {
			return;
		}
		switch (id) {
		case 12: // Varrock
			player.teleport(122, 503, true);
			break;
		case 15: // Lumbridge
			player.teleport(118, 649, true);
			break;
		case 18: // Falador
			player.teleport(313, 550, true);
			break;
		case 22: // Camalot
			player.teleport(465, 456, true);
			break;
		case 26: // Ardougne
			player.teleport(585, 621, true);
			break;
		case 31: // Watchtower
			player.teleport(637, 2628, true);
			break;
		case 37: // Lost city
			player.teleport(131, 3544, true);
			break;
		}
		finalizeSpell(player, spell);
	}

	private static boolean canCast(Player player) {
		if (!player.castTimer()) {
			player.getActionSender().sendMessage(
					"You must wait another " + player.getSpellWait()
							+ " seconds to cast another spell.");
			player.resetPath();
			return false;
		}
		return true;
	}

	private static boolean checkAndRemoveRunes(Player player, SpellDef spell) {
		for (Entry<Integer, Integer> e : spell.getRunesRequired()) {
			boolean skipRune = false;
			for (InvItem staff : getStaffs(e.getKey())) {
				if (player.getInventory().contains(staff)) {
					for (InvItem item : player.getInventory().getItems()) {
						if (item.equals(staff) && item.isWielded()) {
							skipRune = true;
							break;
						}
					}
				}
			}
			if (skipRune) {
				continue;
			}
			if (player.getInventory()
					.countId(((Integer) e.getKey()).intValue()) < ((Integer) e
					.getValue()).intValue()) {
				player.setSuspiciousPlayer(true);
				player.getActionSender()
						.sendMessage(
								"You don't have all the reagents you need for this spell");
				return false;
			}
		}
		for (Entry<Integer, Integer> e : spell.getRunesRequired()) {
			boolean skipRune = false;
			for (InvItem staff : getStaffs(e.getKey())) {
				if (player.getInventory().contains(staff)) {
					for (InvItem item : player.getInventory().getItems()) {
						if (item.equals(staff) && item.isWielded()) {
							skipRune = true;
							break;
						}
					}
				}
			}
			if (skipRune) {
				continue;
			}
			player.getInventory().remove(((Integer) e.getKey()).intValue(),
					((Integer) e.getValue()).intValue());
		}
		return true;
	}

	private void finalizeSpell(Player player, SpellDef spell) {
		player.getActionSender().sendSound("spellok");
		player.getActionSender().sendMessage("Cast spell successfully");
		player.incExp(6, spell.getExp() * 10, true);
		player.setCastTimer();
	}

	private static InvItem[] getStaffs(int runeID) {
		InvItem[] items = staffs.get(runeID);
		if (items == null) {
			return new InvItem[0];
		}
		return items;

	}

	private static TreeMap<Integer, InvItem[]> staffs = new TreeMap<Integer, InvItem[]>();

	static {
		staffs.put(31, new InvItem[] { new InvItem(197), new InvItem(615),
				new InvItem(682) }); // Fire-Rune
		staffs.put(32, new InvItem[] { new InvItem(102), new InvItem(616),
				new InvItem(683) }); // Water-Rune
		staffs.put(33, new InvItem[] { new InvItem(101), new InvItem(617),
				new InvItem(684) }); // Air-Rune
		staffs.put(34, new InvItem[] { new InvItem(103), new InvItem(618),
				new InvItem(685) }); // Earth-Rune
	}

}