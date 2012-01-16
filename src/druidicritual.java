import org.rscdaemon.server.quest.*;
import org.rscdaemon.server.model.*;
import org.rscdaemon.server.event.*;

/**
 * A basic test of the quest system's capabilities.
 * 
 * @author Devin
 */
public class druidicritual extends Quest {
	private World world = World.getWorld();

	public final static int UID = 3;

	public druidicritual(Player owner, Integer uid) {
		super(owner, UID);
	}

	public void define() {
		setName("Druidic Ritual"); // Sets the name of this quest.
		setFinalStage(100); // The stage at which this quest ends.
		associateNpc(204); // Kaqemeex
		associateNpc(205); // Sanfew
	}

	public void completeQuest() {
		getOwner().getActionSender().sendMessage(
				"@que@You can now use the Herblaw skill!");
		getOwner().incExp(15, 3000, false);
		getOwner().getActionSender().sendStat(15);
		getOwner().getActionSender().sendMessage(
				"@que@You have been given " + 3000 + " herblaw experience!");

		// sleep(2500);
		world.getDelayedEventHandler().add(new MiniEvent(getOwner(), 2500) {
			public void action() {
				getOwner().getActionSender().sendMessage(
						"@que@You have completed " + getName() + "!");
				getOwner().getActionSender().sendMessage(
						"@que@@gre@You just gained 4 quest point!");
				getOwner().setQuestPoints(getOwner().getQuestPoints() + 4);
				getOwner().getActionSender().sendQuest3Finished();
			}
		});
	}

	private void handleNpc(final Npc npc) {
		if (npc.getID() == 204) // Fred the Farmer
		{
			startTalking(npc);
			if (!questStarted()) // Say start quest dialogue.
			{
				sayMessage("I'm in search of a quest.");
				// sayNpcMessage("What am I to do?", npc);
				world.getDelayedEventHandler().add(
						new MiniEvent(getOwner(), 1500) {
							public void action() {
								startTalking(npc);
								sayNpcMessage(
										"I think I have a worthwhile quest for you actually",
										npc);
								world.getDelayedEventHandler().add(
										new MiniEvent(getOwner(), 1200) {
											public void action() {
												sayNpcMessage(
														"I don't know if you are familair with the stone circle south of varrock",
														npc);
												world.getDelayedEventHandler()
														.add(new MiniEvent(
																getOwner(),
																3000) {
															public void action() {
																sayNpcMessage(
																		"That used to be our stone circle",
																		npc);
																// sleep(3000);
																world.getDelayedEventHandler()
																		.add(new MiniEvent(
																				getOwner(),
																				3000) {
																			public void action() {
																				sayNpcMessage(
																						"Unfortunatley  many years ago dark wizards cast a wicked spell on it",
																						npc);
																				world.getDelayedEventHandler()
																						.add(new MiniEvent(
																								getOwner(),
																								3000) {
																							public void action() {
																								sayNpcMessage(
																										"Corrupting it for their own evil purposes",
																										npc);
																								world.getDelayedEventHandler()
																										.add(new MiniEvent(
																												getOwner(),
																												3000) {
																											public void action() {
																												sayNpcMessage(
																														"And making it useless for us",
																														npc);
																												world.getDelayedEventHandler()
																														.add(new MiniEvent(
																																getOwner(),
																																3000) {
																															public void action() {
																																sayNpcMessage(
																																		"We need someone who will go on a quest for us",
																																		npc);
																																world.getDelayedEventHandler()
																																		.add(new MiniEvent(
																																				getOwner(),
																																				3000) {
																																			public void action() {
																																				sayNpcMessage(
																																						"To help us purify the circle of varrock",
																																						npc);
																																				world.getDelayedEventHandler()
																																						.add(new MiniEvent(
																																								getOwner(),
																																								3000) {
																																							public void action() {
																																								new QuestMenu(
																																										getOwner(),
																																										new String[] {
																																												"Ok I will try and help",
																																												"Sounds like a lot of work.  No thanks." }) {
																																									public void handleReply(
																																											final int option,
																																											String response) {
																																										if (option == 1) {
																																											startTalking(npc);
																																											sayMessage("Sounds like a lot of work.  No thanks.");
																																											// sleep(1200);
																																											world.getDelayedEventHandler()
																																													.add(new MiniEvent(
																																															getOwner(),
																																															1200) {
																																														public void action() {
																																															sayNpcMessage(
																																																	"Fine, then be gone!",
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
																																															sayMessage("Ok I will try and help");
																																															world.getDelayedEventHandler()
																																																	.add(new MiniEvent(
																																																			getOwner(),
																																																			1200) {
																																																		public void action() {
																																																			sayNpcMessage(
																																																					"Ok go and speak to our elder druid, sanfew",
																																																					npc);
																																																			world.getDelayedEventHandler()
																																																					.add(new MiniEvent(
																																																							getOwner(),
																																																							3000) {
																																																						public void action() {
																																																							sayNpcMessage(
																																																									"He lives in our village to the south of here",
																																																									npc);
																																																							world.getDelayedEventHandler()
																																																									.add(new MiniEvent(
																																																											getOwner(),
																																																											3000) {
																																																										public void action() {
																																																											sayNpcMessage(
																																																													"He knows better what we need than i",
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
											}
										});
							}
						});
			} else if (getStage() == 1) {
				sayNpcMessage(
						"You need to speak to sanfew in the village south of here",
						npc);
				// sleep(1200);
				world.getDelayedEventHandler().add(
						new MiniEvent(getOwner(), 1200) {
							public void action() {
								sayNpcMessage("To continue your quest.", npc);
								stopTalking(npc);
							}
						});
				/*
				 * world.getDelayedEventHandler().add(new MiniEvent(getOwner(),
				 * 1200) { public void action() {
				 * if(owner.getInventory().remove(136, 1) > -1 &&
				 * owner.getInventory().remove(22, 1) > -1 &&
				 * owner.getInventory().remove(19, 1) > -1) {
				 * sayMessage("I now have everything you need for your cake");
				 * world.getDelayedEventHandler().add(new MiniEvent(getOwner(),
				 * 3000) { public void action() {
				 * sayMessage("Milk, flour, and an egg!");
				 * world.getDelayedEventHandler().add(new MiniEvent(getOwner(),
				 * 3000) { public void action() {
				 * sayNpcMessage("I am saved thankyou!", npc);
				 * world.getDelayedEventHandler().add(new MiniEvent(getOwner(),
				 * 1200) { public void action() {
				 * owner.getActionSender().sendMessage
				 * ("@que@You give some milk, an egg and some flour to the cook"
				 * ); owner.getActionSender().sendInventory();
				 * world.getDelayedEventHandler().add(new MiniEvent(getOwner(),
				 * 1200) { public void action() {
				 * owner.getActionSender().sendMessage(
				 * "@que@Well done.  You have completed the cook's assistant quest!"
				 * ); world.getDelayedEventHandler().add(new
				 * MiniEvent(getOwner(), 1200) { public void action() {
				 * setStage(getFinalStage()); // Complete the quest.
				 * 
				 * stopTalking(npc); // getOwner().setMenuHandler(null); } }); }
				 * }); } }); } }); } }); } else {
				 * sayMessage("I'm afraid i don't have them yet");
				 * world.getDelayedEventHandler().add(new MiniEvent(getOwner(),
				 * 1200) { public void action() {
				 * sayNpcMessage("Oh dear oh dear!", npc);
				 * world.getDelayedEventHandler().add(new MiniEvent(getOwner(),
				 * 3000) { public void action() {
				 * sayNpcMessage("I need flour, eggs, and milk", npc);
				 * world.getDelayedEventHandler().add(new MiniEvent(getOwner(),
				 * 3000) { public void action() {
				 * sayNpcMessage("Without them i am doomed!", npc);
				 * world.getDelayedEventHandler().add(new MiniEvent(getOwner(),
				 * 3000) { public void action() { stopTalking(npc); } }); } });
				 * } }); } }); } } }); } });
				 */
			} else if (getStage() == 3) {
				sayNpcMessage(
						"Well done!  You have saved our Varrock stone circle!",
						npc);
				world.getDelayedEventHandler().add(
						new MiniEvent(getOwner(), 3000) {
							public void action() {
								setStage(getFinalStage());
							}
						});
			} else {
				if (completed()) // We've already finished this NPC's quest, so
									// just say a polite hello.
					sayNpcMessage("Good luck with your adventures, good sir!",
							npc);
				stopTalking(npc);
			}
		} else if (npc.getID() == 205) {
			if (getStage() == 1) {
				sayNpcMessage("What can I do ya' fur?", npc);
				world.getDelayedEventHandler().add(
						new MiniEvent(getOwner(), 3000) {
							public void action() {
								new QuestMenu(
										getOwner(),
										new String[] {
												"I've been sent to help purify the varrock stone circle.",
												"Nothing.  Get away from me you old hobo!" }) {
									public void handleReply(final int option,
											String response) {
										if (option == 0) {
											sayMessage("I've been sent to help purify the varrock stone circle.");
											setStage(2);
											world.getDelayedEventHandler().add(
													new MiniEvent(getOwner(),
															3000) {
														public void action() {
															sayNpcMessage(
																	"You don't say!  Well, I guess you'll do.",
																	npc);
															world.getDelayedEventHandler()
																	.add(new MiniEvent(
																			getOwner(),
																			3000) {
																		public void action() {
																			sayNpcMessage(
																					"All I need you to do is find 4 types of meat...",
																					npc);
																			world.getDelayedEventHandler()
																					.add(new MiniEvent(
																							getOwner(),
																							3000) {
																						public void action() {
																							sayNpcMessage(
																									"Raw chicken, raw bear meat, raw beef, and raw rat meat.",
																									npc);
																							world.getDelayedEventHandler()
																									.add(new MiniEvent(
																											getOwner(),
																											3000) {
																										public void action() {
																											sayNpcMessage(
																													"But once you get them, you need to dip them in the Cauldron of Thunder...",
																													npc);
																											world.getDelayedEventHandler()
																													.add(new MiniEvent(
																															getOwner(),
																															3000) {
																														public void action() {
																															sayNpcMessage(
																																	"Which is located in the cave south of here.",
																																	npc);
																															world.getDelayedEventHandler()
																																	.add(new MiniEvent(
																																			getOwner(),
																																			3000) {
																																		public void action() {
																																			sayNpcMessage(
																																					"Once you dip them in the Cauldron of Thunder, bring them back to me",
																																					npc);
																																			world.getDelayedEventHandler()
																																					.add(new MiniEvent(
																																							getOwner(),
																																							3000) {
																																						public void action() {
																																							sayNpcMessage(
																																									"then I will do the ritual, and you will recieve your reward.",
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
																		}
																	});
														}
													});
										} else {
											sayMessage("Nothing.  Get away from me you old hobo!");
											stopTalking(npc);
										}
									}
								};
							}
						});
			} else if (getStage() == 2) {
				sayNpcMessage(
						"How are you coming on getting me those enchanted meats?",
						npc);
				world.getDelayedEventHandler().add(
						new MiniEvent(getOwner(), 3000) {
							public void action() {
								if (owner.getInventory().remove(505, 1) > -1
										&& owner.getInventory().remove(506, 1) > -1
										&& owner.getInventory().remove(507, 1) > -1
										&& owner.getInventory().remove(508, 1) > -1) {
									sayMessage("I have gathered them all and enchanted them in the cauldron.");
									world.getDelayedEventHandler().add(
											new MiniEvent(getOwner(), 3000) {
												public void action() {
													sayNpcMessage(
															"Oh, thank you.",
															npc);
													world.getDelayedEventHandler()
															.add(new MiniEvent(
																	getOwner(),
																	3000) {
																public void action() {
																	sayNpcMessage(
																			"Now I can perform the ritual and save the varrock stone circle!",
																			npc);
																	world.getDelayedEventHandler()
																			.add(new MiniEvent(
																					getOwner(),
																					1200) {
																				public void action() {
																					owner.getActionSender()
																							.sendMessage(
																									"@que@You give Sanfew some enchanted beef, enchanted rat meat, enchanted chicken, and enchanted bear meat.");
																					owner.getActionSender()
																							.sendInventory();
																					world.getDelayedEventHandler()
																							.add(new MiniEvent(
																									getOwner(),
																									1200) {
																								public void action() {
																									world.getDelayedEventHandler()
																											.add(new MiniEvent(
																													getOwner(),
																													1200) {
																												public void action() {
																													setStage(3); // Complete
																																	// the
																																	// quest.
																													sayNpcMessage(
																															"Now go see Kaqemeex in the circle north of here to finish your quest.",
																															npc);

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
									world.getDelayedEventHandler().add(
											new MiniEvent(getOwner(), 1200) {
												public void action() {
													sayNpcMessage(
															"By guthix!  Why are you talking to me wasting time?",
															npc);
													world.getDelayedEventHandler()
															.add(new MiniEvent(
																	getOwner(),
																	3000) {
																public void action() {
																	sayNpcMessage(
																			"I need you to get raw bear meat, raw beef, raw rat meat, and raw chicken",
																			npc);
																	world.getDelayedEventHandler()
																			.add(new MiniEvent(
																					getOwner(),
																					3000) {
																				public void action() {
																					sayNpcMessage(
																							"Then I need you to dip them in the cauldron of thunder...",
																							npc);
																					world.getDelayedEventHandler()
																							.add(new MiniEvent(
																									getOwner(),
																									3000) {
																								public void action() {
																									sayNpcMessage(
																											"located in the cave south of here",
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
							}
						});
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

			if (trigger == QuestTrigger.NPC_TALK) // We're talking to the NPC
													// (For Fred the farmer, not
													// the chicken).
			{
				handleNpc(npc);
			}
		}
	}
}