package org.rscdaemon.server.packethandler.client;

import org.rscdaemon.server.packethandler.PacketHandler;
import org.rscdaemon.server.model.*;
import org.rscdaemon.server.net.Packet;
import org.rscdaemon.server.net.RSCPacket;
import org.rscdaemon.server.util.DataConversions;
import org.rscdaemon.server.entityhandling.EntityHandler;
import org.rscdaemon.server.entityhandling.defs.GameObjectDef;
import org.rscdaemon.server.entityhandling.defs.extras.*;
import org.rscdaemon.server.event.ShortEvent;
import org.rscdaemon.server.event.Thieving;
import org.rscdaemon.server.event.SingleEvent;
import org.rscdaemon.server.event.MiniEvent;
import org.rscdaemon.server.event.WalkToObjectEvent;
import org.rscdaemon.server.util.Formulae;
import org.rscdaemon.server.states.Action;
import org.rscdaemon.server.event.FightEvent;
import org.apache.mina.common.IoSession;
import java.util.List;
import java.util.ArrayList;

public class ObjectAction implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();

	public void handlePacket(Packet p, IoSession session) {
		Player player = (Player) session.getAttachment();
		int pID = ((RSCPacket) p).getID();
		if (player.isBusy()) {
			player.resetPath();
			return;
		}
		player.resetAll();
		final GameObject object = world.getTile(p.readShort(), p.readShort())
				.getGameObject();
		final int click = pID == 51 ? 0 : 1;
		if (object == null) {
			player.setSuspiciousPlayer(true);
			return;
		}
		player.setStatus(Action.USING_OBJECT);
		world.getDelayedEventHandler().add(
				new WalkToObjectEvent(player, object, false) {
					private void replaceGameObject(int newID, boolean open) {
						world.registerGameObject(new GameObject(object
								.getLocation(), newID, object.getDirection(),
								object.getType()));
						owner.getActionSender().sendSound(
								open ? "opendoor" : "closedoor");
					}

					private void doGate() {
						owner.getActionSender().sendSound("opendoor");
						world.registerGameObject(new GameObject(object
								.getLocation(), 181, object.getDirection(),
								object.getType()));
						world.delayedSpawnObject(object.getLoc(), 1000);
					}

					private int[] coordModifier(Player player, boolean up) {
						if (object.getGameObjectDef().getHeight() <= 1) {
							return new int[] { player.getX(),
									Formulae.getNewY(player.getY(), up) };
						}
						int[] coords = { object.getX(),
								Formulae.getNewY(object.getY(), up) };
						switch (object.getDirection()) {
						case 0:
							coords[1] -= (up ? -object.getGameObjectDef()
									.getHeight() : 1);
							break;
						case 2:
							coords[0] -= (up ? -object.getGameObjectDef()
									.getHeight() : 1);
							break;
						case 4:
							coords[1] += (up ? -1 : object.getGameObjectDef()
									.getHeight());
							break;
						case 6:
							coords[0] += (up ? -1 : object.getGameObjectDef()
									.getHeight());
							break;
						}
						return coords;
					}

					public void arrived() {
						owner.resetPath();
						GameObjectDef def = object.getGameObjectDef();
						if (owner.isBusy() || owner.isRanging()
								|| !owner.nextTo(object) || def == null
								|| owner.getStatus() != Action.USING_OBJECT) {
							return;
						}
						owner.resetAll();
						String command = (click == 0 ? def.getCommand1() : def
								.getCommand2()).toLowerCase();
						Point telePoint = EntityHandler.getObjectTelePoint(
								object.getLocation(), command);
						if (telePoint != null) {
							owner.teleport(telePoint.getX(), telePoint.getY(),
									false);
						} else if (object.getID() == 198
								&& object.getX() == 251 && object.getY() == 468) { // Prayer
																					// Guild
																					// Ladder
							if (owner.getMaxStat(5) < 31) {
								owner.setBusy(true);
								Npc abbot = world.getNpc(174, 249, 252, 458,
										468);
								if (abbot != null) {
									owner.informOfNpcMessage(new ChatMessage(
											abbot,
											"Hello only people with high prayer are allowed in here",
											owner));
								}
								world.getDelayedEventHandler().add(
										new ShortEvent(owner) {
											public void action() {
												owner.setBusy(false);
												owner.getActionSender()
														.sendMessage(
																"You need a prayer level of 31 to enter");
											}
										});
							} else {
								owner.teleport(251, 1411, false);
							}
						} else if (object.getID() == 655) { // Agility Log
							owner.setBusy(true);
							owner.getActionSender().sendMessage(
									"You stand on the slippery log");
							world.getDelayedEventHandler().add(
									new ShortEvent(owner) {
										public void action() {
											owner.getActionSender()
													.sendMessage(
															"and walk across");
											if (owner.getX() <= 692) {
												owner.teleport(692, 497, false);
												world.getDelayedEventHandler()
														.add(new ShortEvent(
																owner) {
															public void action() {
																owner.teleport(
																		692,
																		499,
																		false);
																owner.incExp(
																		16,
																		300,
																		true);
																owner.setBusy(false);
															}
														});
												return;
											}
										}
									});
						} else if (object.getID() == 676) { // Agility Log
							owner.setBusy(true);
							owner.getActionSender().sendMessage(
									"You stand on the slippery log");
							world.getDelayedEventHandler().add(
									new ShortEvent(owner) {
										public void action() {
											owner.getActionSender()
													.sendMessage(
															"and walk across");
											if (owner.getX() <= 487) {
												owner.teleport(490, 563, false);
												world.getDelayedEventHandler()
														.add(new ShortEvent(
																owner) {
															public void action() {
																owner.teleport(
																		493,
																		563,
																		false);
																owner.incExp(
																		16,
																		600,
																		true);
																owner.setBusy(false);
															}
														});
												return;
											}
										}
									});
						} else if (object.getID() == 529) { // Agility Ledge
							owner.setBusy(true);
							owner.getActionSender().sendMessage(
									"You stand on the ledge");
							world.getDelayedEventHandler().add(
									new ShortEvent(owner) {
										public void action() {
											owner.getActionSender()
													.sendMessage(
															"and walk across");
											if (owner.getX() <= 497) {
												owner.teleport(499, 1506, false);
												world.getDelayedEventHandler()
														.add(new ShortEvent(
																owner) {
															public void action() {
																owner.teleport(
																		501,
																		1506,
																		false);
																owner.incExp(
																		16,
																		600,
																		true);
																owner.setBusy(false);
															}
														});
												return;
											}
										}
									});
						} else if (object.getID() == 692) { // Agility Log
							owner.setBusy(true);
							owner.getActionSender().sendMessage(
									"You stand on the slippery log");
							world.getDelayedEventHandler().add(
									new ShortEvent(owner) {
										public void action() {
											owner.getActionSender()
													.sendMessage(
															"and walk across");
											if (owner.getX() <= 370) {
												owner.teleport(368, 781, false);
												world.getDelayedEventHandler()
														.add(new ShortEvent(
																owner) {
															public void action() {
																owner.teleport(
																		366,
																		781,
																		false);
																owner.incExp(
																		16,
																		300,
																		true);
																owner.setBusy(false);
															}
														});
												return;
											}
										}
									});
						} else if (object.getID() == 223
								&& object.getX() == 274 && object.getY() == 566) { // Mining
																					// Guild
																					// Ladder
							if (owner.getCurStat(14) < 66) {
								owner.setBusy(true);
								Npc dwarf = world.getNpc(191, 272, 277, 563,
										567);
								if (dwarf != null) {
									owner.informOfNpcMessage(new ChatMessage(
											dwarf,
											"Hello only the top miners are allowed in here",
											owner));
								}
								world.getDelayedEventHandler().add(
										new ShortEvent(owner) {
											public void action() {
												owner.setBusy(false);
												owner.getActionSender()
														.sendMessage(
																"You need a mining level of 66 to enter");
											}
										});
							} else {
								owner.teleport(274, 3397, false);
							}
						}
						/*
						 * else if(object.getID() == 324){ owner.setBusy(true);
						 * if(owner.getMaxStat(17) <
						 * 35){owner.getActionSender().sendMessage(
						 * "You must be at least 35 thieving to steal from this stall."
						 * );owner.setBusy(false);return;}
						 * owner.getActionSender(
						 * ).sendMessage("You attempt to steal from the stall..."
						 * ); world.getDelayedEventHandler().add(new
						 * ShortEvent(owner) { public void action(){
						 * if(!Formulae.catchThief(owner.getCurStat(17), 35)){
						 * owner.getActionSender().sendMessage(
						 * "You steal from the stall!");
						 * owner.getInventory().add(new InvItem(541, 1));
						 * owner.getActionSender().sendInventory();
						 * owner.incExp(17, 36 * 10, true);
						 * owner.getActionSender().sendStat(17);
						 * owner.setBusy(false); }else{
						 * owner.getActionSender().sendMessage
						 * ("You fail to steal from the stall.");
						 * owner.setBusy(false); Npc dwarf = world.getNpc(322,
						 * 321, 324, 323); owner.informOfNpcMessage(new
						 * ChatMessage(dwarf,
						 * "Hey!  Get your hands off of there!", owner));
						 * if(dwarf != null){ owner.informOfNpcMessage(new
						 * ChatMessage(dwarf,
						 * "Hey!  Get your hands off of there!", owner));
						 * if(owner.getLocation().inBounds(dwarf.getLoc().minX -
						 * 4, dwarf.getLoc().minY - 4, dwarf.getLoc().maxX + 4,
						 * dwarf.getLoc().maxY + 4)){ //
						 * owner.informOfNpcMessage(new ChatMessage(dwarf,
						 * "Hey!  Get your hands off of there!", owner));
						 * owner.resetPath(); owner.resetAll();
						 * owner.setStatus(Action.FIGHTING_MOB);
						 * owner.getActionSender
						 * ().sendMessage("You are under attack!");
						 * 
						 * dwarf.setLocation(owner.getLocation(), true);
						 * for(Player p :
						 * dwarf.getViewArea().getPlayersInView()) {
						 * p.removeWatchedNpc(dwarf); }
						 * 
						 * owner.setBusy(true); owner.setSprite(9);
						 * owner.setOpponent(dwarf); owner.setCombatTimer();
						 * 
						 * dwarf.setBusy(true); dwarf.setSprite(8);
						 * dwarf.setOpponent(owner); dwarf.setCombatTimer();
						 * FightEvent fighting = new FightEvent(owner, dwarf,
						 * true); fighting.setLastRun(0);
						 * world.getDelayedEventHandler().add(fighting); } }
						 * else{owner.getActionSender().sendMessage(
						 * "Can not find an npc to alert you...this shouldn't happen."
						 * );} } } }); }
						 */
						// else if(command.equals("steal-from")){
						if (object.getID() == 324) {
							owner.setBusy(true);
							if (owner.getMaxStat(17) < 35) {
								owner.getActionSender()
										.sendMessage(
												"You must be at least 35 thieving to steal from this stall.");
								owner.setBusy(false);
								return;
							}
							owner.getActionSender().sendMessage(
									"You attempt to steal from the stall...");
							world.getDelayedEventHandler().add(
									new ShortEvent(owner) {
										public void action() {
											if (!Formulae.catchThief(
													owner.getCurStat(17), 35)) {
												owner.getActionSender()
														.sendMessage(
																"You steal from the stall!");
												owner.getInventory().add(
														new InvItem(541, 1));
												owner.getActionSender()
														.sendInventory();
												owner.incExp(17, 36 * 10, true);
												owner.getActionSender()
														.sendStat(17);
												world.registerGameObject(new GameObject(
														object.getLocation(),
														341,
														object.getDirection(),
														object.getType()));
												world.delayedSpawnObject(
														object.getLoc(),
														20 * 1000);
												owner.setBusy(false);
											} else {
												owner.getActionSender()
														.sendMessage(
																"You fail to steal from the stall.");
												owner.setBusy(false);
												Npc dwarf = owner
														.getClosestNpc();
												// owner.informOfNpcMessage(new
												// ChatMessage(dwarf,
												// "Hey!  Get your hands off of there!",
												// owner));
												if (dwarf != null) {
													owner.informOfNpcMessage(new ChatMessage(
															dwarf,
															"Hey!  Get your hands off of there!",
															owner));
													if (owner
															.getLocation()
															.inBounds(
																	dwarf.getX() - 4,
																	dwarf.getY() - 4,
																	dwarf.getX() + 4,
																	dwarf.getY() + 4)
															&& dwarf.getID() == 322
															|| owner.getLocation()
																	.inBounds(
																			dwarf.getX() - 4,
																			dwarf.getY() - 4,
																			dwarf.getX() + 4,
																			dwarf.getY() + 4)
															&& dwarf.getID() == 321
															|| owner.getLocation()
																	.inBounds(
																			dwarf.getX() - 4,
																			dwarf.getY() - 4,
																			dwarf.getX() + 4,
																			dwarf.getY() + 4)
															&& dwarf.getID() == 324
															|| owner.getLocation()
																	.inBounds(
																			dwarf.getX() - 4,
																			dwarf.getY() - 4,
																			dwarf.getX() + 4,
																			dwarf.getY() + 4)
															&& dwarf.getID() == 323) {
														// if(dwarf.getID()==322
														// || dwarf.getID()==321
														// ||
														// dwarf.getID()==324||
														// dwarf.getID() == 323)
														dwarf.resetPath();
														owner.resetPath();
														owner.resetAll();
														owner.setStatus(Action.FIGHTING_MOB);
														owner.getActionSender()
																.sendMessage(
																		"You are under attack!");

														dwarf.setLocation(owner
																.getLocation(),
																true);
														for (Player p : dwarf
																.getViewArea()
																.getPlayersInView()) {
															p.removeWatchedNpc(dwarf);
														}

														owner.setBusy(true);
														owner.setSprite(9);
														owner.setOpponent(dwarf);
														owner.setCombatTimer();

														dwarf.setBusy(true);
														dwarf.setSprite(8);
														dwarf.setOpponent(owner);
														dwarf.setCombatTimer();
														FightEvent fighting = new FightEvent(
																owner, dwarf,
																true);
														fighting.setLastRun(0);
														world.getDelayedEventHandler()
																.add(fighting);
													}
												} else {
													owner.getActionSender()
															.sendMessage(
																	"Can not find an npc to alert you...this shouldn't happen.");
												}
											}
										}
									});
						} else if (object.getID() == 141) {
							owner.setBusy(true);
							owner.getActionSender().sendMessage(
									"You search the cupboard...");
							world.getDelayedEventHandler().add(
									new ShortEvent(owner) {
										public void action() {
											owner.setBusy(false);
											owner.getActionSender()
													.sendMessage(
															"You find a clove of garlic!");
											owner.getInventory().add(
													new InvItem(218, 1));
											owner.getActionSender()
													.sendInventory();
										}
									});
						} else if (object.getID() == 340) {
							if (command.equals("search for traps")) {
								owner.setBusy(true);
								if (owner.getMaxStat(17) < 80) {
									owner.getActionSender()
											.sendMessage(
													"You must be at least 80 thieving to loot this chest.");
									owner.setBusy(false);
									return;
								}
								if (System.currentTimeMillis()
										- object.getLootable() < 120000) {
									owner.getActionSender()
											.sendMessage(
													"This chest has already been looted.");
									owner.setBusy(false);
									return;
								}
								// owner.getActionSender().sendMessage("You attempt to loot the chest...");
								if (System.currentTimeMillis()
										- object.getLootable() > 120000) {
									owner.getActionSender()
											.sendMessage(
													"You search the chest for traps...");
									world.getDelayedEventHandler().add(
											new ShortEvent(owner) {
												public void action() {
													if (!Formulae.catchThief(
															owner.getCurStat(17),
															80)) {
														world.registerGameObject(new GameObject(
																object.getLocation(),
																339,
																object.getDirection(),
																object.getType()));
														world.delayedSpawnObject(
																object.getLoc(),
																1000);
														world.getDelayedEventHandler()
																.add(new MiniEvent(
																		owner,
																		1001) {
																	public void action() {
																		ActiveTile tile = world
																				.getTile(object
																						.getLocation());
																		GameObject object2 = tile
																				.getGameObject();
																		object2.setLootable();
																	}
																});
														owner.getActionSender()
																.sendMessage(
																		"You manage to loot the chest!");
														owner.setBusy(false);
														owner.getInventory()
																.add(new InvItem(
																		619, 2));
														owner.getInventory()
																.add(new InvItem(
																		10, 500));
														owner.incExp(17,
																210 * 10, true);
														owner.getActionSender()
																.sendStat(17);
														owner.getActionSender()
																.sendInventory();
													} else {
														owner.getActionSender()
																.sendMessage(
																		"You fail to loot the chest.");
														owner.setBusy(false);
													}
												}
											});
								}
							} else {
								owner.setBusy(true);
								owner.getActionSender().sendMessage(
										"You attempt to open the chest...");
								world.registerGameObject(new GameObject(object
										.getLocation(), 339, object
										.getDirection(), object.getType()));
								world.delayedSpawnObject(object.getLoc(), 1000);
								owner.getActionSender().sendMessage(
										"It was booby trapped!");
								owner.setLastDamage(15);
								int newHp = owner.getHits() - 15;
								owner.setHits(newHp);
								Npc dwarf = owner.getClosestNpc();
								ArrayList<Player> playersToInform = new ArrayList<Player>();
								playersToInform.addAll(owner.getViewArea()
										.getPlayersInView());
								for (Player p : playersToInform) {
									p.informOfModifiedHits(owner);
								}
								if (newHp <= 0) {
									owner.killedBy(dwarf, false);
								}
								owner.setBusy(false);
							}
						} else if (object.getID() == 336) {
							if (command.equals("search for traps")) {
								owner.setBusy(true);
								if (owner.getMaxStat(17) < 28) {
									owner.getActionSender()
											.sendMessage(
													"You must be at least 28 thieving to loot this chest.");
									owner.setBusy(false);
									return;
								}
								if (System.currentTimeMillis()
										- object.getLootable() < 60000) {
									owner.getActionSender()
											.sendMessage(
													"This chest has already been looted.");
									owner.setBusy(false);
									return;
								}
								// owner.getActionSender().sendMessage("You attempt to loot the chest...");
								if (System.currentTimeMillis()
										- object.getLootable() > 60000) {
									owner.getActionSender()
											.sendMessage(
													"You search the chest for traps...");
									world.getDelayedEventHandler().add(
											new ShortEvent(owner) {
												public void action() {
													if (!Formulae.catchThief(
															owner.getCurStat(17),
															28)) {
														world.registerGameObject(new GameObject(
																object.getLocation(),
																339,
																object.getDirection(),
																object.getType()));
														world.delayedSpawnObject(
																object.getLoc(),
																1000);
														world.getDelayedEventHandler()
																.add(new MiniEvent(
																		owner,
																		1001) {
																	public void action() {
																		ActiveTile tile = world
																				.getTile(object
																						.getLocation());
																		GameObject object2 = tile
																				.getGameObject();
																		object2.setLootable();
																	}
																});
														owner.getActionSender()
																.sendMessage(
																		"You manage to loot the chest!");
														owner.setBusy(false);
														owner.getInventory()
																.add(new InvItem(
																		40, 1));
														owner.getInventory()
																.add(new InvItem(
																		10, 200));
														owner.incExp(17,
																110 * 10, true);
														owner.getActionSender()
																.sendStat(17);
														owner.getActionSender()
																.sendInventory();
													} else {
														owner.getActionSender()
																.sendMessage(
																		"You fail to loot the chest.");
														owner.setBusy(false);
													}
												}
											});
								}
							} else {
								owner.setBusy(true);
								owner.getActionSender().sendMessage(
										"You attempt to open the chest...");
								world.registerGameObject(new GameObject(object
										.getLocation(), 339, object
										.getDirection(), object.getType()));
								world.delayedSpawnObject(object.getLoc(), 1000);
								owner.getActionSender().sendMessage(
										"It was booby trapped!");
								owner.setLastDamage(15);
								int newHp = owner.getHits() - 15;
								owner.setHits(newHp);
								Npc dwarf = owner.getClosestNpc();
								ArrayList<Player> playersToInform = new ArrayList<Player>();
								playersToInform.addAll(owner.getViewArea()
										.getPlayersInView());
								for (Player p : playersToInform) {
									p.informOfModifiedHits(owner);
								}
								if (newHp <= 0) {
									owner.killedBy(dwarf, false);
								}
								owner.setBusy(false);
							}
						} else if (object.getID() == 325) {
							owner.setBusy(true);
							if (owner.getMaxStat(17) < 50) {
								owner.getActionSender()
										.sendMessage(
												"You must be at least 50 thieving to steal from this stall.");
								owner.setBusy(false);
								return;
							}
							owner.getActionSender().sendMessage(
									"You attempt to steal from the stall...");
							world.getDelayedEventHandler().add(
									new ShortEvent(owner) {
										public void action() {
											if (!Formulae.catchThief(
													owner.getCurStat(17), 50)) {
												owner.getActionSender()
														.sendMessage(
																"You steal from the stall!");
												owner.getInventory().add(
														new InvItem(383, 1));
												owner.getActionSender()
														.sendInventory();
												owner.incExp(17, 50 * 10, true);
												owner.getActionSender()
														.sendStat(17);
												world.registerGameObject(new GameObject(
														object.getLocation(),
														341,
														object.getDirection(),
														object.getType()));
												world.delayedSpawnObject(
														object.getLoc(),
														30 * 1000);
												owner.setBusy(false);
											} else {
												owner.getActionSender()
														.sendMessage(
																"You fail to steal from the stall.");
												owner.setBusy(false);
												Npc dwarf = owner
														.getClosestNpc();
												// owner.informOfNpcMessage(new
												// ChatMessage(dwarf,
												// "Hey!  Get your hands off of there!",
												// owner));
												if (dwarf != null) {
													owner.informOfNpcMessage(new ChatMessage(
															dwarf,
															"Hey!  Get your hands off of there!",
															owner));
													if (owner
															.getLocation()
															.inBounds(
																	dwarf.getX() - 4,
																	dwarf.getY() - 4,
																	dwarf.getX() + 4,
																	dwarf.getY() + 4)
															&& dwarf.getID() == 322
															|| owner.getLocation()
																	.inBounds(
																			dwarf.getX() - 4,
																			dwarf.getY() - 4,
																			dwarf.getX() + 4,
																			dwarf.getY() + 4)
															&& dwarf.getID() == 321
															|| owner.getLocation()
																	.inBounds(
																			dwarf.getX() - 4,
																			dwarf.getY() - 4,
																			dwarf.getX() + 4,
																			dwarf.getY() + 4)
															&& dwarf.getID() == 324
															|| owner.getLocation()
																	.inBounds(
																			dwarf.getX() - 4,
																			dwarf.getY() - 4,
																			dwarf.getX() + 4,
																			dwarf.getY() + 4)
															&& dwarf.getID() == 323) {
														// if(dwarf.getID()==322
														// || dwarf.getID()==321
														// ||
														// dwarf.getID()==324||
														// dwarf.getID() == 323)
														dwarf.resetPath();
														owner.resetPath();
														owner.resetAll();
														owner.setStatus(Action.FIGHTING_MOB);
														owner.getActionSender()
																.sendMessage(
																		"You are under attack!");

														dwarf.setLocation(owner
																.getLocation(),
																true);
														for (Player p : dwarf
																.getViewArea()
																.getPlayersInView()) {
															p.removeWatchedNpc(dwarf);
														}

														owner.setBusy(true);
														owner.setSprite(9);
														owner.setOpponent(dwarf);
														owner.setCombatTimer();

														dwarf.setBusy(true);
														dwarf.setSprite(8);
														dwarf.setOpponent(owner);
														dwarf.setCombatTimer();
														FightEvent fighting = new FightEvent(
																owner, dwarf,
																true);
														fighting.setLastRun(0);
														world.getDelayedEventHandler()
																.add(fighting);
													}
												} else {
													owner.getActionSender()
															.sendMessage(
																	"Can not find an npc to alert you...this shouldn't happen.");
												}
											}
										}
									});
						} else if (object.getID() == 327) {
							owner.setBusy(true);
							if (owner.getMaxStat(17) < 75) {
								owner.getActionSender()
										.sendMessage(
												"You must be at least 75 thieving to steal from this stall.");
								owner.setBusy(false);
								return;
							}
							owner.getActionSender().sendMessage(
									"You attempt to steal from the stall...");
							world.getDelayedEventHandler().add(
									new ShortEvent(owner) {
										public void action() {
											if (!Formulae.catchThief(
													owner.getCurStat(17), 75)) {
												owner.getActionSender()
														.sendMessage(
																"You steal from the stall!");
												int rand = DataConversions
														.random(0, 3);
												switch (rand) {
												case 0:
													owner.getInventory()
															.add(new InvItem(
																	157, 1));
													break;
												case 1:
													owner.getInventory()
															.add(new InvItem(
																	158, 1));
													break;
												case 2:
													owner.getInventory()
															.add(new InvItem(
																	159, 1));
													break;
												case 3:
													owner.getInventory()
															.add(new InvItem(
																	160, 1));
													break;
												}
												owner.getActionSender()
														.sendInventory();
												owner.incExp(17, 160 * 10, true);
												owner.getActionSender()
														.sendStat(17);
												world.registerGameObject(new GameObject(
														object.getLocation(),
														341,
														object.getDirection(),
														object.getType()));
												world.delayedSpawnObject(
														object.getLoc(),
														60 * 1000);
												owner.setBusy(false);
											} else {
												owner.getActionSender()
														.sendMessage(
																"You fail to steal from the stall.");
												owner.setBusy(false);
												Npc dwarf = owner
														.getClosestNpc();
												// owner.informOfNpcMessage(new
												// ChatMessage(dwarf,
												// "Hey!  Get your hands off of there!",
												// owner));
												if (dwarf != null) {
													owner.informOfNpcMessage(new ChatMessage(
															dwarf,
															"Hey!  Get your hands off of there!",
															owner));
													if (owner
															.getLocation()
															.inBounds(
																	dwarf.getX() - 4,
																	dwarf.getY() - 4,
																	dwarf.getX() + 4,
																	dwarf.getY() + 4)
															&& dwarf.getID() == 322
															|| owner.getLocation()
																	.inBounds(
																			dwarf.getX() - 4,
																			dwarf.getY() - 4,
																			dwarf.getX() + 4,
																			dwarf.getY() + 4)
															&& dwarf.getID() == 321
															|| owner.getLocation()
																	.inBounds(
																			dwarf.getX() - 4,
																			dwarf.getY() - 4,
																			dwarf.getX() + 4,
																			dwarf.getY() + 4)
															&& dwarf.getID() == 324
															|| owner.getLocation()
																	.inBounds(
																			dwarf.getX() - 4,
																			dwarf.getY() - 4,
																			dwarf.getX() + 4,
																			dwarf.getY() + 4)
															&& dwarf.getID() == 323) {
														// if(dwarf.getID()==322
														// || dwarf.getID()==321
														// ||
														// dwarf.getID()==324||
														// dwarf.getID() == 323)
														dwarf.resetPath();
														owner.resetPath();
														owner.resetAll();
														owner.setStatus(Action.FIGHTING_MOB);
														owner.getActionSender()
																.sendMessage(
																		"You are under attack!");

														dwarf.setLocation(owner
																.getLocation(),
																true);
														for (Player p : dwarf
																.getViewArea()
																.getPlayersInView()) {
															p.removeWatchedNpc(dwarf);
														}

														owner.setBusy(true);
														owner.setSprite(9);
														owner.setOpponent(dwarf);
														owner.setCombatTimer();

														dwarf.setBusy(true);
														dwarf.setSprite(8);
														dwarf.setOpponent(owner);
														dwarf.setCombatTimer();
														FightEvent fighting = new FightEvent(
																owner, dwarf,
																true);
														fighting.setLastRun(0);
														world.getDelayedEventHandler()
																.add(fighting);
													}
												} else {
													owner.getActionSender()
															.sendMessage(
																	"Can not find an npc to alert you...this shouldn't happen.");
												}
											}
										}
									});
						} else if (object.getID() == 322) {
							owner.setBusy(true);
							if (owner.getMaxStat(17) < 5) {
								owner.getActionSender()
										.sendMessage(
												"You must be at least 5 thieving to steal from this stall.");
								owner.setBusy(false);
								return;
							}
							owner.getActionSender().sendMessage(
									"You attempt to steal from the stall...");
							world.getDelayedEventHandler().add(
									new ShortEvent(owner) {
										public void action() {
											if (!Formulae.catchThief(
													owner.getCurStat(17), 5)) {
												owner.getActionSender()
														.sendMessage(
																"You steal from the stall!");
												owner.getInventory().add(
														new InvItem(330, 1));
												owner.getActionSender()
														.sendInventory();
												owner.incExp(17, 16 * 10, true);
												owner.getActionSender()
														.sendStat(17);
												world.registerGameObject(new GameObject(
														object.getLocation(),
														341,
														object.getDirection(),
														object.getType()));
												world.delayedSpawnObject(
														object.getLoc(),
														10 * 1000);
												owner.setBusy(false);
											} else {
												owner.getActionSender()
														.sendMessage(
																"You fail to steal from the stall.");
												owner.setBusy(false);
												Npc dwarf = owner
														.getClosestNpc();
												// owner.informOfNpcMessage(new
												// ChatMessage(dwarf,
												// "Hey!  Get your hands off of there!",
												// owner));
												if (dwarf != null) {
													owner.informOfNpcMessage(new ChatMessage(
															dwarf,
															"Hey!  Get your hands off of there!",
															owner));
													if (owner
															.getLocation()
															.inBounds(
																	dwarf.getX() - 4,
																	dwarf.getY() - 4,
																	dwarf.getX() + 4,
																	dwarf.getY() + 4)
															&& dwarf.getID() == 322
															|| owner.getLocation()
																	.inBounds(
																			dwarf.getX() - 4,
																			dwarf.getY() - 4,
																			dwarf.getX() + 4,
																			dwarf.getY() + 4)
															&& dwarf.getID() == 321
															|| owner.getLocation()
																	.inBounds(
																			dwarf.getX() - 4,
																			dwarf.getY() - 4,
																			dwarf.getX() + 4,
																			dwarf.getY() + 4)
															&& dwarf.getID() == 324
															|| owner.getLocation()
																	.inBounds(
																			dwarf.getX() - 4,
																			dwarf.getY() - 4,
																			dwarf.getX() + 4,
																			dwarf.getY() + 4)
															&& dwarf.getID() == 323) {
														// if(dwarf.getID()==322
														// || dwarf.getID()==321
														// ||
														// dwarf.getID()==324||
														// dwarf.getID() == 323)
														dwarf.resetPath();
														owner.resetPath();
														owner.resetAll();
														owner.setStatus(Action.FIGHTING_MOB);
														owner.getActionSender()
																.sendMessage(
																		"You are under attack!");

														dwarf.setLocation(owner
																.getLocation(),
																true);
														for (Player p : dwarf
																.getViewArea()
																.getPlayersInView()) {
															p.removeWatchedNpc(dwarf);
														}

														owner.setBusy(true);
														owner.setSprite(9);
														owner.setOpponent(dwarf);
														owner.setCombatTimer();

														dwarf.setBusy(true);
														dwarf.setSprite(8);
														dwarf.setOpponent(owner);
														dwarf.setCombatTimer();
														FightEvent fighting = new FightEvent(
																owner, dwarf,
																true);
														fighting.setLastRun(0);
														world.getDelayedEventHandler()
																.add(fighting);
													}
												} else {
													owner.getActionSender()
															.sendMessage(
																	"Can not find an npc to alert you...this shouldn't happen.");
												}
											}
										}
									});
						} else if (object.getID() == 326) {
							owner.setBusy(true);
							if (owner.getMaxStat(17) < 65) {
								owner.getActionSender()
										.sendMessage(
												"You must be at least 65 thieving to steal from this stall.");
								owner.setBusy(false);
								return;
							}
							owner.getActionSender().sendMessage(
									"You attempt to steal from the stall...");
							world.getDelayedEventHandler().add(
									new ShortEvent(owner) {
										public void action() {
											if (!Formulae.catchThief(
													owner.getCurStat(17), 65)) {
												owner.getActionSender()
														.sendMessage(
																"You steal from the stall!");
												owner.getInventory().add(
														new InvItem(707, 1));
												owner.getActionSender()
														.sendInventory();
												owner.incExp(17, 81 * 10, true);
												world.registerGameObject(new GameObject(
														object.getLocation(),
														341,
														object.getDirection(),
														object.getType()));
												world.delayedSpawnObject(
														object.getLoc(),
														45 * 1000);
												owner.getActionSender()
														.sendStat(17);
												owner.setBusy(false);
											} else {
												owner.getActionSender()
														.sendMessage(
																"You fail to steal from the stall.");
												owner.setBusy(false);
												Npc dwarf = owner
														.getClosestNpc();
												// owner.informOfNpcMessage(new
												// ChatMessage(dwarf,
												// "Hey!  Get your hands off of there!",
												// owner));
												if (dwarf != null) {
													owner.informOfNpcMessage(new ChatMessage(
															dwarf,
															"Hey!  Get your hands off of there!",
															owner));
													if (owner
															.getLocation()
															.inBounds(
																	dwarf.getX() - 4,
																	dwarf.getY() - 4,
																	dwarf.getX() + 4,
																	dwarf.getY() + 4)
															&& dwarf.getID() == 322
															|| owner.getLocation()
																	.inBounds(
																			dwarf.getX() - 4,
																			dwarf.getY() - 4,
																			dwarf.getX() + 4,
																			dwarf.getY() + 4)
															&& dwarf.getID() == 321
															|| owner.getLocation()
																	.inBounds(
																			dwarf.getX() - 4,
																			dwarf.getY() - 4,
																			dwarf.getX() + 4,
																			dwarf.getY() + 4)
															&& dwarf.getID() == 324
															|| owner.getLocation()
																	.inBounds(
																			dwarf.getX() - 4,
																			dwarf.getY() - 4,
																			dwarf.getX() + 4,
																			dwarf.getY() + 4)
															&& dwarf.getID() == 323) {
														// if(dwarf.getID()==322
														// || dwarf.getID()==321
														// ||
														// dwarf.getID()==324||
														// dwarf.getID() == 323)
														dwarf.resetPath();
														owner.resetPath();
														owner.resetAll();
														owner.setStatus(Action.FIGHTING_MOB);
														owner.getActionSender()
																.sendMessage(
																		"You are under attack!");

														dwarf.setLocation(owner
																.getLocation(),
																true);
														for (Player p : dwarf
																.getViewArea()
																.getPlayersInView()) {
															p.removeWatchedNpc(dwarf);
														}

														owner.setBusy(true);
														owner.setSprite(9);
														owner.setOpponent(dwarf);
														owner.setCombatTimer();

														dwarf.setBusy(true);
														dwarf.setSprite(8);
														dwarf.setOpponent(owner);
														dwarf.setCombatTimer();
														FightEvent fighting = new FightEvent(
																owner, dwarf,
																true);
														fighting.setLastRun(0);
														world.getDelayedEventHandler()
																.add(fighting);
													}
												} else {
													owner.getActionSender()
															.sendMessage(
																	"Can not find an npc to alert you...this shouldn't happen.");
												}
											}
										}
									});
						} else if (object.getID() == 323) {
							int x = object.getLoc().getX();
							int y = object.getLoc().getY();
							int direction = object.getDirection();
							int type = object.getType();
							owner.setBusy(true);
							if (owner.getMaxStat(17) < 20) {
								owner.getActionSender()
										.sendMessage(
												"You must be at least 20 thieving to steal from this stall.");
								owner.setBusy(false);
								return;
							}
							owner.getActionSender().sendMessage(
									"You attempt to steal from the stall...");
							world.getDelayedEventHandler().add(
									new ShortEvent(owner) {
										public void action() {
											if (!Formulae.catchThief(
													owner.getCurStat(17), 20)) {
												owner.getActionSender()
														.sendMessage(
																"You steal from the stall!");
												owner.getInventory().add(
														new InvItem(383, 1));
												owner.getActionSender()
														.sendInventory();
												owner.incExp(17, 24 * 10, true);
												owner.getActionSender()
														.sendStat(17);
												world.registerGameObject(new GameObject(
														object.getLocation(),
														341,
														object.getDirection(),
														object.getType()));
												world.delayedSpawnObject(
														object.getLoc(),
														10 * 1000);
												owner.setBusy(false);
											} else {
												owner.getActionSender()
														.sendMessage(
																"You fail to steal from the stall.");
												owner.setBusy(false);
												Npc dwarf = owner
														.getClosestNpc();
												// owner.informOfNpcMessage(new
												// ChatMessage(dwarf,
												// "Hey!  Get your hands off of there!",
												// owner));
												if (dwarf != null) {
													owner.informOfNpcMessage(new ChatMessage(
															dwarf,
															"Hey!  Get your hands off of there!",
															owner));
													if (owner
															.getLocation()
															.inBounds(
																	dwarf.getX() - 4,
																	dwarf.getY() - 4,
																	dwarf.getX() + 4,
																	dwarf.getY() + 4)
															&& dwarf.getID() == 322
															|| owner.getLocation()
																	.inBounds(
																			dwarf.getX() - 4,
																			dwarf.getY() - 4,
																			dwarf.getX() + 4,
																			dwarf.getY() + 4)
															&& dwarf.getID() == 321
															|| owner.getLocation()
																	.inBounds(
																			dwarf.getX() - 4,
																			dwarf.getY() - 4,
																			dwarf.getX() + 4,
																			dwarf.getY() + 4)
															&& dwarf.getID() == 324
															|| owner.getLocation()
																	.inBounds(
																			dwarf.getX() - 4,
																			dwarf.getY() - 4,
																			dwarf.getX() + 4,
																			dwarf.getY() + 4)
															&& dwarf.getID() == 323) {
														// if(dwarf.getID()==322
														// || dwarf.getID()==321
														// ||
														// dwarf.getID()==324||
														// dwarf.getID() == 323)
														dwarf.resetPath();
														owner.resetPath();
														owner.resetAll();
														owner.setStatus(Action.FIGHTING_MOB);
														owner.getActionSender()
																.sendMessage(
																		"You are under attack!");

														dwarf.setLocation(owner
																.getLocation(),
																true);
														for (Player p : dwarf
																.getViewArea()
																.getPlayersInView()) {
															p.removeWatchedNpc(dwarf);
														}

														owner.setBusy(true);
														owner.setSprite(9);
														owner.setOpponent(dwarf);
														owner.setCombatTimer();

														dwarf.setBusy(true);
														dwarf.setSprite(8);
														dwarf.setOpponent(owner);
														dwarf.setCombatTimer();
														FightEvent fighting = new FightEvent(
																owner, dwarf,
																true);
														fighting.setLastRun(0);
														world.getDelayedEventHandler()
																.add(fighting);
													}
												} else {
													owner.getActionSender()
															.sendMessage(
																	"Can not find an npc to alert you...this shouldn't happen.");
												}
											}
										}
									});
						} else if (command.equals("climb-up")
								|| command.equals("climb up")
								|| command.equals("go up")) {
							int[] coords = coordModifier(owner, true);
							owner.teleport(coords[0], coords[1], false);
						} else if (command.equals("climb-down")
								|| command.equals("climb down")
								|| command.equals("go down")) {
							// if(object.getID() != 223 && object.getX() != 274
							// && object.getY() != 566){
							int[] coords = coordModifier(owner, false);
							owner.teleport(coords[0], coords[1], false);
						} else if (command.equals("steal from")) {
							if (object == null) {
								return;
							}
							if (owner.getSpam()) {
								return;
							} else {
								owner.setSpam(true);
								Thieving thiev = new Thieving(owner, object);
								thiev.thieveStall();
							}

						} else if (command.equals("search for traps")) {
							Thieving thieving = new Thieving(owner, object);
							thieving.thieveChest();
						} else if (object.getID() == 52
								&& object.getGrainable()) {
							owner.getActionSender()
									.sendMessage(
											"The grain slides down the hopper and grinds up into flour.");
							world.registerItem(new Item(23, 166, 599, 1, owner));
							object.setGrainable(false);
						} else if (object.getID() == 173
								&& object.getGrainable()) {
							owner.getActionSender()
									.sendMessage(
											"The grain slides down the hopper and grinds up into flour.");
							world.registerItem(new Item(23, 179, 481, 1, owner));
							object.setGrainable(false);

						} else if (command.equals("rest")) {
							owner.getActionSender().sendMessage(
									"You rest on the bed");
							world.getDelayedEventHandler().add(
									new ShortEvent(owner) {
										public void action() {
											owner.setFatigue(0);
											owner.getActionSender()
													.sendFatigue();
											owner.getActionSender()
													.sendMessage(
															"You wake up - feeling refreshed");
										}
									});
						} else if (command.equals("swing")) {
							switch (object.getID()) {
							case 684:
								if (owner.getCurStat(16) < 35) {
									owner.getActionSender().sendMessage(
											"You are not high enough agility");
								} else {
									if (owner.getX() <= 208) {
										owner.teleport(207, 3221, false);
									}
									owner.getActionSender().sendMessage(
											"You swing across the pit.");
									owner.incExp(16, 100, false);
									owner.getActionSender().sendStat(16);
								}
								break;
							case 685:
								if (owner.getCurStat(16) < 35) {
									owner.getActionSender().sendMessage(
											"You are not high enough agility");
								} else {
									if (owner.getX() <= 206) {
										owner.teleport(206, 3225, false);
									}
									owner.getActionSender().sendMessage(
											"You swing across the pit.");
									owner.incExp(16, 100, false);
								}
								owner.getActionSender().sendStat(16);
								break;
							case 675:
								if (owner.getCurStat(16) < 35) {
									owner.getActionSender().sendMessage(
											"You are not high enough agility");
								} else {
									if (owner.getX() <= 487) {
										owner.teleport(487, 559, false);
									}
									owner.getActionSender().sendMessage(
											"You swing across the pit.");
									owner.incExp(16, 350, false);
									owner.getActionSender().sendStat(16);
								}
							}
						} else if (command.equals("enter")) {
							switch (object.getID()) {
							case 671:
								if (owner.getCurStat(16) < 40) {
									owner.getActionSender().sendMessage(
											"You are not high enough agility");
									owner.getActionSender().sendMessage(
											"You need 40 agility to enter");
								} else {
									if (owner.getX() <= 487) {
										owner.teleport(487, 554, false);
									}
									owner.getActionSender().sendMessage(
											"You climb through the pipe.");
									owner.incExp(16, 500, false);
									owner.getActionSender().sendStat(16);
								}
								break;
							case 672:
								if (owner.getCurStat(16) < 40) {
									owner.getActionSender().sendMessage(
											"You are not high enough agility");
									owner.getActionSender().sendMessage(
											"You need 40 agility to enter");
								} else {
									if (owner.getX() <= 487) {
										owner.teleport(487, 551, false);
									}
									owner.getActionSender().sendMessage(
											"You climb through the pipe.");
									owner.incExp(16, 500, false);
									owner.getActionSender().sendStat(16);
								}
							case 654:
								if (owner.getX() <= 683) {
									owner.teleport(683, 494, false);
								}
								owner.getActionSender().sendMessage(
										"You climb through the pipe.");
								owner.incExp(16, 500, false);
								owner.getActionSender().sendStat(16);
							}
						} else if (command.equals("search")) {
							switch (object.getID()) {
							case 228: // chest
								owner.getActionSender().sendMessage(
										"You find a piece of map in the chest");
								if (owner.getInventory().countId(417) <= 0) {
									owner.getInventory().add(
											new InvItem(417, 1));
									owner.getActionSender().sendInventory();
								} else
									owner.getActionSender()
											.sendMessage(
													"You have already looted this chest.");

								break;
							case 203:
								owner.getActionSender()
										.sendMessage(
												"You search the coffin and find some human remains");
								break;
							case 77: // Varrock Drain
								owner.getActionSender()
										.sendMessage(
												"I can see a key but can't quite reach it..");
								break;
							case 141: // Morgan's Cupboard
								owner.getActionSender()
										.sendMessage(
												"You search the cupboard and find some garlic.");
								owner.getInventory().add(new InvItem(218, 1));
								owner.getActionSender().sendInventory();
								break;
							case 86:
								owner.informOfChatMessage(new ChatMessage(
										owner,
										"There seems to be pressure gauge in here",
										owner));
								world.getDelayedEventHandler().add(
										new ShortEvent(owner) {
											public void action() {
												owner.informOfChatMessage(new ChatMessage(
														owner,
														"There are a lot of pirhanas in there though",
														owner));
												world.getDelayedEventHandler()
														.add(new ShortEvent(
																owner) {
															public void action() {
																owner.informOfChatMessage(new ChatMessage(
																		owner,
																		"I can't get the gauge out",
																		owner));
															}
														});
											}
										});
							}
						} else if (command.equals("walk through")) {
							switch (object.getID()) {
							case 1027: // Mage Gate
								/*
								 * if(object.getY() != 118) { return; }
								 */
								if (owner.getY() < 120) {
									owner.teleport(228, 120, false);
								} else {
									owner.teleport(228, 118, false);
								}
								break;
							default:
								owner.getActionSender().sendMessage(
										"Nothing interesting happens.");
								return;
							}
						} else if (command.equals("close")
								|| command.equals("open")) {
							switch (object.getID()) {
							case 228:
								replaceGameObject(229, false);
								return;
							case 229:
								replaceGameObject(228, false);
								return;
							case 58:
								replaceGameObject(57, false);
								return;
							case 57:
								replaceGameObject(58, true);
								return;
							case 63:
								replaceGameObject(64, false);
								return;
							case 64:
								replaceGameObject(63, true);
								return;
							case 79:
								replaceGameObject(78, false);
								return;
							case 78:
								replaceGameObject(79, true);
								return;
							case 60:
								replaceGameObject(59, true);
								return;
							case 59:
								replaceGameObject(60, false);
								return;
							case 137: // Members Gate (Doriks)
								if (object.getX() != 341
										|| object.getY() != 487) {
									return;
								}
								doGate();
								if (owner.getX() <= 341) {
									owner.teleport(342, 487, false);
								} else {
									owner.teleport(341, 487, false);
								}
								break;
							case 311: // Barb Outpost Gate
								if (object.getX() != 494
										|| object.getY() != 543) {
									return;
								}
								doGate();
								if (owner.getX() <= 493) {
									owner.teleport(494, 543, false);
								} else {
									owner.teleport(493, 543, false);
								}
								break;
							case 1020: // mage gate 1
								doGate();
								if (owner.getX() <= 219) {
									owner.teleport(220, 130, false);
								} else {
									owner.teleport(219, 130, false);
								}
								break;
							case 138: // Members Gate (Crafting Guild)
								if (object.getX() != 343
										|| object.getY() != 581) {
									return;
								}
								doGate();
								if (owner.getY() <= 580) {
									owner.teleport(343, 581, false);
								} else {
									owner.teleport(343, 580, false);
								}
								break;
							case 180: // Al-Kharid Gate
								if (object.getX() != 92 || object.getY() != 649) {
									return;
								}
								doGate();
								if (owner.getX() <= 91) {
									owner.teleport(92, 649, false);
								} else {
									owner.teleport(91, 649, false);
								}
								break;
							case 254: // Karamja Gate
								if (object.getX() != 434
										|| object.getY() != 682) {
									return;
								}
								doGate();
								if (owner.getX() <= 434) {
									owner.teleport(435, 682, false);
								} else {
									owner.teleport(434, 682, false);
								}
								break;
							case 563: // King Lanthlas Gate
								if (object.getX() != 660
										|| object.getY() != 551) {
									return;
								}
								doGate();
								if (owner.getY() <= 551) {
									owner.teleport(660, 552, false);
								} else {
									owner.teleport(660, 551, false);
								}
								break;
							case 626: // Gnome Stronghold Gate
								if (object.getX() != 703
										|| object.getY() != 531) {
									return;
								}
								doGate();
								if (owner.getY() <= 531) {
									owner.teleport(703, 532, false);
								} else {
									owner.teleport(703, 531, false);
								}
								break;
							case 305: // Edgeville Members Gate
								if (object.getX() != 196
										|| object.getY() != 3266) {
									return;
								}
								doGate();
								if (owner.getY() <= 3265) {
									owner.teleport(196, 3266, false);
								} else {
									owner.teleport(196, 3265, false);
								}
								break;
							case 1089: // Dig Site Gate
								if (object.getX() != 59 || object.getY() != 573) {
									return;
								}
								doGate();
								if (owner.getX() <= 58) {
									owner.teleport(59, 573, false);
								} else {
									owner.teleport(58, 573, false);
								}
								break;
							case 356: // Woodcutting Guild Gate
								if (object.getX() != 560
										|| object.getY() != 472) {
									return;
								}
								if (owner.getY() <= 472) {
									doGate();
									owner.teleport(560, 473, false);
								} else {
									if (owner.getCurStat(8) < 70) {
										owner.setBusy(true);
										Npc mcgrubor = world.getNpc(255, 556,
												564, 473, 476);
										if (mcgrubor != null) {
											owner.informOfNpcMessage(new ChatMessage(
													mcgrubor,
													"Hello only the top woodcutters are allowed in here",
													owner));
										}
										world.getDelayedEventHandler().add(
												new ShortEvent(owner) {
													public void action() {
														owner.setBusy(false);
														owner.getActionSender()
																.sendMessage(
																		"You need a woodcutting level of 70 to enter");
													}
												});
									} else {
										doGate();
										owner.teleport(560, 472, false);
									}
								}
								break;
							case 142: // Black Knight Big Door
								owner.getActionSender().sendMessage(
										"The doors are locked");
								break;
							case 93: // Red dragon gate
								if (object.getX() != 140
										|| object.getY() != 180) {
									return;
								}
								doGate();
								if (owner.getY() <= 180) {
									owner.teleport(140, 181, false);
								} else {
									owner.teleport(140, 180, false);
								}
								break;
							case 508: // Lesser demon gate
								if (object.getX() != 285
										|| object.getY() != 185) {
									return;
								}
								doGate();
								if (owner.getX() <= 284) {
									owner.teleport(285, 185, false);
								} else {
									owner.teleport(284, 185, false);
								}
								break;
							case 319: // Lava Maze Gate
								if (object.getX() != 243
										|| object.getY() != 178) {
									return;
								}
								doGate();
								if (owner.getY() <= 178) {
									owner.teleport(243, 179, false);
								} else {
									owner.teleport(243, 178, false);
								}
								break;
							case 712: // Shilo inside gate
								if (object.getX() != 394
										|| object.getY() != 851) {
									return;
								}
								owner.teleport(383, 851, false);
								break;
							case 611: // Shilo outside gate
								if (object.getX() != 388
										|| object.getY() != 851) {
									return;
								}
								owner.teleport(394, 851, false);
								break;
							case 1079: // Legends guild gate
								if (object.getX() != 512
										|| object.getY() != 550) {
									return;
								}
								doGate();
								if (owner.getY() <= 550) {
									owner.teleport(513, 551, false);
								} else {
									owner.teleport(513, 550, false);
								}
								break;
							case 94:
								owner.getActionSender().sendMessage(
										"This door is locked.");
								break;
							default:
								owner.getActionSender().sendMessage(
										"Nothing interesting happens.");
								return;
							}
						} else if (command.equals("pick")
								|| command.equals("pick banana")) {
							switch (object.getID()) {
							case 72: // Wheat
								owner.getActionSender().sendMessage(
										"You get some grain");
								owner.getInventory().add(new InvItem(29, 1));
								break;
							case 191: // Potatos
								owner.getActionSender().sendMessage(
										"You pick a potato");
								owner.getInventory().add(new InvItem(348, 1));
								break;
							case 313: // Flax
								owner.getActionSender().sendMessage(
										"You uproot a flax plant");
								owner.getInventory().add(new InvItem(675, 1));
								break;
							case 183: // Banana
								owner.getActionSender().sendMessage(
										"You pull a banana off the tree");
								owner.getInventory().add(new InvItem(249, 1));
								break;
							default:
								owner.getActionSender().sendMessage(
										"Nothing interesting happens.");
								return;
							}
							owner.getActionSender().sendInventory();
							owner.getActionSender().sendSound("potato");
							owner.setBusy(true);
							world.getDelayedEventHandler().add(
									new SingleEvent(owner, 200) {
										public void action() {
											owner.setBusy(false);
										}
									});
						} else if (command.equals("mine")
								|| command.equals("prospect")) {
							handleMining(click);
						} else if (command.equals("pick lock")) {
							if (object.getID() == 94)
								if (owner.getMaxStat(17) < 55) {
									owner.getActionSender()
											.sendMessage(
													"You must have at least 55 theiving to pick this lock");
									return;
								}
							if (!Formulae.catchThief(owner.getCurStat(17), 55)) {
								world.registerGameObject(new GameObject(object
										.getLocation(), 1, object
										.getDirection(), object.getType()));
								world.delayedSpawnObject(object.getLoc(),
										2 * 1000);
							} else {
								owner.getActionSender().sendMessage(
										"You fail to pick the lock.");
							}
						} else if (command.equals("lure")
								|| command.equals("bait")
								|| command.equals("net")
								|| command.equals("harpoon")
								|| command.equals("cage")) {
							handleFishing(click);
						} else if (command.equals("chop")) {
							handleWoodcutting(click);
						} else if (command.equals("hit")) {
							// case 49: Dummy
							if (owner.getCurStat(0) < 7) {
								owner.getActionSender().sendMessage(
										"You swing at the dummy!");
								world.getDelayedEventHandler().add(
										new ShortEvent(owner) {
											public void action() {
												owner.incExp(0, 3, true);
											}
										});
							} else {
								owner.getActionSender()
										.sendMessage(
												"You swing at the dummy, but it has no effect because you are too powerful.");
							}

						} else if (command.equals("recharge at")) {
							owner.getActionSender().sendMessage(
									"You recharge at the altar.");
							owner.getActionSender().sendSound("recharge");
							int maxPray = object.getID() == 200 ? owner
									.getMaxStat(5) + 2 : owner.getMaxStat(5);
							if (owner.getCurStat(5) < maxPray) {
								owner.setCurStat(5, maxPray);
							}
							owner.getActionSender().sendStat(5);
						} else if (command.equals("board")) {
							owner.getActionSender().sendMessage(
									"You must talk to the owner about this.");
						} else {
							switch (object.getID()) {
							case 613: // Shilo cart
								if (object.getX() != 384
										|| object.getY() != 851) {
									return;
								}
								owner.setBusy(true);
								owner.getActionSender().sendMessage(
										"You search for a way over the cart");
								world.getDelayedEventHandler().add(
										new ShortEvent(owner) {
											public void action() {
												owner.getActionSender()
														.sendMessage(
																"You climb across");
												if (owner.getX() <= 383) {
													owner.teleport(386, 851,
															false);
												} else {
													owner.teleport(383, 851,
															false);
												}
												owner.setBusy(false);
											}
										});
								break;
							case 643: // Gnome tree stone
								if (object.getX() != 416
										|| object.getY() != 161) {
									return;
								}
								owner.setBusy(true);
								owner.getActionSender().sendMessage(
										"You twist the stone tile to one side");
								world.getDelayedEventHandler().add(
										new ShortEvent(owner) {
											public void action() {
												owner.getActionSender()
														.sendMessage(
																"It reveals a ladder, you climb down");
												owner.teleport(703, 3284, false);
												owner.setBusy(false);
											}
										});
								break;
							case 638: // First roots in gnome cave
								if (object.getX() != 701
										|| object.getY() != 3280) {
									return;
								}
								owner.setBusy(true);
								owner.getActionSender().sendMessage(
										"You push the roots");
								world.getDelayedEventHandler().add(
										new ShortEvent(owner) {
											public void action() {
												owner.getActionSender()
														.sendMessage(
																"They wrap around you and drag you forwards");
												owner.teleport(701, 3278, false);
												owner.setBusy(false);
											}
										});
							case 639: // Second roots in gnome cave
								if (object.getX() != 701
										|| object.getY() != 3279) {
									return;
								}
								owner.setBusy(true);
								owner.getActionSender().sendMessage(
										"You push the roots");
								world.getDelayedEventHandler().add(
										new ShortEvent(owner) {
											public void action() {
												owner.getActionSender()
														.sendMessage(
																"They wrap around you and drag you forwards");
												owner.teleport(701, 3281, false);
												owner.setBusy(false);
											}
										});
								break;
							default:
								owner.getActionSender().sendMessage(
										"Nothing interesting happens.");
								return;
							}
						}
					}

					private void handleMining(final int click) {
						final ObjectMiningDef def = EntityHandler
								.getObjectMiningDef(object.getID());
						if (def == null) {
							owner.getActionSender()
									.sendMessage(
											"There is currently no ore available in this rock.");
							return;
						}
						if (owner.getCurStat(14) < def.getReqLevel()) {
							owner.getActionSender().sendMessage(
									"You need a mining level of "
											+ def.getReqLevel()
											+ " to mine this rock.");
							return;
						}
						final InvItem ore = new InvItem(def.getOreId());
						if (click == 1) {
							owner.getActionSender().sendMessage(
									"This rock contains "
											+ ore.getDef().getName() + ".");
							return;
						}
						int axeId = -1;
						for (int id : Formulae.miningAxeIDs) {
							if (owner.getInventory().countId(id) > 0) {
								axeId = id;
								break;
							}
						}
						if (axeId < 0) {
							owner.getActionSender().sendMessage(
									"You need a pickaxe to mine this rock.");
							return;
						}
						owner.setBusy(true);
						owner.getActionSender().sendSound("mine");
						Bubble bubble = new Bubble(owner, axeId);
						for (Player p : owner.getViewArea().getPlayersInView()) {
							p.informOfBubble(bubble);
						}
						owner.getActionSender().sendMessage(
								"You swing your pick at the rock...");
						final int axeID = axeId;
						world.getDelayedEventHandler().add(
								new ShortEvent(owner) {
									public void action() {
										if (Formulae.getOre(def,
												owner.getCurStat(14), axeID)) {
											if (DataConversions.random(0, 200) == 0) {
												InvItem gem = new InvItem(
														Formulae.getGem(), 1);
												owner.getInventory().add(gem);
												owner.getActionSender()
														.sendMessage(
																"You found a gem!");
											} else {
												owner.getInventory().add(ore);
												owner.getActionSender()
														.sendMessage(
																"You manage to obtain some "
																		+ ore.getDef()
																				.getName()
																		+ ".");
												owner.incExp(14,
														def.getExp() * 10, true);
												owner.getActionSender()
														.sendStat(14);
												world.registerGameObject(new GameObject(
														object.getLocation(),
														98,
														object.getDirection(),
														object.getType()));
												world.delayedSpawnObject(
														object.getLoc(),
														def.getRespawnTime() * 1000);
											}
											owner.getActionSender()
													.sendInventory();
										} else {
											owner.getActionSender()
													.sendMessage(
															"You only succeed in scratching the rock.");
										}
										owner.setBusy(false);
									}
								});
					}

					private void handleFishing(final int click) {
						final ObjectFishingDef def = EntityHandler
								.getObjectFishingDef(object.getID(), click);
						if (def == null) { // This shouldn't happen
							return;
						}
						if (owner.getCurStat(10) < def.getReqLevel()) {
							owner.getActionSender().sendMessage(
									"You need a fishing level of "
											+ def.getReqLevel()
											+ " to fish here.");
							return;
						}
						int netId = def.getNetId();
						if (owner.getInventory().countId(netId) <= 0) {
							owner.getActionSender().sendMessage(
									"You need a "
											+ EntityHandler.getItemDef(netId)
													.getName()
											+ " to catch these fish.");
							return;
						}
						final int baitId = def.getBaitId();
						if (baitId >= 0) {
							if (owner.getInventory().countId(baitId) <= 0) {
								owner.getActionSender().sendMessage(
										"You don't have any "
												+ EntityHandler.getItemDef(
														baitId).getName()
												+ " left.");
								return;
							}
						}
						owner.setBusy(true);
						owner.getActionSender().sendSound("fish");
						Bubble bubble = new Bubble(owner, netId);
						for (Player p : owner.getViewArea().getPlayersInView()) {
							p.informOfBubble(bubble);
						}
						owner.getActionSender().sendMessage(
								"You attempt to catch some fish");
						world.getDelayedEventHandler().add(
								new ShortEvent(owner) {
									public void action() {
										ObjectFishDef def = Formulae.getFish(
												object.getID(),
												owner.getCurStat(10), click);
										if (def != null) {
											if (baitId >= 0) {
												int idx = owner.getInventory()
														.getLastIndexById(
																baitId);
												InvItem bait = owner
														.getInventory()
														.get(idx);
												int newCount = bait.getAmount() - 1;
												if (newCount <= 0) {
													owner.getInventory()
															.remove(idx);
												} else {
													bait.setAmount(newCount);
												}
											}
											InvItem fish = new InvItem(def
													.getId());
											owner.getInventory().add(fish);
											owner.getActionSender()
													.sendMessage(
															"You catch a "
																	+ fish.getDef()
																			.getName()
																	+ ".");
											owner.getActionSender()
													.sendInventory();
											owner.incExp(10, def.getExp() * 10,
													true);
											owner.getActionSender()
													.sendStat(10);
										} else {
											owner.getActionSender()
													.sendMessage(
															"You fail to catch anything.");
										}
										owner.setBusy(false);
									}
								});
					}

					private void handleWoodcutting(final int click) {
						final ObjectWoodcuttingDef def = EntityHandler
								.getObjectWoodcuttingDef(object.getID());
						if (def == null) { // This shoudln't happen
							return;
						}
						if (owner.getCurStat(8) < def.getReqLevel()) {
							owner.getActionSender().sendMessage(
									"You need a woodcutting level of "
											+ def.getReqLevel()
											+ " to axe this tree.");
							return;
						}
						int axeId = -1;
						for (int a : Formulae.woodcuttingAxeIDs) {
							if (owner.getInventory().countId(a) > 0) {
								axeId = a;
								break;
							}
						}
						if (axeId < 0) {
							owner.getActionSender().sendMessage(
									"You need an axe to chop this tree down.");
							return;
						}
						owner.setBusy(true);
						Bubble bubble = new Bubble(owner, axeId);
						for (Player p : owner.getViewArea().getPlayersInView()) {
							p.informOfBubble(bubble);
						}
						owner.getActionSender().sendMessage(
								"You swing your "
										+ EntityHandler.getItemDef(axeId)
												.getName() + " at the tree...");
						final int axeID = axeId;
						world.getDelayedEventHandler().add(
								new ShortEvent(owner) {
									public void action() {
										if (Formulae.getLog(def,
												owner.getCurStat(8), axeID)) {
											InvItem log = new InvItem(def
													.getLogId());
											owner.getInventory().add(log);
											owner.getActionSender()
													.sendMessage(
															"You get some wood.");
											owner.getActionSender()
													.sendInventory();
											owner.incExp(8, def.getExp() * 10,
													true);
											owner.getActionSender().sendStat(8);
											if (DataConversions.random(1, 100) <= def
													.getFell()) {
												world.registerGameObject(new GameObject(
														object.getLocation(),
														4,
														object.getDirection(),
														object.getType()));
												world.delayedSpawnObject(
														object.getLoc(),
														def.getRespawnTime() * 1000);
											}
										} else {
											owner.getActionSender()
													.sendMessage(
															"You slip and fail to hit the tree.");
										}
										owner.setBusy(false);
									}
								});
					}
				});
	}

}