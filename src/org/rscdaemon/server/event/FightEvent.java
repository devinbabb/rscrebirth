package org.rscdaemon.server.event;

import org.rscdaemon.server.model.*;
import org.rscdaemon.server.util.DataConversions;
import org.rscdaemon.server.util.Formulae;
import org.rscdaemon.server.states.CombatState;

import java.util.ArrayList;

/**
 * Handles npc vs player combat
 * 
 * @author Devin
 */

public class FightEvent extends DelayedEvent {
	private Mob affectedMob;
	private int hits;
	private int firstHit;

	public FightEvent(Player owner, Mob affectedMob) {
		this(owner, affectedMob, false);
	}

	public FightEvent(Player owner, Mob affectedMob, boolean attacked) {
		super(owner, 1000);
		this.affectedMob = affectedMob;
		firstHit = attacked ? 1 : 0;
		hits = 0;
	}

	public void run() {
		if (!owner.loggedIn()
				|| (affectedMob instanceof Player && !((Player) affectedMob)
						.loggedIn())) {
			owner.resetCombat(CombatState.ERROR);
			affectedMob.resetCombat(CombatState.ERROR);
			return;
		}
		Mob attacker, opponent;
		if (hits++ % 2 == firstHit) {
			attacker = owner;
			opponent = affectedMob;
		} else {
			attacker = affectedMob;
			opponent = owner;
		}
		if (opponent.getHits() <= 0) {
			attacker.resetCombat(CombatState.WON);
			opponent.resetCombat(CombatState.LOST);
			return;
		}
		attacker.incHitsMade();
		if (attacker instanceof Npc && opponent.isPrayerActivated(12)) {
			return;
		}
		int damage = Formulae.calcFightHit(attacker, opponent);
		opponent.setLastDamage(damage);
		int newHp = opponent.getHits() - damage;
		opponent.setHits(newHp);
		ArrayList<Player> playersToInform = new ArrayList<Player>();
		playersToInform.addAll(opponent.getViewArea().getPlayersInView());
		playersToInform.addAll(attacker.getViewArea().getPlayersInView());
		for (Player p : playersToInform) {
			p.informOfModifiedHits(opponent);
		}
		String combatSound = damage > 0 ? "combat1b" : "combat1a";
		if (opponent instanceof Player) {
			Player opponentPlayer = ((Player) opponent);
			opponentPlayer.getActionSender().sendStat(3);
			opponentPlayer.getActionSender().sendSound(combatSound);
		}
		if (attacker instanceof Player) {
			Player attackerPlayer = (Player) attacker;
			attackerPlayer.getActionSender().sendSound(combatSound);
		}
		if (newHp <= 0) {
			opponent.killedBy(attacker, false);
			if (attacker instanceof Player) {
				Player attackerPlayer = (Player) attacker;
				int exp = DataConversions.roundUp(Formulae
						.combatExperience(opponent) / 4D);
				switch (attackerPlayer.getCombatStyle()) {
				case 0:
					for (int x = 0; x < 3; x++) {
						attackerPlayer.incExp(x, exp * 10, true);
						attackerPlayer.getActionSender().sendStat(x);
					}
					break;
				case 1:
					attackerPlayer.incExp(2, (exp * 3) * 10, true);
					attackerPlayer.getActionSender().sendStat(2);
					break;
				case 2:
					attackerPlayer.incExp(0, (exp * 3) * 10, true);
					attackerPlayer.getActionSender().sendStat(0);
					break;
				case 3:
					attackerPlayer.incExp(1, (exp * 3) * 10, true);
					attackerPlayer.getActionSender().sendStat(1);
					break;
				}
				attackerPlayer.incExp(3, exp * 10, true);
				attackerPlayer.getActionSender().sendStat(3);
			}
			attacker.resetCombat(CombatState.WON);
			opponent.resetCombat(CombatState.LOST);
		}
	}

	public Mob getAffectedMob() {
		return affectedMob;
	}

	public boolean equals(Object o) {
		if (o instanceof FightEvent) {
			FightEvent e = (FightEvent) o;
			return e.belongsTo(owner) && e.getAffectedMob().equals(affectedMob);
		}
		return false;
	}
}