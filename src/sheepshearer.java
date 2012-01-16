import org.rscdaemon.server.quest.*;
import org.rscdaemon.server.model.*;
import org.rscdaemon.server.event.*;

/**
 * A basic test of the quest system's capabilities.
 * 
 * @author Devin
 */
public class sheepshearer extends Quest {
	private World world = World.getWorld();
	/**
	 * This is the quest's UniqueID. IT MUST BE UNIQUE, as it's the database
	 * identifier. Conflicting quest UIDs will result in many angry people, do
	 * not mess this up.
	 */
	public final static int UID = 2;

	/**
	 * The TestQuest constructor, all quests need one, it defines the basics of
	 * the quest (its owner and UID).
	 */
	public sheepshearer(Player owner, Integer uid) {
		super(owner, UID);
	}

	/**
	 * This is an abstract Method, and as such must be overwritten by every
	 * subclass of Quest. It defines the unique features of each quest - its
	 * name, its 'completion stage', any items, npcs, objects that are
	 * associated with it.
	 */
	public void define() {
		setName("Sheep shearer"); // Sets the name of this quest.
		setFinalStage(100); // The stage at which this quest ends.
		associateNpc(77); // Fred the farmer
		associateNpc(3); // Chicken
		associateItem(10); // Coins
		associateObject(0); // Tree
	}

	/**
	 * Also an abstract Method that must be overwritten. This is called when the
	 * getFinalStage() is met.
	 */
	public void completeQuest() {
		getOwner().getInventory().add(new InvItem(10, 500)); // Give the player
																// 500 coins
		getOwner().getActionSender().sendInventory();

		getOwner().getActionSender().sendMessage(
				"Fred the Farmer hands you 500 coins.");
		getOwner().incExp(12, 5000, false); // 5000 Crafting xp
		getOwner().getActionSender().sendStat(12);
		getOwner().getActionSender().sendMessage(
				"@que@You have been given " + 5000 + " crafting experience!");

		// sleep(2500);
		world.getDelayedEventHandler().add(new MiniEvent(getOwner(), 2500) {
			public void action() {
				getOwner().getActionSender().sendMessage(
						"You have completed " + getName() + "!");
				getOwner().getActionSender().sendMessage(
						"@gre@You just gained 1 quest point!");
				getOwner().setQuestPoints(getOwner().getQuestPoints() + 1);
				getOwner().getActionSender().sendQuest2Finished();
			}
		});
	}

	/**
	 * This is my own method I made for organization's sake, and has nothing to
	 * do with the Quest superclass. It handles all the main quest line's stage
	 * progression.
	 */
	private void handleNpc(final Npc npc) {
		if (npc.getID() == 77) // Fred the Farmer
		{
			startTalking(npc);
			if (!questStarted()) // Say start quest dialogue.
			{
				// sayMessage("Test");
				sayNpcMessage("Hi there, traveller. Care to make some money?",
						npc);
				world.getDelayedEventHandler().add(
						new MiniEvent(getOwner(), 1500) {
							public void action() {
								new QuestMenu(getOwner(), new String[] {
										"Sure, what do I need to do?",
										"No thanks, I'm good." }) // Creates a
																	// new
																	// option
																	// menu.
								{
									public void handleReply(final int option,
											String response) {
										if (option == 0) {
											startTalking(npc);
											sayMessage("Sure, what do I need to do?");
											world.getDelayedEventHandler().add(
													new MiniEvent(getOwner(),
															1200) {
														public void action() {
															sayNpcMessage(
																	"If you collect 10 balls of wool for me, I'll pay you 500 coins.",
																	npc);
															world.getDelayedEventHandler()
																	.add(new MiniEvent(
																			getOwner(),
																			3000) {
																		public void action() {
																			sayNpcMessage(
																					"Maybe I'll teach you a thing or two about crafting, too.",
																					npc);
																			// sleep(3000);
																			world.getDelayedEventHandler()
																					.add(new MiniEvent(
																							getOwner(),
																							3000) {
																						public void action() {
																							sayNpcMessage(
																									"I'm afraid you'll have to find your own shears, but the sheep are outside.",
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
																															"Sorry, I don't like the sound of that.",
																															"I'd be happy to help." }) {
																												public void handleReply(
																														final int option,
																														String response) {
																													if (option == 0) {
																														startTalking(npc);
																														sayMessage("Sorry, I don't like the sound of that.");
																														// sleep(1200);
																														world.getDelayedEventHandler()
																																.add(new MiniEvent(
																																		getOwner(),
																																		1200) {
																																	public void action() {
																																		sayNpcMessage(
																																				"Suit yourself. Good day, then.",
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
																																		sayMessage("I'd be happy to help.");
																																		world.getDelayedEventHandler()
																																				.add(new MiniEvent(
																																						getOwner(),
																																						1200) {
																																					public void action() {
																																						// sleep(1200);
																																						sayNpcMessage(
																																								"Great! Come back and see me when you're done.",
																																								npc);
																																						// sleep(1000);
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
										} else // The player declined the quest.
										{
											sayMessage("No thanks, I'm good.");
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

										// stopTalking(npc); // Reset talking
										// vars and MobState.
										// getOwner().setMenuHandler(null); //
										// Remove this menu handler.
									}
								};
							}
						});
			} else if (getStage() == 1) // We've already started this quest.
			{
				sayNpcMessage("Ahh, you've returned! Do you have my wool?", npc);
				// sleep(1200);
				world.getDelayedEventHandler().add(
						new MiniEvent(getOwner(), 1200) {
							public void action() {
								new QuestMenu(getOwner(), new String[] {
										"Yes, I do.", "I'm afraid not." }) {
									public void handleReply(final int option,
											String response) {
										if (option == 0) {
											sayMessage("Yes, I do.");
											// sleep(1200);
											world.getDelayedEventHandler().add(
													new MiniEvent(getOwner(),
															1200) {
														public void action() {
															if (owner
																	.getInventory()
																	.countId(
																			207) < 10) // Check
																						// if
																						// we
																						// actually
																						// have
																						// the
																						// balls
																						// of
																						// wool.
															{
																if (owner
																		.getInventory()
																		.countId(
																				145) > 0) {
																	sayNpcMessage(
																			"You need to spin this wool!",
																			npc);
																	stopTalking(npc);
																	return;
																}
																sayNpcMessage(
																		"Um, no you don't. Get back to me when you do. The reward still stands!",
																		npc);
																stopTalking(npc);
																// getOwner().setMenuHandler(null);
															} else {
																sayNpcMessage(
																		"Excellent! Hand them over, then.",
																		npc);
																// sleep(1200);
																world.getDelayedEventHandler()
																		.add(new MiniEvent(
																				getOwner(),
																				1200) {
																			public void action() {
																				owner.getActionSender()
																						.sendMessage(
																								"You hand over 10 balls of wool to "
																										+ npc.getDef()
																												.getName()
																										+ ".");
																				world.getDelayedEventHandler()
																						.add(new MiniEvent(
																								getOwner(),
																								1200) {
																							public void action() {
																								for (int i = 0; i < 10; i++)
																									// Remove
																									// the
																									// wool.
																									owner.getInventory()
																											.remove(owner
																													.getInventory()
																													.getFirstIndexById(
																															207));

																								owner.getActionSender()
																										.sendInventory();
																								world.getDelayedEventHandler()
																										.add(new MiniEvent(
																												getOwner(),
																												1200) {
																											public void action() {
																												sayNpcMessage(
																														"Thank you very much! As promised, here's your reward.",
																														npc);
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
														}
													});
										} else {
											sayMessage("I'm afraid not.");
											world.getDelayedEventHandler().add(
													new MiniEvent(getOwner(),
															1200) {
														public void action() {
															sayNpcMessage(
																	"Well, come and see me when you do. The offer still stands",
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
			} else {
				if (completed()) // We've already finished this NPC's quest, so
									// just say a polite hello.
					sayNpcMessage("Hey there, " + getOwner().getUsername()
							+ ". Thank you for your help with the wool!", npc);

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

			if (trigger == QuestTrigger.NPC_KILLED) // We killed the NPC.
			{
				if (npc.getID() == 3) // chicken
				{
					getOwner().getActionSender().sendMessage(
							getName() + " You killed a chicken.");
					setStage(1);
				}
			} else if (trigger == QuestTrigger.NPC_TALK) // We're talking to the
															// NPC (For Fred the
															// farmer, not the
															// chicken).
			{
				handleNpc(npc);
			}
		} else if (entity instanceof InvItem) // This entity is an InvItem.
		{
			InvItem item = (InvItem) entity;

			if (trigger == QuestTrigger.ITEM_PICKUP) // We picked the item up.
			{
				getOwner().getActionSender().sendMessage(
						getName() + " - You picked up a "
								+ item.getDef().getName());
			} else if (trigger == QuestTrigger.ITEM_DROP) // We dropped the
															// item.
			{
				getOwner().getActionSender().sendMessage(
						getName() + " - You dropped a "
								+ item.getDef().getName());
			}
		} else if (entity instanceof GameObject) // This entity is a GameObject.
		{
			GameObject object = (GameObject) entity;

			if (trigger == QuestTrigger.OBJECT_ACT1) // We acted upon command 1
														// of this object
			{
				getOwner().getActionSender().sendMessage(
						getName() + " - You acted on "
								+ object.getGameObjectDef().getName());
			} else if (trigger == QuestTrigger.OBJECT_ACT2) // We acted upon
															// command 2 of this
															// object
			{
				getOwner().getActionSender().sendMessage(
						getName() + " - You acted2 on "
								+ object.getGameObjectDef().getName());
			}
		}
	}
}