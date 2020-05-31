package me.arkorwan.srpg.models;

import java.util.List;

import me.arkorwan.srpg.controller.BattleCommand;
import me.arkorwan.srpg.controller.BattleCommand.CommandType;

/**
 * A player issues commands to units in the same party.
 * 
 * @author arkorwan
 *
 */
public abstract class Player {

	public abstract static class Factory {

		public Player createInstance(BattleInfoReader battleInfo, Party party) {
			Player p = create();
			p.init(battleInfo, party);
			return p;
		}

		/**
		 * Subclass provides the instantiation.
		 * 
		 * @return
		 */
		protected abstract Player create();

		/**
		 * Display name
		 * 
		 * @return
		 */
		public abstract String playerTypeName();

		@Override
		public String toString() {
			return playerTypeName();
		}

	}

	protected BattleInfoReader battleInfo;
	Party party;

	protected void init(BattleInfoReader battleInfo, Party party) {
		this.battleInfo = battleInfo;
		this.party = party;
	}

	/**
	 * Get all the units under this player control
	 * 
	 * @return
	 */
	public Party getParty() {
		return party;
	}

	/**
	 * Get all the units of the opposing party
	 * 
	 * @return
	 */
	public Party getEnemyParty() {
		return party == battleInfo.getParty1() ? battleInfo.getParty2()
				: battleInfo.getParty1();
	}

	/**
	 * Subclass issues a command from a given list of command types.
	 * 
	 * @param commands
	 * @return
	 */
	public abstract BattleCommand selectPlay(Unit currentUnit,
			List<CommandType> commands);

	public abstract void onBattleEnded(boolean winning);

	public abstract boolean shouldAnimate();

}
