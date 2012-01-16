package org.rscdaemon.server.packethandler.client;

import org.rscdaemon.server.packethandler.PacketHandler;
import org.rscdaemon.server.model.*;
import org.rscdaemon.server.net.Packet;
import org.rscdaemon.server.event.WalkToPointEvent;
import org.rscdaemon.server.states.Action;
import org.apache.mina.common.IoSession;

public class PickupItem implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();

	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player) session.getAttachment();
		if (player.isBusy()) {
			player.resetPath();
			return;
		}
		player.resetAll();
		Point location = Point.location(p.readShort(), p.readShort());
		int id = p.readShort();
		final ActiveTile tile = world.getTile(location);
		final Item item = getItem(id, tile, player);
		if (item == null) {
			player.setSuspiciousPlayer(true);
			player.resetPath();
			return;
		}
		player.setStatus(Action.TAKING_GITEM);
		world.getDelayedEventHandler().add(
				new WalkToPointEvent(player, location, 1, false) {
					public void arrived() {
						if (owner.isBusy() || owner.isRanging()
								|| !tile.hasItem(item) || !owner.nextTo(item)
								|| owner.getStatus() != Action.TAKING_GITEM) {
							return;
						}
						if (item.getID() == 501 && item.getX() == 333
								&& item.getY() == 434) {
							owner.getActionSender()
									.sendMessage(
											"A force stops you from taking the holy wine.");
							return;
						}
						if (item.getID() == 23) {
							owner.getActionSender().sendMessage(
									"You need a pot to hold this flour in!");
							return;
						}
						owner.resetAll();
						InvItem invItem = new InvItem(item.getID(), item
								.getAmount());
						if (!owner.getInventory().canHold(invItem)) {
							owner.getActionSender()
									.sendMessage(
											"You cannot pickup this item, your inventory is full!");
							return;
						}
						world.unregisterItem(item);
						owner.getActionSender().sendSound("takeobject");
						owner.getInventory().add(invItem);
						owner.getActionSender().sendInventory();
					}
				});
	}

	private Item getItem(int id, ActiveTile tile, Player player) {
		for (Item i : tile.getItems()) {
			if (i.getID() == id && i.visibleTo(player)) {
				return i;
			}
		}
		return null;
	}

}
