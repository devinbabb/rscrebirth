package org.rscdaemon.server.npchandler;

import org.rscdaemon.server.model.Player;
import org.rscdaemon.server.model.Npc;
import org.rscdaemon.server.model.World;
import org.rscdaemon.server.model.ChatMessage;
import org.rscdaemon.server.model.MenuHandler;
import org.rscdaemon.server.model.InvItem;
import org.rscdaemon.server.event.ShortEvent;

public class bartender implements NpcHandler {
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();

	public void handleNpc(final Npc npc, Player player) throws Exception {
		player.informOfNpcMessage(new ChatMessage(npc,
				"Would you like a beer?  Only 2 gold pieces.", player));
		player.setBusy(true);
		world.getDelayedEventHandler().add(new ShortEvent(player) {
			public void action() {
				owner.setBusy(false);
				String[] options = new String[] { "Sure", "No thanks" };
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
										owner.setBusy(false);
										switch (option) {
										case 0:
											if (owner.getInventory()
													.countId(10) < 2) {
												owner.informOfChatMessage(new ChatMessage(
														owner,
														"I'll just go get the cash",
														npc));
											} else if (owner.getInventory()
													.remove(10, 2) > -1) {
												owner.getInventory().add(
														new InvItem(193, 1));
												owner.informOfNpcMessage(new ChatMessage(
														npc, "There ya' go!",
														owner));
												owner.getActionSender()
														.sendInventory();
											}
											break;
										}
										npc.unblock();
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