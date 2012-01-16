package org.rscdaemon.server.npchandler;

import org.rscdaemon.server.model.Player;
import org.rscdaemon.server.model.Npc;
import org.rscdaemon.server.model.World;
import org.rscdaemon.server.model.InvItem;
import org.rscdaemon.server.model.ChatMessage;
import org.rscdaemon.server.model.MenuHandler;
import org.rscdaemon.server.model.Point;
import org.rscdaemon.server.event.ShortEvent;

public class Adventurer implements NpcHandler {
	/*
	 * green drags 155 269
	 * 
	 * red drags 162 202
	 * 
	 * greater demons 85 171
	 * 
	 * zombies 161 315
	 * 
	 * axe house 159 100
	 * 
	 * bats 198 109
	 * 
	 * pirates 260 104
	 * 
	 * mage arena 447 3371
	 * 
	 * edge 219 445
	 */

	public static final World world = World.getWorld();

	private static final String[] destinationNames = { "Green Dragons",
			"Red Dragons", "Greater Demons", "Zombies", "Axe House", "Castle",
			"Pirates", "Mage Arena", "Edgeville" };
	private static final Point[] destinationCoords = {
			Point.location(155, 269), Point.location(162, 202),
			Point.location(85, 171), Point.location(161, 315),
			Point.location(159, 100), Point.location(269, 354),
			Point.location(260, 104), Point.location(447, 3371),
			Point.location(219, 445) };

	public void handleNpc(final Npc npc, Player player) throws Exception {
		if (System.currentTimeMillis() - player.getLastMoved() < 7000
				&& player.getLocation().inWilderness()) {
			player.getActionSender().sendMessage(
					"There is a 7 second delay on teleporting, please wait for "
							+ ((7000 - (System.currentTimeMillis() - player
									.getLastMoved())) / 1000) + " secs");
		} else if (System.currentTimeMillis() - player.getLastMoved() > 7000
				&& player.getLocation().inWilderness()) {
			player.informOfNpcMessage(new ChatMessage(npc,
					"Do you need a teleport? These areas are unsafe.", player));
			player.setBusy(true);
			world.getDelayedEventHandler().add(new ShortEvent(player) {
				public void action() {
					owner.setBusy(false);
					owner.setMenuHandler(new MenuHandler(destinationNames) {
						public void handleReply(final int option,
								final String reply) {
							if (owner.isBusy() || option < 0
									|| option >= destinationNames.length) {
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
															Point p = destinationCoords[option];
															owner.teleport(
																	p.getX(),
																	p.getY(),
																	false);
															owner.setBusy(false);
															npc.unblock();
														}
													});
										}
									});
						}
					});
					owner.getActionSender().sendMenu(destinationNames);
				}
			});
			npc.blockedBy(player);
		} else if (!player.getLocation().inWilderness()) {
			player.informOfNpcMessage(new ChatMessage(npc,
					"Do you need a teleport? These areas are unsafe.", player));
			player.setBusy(true);
			world.getDelayedEventHandler().add(new ShortEvent(player) {
				public void action() {
					owner.setBusy(false);
					owner.setMenuHandler(new MenuHandler(destinationNames) {
						public void handleReply(final int option,
								final String reply) {
							if (owner.isBusy() || option < 0
									|| option >= destinationNames.length) {
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
															Point p = destinationCoords[option];
															owner.teleport(
																	p.getX(),
																	p.getY(),
																	false);
															owner.setBusy(false);
															npc.unblock();
														}
													});
										}
									});
						}
					});
					owner.getActionSender().sendMenu(destinationNames);
				}
			});
			npc.blockedBy(player);
		} else {
			player.getActionSender().sendMessage("Error");
		}
		return;
	}

}