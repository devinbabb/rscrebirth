package org.rscdaemon.server.event;

import org.rscdaemon.server.model.Player;

/**
 * Handles events outside game context
 * 
 * @author Devin
 */

public abstract class MiniEvent extends SingleEvent {

	public MiniEvent(Player owner) {
		super(owner, 500);
	}

	public MiniEvent(Player owner, int delay) {
		super(owner, delay);
	}

	public abstract void action();

}