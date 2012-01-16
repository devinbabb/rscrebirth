package org.rscdaemon.server.npchandler;

import org.rscdaemon.server.model.Player;
import org.rscdaemon.server.model.Npc;
import org.rscdaemon.server.model.World;
import org.rscdaemon.server.model.InvItem;
import org.rscdaemon.server.model.ChatMessage;
import org.rscdaemon.server.model.MenuHandler;
import org.rscdaemon.server.event.ShortEvent;

public class Inmate implements NpcHandler {
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();

	public void handleNpc(final Npc npc, Player player) throws Exception {
		player.informOfNpcMessage(new ChatMessage(npc, "Hey there.. ;)", player));
		player.informOfNpcMessage(new ChatMessage(npc,
				"Wanna escape this dump? Only costs 500k coins!", player));
		player.setBusy(true);
		world.getDelayedEventHandler().add(new ShortEvent(player) {
			public void action() {
				owner.setBusy(false);
				String[] options = new String[] {
						"Oh hell yeah, screw the Pmods!",
						"Sorry, I don't want to break more rules." };
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
																	.countId(10) < 500000) {
																owner.getActionSender()
																		.sendMessage(
																				"You do not have enough money, yo!");
																running = false;
																owner.setBusy(false);
																npc.unblock();
															} else {
																owner.getInventory()
																		.remove(10,
																				500000);
																owner.getInventory()
																		.add(new InvItem(
																				387,
																				1));
																owner.informOfNpcMessage(new ChatMessage(
																		npc,
																		"Spin dis shit yo and your free!",
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
													"Ok. Come back when you change your mind, dawg.",
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