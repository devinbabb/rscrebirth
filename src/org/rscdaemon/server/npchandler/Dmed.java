package org.rscdaemon.server.npchandler;

import org.rscdaemon.server.model.Player;
import org.rscdaemon.server.model.Npc;
import org.rscdaemon.server.model.World;
import org.rscdaemon.server.model.InvItem;
import org.rscdaemon.server.model.ChatMessage;
import org.rscdaemon.server.model.MenuHandler;
import org.rscdaemon.server.event.ShortEvent;

public class Dmed implements NpcHandler {
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();

	public void handleNpc(final Npc npc, Player player) throws Exception {
		player.informOfNpcMessage(new ChatMessage(npc,
				"The dragons called.. they want their dmeds back!", player));
		player.informOfNpcMessage(new ChatMessage(npc,
				"Trade me a dmed for EXP in a skill of your choice?", player));
		player.setBusy(true);
		world.getDelayedEventHandler().add(new ShortEvent(player) {
			public void action() {
				owner.setBusy(false);
				String[] options = new String[] { "Yeah, sure.",
						"Nah, I like my Dmed." };
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
										switch (option) {
										case 0: // Yes
											world.getDelayedEventHandler().add(
													new ShortEvent(owner) {
														public void action() {
															if (owner
																	.getInventory()
																	.countId(
																			795) < 1) {
																owner.getActionSender()
																		.sendMessage(
																				"You need a Dmed to trade him.");
																running = false;
																owner.setBusy(false);
																npc.unblock();
															} else {
																owner.getInventory()
																		.remove(795,
																				1); // takes
																					// the
																					// dmed
																// owner.getInventory().add(new
																// InvItem(387,
																// 1));
																// owner.incExp(15,
																// 30408);
																// //only adding
																// xp
																// owner.setCurStat(15,
																// 3);
																// owner.setMaxStat(15,
																// 3);
																owner.getActionSender()
																		.sendStat(
																				15);
																owner.informOfNpcMessage(new ChatMessage(
																		npc,
																		"Please.. Bring me more, they already ate my children..",
																		owner));
																owner.getActionSender()
																		.sendInventory();
																npc.unblock();
																owner.setBusy(false);
															}
														}
													});
											return;
										case 1: // No
											owner.setBusy(false);
											owner.informOfNpcMessage(new ChatMessage(
													npc,
													"The dragons.. Please! They ate my children..",
													owner));
											npc.unblock();
											break;
										default:
											owner.setBusy(false);
											npc.unblock();
											return;
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