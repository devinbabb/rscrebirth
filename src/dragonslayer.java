import org.rscdaemon.server.quest.*;
import org.rscdaemon.server.model.*;
import org.rscdaemon.server.event.*;

/**
 * A basic test of the quest system's capabilities.
 * 
 * @author Devin
 */
public class dragonslayer extends Quest {
	private World world = World.getWorld();

	public final static int UID = 6;

	public dragonslayer(Player owner, Integer uid) {
		super(owner, UID);
	}

	public void define() {
		setName("Dragon Slayer"); // Sets the name of this quest.
		setFinalStage(100); // The stage at which this quest ends.
		associateNpc(187); // Oziach
		associateNpc(111); // Champions Guild Master
		associateNpc(124); // Ned
		associateNpc(197); // Oracle
		associateNpc(152); // General Bentnose
		associateNpc(192); // Wormbrain
		associateNpc(196); // Green Dragon Elvarg
	}

	public void completeQuest() {
		getOwner().incExp(1, 18650, false);
		getOwner().incExp(2, 18650, false);
		getOwner().getActionSender().sendStat(1);
		getOwner().getActionSender().sendStat(2);
		getOwner().getActionSender().sendMessage(
				"@que@You have been given " + 18650 + " Defence experience!");
		getOwner().getActionSender().sendMessage(
				"@que@You have been given " + 18650 + " Strength experience!");

		// sleep(2500);
		world.getDelayedEventHandler().add(new MiniEvent(getOwner(), 2500) {
			public void action() {
				getOwner().getActionSender().sendMessage(
						"@que@You have completed " + getName() + "!");
				getOwner().getActionSender().sendMessage(
						"@que@@gre@You just gained 2 quest points!");
				getOwner().setQuestPoints(getOwner().getQuestPoints() + 2);
				getOwner().getActionSender().sendQuest4Finished();
			}
		});
	}

	private void handleNpc(final Npc npc) {
		if (npc.getID() == 97) {
			// sayNpcMessage("Please please help us, bold hero", npc);
			startTalking(npc);
			if (!questStarted()) // Say start quest dialogue.
			{
				sayNpcMessage("Please please help us, bold hero", npc);
				// sayNpcMessage("What am I to do?", npc);
				world.getDelayedEventHandler().add(
						new MiniEvent(getOwner(), 1500) {
							public void action() {
								sayMessage("What's the problem?");
								world.getDelayedEventHandler().add(
										new MiniEvent(getOwner(), 1500) {
											public void action() {
												sayNpcMessage(
														"Our little village has been dreadfully ravaged by an evil vampire",
														npc);

												startTalking(npc);
												// sayNpcMessage("I think I have a worthwhile quest for you actually",
												// npc);
												world.getDelayedEventHandler()
														.add(new MiniEvent(
																getOwner(),
																1200) {
															public void action() {
																sayNpcMessage(
																		"There's hardly any of us left",
																		npc);
																world.getDelayedEventHandler()
																		.add(new MiniEvent(
																				getOwner(),
																				3000) {
																			public void action() {
																				sayNpcMessage(
																						"We need someone to get rid of him once and for good",
																						npc);
																				// sleep(3000);
																				world.getDelayedEventHandler()
																						.add(new MiniEvent(
																								getOwner(),
																								3000) {
																							public void action() {
																								new QuestMenu(
																										getOwner(),
																										new String[] {
																												"I can be that someone!",
																												"Pfftt....I'm not helping you!" }) {
																									public void handleReply(
																											final int option,
																											String response) {
																										if (option == 1) {
																											startTalking(npc);
																											sayMessage("Pfftt....I'm not helping you!");
																											// sleep(1200);
																											world.getDelayedEventHandler()
																													.add(new MiniEvent(
																															getOwner(),
																															1200) {
																														public void action() {
																															sayNpcMessage(
																																	"I guess Draynor will never be saved.  Good day.",
																																	npc);
																															world.getDelayedEventHandler()
																																	.add(new MiniEvent(
																																			getOwner(),
																																			1200) {
																																		public void action() {
																																			stopTalking(npc); // Reset
																																								// talking
																																								// vars
																																								// and
																																								// MobState.
																																			// getOwner().setMenuHandler(null);
																																			// //
																																			// Remove
																																			// this
																																			// menu
																																			// handler.
																																		}
																																	});
																														}
																													});
																										} else {
																											startTalking(npc);
																											world.getDelayedEventHandler()
																													.add(new MiniEvent(
																															getOwner(),
																															1000) {
																														public void action() {
																															setStage(1); // The
																																			// user
																																			// has
																																			// begun
																																			// the
																																			// quest
																																			// -
																																			// set
																																			// its
																																			// stage
																																			// to
																																			// 1.
																															sayMessage("I can be that someone!");
																															world.getDelayedEventHandler()
																																	.add(new MiniEvent(
																																			getOwner(),
																																			1200) {
																																		public void action() {
																																			sayNpcMessage(
																																					"Thank you so much, brave "
																																							+ owner.getUsername(),
																																					npc);
																																			world.getDelayedEventHandler()
																																					.add(new MiniEvent(
																																							getOwner(),
																																							3000) {
																																						public void action() {
																																							sayNpcMessage(
																																									"You must go speak to Dr. Harlow in Varrock.",
																																									npc);
																																							world.getDelayedEventHandler()
																																									.add(new MiniEvent(
																																											getOwner(),
																																											3000) {
																																										public void action() {
																																											sayNpcMessage(
																																													"He's in the Jolly Boar Inn.",
																																													npc);
																																											world.getDelayedEventHandler()
																																													.add(new MiniEvent(
																																															getOwner(),
																																															1200) {
																																														public void action() {
																																															stopTalking(npc); // Reset
																																																				// talking
																																																				// vars
																																																				// and
																																																				// MobState.
																																															// getOwner().setMenuHandler(null);
																																															// //
																																															// Remove
																																															// this
																																															// menu
																																															// handler.
																																														}
																																													});
																																										}
																																									});
																																						}
																																					});
																																		}
																																	});
																														}
																													});
																										}
																									}
																								};
																							}
																						});
																			}
																		});
															}
														});
											}
										});
							}
						});
			} else if (getStage() == 1) {
				sayNpcMessage(
						"You must speak to Dr. Harlow in the Jolly Boar Inn",
						npc);
				// sleep(1200);
				world.getDelayedEventHandler().add(
						new MiniEvent(getOwner(), 1200) {
							public void action() {
								sayNpcMessage("To continue your quest!", npc);
								stopTalking(npc);
							}
						});
			} else if (getStage() == 2) {
				sayNpcMessage(
						"You need to kill the vampire now. Remember a garlic and wield the stake!",
						npc);
				// sleep(1200);
				owner.getInventory().add(new InvItem(217, 1));
				owner.getActionSender().sendInventory();
				world.getDelayedEventHandler().add(
						new MiniEvent(getOwner(), 1200) {
							public void action() {
								sayNpcMessage("Good luck!", npc);
								stopTalking(npc);
							}
						});
			} else {
				if (completed()) // We've already finished this NPC's quest, so
									// just say a polite hello.
					sayNpcMessage("Thank you for saving draynor!", npc);
				stopTalking(npc);
			}
		} else if (npc.getID() == 98) {
			if (getStage() == 1) {
				startTalking(npc);
				sayNpcMessage("Buy me a drink pleesh", npc);
				world.getDelayedEventHandler().add(
						new MiniEvent(getOwner(), 3000) {
							public void action() {
								new QuestMenu(getOwner(), new String[] {
										"I think you've had enough",
										"Morgan is in trouble" }) {
									public void handleReply(final int option,
											String response) {
										if (option == 1) {
											sayMessage("Morgan is in trouble");
											world.getDelayedEventHandler().add(
													new MiniEvent(getOwner(),
															3000) {
														public void action() {
															sayNpcMessage(
																	"Buy mee uh drenk and i wil halp you",
																	npc);
															world.getDelayedEventHandler()
																	.add(new MiniEvent(
																			getOwner(),
																			3000) {
																		public void action() {
																			if (owner
																					.getInventory()
																					.remove(193,
																							1) > -1) {
																				setStage(2);
																				sayMessage(
																						"Here you go, one cold beer!",
																						npc);
																				world.getDelayedEventHandler()
																						.add(new MiniEvent(
																								getOwner(),
																								1500) {
																							public void action() {
																								sayNpcMessage(
																										"Cheersh matey!",
																										npc);
																								world.getDelayedEventHandler()
																										.add(new MiniEvent(
																												getOwner(),
																												3000) {
																											public void action() {
																												sayMessage("A vampire has been terrorizing Draynor.");
																												world.getDelayedEventHandler()
																														.add(new MiniEvent(
																																getOwner(),
																																3000) {
																															public void action() {
																																sayNpcMessage(
																																		"In order to kill the vampire you'll need to have this stake.",
																																		npc);
																																world.getDelayedEventHandler()
																																		.add(new MiniEvent(
																																				getOwner(),
																																				3000) {
																																			public void action() {
																																				owner.getInventory()
																																						.add(new InvItem(
																																								217,
																																								1));
																																				owner.getActionSender()
																																						.sendInventory();
																																				sayNpcMessage(
																																						"You also need a clove of garlic.",
																																						npc);
																																				world.getDelayedEventHandler()
																																						.add(new MiniEvent(
																																								getOwner(),
																																								3000) {
																																							public void action() {
																																								sayNpcMessage(
																																										"I hope you save Draynor, bold adventurer.",
																																										npc);
																																								stopTalking(npc);
																																							}
																																						});
																																			}
																																		});
																															}
																														});
																											}
																										});
																							}
																						});
																				owner.getActionSender()
																						.sendInventory();
																			} else {
																				sayMessage("I don't have a beer.");
																				world.getDelayedEventHandler()
																						.add(new MiniEvent(
																								getOwner(),
																								1500) {
																							public void action() {
																								sayNpcMessage(
																										"Wut da fak satrv yuu sdrar ded",
																										npc);
																								stopTalking(npc);
																							}
																						});
																			}
																		}
																	});
														}
													});
										} else {
											sayMessage("I think you've had enough");
											stopTalking(npc);
										}
									}
								};
							}
						});
			} else {
				owner.getActionSender()
						.sendMessage(
								"The "
										+ npc.getDef().getName()
										+ " does not seem interested in talking to you.");
			}
		} else {
			owner.getActionSender().sendMessage(
					"The " + npc.getDef().getName()
							+ " does not seem interested in talking to you.");
		}
	}

	/**
	 * This is an optional trigger. If an entity that has been associated
	 * (associateNpc(), associateItem(), associateObject() etc.) has been
	 * interacted with in any way by the player, this will be called, along with
	 * the QuestTrigger enum to define the action.
	 */
	public void triggerEntity(QuestTrigger trigger, Entity entity) {
		if (entity instanceof Npc) // This entity is an NPC.
		{
			Npc npc = (Npc) entity;
			if (trigger == QuestTrigger.NPC_KILLED) // We killed the NPC.
			{
				if (npc.getID() == 96) // Vampire
				{
					if (getStage() == 100) {
						owner.getActionSender().sendMessage(
								"You have already finished this quest.");
						return;
					} else if (getStage() != 2) {
						owner.getActionSender().sendMessage(
								"You are in the wrong part of the quest.");
						return;
					} else if (getStage() == 2) {
						setStage(getFinalStage());
						owner.getActionSender()
								.sendMessage(
										"You drive the stake into the vampire's heart...");
						if (owner.getInventory().remove(217, 1) > -1) {
							owner.getActionSender().sendInventory();
						}
						return;
					}
				}
			} else if (trigger == QuestTrigger.NPC_TALK) // We're talking to the
															// NPC
			{
				handleNpc(npc);
			}
		}
	}
}