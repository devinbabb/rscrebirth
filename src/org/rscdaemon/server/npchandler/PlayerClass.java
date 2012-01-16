package org.rscdaemon.server.npchandler;

import org.rscdaemon.server.model.*;
import org.rscdaemon.server.util.Formulae;
import org.rscdaemon.server.event.ShortEvent;

public class PlayerClass implements NpcHandler {
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();

	public void handleNpc(final Npc npc, Player player) throws Exception {
		player.informOfNpcMessage(new ChatMessage(npc, "Greetings!!", player));
		player.informOfNpcMessage(new ChatMessage(npc,
				"I see you are new the this land", player));
		player.informOfNpcMessage(new ChatMessage(npc,
				"You can become very powerful with the right help", player));
		player.informOfNpcMessage(new ChatMessage(npc,
				"Would you like me to show you the way?", player));
		player.setBusy(true);
		world.getDelayedEventHandler().add(new ShortEvent(player) {
			public void action() {
				owner.setBusy(false);
				String[] options = new String[] {
						"I wish to become a mighty warrior",
						"I would like to become a powerful magician",
						"I want to be an adventurer",
						"Teach me the ways of the ranger",
						"I want to mine like the Dwarfs!" };// NPC Chat options,
															// you can make more
															// if you want
				owner.setMenuHandler(new MenuHandler(options) {
					public void handleReply(final int option, final String reply) {
						if (owner.isBusy()) {
							return;
						}
						owner.informOfChatMessage(new ChatMessage(owner, reply,
								npc));
						owner.setBusy(true);
						world.getDelayedEventHandler().add(
								new ShortEvent(owner) {
									public void action() {
										owner.setBusy(false);
										if (option == 0) {
											owner.informOfNpcMessage(new ChatMessage(
													npc,
													"A wise choice! You are now a mighty warrior!",
													owner));
											owner.getInventory().add(
													new InvItem(10, 100)); // 100gp
											owner.getInventory().add(
													new InvItem(66, 1)); // bronze
																			// short
																			// sword
											owner.getInventory().add(
													new InvItem(4, 1)); // wooden
																		// shield
											owner.getActionSender()
													.sendInventory();
											owner.setCurStat(0, 5);// (Stat
																	// Number,Stat
																	// Level)
											owner.setMaxStat(0, 5);// (Stat
																	// Number,Stat
																	// Level)
											owner.setExp(0, Formulae.lvlToXp(5));// (Stat
																					// Number,Stat
																					// Level)
											owner.setCurStat(2, 5);// (Stat
																	// Number,Stat
																	// Level)
											owner.setMaxStat(2, 5);// (Stat
																	// Number,Stat
																	// Level)
											owner.setExp(2, Formulae.lvlToXp(5));// (Stat
																					// Number,Stat
																					// Level)
											owner.setCurStat(3, 12);// (Stat
																	// Number,Stat
																	// Level)
											owner.setMaxStat(3, 12);// (Stat
																	// Number,Stat
																	// Level)
											owner.setExp(3,
													Formulae.lvlToXp(12));// (Stat
																			// Number,Stat
																			// Level)
											owner.getActionSender()
													.sendMessage(
															"Good luck on your journey!");
											// owner.teleport(123, 647, false);
											npc.unblock();
										}
										if (option == 1) {
											owner.informOfNpcMessage(new ChatMessage(
													npc,
													"A wise choice! You are now a powerful magician!",
													owner));
											owner.getInventory().add(
													new InvItem(10, 100)); // 100gp
											owner.getInventory().add(
													new InvItem(199, 1)); // black
																			// wizard
																			// hat
											owner.getInventory().add(
													new InvItem(100, 1)); // staff
											owner.getActionSender()
													.sendInventory();
											owner.setCurStat(6, 7);// (Stat
																	// Number,Stat
																	// Level)
											owner.setMaxStat(6, 7);// (Stat
																	// Number,Stat
																	// Level)
											owner.setExp(6, Formulae.lvlToXp(7));// (Stat
																					// Number,Stat
																					// Level)
											owner.getActionSender()
													.sendMessage(
															"Good luck on your journey!");
											// owner.teleport(123, 647, false);
											npc.unblock();
										}
										if (option == 2) {
											owner.informOfNpcMessage(new ChatMessage(
													npc,
													"A wise choice! You are now a skilled adventurer!",
													owner));
											owner.getInventory().add(
													new InvItem(10, 100)); // Add
																			// Items
																			// Here
											owner.getInventory().add(
													new InvItem(87, 1)); // Add
																			// Items
																			// Here
											owner.getInventory().add(
													new InvItem(166, 1)); // Add
																			// Items
																			// Here
											owner.getInventory().add(
													new InvItem(17, 1)); // Add
																			// Items
																			// Here
											owner.getActionSender()
													.sendInventory();
											owner.setCurStat(8, 7);// (Stat
																	// Number,Stat
																	// Level)
											owner.setMaxStat(8, 7);// (Stat
																	// Number,Stat
																	// Level)
											owner.setExp(8, Formulae.lvlToXp(7));// (Stat
																					// Number,Stat
																					// Level)
											owner.getActionSender()
													.sendMessage(
															"Good luck on your journey!");
											// owner.teleport(123, 647, false);
											npc.unblock();
										}
										if (option == 3) {
											owner.informOfNpcMessage(new ChatMessage(
													npc,
													"A wise choice! You are now a sharp-shooting archer!",
													owner));
											owner.getInventory().add(
													new InvItem(10, 100)); // Add
																			// Items
																			// Here
											owner.getInventory().add(
													new InvItem(11, 50)); // Add
																			// Items
																			// Here
											owner.getInventory().add(
													new InvItem(15, 1)); // Add
																			// Items
																			// Here
											owner.getInventory().add(
													new InvItem(16, 1)); // Add
																			// Items
																			// Here
											owner.getInventory().add(
													new InvItem(189, 50)); // Add
																			// Items
																			// Here
											owner.getActionSender()
													.sendInventory();
											owner.setCurStat(4, 7);// (Stat
																	// Number,Stat
																	// Level)range
											owner.setMaxStat(4, 7);// (Stat
																	// Number,Stat
																	// Level)range
											owner.setExp(4, Formulae.lvlToXp(7));// (Stat
																					// Number,Stat
																					// Level)
											owner.getActionSender()
													.sendMessage(
															"Good luck on your journey!");
											// owner.teleport(123, 647, false);
											npc.unblock();

										}
										if (option == 4) {
											owner.informOfNpcMessage(new ChatMessage(
													npc,
													"A wise choice! You are now a miner!",
													owner));
											owner.getInventory().add(
													new InvItem(10, 100)); // Add
																			// Items
																			// Here
											owner.getInventory().add(
													new InvItem(11, 50)); // Add
																			// Items
																			// Here
											owner.getInventory().add(
													new InvItem(15, 1)); // Add
																			// Items
																			// Here
											owner.getInventory().add(
													new InvItem(16, 1)); // Add
																			// Items
																			// Here
											owner.getInventory().add(
													new InvItem(189, 50)); // Add
																			// Items
																			// Here
											owner.getActionSender()
													.sendInventory();
											owner.setCurStat(4, 7);// (Stat
																	// Number,Stat
																	// Level)range
											owner.setMaxStat(4, 7);// (Stat
																	// Number,Stat
																	// Level)range
											owner.setExp(4, Formulae.lvlToXp(7));// (Stat
																					// Number,Stat
																					// Level)
											owner.getActionSender()
													.sendMessage(
															"Good luck on your journey!");
											// owner.teleport(123, 647, false);
											npc.unblock();

										}
									}
								});
					}
				});
				owner.getActionSender().sendMenu(options);
			}
		});
		npc.blockedBy(player);
	}
}