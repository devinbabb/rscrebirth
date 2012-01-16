package org.rscdaemon.server.npchandler;

import org.rscdaemon.server.model.Player;
import org.rscdaemon.server.model.Npc;
import org.rscdaemon.server.model.World;
import org.rscdaemon.server.model.InvItem;
import org.rscdaemon.server.model.ChatMessage;
import org.rscdaemon.server.model.MenuHandler;
import org.rscdaemon.server.event.ShortEvent;
import org.rscdaemon.server.event.DelayedEvent;
import org.rscdaemon.server.event.MiniEvent;
import org.rscdaemon.server.event.DelayedQuestChat;

public class Tramp implements NpcHandler {

	public static final World world = World.getWorld();

	private static final String[] Alts = { "Sorry I haven't got any",
			"Go get a job", "Ok here you go",
			"Is there anything down this alleyway" };
	private static final String[] Alts1 = { "No problem",
			"So don't i get some sort of quest hint or something now?" };
	private static final String[] Alts2 = { "Thanks for the warning",
			"Do you think they would let me join?" };

	public void handleNpc(final Npc npc, Player player) throws Exception {
		player.informOfNpcMessage(new ChatMessage(npc,
				"Spare some change guy?", player));
		player.setBusy(true);
		world.getDelayedEventHandler().add(new ShortEvent(player) {
			public void action() {
				owner.setBusy(false);
				owner.setMenuHandler(new MenuHandler(Alts) {
					public void handleReply(final int option, final String reply) {
						if (owner.isBusy() || option < 0
								|| option >= Alts.length) {
							npc.unblock();
							return;
						}

						owner.setBusy(true);
						world.getDelayedEventHandler().add(
								new ShortEvent(owner) {
									public void action() {
										world.getDelayedEventHandler().add(
												new ShortEvent(owner) {
													public void action() {
														switch (option) {
														case 0:
															owner.informOfChatMessage(new ChatMessage(
																	owner,
																	reply, npc));
															owner.informOfNpcMessage(new ChatMessage(
																	npc,
																	"Thanks anyway",
																	owner));
															owner.setBusy(false);
															npc.unblock();
															break;

														case 1:
															owner.informOfChatMessage(new ChatMessage(
																	owner,
																	reply, npc));
															owner.informOfNpcMessage(new ChatMessage(
																	npc,
																	"You startin?",
																	owner));
															owner.setBusy(false);
															npc.unblock();
															break;

														case 2:
															owner.informOfChatMessage(new ChatMessage(
																	owner,
																	reply, npc));
															owner.informOfNpcMessage(new ChatMessage(
																	npc,
																	"Thank you, thats great",
																	owner));
															world.getDelayedEventHandler()
																	.add(new ShortEvent(
																			owner) {
																		public void action() {
																			owner.setBusy(false);
																			owner.setMenuHandler(new MenuHandler(
																					Alts1) {
																				public void handleReply(
																						final int option,
																						final String reply) {
																					if (owner
																							.isBusy()
																							|| option < 0
																							|| option >= Alts.length) {
																						npc.unblock();
																						return;
																					}
																					owner.setBusy(true);
																					world.getDelayedEventHandler()
																							.add(new ShortEvent(
																									owner) {
																								public void action() {
																									world.getDelayedEventHandler()
																											.add(new ShortEvent(
																													owner) {
																												public void action() {
																													switch (option) {
																													case 0:
																														owner.informOfChatMessage(new ChatMessage(
																																owner,
																																reply,
																																npc));
																														owner.setBusy(false);
																														npc.unblock();
																														break;
																													case 1:
																														owner.informOfChatMessage(new ChatMessage(
																																owner,
																																reply,
																																npc));
																														owner.informOfNpcMessage(new ChatMessage(
																																npc,
																																"No that's not why i'm asking for money",
																																owner));
																														world.getDelayedEventHandler()
																																.add(new MiniEvent(
																																		owner,
																																		2000) {
																																	public void action() {
																																		owner.informOfNpcMessage(new ChatMessage(
																																				npc,
																																				"I just need to eat",
																																				owner));
																																	}
																																});
																														owner.setBusy(false);
																														npc.unblock();
																														break;
																													}
																												}
																											});
																								}
																							});
																				}
																			});
																			owner.getActionSender()
																					.sendMenu(
																							Alts1);
																		}
																	});
															// npc.blockedBy(player);
															owner.setBusy(false);
															npc.unblock();
															break;

														case 3:
															owner.informOfChatMessage(new ChatMessage(
																	owner,
																	reply, npc));
															owner.informOfNpcMessage(new ChatMessage(
																	npc,
																	"Yes, there is actually",
																	owner));
															world.getDelayedEventHandler()
																	.add(new MiniEvent(
																			owner,
																			2000) {
																		public void action() {
																			owner.informOfNpcMessage(new ChatMessage(
																					npc,
																					"A notorious gang of thieves and hoodlums",
																					owner));
																			world.getDelayedEventHandler()
																					.add(new MiniEvent(
																							owner,
																							2000) {
																						public void action() {
																							owner.informOfNpcMessage(new ChatMessage(
																									npc,
																									"Called the blackarm gang",
																									owner));
																						}
																					});
																		}
																	});
															world.getDelayedEventHandler()
																	.add(new ShortEvent(
																			owner) {
																		public void action() {
																			owner.setBusy(false);
																			owner.setMenuHandler(new MenuHandler(
																					Alts2) {
																				public void handleReply(
																						final int option,
																						final String reply) {
																					if (owner
																							.isBusy()
																							|| option < 0
																							|| option >= Alts.length) {
																						npc.unblock();
																						return;
																					}
																					owner.setBusy(true);
																					world.getDelayedEventHandler()
																							.add(new ShortEvent(
																									owner) {
																								public void action() {
																									world.getDelayedEventHandler()
																											.add(new ShortEvent(
																													owner) {
																												public void action() {
																													switch (option) {
																													case 0:
																														owner.informOfChatMessage(new ChatMessage(
																																owner,
																																reply,
																																npc));
																														owner.setBusy(false);
																														npc.unblock();
																														break;
																													case 1:
																														owner.informOfChatMessage(new ChatMessage(
																																owner,
																																reply,
																																npc));
																														owner.informOfNpcMessage(new ChatMessage(
																																npc,
																																"You never know",
																																owner));
																														world.getDelayedEventHandler()
																																.add(new MiniEvent(
																																		owner,
																																		2000) {
																																	public void action() {
																																		owner.informOfNpcMessage(new ChatMessage(
																																				npc,
																																				"You'll find a lady down there called katrine",
																																				owner));
																																		world.getDelayedEventHandler()
																																				.add(new MiniEvent(
																																						owner,
																																						2000) {
																																					public void action() {
																																						owner.informOfNpcMessage(new ChatMessage(
																																								npc,
																																								"Speak to her",
																																								owner));
																																						world.getDelayedEventHandler()
																																								.add(new MiniEvent(
																																										owner,
																																										2000) {
																																									public void action() {
																																										owner.informOfNpcMessage(new ChatMessage(
																																												npc,
																																												"But don't upset her, she's pretty dangerous",
																																												owner));
																																									}
																																								});
																																					}
																																				});
																																	}
																																});
																														owner.setBusy(false);
																														npc.unblock();
																														break;
																													}
																												}
																											});
																								}
																							});
																				}
																			});
																			owner.getActionSender()
																					.sendMenu(
																							Alts2);
																		}
																	});
															owner.setBusy(false);
															npc.unblock();
															break;
														}
													}
												});
									}
								});
					}
				});
				owner.getActionSender().sendMenu(Alts);
			}
		});
		npc.blockedBy(player);
	}
}
