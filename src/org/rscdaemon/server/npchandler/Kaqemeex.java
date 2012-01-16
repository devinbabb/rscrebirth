package org.rscdaemon.server.npchandler;

import org.rscdaemon.server.model.Player;
import org.rscdaemon.server.model.Npc;
import org.rscdaemon.server.model.World;
import org.rscdaemon.server.model.InvItem;
import org.rscdaemon.server.model.ChatMessage;
import org.rscdaemon.server.model.MenuHandler;
import org.rscdaemon.server.event.ShortEvent;
import org.rscdaemon.server.util.Formulae;
import org.rscdaemon.server.model.*;
import org.rscdaemon.server.event.*;

public class Kaqemeex implements NpcHandler {
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();

	public void handleNpc(final Npc npc, Player player) throws Exception {
		if (player.getMaxStat(15) > 2) {
			player.getActionSender().sendMessage(
					"@gre@You have already completed this quest!");
			npc.unblock();
		} else if (player.getMaxStat(15) < 2) {
			player.informOfNpcMessage(new ChatMessage(
					npc,
					"If you bring me the following items, I will reward you greatly.",
					player));
			player.informOfNpcMessage(new ChatMessage(npc,
					"I need the meat from four different animals.", player));
			player.informOfNpcMessage(new ChatMessage(npc,
					"You will also be required to enchant the meat.", player));
			player.setBusy(true);
			world.getDelayedEventHandler().add(new ShortEvent(player) {
				public void action() {
					owner.setBusy(false);
					String[] options = new String[] { "Ok, I will help you.",
							"No, thank you." };
					owner.setMenuHandler(new MenuHandler(options) {
						public void handleReply(final int option,
								final String reply) {
							if (owner.isBusy()) {
								return;
							}
							owner.informOfChatMessage(new ChatMessage(owner,
									reply, npc));
							owner.setBusy(true);
							world.getDelayedEventHandler().add(
									new ShortEvent(owner) {
										public void action() {
											switch (option) {
											case 0: // Yes
												world.getDelayedEventHandler()
														.add(new ShortEvent(
																owner) {
															public void action() {
																if (owner
																		.getInventory()
																		.countId(
																				505) < 1) {
																}
																if (owner
																		.getInventory()
																		.countId(
																				506) < 1) {
																}
																if (owner
																		.getInventory()
																		.countId(
																				507) < 1) {
																}
																if (owner
																		.getInventory()
																		.countId(
																				508) < 1) {
																	owner.getActionSender()
																			.sendMessage(
																					"You do not have all the required items.");
																	running = false;
																	owner.setBusy(false);
																	npc.unblock();
																} else {
																	owner.getInventory()
																			.remove(505,
																					1);
																	owner.getInventory()
																			.remove(506,
																					1);
																	owner.getInventory()
																			.remove(507,
																					1);
																	owner.getInventory()
																			.remove(508,
																					1);
																	owner.setExp(
																			15,
																			250);
																	owner.setCurStat(
																			15,
																			3);
																	owner.setMaxStat(
																			15,
																			3);
																	owner.getActionSender()
																			.sendStat(
																					15);
																	owner.getActionSender()
																			.sendInventory();
																	owner.getActionSender()
																			.sendMessage(
																					"@gre@Congratulations! You have just completed the: @whi@Druidic Ritual @gre@quest!");
																	owner.getActionSender()
																			.sendMessage(
																					"@gre@You now have access to the Herblaw skill!");
																	owner.getActionSender()
																			.sendMessage(
																					"@gre@You just advanced 2 "
																							+ Formulae.statArray[15]
																							+ " levels!");
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
														"Ok. Come back when you change your mind.",
														owner));
												npc.unblock();
												return;
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

}