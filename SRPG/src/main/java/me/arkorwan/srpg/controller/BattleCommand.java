package me.arkorwan.srpg.controller;

import me.arkorwan.srpg.models.Coordinate;
import me.arkorwan.srpg.models.MagicSpell;

/**
 * A BattleCommand tells a unit to do one action -- move, physical attack, magic
 * casting, announcing end of turn.
 * 
 * @author arkorwan
 *
 */
public class BattleCommand {

	public static enum CommandType {
		Move("Move"), Attack("Attack"), BlackMagic("Black Magic"), WhiteMagic(
				"White Magic"), EndTurn("End Turn");
		private String name;

		CommandType(String name) {
			this.name = name;
		}

		public String getDisplayName() {
			return name;
		}
	}

	private CommandType type;
	private Coordinate targetCoordinate;
	private MagicSpell spell;

	private BattleCommand(CommandType type) {
		this.type = type;
	}

	public static BattleCommand ofType(CommandType type) {
		if (type == CommandType.EndTurn) {
			return endTurnCommand();
		} else {
			return new BattleCommand(type);
		}
	}

	// A singleton instance for the end of turn command.
	private static BattleCommand endTurnInstance = new BattleCommand(
			CommandType.EndTurn);

	public static BattleCommand endTurnCommand() {
		return endTurnInstance;
	}

	public CommandType getCommandType() {
		return type;
	}

	public void setTargetCoordinate(Coordinate coord) {
		this.targetCoordinate = coord;
	}

	public Coordinate getTargetCoordinate() {
		return targetCoordinate;
	}

	public void setMagicSpell(MagicSpell spell) {
		this.spell = spell;
	}

	public MagicSpell getMagicSpell() {
		return spell;
	}

}
