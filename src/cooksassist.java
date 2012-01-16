import org.rscdaemon.server.quest.*;
import org.rscdaemon.server.model.*;
import org.rscdaemon.server.event.*;

/**
 * A basic test of the quest system's capabilities.
 * 
 * @author Devin
 */
public class cooksassist extends Quest {
	private World world = World.getWorld();
	/**
	 * This is the quest's UniqueID. IT MUST BE UNIQUE, as it's the database
	 * identifier. Conflicting quest UIDs will result in many angry people, do
	 * not mess this up.
	 */
	public final static int UID = 1;

	/**
	 * The TestQuest constructor, all quests need one, it defines the basics of
	 * the quest (its owner and UID).
	 */
	public cooksassist(Player owner, Integer uid) {
		super(owner, UID);
	}

	/**
	 * This is an abstract Method, and as such must be overwritten by every
	 * subclass of Quest. It defines the unique features of each quest - its
	 * name, its 'completion stage', any items, npcs, objects that are
	 * associated with it.
	 */
	public void define() {
		setName("Cook's Assistant"); // Sets the name of this quest.
		setFinalStage(100); // The stage at which this quest ends.
		associateNpc(7); // Cook
	}

	/**
	 * Also an abstract Method that must be overwritten. This is called when the
	 * getFinalStage() is met.
	 */
	public void completeQuest() {
		getOwner().getActionSender().sendMessage(
				"@que@The Cook gives you permission to use his range.");
		getOwner().incExp(7, 3000, false);
		getOwner().getActionSender().sendStat(7);
		getOwner().getActionSender().sendMessage(
				"@que@You have been given " + 3000 + " cooking experience!");

		// sleep(2500);
		world.getDelayedEventHandler().add(new MiniEvent(getOwner(), 2500) {
			public void action() {
				getOwner().getActionSender().sendMessage(
						"@que@You have completed " + getName() + "!");
				getOwner().getActionSender().sendMessage(
						"@que@@gre@You just gained 1 quest point!");
				getOwner().setQuestPoints(getOwner().getQuestPoints() + 1);
				getOwner().getActionSender().sendQuest1Finished();
			}
		});
	}

	/**
	 * This is my own method I made for organization's sake, and has nothing to
	 * do with the Quest superclass. It handles all the main quest line's stage
	 * progression.
	 */
	private void handleNpc(final Npc npc) {
		if (npc.getID() == 7) // Fred the Farmer
		{
			startTalking(npc);
			if (!questStarted()) // Say start quest dialogue.
			{
				// sayMessage("Test");
				sayNpcMessage("What am I to do?", npc);
				world.getDelayedEventHandler().add(
						new MiniEvent(getOwner(), 1500) {
							public void action() {
								new QuestMenu(
										getOwner(),
										new String[] {
												"What's wrong?",
												"Well could you give me all your money",
												"You don't look very happy",
												"Nice hat" }) // Creates a new
																// option menu.
								{
									public void handleReply(final int option,
											String response) {
										if (option == 0) {
											startTalking(npc);
											sayMessage("What's wrong?");
											world.getDelayedEventHandler().add(
													new MiniEvent(getOwner(),
															1200) {
														public void action() {
															sayNpcMessage(
																	"Ooh dear I'm in a terrible mess",
																	npc);
															world.getDelayedEventHandler()
																	.add(new MiniEvent(
																			getOwner(),
																			3000) {
																		public void action() {
																			sayNpcMessage(
																					"It's the duke's birthday today",
																					npc);
																			// sleep(3000);
																			world.getDelayedEventHandler()
																					.add(new MiniEvent(
																							getOwner(),
																							3000) {
																						public void action() {
																							sayNpcMessage(
																									"I'm meant to be making him a big cake for this evening",
																									npc);
																							// sleep(3000);
																							world.getDelayedEventHandler()
																									.add(new MiniEvent(
																											getOwner(),
																											3000) {
																										public void action() {
																											sayNpcMessage(
																													"Unfortunately, i've forgotten to buy some of the ingredients",
																													npc);
																											world.getDelayedEventHandler()
																													.add(new MiniEvent(
																															getOwner(),
																															3000) {
																														public void action() {
																															sayNpcMessage(
																																	"I'll never get them in time now",
																																	npc);
																															world.getDelayedEventHandler()
																																	.add(new MiniEvent(
																																			getOwner(),
																																			3000) {
																																		public void action() {
																																			sayNpcMessage(
																																					"I don't suppose you could help me?",
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
																																											"No, I don't feel like it.  Maybe later" }) {
																																								public void handleReply(
																																										final int option,
																																										String response) {
																																									if (option == 1) {
																																										startTalking(npc);
																																										sayMessage("No, I don't feel like it.  Maybe later");
																																										// sleep(1200);
																																										world.getDelayedEventHandler()
																																												.add(new MiniEvent(
																																														getOwner(),
																																														1200) {
																																													public void action() {
																																														sayNpcMessage(
																																																"Ok, suit yourself",
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
																																																				"Oh thank you, thank you",
																																																				npc);
																																																		world.getDelayedEventHandler()
																																																				.add(new MiniEvent(
																																																						getOwner(),
																																																						3000) {
																																																					public void action() {
																																																						sayNpcMessage(
																																																								"I need milk, eggs and flour",
																																																								npc);
																																																						world.getDelayedEventHandler()
																																																								.add(new MiniEvent(
																																																										getOwner(),
																																																										3000) {
																																																									public void action() {
																																																										sayNpcMessage(
																																																												"I'd be very grateful if you can get them to me",
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
											sayMessage("Well could you give me all your money");
											world.getDelayedEventHandler().add(
													new MiniEvent(getOwner(),
															1200) {
														public void action() {
															sayNpcMessage(
																	"Haha very funny",
																	npc);
														}
													});
										} else if (option == 2) {
											sayMessage("You don't look very happy");
											world.getDelayedEventHandler().add(
													new MiniEvent(getOwner(),
															1200) {
														public void action() {
															sayNpcMessage(
																	"No, I'm not",
																	npc);
															new QuestMenu(
																	getOwner(),
																	new String[] {
																			"What's wrong?",
																			"I'd take the rest of the day off if I were you" }) {
																public void handleReply(
																		final int option,
																		final String response) {
																	if (option == 0) {
																		sayMessage("What's wrong?");
																		world.getDelayedEventHandler()
																				.add(new MiniEvent(
																						getOwner(),
																						1200) {
																					public void action() {
																						sayNpcMessage(
																								"Ooh dear I'm in a terrible mess",
																								npc);
																						world.getDelayedEventHandler()
																								.add(new MiniEvent(
																										getOwner(),
																										3000) {
																									public void action() {
																										sayNpcMessage(
																												"It's the duke's birthday today",
																												npc);
																										// sleep(3000);
																										world.getDelayedEventHandler()
																												.add(new MiniEvent(
																														getOwner(),
																														3000) {
																													public void action() {
																														sayNpcMessage(
																																"I'm meant to be making him a big cake for this evening",
																																npc);
																														// sleep(3000);
																														world.getDelayedEventHandler()
																																.add(new MiniEvent(
																																		getOwner(),
																																		3000) {
																																	public void action() {
																																		sayNpcMessage(
																																				"Unfortunately, i've forgotten to buy some of the ingredients",
																																				npc);
																																		world.getDelayedEventHandler()
																																				.add(new MiniEvent(
																																						getOwner(),
																																						3000) {
																																					public void action() {
																																						sayNpcMessage(
																																								"I'll never get them in time now",
																																								npc);
																																						world.getDelayedEventHandler()
																																								.add(new MiniEvent(
																																										getOwner(),
																																										3000) {
																																									public void action() {
																																										sayNpcMessage(
																																												"I don't suppose you could help me?",
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
																																																		"No, I don't feel like it.  Maybe later" }) {
																																															public void handleReply(
																																																	final int option,
																																																	String response) {
																																																if (option == 1) {
																																																	startTalking(npc);
																																																	sayMessage("No, I don't feel like it.  Maybe later");
																																																	// sleep(1200);
																																																	world.getDelayedEventHandler()
																																																			.add(new MiniEvent(
																																																					getOwner(),
																																																					1200) {
																																																				public void action() {
																																																					sayNpcMessage(
																																																							"Ok, suit yourself",
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
																																																											"Oh thank you, thank you",
																																																											npc);
																																																									world.getDelayedEventHandler()
																																																											.add(new MiniEvent(
																																																													getOwner(),
																																																													3000) {
																																																												public void action() {
																																																													sayNpcMessage(
																																																															"I need milk, eggs and flour",
																																																															npc);
																																																													world.getDelayedEventHandler()
																																																															.add(new MiniEvent(
																																																																	getOwner(),
																																																																	3000) {
																																																																public void action() {
																																																																	sayNpcMessage(
																																																																			"I'd be very grateful if you can get them to me",
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
																		sayMessage("I'd take the rest of the day off if I were you");
																		world.getDelayedEventHandler()
																				.add(new MiniEvent(
																						getOwner(),
																						1200) {
																					public void action() {
																						sayNpcMessage(
																								"No, that's the worst thing I could do - I'd get in terrible trouble",
																								npc);
																						world.getDelayedEventHandler()
																								.add(new MiniEvent(
																										getOwner(),
																										1200) {
																									public void action() {
																										sayMessage("What's wrong?");
																										world.getDelayedEventHandler()
																												.add(new MiniEvent(
																														getOwner(),
																														1200) {
																													public void action() {
																														sayNpcMessage(
																																"Ooh dear I'm in a terrible mess",
																																npc);
																														world.getDelayedEventHandler()
																																.add(new MiniEvent(
																																		getOwner(),
																																		3000) {
																																	public void action() {
																																		sayNpcMessage(
																																				"It's the duke's birthday today",
																																				npc);
																																		// sleep(3000);
																																		world.getDelayedEventHandler()
																																				.add(new MiniEvent(
																																						getOwner(),
																																						3000) {
																																					public void action() {
																																						sayNpcMessage(
																																								"I'm meant to be making him a big cake for this evening",
																																								npc);
																																						// sleep(3000);
																																						world.getDelayedEventHandler()
																																								.add(new MiniEvent(
																																										getOwner(),
																																										3000) {
																																									public void action() {
																																										sayNpcMessage(
																																												"Unfortunately, i've forgotten to buy some of the ingredients",
																																												npc);
																																										world.getDelayedEventHandler()
																																												.add(new MiniEvent(
																																														getOwner(),
																																														3000) {
																																													public void action() {
																																														sayNpcMessage(
																																																"I'll never get them in time now",
																																																npc);
																																														world.getDelayedEventHandler()
																																																.add(new MiniEvent(
																																																		getOwner(),
																																																		3000) {
																																																	public void action() {
																																																		sayNpcMessage(
																																																				"I don't suppose you could help me?",
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
																																																										"No, I don't feel like it.  Maybe later" }) {
																																																							public void handleReply(
																																																									final int option,
																																																									String response) {
																																																								if (option == 1) {
																																																									startTalking(npc);
																																																									sayMessage("No, I don't feel like it.  Maybe later");
																																																									// sleep(1200);
																																																									world.getDelayedEventHandler()
																																																											.add(new MiniEvent(
																																																													getOwner(),
																																																													1200) {
																																																												public void action() {
																																																													sayNpcMessage(
																																																															"Ok, suit yourself",
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
																																																																			"Oh thank you, thank you",
																																																																			npc);
																																																																	world.getDelayedEventHandler()
																																																																			.add(new MiniEvent(
																																																																					getOwner(),
																																																																					3000) {
																																																																				public void action() {
																																																																					sayNpcMessage(
																																																																							"I need milk, eggs and flour",
																																																																							npc);
																																																																					world.getDelayedEventHandler()
																																																																							.add(new MiniEvent(
																																																																									getOwner(),
																																																																									3000) {
																																																																								public void action() {
																																																																									sayNpcMessage(
																																																																											"I'd be very grateful if you can get them to me",
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
																	}
																}
															};
														}
													});
										} else if (option == 3) {
											sayMessage("Nice hat");
											world.getDelayedEventHandler().add(
													new MiniEvent(getOwner(),
															1200) {
														public void action() {
															sayNpcMessage(
																	"Err thank you -it's a pretty ordinary cooks hat really",
																	npc);
														}
													});
										} else {
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
									}
								};
							}
						});
			} else if (getStage() == 1) {
				sayNpcMessage(
						"How are you getting on with finding the ingredients?",
						npc);
				// sleep(1200);
				world.getDelayedEventHandler().add(
						new MiniEvent(getOwner(), 1200) {
							public void action() {
								world.getDelayedEventHandler().add(
										new MiniEvent(getOwner(), 1200) {
											public void action() {
												if (owner.getInventory()
														.remove(136, 1) > -1
														&& owner.getInventory()
																.remove(22, 1) > -1
														&& owner.getInventory()
																.remove(19, 1) > -1) {
													sayMessage("I now have everything you need for your cake");
													world.getDelayedEventHandler()
															.add(new MiniEvent(
																	getOwner(),
																	3000) {
																public void action() {
																	sayMessage("Milk, flour, and an egg!");
																	world.getDelayedEventHandler()
																			.add(new MiniEvent(
																					getOwner(),
																					3000) {
																				public void action() {
																					sayNpcMessage(
																							"I am saved thankyou!",
																							npc);
																					world.getDelayedEventHandler()
																							.add(new MiniEvent(
																									getOwner(),
																									1200) {
																								public void action() {
																									owner.getActionSender()
																											.sendMessage(
																													"@que@You give some milk, an egg and some flour to the cook");
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
																			"Oh dear oh dear!",
																			npc);
																	world.getDelayedEventHandler()
																			.add(new MiniEvent(
																					getOwner(),
																					3000) {
																				public void action() {
																					sayNpcMessage(
																							"I need flour, eggs, and milk",
																							npc);
																					world.getDelayedEventHandler()
																							.add(new MiniEvent(
																									getOwner(),
																									3000) {
																								public void action() {
																									sayNpcMessage(
																											"Without them i am doomed!",
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
					sayNpcMessage(
							"Hey there, "
									+ getOwner().getUsername()
									+ ". Thank you for all the help, you saved my life.",
							npc);

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