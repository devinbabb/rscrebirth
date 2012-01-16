package org.rscdaemon.server.quest;

import org.rscdaemon.server.model.*;
import org.rscdaemon.server.states.Action;
import org.rscdaemon.server.Server;
import org.rscdaemon.server.packetbuilder.loginserver.MiscPacketBuilder;
import org.rscdaemon.server.util.DataConversions;
import org.rscdaemon.server.packetbuilder.RSCPacketBuilder;
import org.rscdaemon.server.event.MiniEvent;

import java.sql.ResultSet;
import java.util.Vector;
import java.util.Date;

/**
 * Quest handler
 * 
 * @author Devin
 */

public abstract class Quest {
	public Quest(Player owner, Integer uid) {
		this.owner = owner;
		this.uid = uid;
		world.getDelayedEventHandler().add(new MiniEvent(owner, 650) {
			public void action() {
				load();
			}
		});
		/*
		 * if(!load()) System.out.println("Big troubles");
		 */}

	// The Player object whom owns this quest.
	protected Player owner = null;
	// The name of this quest.
	protected String name = "";
	// The unique ID of this quest (MUST BE UNIQUE!!!)
	protected int uid = -1;
	// The current 'stage' of this quest.
	protected int stage = -1;
	// The stage that this quest finishes at.
	protected int finalStage = -1;
	// All NPCs that are associated with this quest.
	protected Vector<Integer> associatedNpcs = new Vector<Integer>();
	// All Items associated with this quest.
	protected Vector<Integer> associatedItems = new Vector<Integer>();
	// All the Objects associated with this quest.
	protected Vector<Integer> associatedObjects = new Vector<Integer>();
	// The World object.
	protected World world = World.getWorld();

	public abstract void define();

	public void triggerEntity(QuestTrigger type, Entity entity) {
	}

	public abstract void completeQuest();

	public MiscPacketBuilder loginServer = world.getServer()
			.getLoginConnector().getActionSender();

	/**
	 * @return The owner of this quest.
	 */
	public final synchronized Player getOwner() {
		return owner;
	}

	/**
	 * @return The unique ID of this quest.
	 */
	public final int getUID() {
		return uid;
	}

	/**
	 * Changes the current stage of this quest.
	 */
	public final void setStages(int stage) {
		// System.out.println("Quest load method2 called!");
		this.stage = stage;
	}

	public final void setStage(int stage) {
		this.stage = stage;

		if (stage == 1) {
			loginServer.setStage(uid, stage, owner.getUsernameHash());
		}

		if (stage == 100)
			completeQuest();

		if (save() && stage != 1)
			System.out.println("Successfully saved quest (stage = " + stage
					+ ").");
	}

	/**
	 * Sets the final stage of this quest.
	 */
	public final void setFinalStage(int stage) {
		this.finalStage = stage;
	}

	/**
	 * @return This quest's finishing stage.
	 */
	public final int getFinalStage() {
		return finalStage;
	}

	/**
	 * @return If this quest has been completed.
	 */
	public final boolean completed() {
		return stage == 100;
	}

	/**
	 * @return This quest's current stage.
	 */
	public final int getStage() {
		return stage;
	}

	/**
	 * Changes the name of this quest.
	 */
	public final void setName(String name) {
		this.name = name;
	}

	/**
	 * Saves the quest data to the database.
	 */
	public final boolean save() {
		if (questStarted()) {
			loginServer.saveQuest(uid, stage, owner.getUsernameHash());
			return true;
		}
		return false;
	}

	/**
	 * Loads the quest data from the database.
	 */
	public final void load() {
		// System.out.println(owner.getUsername() +"\t"+
		// owner.getUsernameHash());
		loginServer.loadQuest(owner, uid, owner.getUsernameHash());
	}

	/**
	 * Adds the NPC to the list of NPCs associated with this quest.
	 */
	public final void associateNpc(int id) {
		if (!associatedNpcs.contains(id))
			associatedNpcs.add(id);
	}

	/**
	 * @return If the given NPC ID is associated with this quest.
	 */
	public final boolean npcAssociated(int id) {
		return associatedNpcs.contains(id);
	}

	/**
	 * Adds the item to the list of items associated with this quest.
	 */
	public final void associateItem(int id) {
		if (!associatedItems.contains(id))
			associatedItems.add(id);
	}

	/**
	 * @return If the given item id is associated with this quest.
	 */
	public final boolean itemAssociated(int id) {
		return associatedItems.contains(id);
	}

	/**
	 * Adds the item to the list of objects associated with this quest.
	 */
	public final void associateObject(int id) {
		if (!associatedObjects.contains(id))
			associatedObjects.add(id);
	}

	/**
	 * @return If the given object id is associated with this quest.
	 */
	public final boolean objectAssociated(int id) {
		return associatedObjects.contains(id);
	}

	/**
	 * Unfreezes the player from talking.
	 */
	public final void stopTalking(Npc npc) {
		npc.unblock();
	}

	public final void startTalking(Npc npc) {
		npc.blockedBy(owner);
	}

	/**
	 * @return Whether or not this quest has started.
	 */
	public final boolean questStarted() {
		return stage != -1;
	}

	/**
	 * @return This quest's name.
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Pauses the current thread.
	 */
	public final void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException ie) {
		}
	}

	public void sayMessage(String message) {
		owner.informOfChatMessage(new ChatMessage(owner, message, owner
				.getNpc()));
	}

	public void sayNpcMessage(String message) {
		try {
			owner.informOfNpcMessage(new ChatMessage(owner.getNpc(), message,
					owner));
		} catch (NullPointerException npe) {
			System.out.println("FUCKING NULL!!! " + npe);
		}
	}

	public void sayMessage(String message, Npc npc) {
		owner.informOfChatMessage(new ChatMessage(owner, message, npc));
	}

	public void sayNpcMessage(String message, Npc npc) {
		try {
			owner.informOfNpcMessage(new ChatMessage(npc, message, owner));
		} catch (NullPointerException npe) {
			System.out.println("FUCKING NULL!!! " + npe);
		}
	}
}