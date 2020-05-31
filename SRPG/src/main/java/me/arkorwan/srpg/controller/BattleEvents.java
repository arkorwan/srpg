package me.arkorwan.srpg.controller;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;

import me.arkorwan.srpg.controller.BattleCommand.CommandType;
import me.arkorwan.srpg.models.Battle;
import me.arkorwan.srpg.models.BattleCell;
import me.arkorwan.srpg.models.Coordinate;
import me.arkorwan.srpg.models.Party;
import me.arkorwan.srpg.models.Player;
import me.arkorwan.srpg.models.Unit;

/**
 * 
 * The controller uses publish-subscribe model to announces events to any
 * interested parties -- a GUI for example.
 * 
 * @author arkorwan
 *
 */
public class BattleEvents {

	static final Logger logger = LoggerFactory.getLogger(BattleEvents.class);

	public static class DamageEvent {
		public Unit unit;
		public Coordinate unitLocation;
		public int damage;
		public int effectiveDamage;
		public boolean hit;
		public boolean isAttack;

		private String damageString;

		public String asString() {
			if (damageString == null) {
				damageString = hit ? String.valueOf(effectiveDamage) : "missed";
			}
			return damageString;
		}
	}

	public static class ActionTargetAreaEvent {
		public Player player;
		public Set<BattleCell> area;
		public CommandType commandType;
	}

	public static class UnitActionEvent {
		public Player player;
		public Unit unit;
		public BattleCommand command;
	}

	public static class UnitDeadEvent {
		public Unit casualty;
		public Party party;
	}

	public static class BattleEndEvent {
		public Player winner;
	}

	EventBus eventBus = new EventBus();

	public void register(Object obj) {
		eventBus.register(obj);
	}

	public void unregister(Object obj) {
		eventBus.unregister(obj);
	}

	/**
	 * Announce the amount of damage done to a unit.
	 * 
	 * @param unit
	 * @param damage
	 * @param hit
	 * @param isAttack
	 * @param model
	 */
	public void postDamage(Unit unit, int damage, int effectiveDamage,
			boolean hit, boolean isAttack, Battle model) {
		DamageEvent e = new DamageEvent();
		e.unit = unit;
		e.damage = damage;
		e.effectiveDamage = effectiveDamage;
		e.unitLocation = model.getUnitLocation(unit).getLocation();
		e.hit = hit;
		e.isAttack = isAttack;
		logger.trace("damage event being issued.");
		eventBus.post(e);
	}

	/**
	 * Announce that a player is considering an action on a target area.
	 * 
	 * @param p
	 * @param area
	 * @param commandType
	 */
	public void postActionTargetArea(Player p, Set<BattleCell> area,
			CommandType commandType) {
		ActionTargetAreaEvent e = new ActionTargetAreaEvent();
		e.player = p;
		e.area = area;
		e.commandType = commandType;
		logger.trace("action target event being issued.");
		eventBus.post(e);
	}

	/**
	 * Announce that a certain action has been done.
	 * 
	 * @param p
	 * @param command
	 */
	public void postUnitAction(Player p, Unit u, BattleCommand command) {
		UnitActionEvent e = new UnitActionEvent();
		e.player = p;
		e.unit = u;
		e.command = command;
		logger.trace("unit action event being issued.");
		eventBus.post(e);
	}

	public void postUnitDead(Unit u, Party party) {
		UnitDeadEvent e = new UnitDeadEvent();
		e.casualty = u;
		e.party = party;
		logger.trace("unit dead event being issued.");
		eventBus.post(e);
	}

	public void postBattleEnded(Player winner) {
		BattleEndEvent e = new BattleEndEvent();
		e.winner = winner;
		logger.trace("battle end event being issued.");
		eventBus.post(e);
	}
}
