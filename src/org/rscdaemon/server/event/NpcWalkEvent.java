package org.rscdaemon.server.event;

import org.rscdaemon.server.model.Player;
import org.rscdaemon.server.model.Npc;
import org.rscdaemon.server.model.Path;

/**
 * Handles random npc roaming
 * 
 * @author Devin
 */

public abstract class NpcWalkEvent extends DelayedEvent {
	protected Npc affectedMob;
	private int radius;

	public NpcWalkEvent(Player owner, Npc affectedMob, int radius) {
		super(owner, 500);
		this.affectedMob = affectedMob;
		this.radius = radius;
		affectedMob.moveNpc(new Path(affectedMob.getX(), affectedMob.getY(),
				owner.getLocation().getX(), owner.getLocation().getY()));
		if (affectedMob.withinRange(owner, radius)) {
			arrived();
			super.running = false;
		}
	}

	public final void run() {
		if (owner.withinRange(affectedMob, radius)) {
			arrived();
		} else if (owner.hasMoved()) {
			return; // We're still moving
		} else {
			failed();
		}
		super.running = false;
	}

	public abstract void arrived();

	public void failed() {
	} // Not abstract as isn't required

	public Npc getAffectedMob() {
		return affectedMob;
	}

}