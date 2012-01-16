package org.rscdaemon.server.event;

import org.rscdaemon.server.model.Player;

/**
 * Handles short events
 * 
 * @author Devin
 */

public abstract class ShortEvent extends SingleEvent {

	public ShortEvent(Player owner) {
		super(owner, 1500);
	}

	public abstract void action();

}