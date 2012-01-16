import org.rscdaemon.server.quest.*;
import org.rscdaemon.server.model.*;
import org.rscdaemon.server.event.*;

/**
 * A basic test of the quest system's capabilities.
 * 
 * @author Devin
 */
public class impcatcher extends Quest {
	private World world = World.getWorld();
	/**
	 * This is the quest's UniqueID. IT MUST BE UNIQUE, as it's the database
	 * identifier. Conflicting quest UIDs will result in many angry people, do
	 * not mess this up.
	 */
	public final static int UID = 5;

	/**
	 * The TestQuest constructor, all quests need one, it defines the basics of
	 * the quest (its owner and UID).
	 */
	public impcatcher(Player owner, Integer uid) {
		super(owner, UID);
	}

	/**
	 * This is an abstract Method, and as such must be overwritten by every
	 * subclass of Quest. It defines the unique features of each quest - its
	 * name, its 'completion stage', any items, npcs, objects that are
	 * associated with it.
	 */
	public void define() {
		setName("Imp Catcher"); // Sets the name of this quest.
		setFinalStage(100); // The stage at which this quest ends.
		associateNpc(117); // Cook
	}

	/**
	 * Also an abstract Method that must be overwritten. This is called when the
	 * getFinalStage() is met.
	 */
	public void completeQuest() {
		getOwner().incExp(6, 3750, false);
		getOwner().getActionSender().sendStat(6);
		getOwner().getActionSender().sendMessage(
				"@que@You have been given " + 3750 + " magic experience!");
		getOwner().getActionSender().sendMessage(
				"@que@and an Amulet of Accuracy");
		getOwner().getInventory().add(new InvItem(235, 1));
		getOwner().getActionSender().sendInventory();

		// sleep(2500);
		world.getDelayedEventHandler().add(new MiniEvent(getOwner(), 2500) {
			public void action() {
				getOwner().getActionSender().sendMessage(
						"@que@You have completed " + getName() + "!");
				getOwner().getActionSender().sendMessage(
						"@que@@gre@You just gained 1 quest point!");
				getOwner().setQuestPoints(getOwner().getQuestPoints() + 1);
				getOwner().getActionSender().sendQuest5Finished();
			}
		});
	}

	/**
	 * This is my own method I made for organization's sake, and has nothing to
	 * do with the Quest superclass. It handles all the main quest line's stage
	 * progression.
	 */
	private void handleNpc(final Npc npc) {
		if (npc.getID() == 117) // Fred the Farmer
		{
			startTalking(npc);
			if (!questStarted()) // Say start quest dialogue.
			{
				// sayMessage("Test");
				sayNpcMessage("Oh dear!  Imps have stolen my magic beads!", npc);
				world.getDelayedEventHandler().add(
						new MiniEvent(getOwner(), 1500) {
							public void action() {
								new QuestMenu(getOwner(), new String[] {
										"What can I do to help?",
										"Well, that's nice..." }) // Creates a
																	// new
																	// option
																	// menu.
								{
									public void handleReply(final int option,
											String response) {
										if (option == 0) {
											startTalking(npc);
											sayMessage("What can I do to help?");
											world.getDelayedEventHandler().add(
													new MiniEvent(getOwner(),
															1200) {
														public void action() {
															sayNpcMessage(
																	"Basically what I need you to do is",
																	npc);
															world.getDelayedEventHandler()
																	.add(new MiniEvent(
																			getOwner(),
																			3000) {
																		public void action() {
																			sayNpcMessage(
																					"Kill any imps you see, and if they drop a colored bead...",
																					npc);
																			// sleep(3000);
																			world.getDelayedEventHandler()
																					.add(new MiniEvent(
																							getOwner(),
																							3000) {
																						public void action() {
																							sayNpcMessage(
																									"pick it up, once you collect 4 different colors...",
																									npc);
																							// sleep(3000);
																							world.getDelayedEventHandler()
																									.add(new MiniEvent(
																											getOwner(),
																											3000) {
																										public void action() {
																											sayNpcMessage(
																													"Bring them back to me",
																													npc);
																											world.getDelayedEventHandler()
																													.add(new MiniEvent(
																															getOwner(),
																															3000) {
																														public void action() {
																															sayNpcMessage(
																																	"Once you do that you will get a reward",
																																	npc);
																															world.getDelayedEventHandler()
																																	.add(new MiniEvent(
																																			getOwner(),
																																			3000) {
																																		public void action() {
																																			sayNpcMessage(
																																					"I don't suppose you could do that for me?",
																																					npc);
																																			world.getDelayedEventHandler()
																																					.add(new MiniEvent(
																																							getOwner(),
																																							3000) {
																																						public void action() {
																																							new QuestMenu(
																																									getOwner(),
																																									new String[] {
																																											"Yes I'll help you",
																																											"No!" }) {
																																								public void handleReply(
																																										final int option,
																																										String response) {
																																									if (option == 1) {
																																										startTalking(npc);
																																										sayMessage("No!");
																																										// sleep(1200);
																																										world.getDelayedEventHandler()
																																												.add(new MiniEvent(
																																														getOwner(),
																																														1200) {
																																													public void action() {
																																														sayNpcMessage(
																																																"Fine!  May saradomin guide you through your adventures.",
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
																																														sayMessage("Yes I'll help you");
																																														world.getDelayedEventHandler()
																																																.add(new MiniEvent(
																																																		getOwner(),
																																																		1200) {
																																																	public void action() {
																																																		sayNpcMessage(
																																																				"Alright.",
																																																				npc);
																																																		world.getDelayedEventHandler()
																																																				.add(new MiniEvent(
																																																						getOwner(),
																																																						3000) {
																																																					public void action() {
																																																						sayNpcMessage(
																																																								"I need a Red Bead, Yellow Bead, Black Bead, and White Bead",
																																																								npc);
																																																						world.getDelayedEventHandler()
																																																								.add(new MiniEvent(
																																																										getOwner(),
																																																										3000) {
																																																									public void action() {
																																																										sayNpcMessage(
																																																												"There will be a reward in the end for you.",
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
																		}
																	});
														}
													});
										} else if (option == 1) {
											sayMessage("Well, that's nice...");
											stopTalking(npc);
										} else {
											sayMessage("Well, that's nice...");
											// sleep(1200);
											world.getDelayedEventHandler().add(
													new MiniEvent(getOwner(),
															1200) {
														public void action() {
															sayNpcMessage(
																	"Suit yourself. Come and see me if you change your mind.",
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
									}
								};
							}
						});
			} else if (getStage() == 1) {
				sayNpcMessage("Do you have my magical beads yet?", npc);
				// sleep(1200);
				world.getDelayedEventHandler().add(
						new MiniEvent(getOwner(), 1200) {
							public void action() {
								world.getDelayedEventHandler().add(
										new MiniEvent(getOwner(), 1200) {
											public void action() {
												if (owner.getInventory()
														.remove(231, 1) > -1
														&& owner.getInventory()
																.remove(232, 1) > -1
														&& owner.getInventory()
																.remove(233, 1) > -1
														&& owner.getInventory()
																.remove(234, 1) > -1) {
													sayMessage("I have gathered all the beads from the imps!");
													world.getDelayedEventHandler()
															.add(new MiniEvent(
																	getOwner(),
																	3000) {
																public void action() {
																	sayMessage("A red bead, black bead, white bead, and yellow bead!");
																	world.getDelayedEventHandler()
																			.add(new MiniEvent(
																					getOwner(),
																					3000) {
																				public void action() {
																					sayNpcMessage(
																							"Oh thank you kind sir!",
																							npc);
																					world.getDelayedEventHandler()
																							.add(new MiniEvent(
																									getOwner(),
																									1200) {
																								public void action() {
																									owner.getActionSender()
																											.sendMessage(
																													"@que@You give the beads to Wizard Mizmog");
																									/*
																									 * owner
																									 * .
																									 * getInventory
																									 * (
																									 * )
																									 * .
																									 * remove
																									 * (
																									 * owner
																									 * .
																									 * getInventory
																									 * (
																									 * )
																									 * .
																									 * getFirstIndexById
																									 * (
																									 * 23
																									 * )
																									 * )
																									 * ;
																									 * owner
																									 * .
																									 * getInventory
																									 * (
																									 * )
																									 * .
																									 * remove
																									 * (
																									 * owner
																									 * .
																									 * getInventory
																									 * (
																									 * )
																									 * .
																									 * getFirstIndexById
																									 * (
																									 * 22
																									 * )
																									 * )
																									 * ;
																									 * owner
																									 * .
																									 * getInventory
																									 * (
																									 * )
																									 * .
																									 * remove
																									 * (
																									 * owner
																									 * .
																									 * getInventory
																									 * (
																									 * )
																									 * .
																									 * getFirstIndexById
																									 * (
																									 * 19
																									 * )
																									 * )
																									 * ;
																									 */owner.getActionSender()
																											.sendInventory();
																									world.getDelayedEventHandler()
																											.add(new MiniEvent(
																													getOwner(),
																													1200) {
																												public void action() {
																													// owner.getActionSender().sendMessage("@que@Well done.  You have completed the cook's assistant quest!");
																													world.getDelayedEventHandler()
																															.add(new MiniEvent(
																																	getOwner(),
																																	1200) {
																																public void action() {
																																	setStage(getFinalStage()); // Complete
																																								// the
																																								// quest.

																																	stopTalking(npc);
																																	// getOwner().setMenuHandler(null);
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
												} else {
													sayMessage("I'm afraid i don't have them yet");
													world.getDelayedEventHandler()
															.add(new MiniEvent(
																	getOwner(),
																	1200) {
																public void action() {
																	sayNpcMessage(
																			"By saradomin, why are you wasting time then!",
																			npc);
																	world.getDelayedEventHandler()
																			.add(new MiniEvent(
																					getOwner(),
																					3000) {
																				public void action() {
																					sayNpcMessage(
																							"I need a red bead, yellow bead, black bead, and white bead!",
																							npc);
																					world.getDelayedEventHandler()
																							.add(new MiniEvent(
																									getOwner(),
																									3000) {
																								public void action() {
																									sayNpcMessage(
																											"I need those magic beads kind sir!",
																											npc);
																									world.getDelayedEventHandler()
																											.add(new MiniEvent(
																													getOwner(),
																													3000) {
																												public void action() {
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
											}
										});
							}
						});
			} else {
				if (completed()) // We've already finished this NPC's quest, so
									// just say a polite hello.
					sayNpcMessage("Hey there, " + getOwner().getUsername()
							+ ". Thanks for the beads.", npc);

				stopTalking(npc);
			}
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

			if (trigger == QuestTrigger.NPC_TALK) // We're talking to the NPC
													// (For Fred the farmer, not
													// the chicken).
			{
				handleNpc(npc);
			}
		}
	}
}