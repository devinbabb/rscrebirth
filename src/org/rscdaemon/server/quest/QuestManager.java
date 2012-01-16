package org.rscdaemon.server.quest;

import org.rscdaemon.server.model.*;

import java.io.File;
import java.util.HashMap;
import java.util.Vector;

/**
 * Quest manager plugin
 * 
 * @author Devin
 */

public class QuestManager {
	// The player who owns this quest object.
	private Player owner;
	// The list of quests.
	private HashMap<String, Quest> quests = new HashMap<String, Quest>();

	public QuestManager(Player owner) {
		this.owner = owner;
		try {
			loadQuests();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public final void loadQuests() throws Exception {
		Vector<Class> classes = QuestLoader.getClasses();
		if (classes == null) {
			System.out
					.println("Problem loading classes at loadQuests() inside QuestManager.java");
		}

		for (Class c : classes) {
			Quest quest = (Quest) c.getConstructor(
					new Class[] { Player.class, Integer.class }).newInstance(
					new Object[] { owner, -1 });
			quest.define();
			quests.put(c.getName(), quest);
		}
	}

	public boolean associatedWithQuest(Entity entity) {
		HashMap<String, Quest> clone = (HashMap<String, Quest>) quests.clone();

		try {
			for (Quest quest : clone.values()) {
				if (entity instanceof Npc) {
					if (quest.npcAssociated(((Npc) entity).getID()))
						return true;
				} else if (entity instanceof GameObject) {
					if (quest.objectAssociated(((GameObject) entity).getID()))
						return true;
				} else if (entity instanceof InvItem) {
					if (quest.itemAssociated(((InvItem) entity).getID()))
						return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public void setStages(int s, int questid) {
		HashMap<String, Quest> clone = (HashMap<String, Quest>) quests.clone();
		// System.out.println("Quest load method1 called!");
		for (Quest quest : clone.values()) {
			if (quest.getUID() == questid) {
				quest.setStages(s);
			}
		}
	}

	public boolean completed(String questName) {
		HashMap<String, Quest> clone = (HashMap<String, Quest>) quests.clone();
		// System.out.println("Quest load method1 called!");
		for (Quest quest : clone.values()) {
			if (quest.getName() == questName) {
				return quest.completed();
			}
		}
		return false;
	}

	public boolean triggerEntity(final QuestTrigger trigger, final Entity entity) {
		HashMap<String, Quest> clone = (HashMap<String, Quest>) quests.clone();
		boolean handled = false;

		try {
			synchronized (this) {
				for (final Quest quest : clone.values()) {
					if (entity instanceof Npc)
						if (!quest.npcAssociated(((Npc) entity).getID()))
							continue;

					if (entity instanceof InvItem)
						if (!quest.itemAssociated(((InvItem) entity).getID()))
							continue;

					if (entity instanceof GameObject)
						if (!quest.objectAssociated(((GameObject) entity)
								.getID()))
							continue;

					new Thread(new Runnable() {
						public void run() {
							quest.triggerEntity(trigger, entity);
						}
					}).start();

					handled = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return handled;
	}
}