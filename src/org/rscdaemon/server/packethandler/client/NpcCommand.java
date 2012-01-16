package org.rscdaemon.server.packethandler.client;

import org.rscdaemon.server.packethandler.PacketHandler;
import org.rscdaemon.server.model.Player;
import org.rscdaemon.server.model.World;

import org.rscdaemon.server.net.Packet;
import org.rscdaemon.server.event.WalkToMobEvent;
import org.rscdaemon.server.event.ShortEvent;
import org.rscdaemon.server.model.Npc;
import org.rscdaemon.server.model.Mob;
import org.rscdaemon.server.event.FightEvent;
import org.rscdaemon.server.states.Action;
import org.rscdaemon.server.util.Formulae;
import org.rscdaemon.server.model.InvItem;
import org.rscdaemon.server.model.ChatMessage;
import org.apache.mina.common.IoSession;

public class NpcCommand implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();

	int amount = 0;
	int exp = 0;
	int lvl = 1;

	public void handlePacket(Packet p, IoSession session) throws Exception {
		int serverIndex = p.readShort();
		final Player player = (Player) session.getAttachment();
		if (player.isBusy()) {
			return;
		}
		final Mob affectedMob = world.getNpc(serverIndex);
		final Npc affectedNpc = (Npc) affectedMob;

		final int npcID = affectedNpc.getID();

		/*
		 * if(!affectedNpc.getDef().getCommand().equalsIgnoreCase("pickpocket")){
		 * player
		 * .getActionSender().sendMessage("You cannot thieve from this npc.");
		 * return; } //
		 * if(bool){player.getActionSender().sendMessage(affectedNpc
		 * .getDef().getName()+" is busy at the moment.");return;}
		 * if(affectedNpc.isBusy()){
		 * player.getActionSender().sendMessage(affectedNpc
		 * .getDef().getName()+" is busy at the moment."); return; }
		 */
		if (player.isBusy()) {
			return;
		}
		switch (npcID) {
		case 65:
		case 321:
			amount = 30;
			exp = 47;
			lvl = 40;
			break;
		case 11:
		case 72:
			amount = 3;
			exp = 8;
			lvl = 1;
			break;
		case 63:
			amount = 9;
			exp = 15;
			lvl = 10;
			break;
		case 86:
			amount = 18;
			exp = 26;
			lvl = 25;
			break;
		case 342:
			amount = 25;
			exp = 36;
			lvl = 32;
			break;
		case 322:
			amount = 50;
			exp = 85;
			lvl = 55;
			break;
		case 323:
			amount = 80;
			exp = 152;
			lvl = 70;
			break;
		case 574:
			amount = 60;
			exp = 138;
			lvl = 65;
			break;
		case 324:
			amount = 250;
			exp = 274;
			lvl = 80;
			break;
		default:
			amount = 0;
			exp = 0;
			lvl = 1;
			break;
		}
		if (affectedMob != null) {
			player.setFollowing(affectedMob);
			// player.setBusy(true);
			// affectedNpc.blockedBy(player);
			setBool(true);
			world.getDelayedEventHandler().add(
					new WalkToMobEvent(player, affectedMob, 1) {
						public void arrived() {
							if (!affectedNpc.getDef().getCommand()
									.equalsIgnoreCase("pickpocket")) {
								player.getActionSender().sendMessage(
										"You cannot thieve from this npc.");
								return;
							}
							// if(bool){player.getActionSender().sendMessage(affectedNpc.getDef().getName()+" is busy at the moment.");return;}
							if (affectedNpc.isBusy()) {
								player.getActionSender().sendMessage(
										affectedNpc.getDef().getName()
												+ " is busy at the moment.");
								return;
							}
							if (owner.isBusy() || owner.isRanging()
									|| !owner.nextTo(affectedMob)
									|| affectedMob == null) {
								return;
							}
							player.setBusy(true);
							affectedNpc.blockedBy(player);
							if (owner.getCurStat(17) < getLvl()) {
								owner.getActionSender().sendMessage(
										"You must be at least " + getLvl()
												+ " thieving to pick the "
												+ affectedNpc.getName()
												+ "'s pocket.");
								owner.setBusy(false);
								affectedNpc.unblock();
								setBool(false);
								return;
							}
							owner.getActionSender().sendMessage(
									"You attempt to pick the "
											+ affectedNpc.getName()
											+ "'s pocket...");
							world.getDelayedEventHandler().add(
									new ShortEvent(owner) {
										public void action() {
											if (!Formulae.catchThief(
													owner.getCurStat(17),
													getLvl())) {
												owner.getInventory().add(
														new InvItem(10,
																getAmount()));
												owner.getActionSender()
														.sendInventory();
												owner.getActionSender()
														.sendMessage(
																"You pick the "
																		+ affectedNpc
																				.getName()
																		+ "'s pocket!");
												owner.incExp(17, getExp() * 10,
														true);
												owner.getActionSender()
														.sendStat(17);
												owner.setBusy(false);
												setBool(false);
												affectedNpc.unblock();
											} else {
												owner.getActionSender()
														.sendMessage(
																"You fail to pick the "
																		+ affectedNpc
																				.getName()
																		+ "'s pocket.");
												owner.informOfNpcMessage(new ChatMessage(
														affectedNpc,
														"Oi!  What do you think you're doing?",
														owner));
												owner.setBusy(false);
												affectedNpc.unblock();
												affectedNpc.resetPath();
												player.resetPath();
												player.resetAll();
												player.setStatus(Action.FIGHTING_MOB);
												setBool(false);
												player.getActionSender()
														.sendMessage(
																"You are under attack!");

												affectedNpc.setLocation(
														player.getLocation(),
														true);
												for (Player p : affectedNpc
														.getViewArea()
														.getPlayersInView()) {
													p.removeWatchedNpc(affectedNpc);
												}

												owner.setBusy(true);
												owner.setSprite(9);
												owner.setOpponent(affectedNpc);
												owner.setCombatTimer();

												affectedNpc.setBusy(true);
												affectedNpc.setSprite(8);
												affectedNpc.setOpponent(owner);
												affectedNpc.setCombatTimer();
												FightEvent fighting = new FightEvent(
														owner, affectedNpc,
														true);
												fighting.setLastRun(0);
												world.getDelayedEventHandler()
														.add(fighting);
											}
										}
									});
						}
					});
		}
	}

	boolean bool = false;

	public void setBool(boolean b) {
		bool = b;
	}

	public final int getAmount() {
		return amount;
	}

	public final int getExp() {
		return exp;
	}

	public final int getLvl() {
		return lvl;
	}
}
