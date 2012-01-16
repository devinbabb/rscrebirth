package org.rscdaemon.server.npchandler;

import org.rscdaemon.server.model.Player;
import org.rscdaemon.server.model.Npc;
import org.rscdaemon.server.model.World;
import org.rscdaemon.server.model.ChatMessage;
import org.rscdaemon.server.event.ShortEvent;
import org.rscdaemon.server.model.MenuHandler;

public class man implements NpcHandler {
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();

	public void handleNpc(final Npc npc, Player player) throws Exception {
		player.setBusy(true);
		player.informOfChatMessage(new ChatMessage(player, "Hello", npc));
		world.getDelayedEventHandler().add(new ShortEvent(player) {
			public void action() {
				owner.informOfChatMessage(new ChatMessage(owner,
						"How's it going?", npc));
				world.getDelayedEventHandler().add(new ShortEvent(owner) {
					public void action() {
						owner.informOfNpcMessage(new ChatMessage(npc,
								"I'm fine", owner));
						world.getDelayedEventHandler().add(
								new ShortEvent(owner) {
									public void action() {
										owner.informOfNpcMessage(new ChatMessage(
												npc, "How are you?", owner));
										world.getDelayedEventHandler().add(
												new ShortEvent(owner) {
													public void action() {
														owner.informOfChatMessage(new ChatMessage(
																owner,
																"Very well, thank you",
																npc));
														npc.unblock();
														owner.setBusy(false);
													}
												});
									}
								});
					}
				});
			}
		});
		npc.blockedBy(player);
	}
}