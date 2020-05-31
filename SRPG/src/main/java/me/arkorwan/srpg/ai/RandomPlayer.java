package me.arkorwan.srpg.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import me.arkorwan.srpg.controller.BattleCommand;
import me.arkorwan.srpg.controller.BattleCommand.CommandType;
import me.arkorwan.srpg.models.BattleCell;
import me.arkorwan.srpg.models.MagicSpell;
import me.arkorwan.srpg.models.Player;
import me.arkorwan.srpg.models.Unit;

/**
 * A player who selects all the moves randomly. The available actions are
 * shuffled, then tried one by one.
 * 
 * @author arkorwan
 *
 */
public class RandomPlayer extends Player {

	private static Player.Factory factory = new Player.Factory() {

		@Override
		public String playerTypeName() {
			return "Random Player";
		}

		@Override
		protected Player create() {
			return new RandomPlayer();
		}
	};

	public static Player.Factory getFactory() {
		return factory;
	}

	Random r = ThreadLocalRandom.current();

	@Override
	public BattleCommand selectPlay(Unit currentUnit, List<CommandType> commands) {

		commands = new ArrayList<>(commands);
		Collections.shuffle(commands);
		
		for (CommandType cmd : commands) {
			List<BattleCell> targets;
			switch (cmd) {
			case Move:
				targets = new ArrayList<>(battleInfo
						.getVisitableArea(currentUnit).getCurrentArea());
				if (!targets.isEmpty()) {
					BattleCommand command = BattleCommand.ofType(cmd);
					command.setTargetCoordinate(targets
							.get(r.nextInt(targets.size())).getLocation());
					return command;
				}
				break;
			case Attack:
				targets = new ArrayList<>(
						battleInfo.getPhysicallyAttackableArea(currentUnit)
								.getCurrentArea());
				Optional<BattleCell> attackable = getEnemyParty().getMembers()
						.stream().map(u -> battleInfo.getUnitLocation(u))
						.filter(targets::contains).findAny();
				if (attackable.isPresent()) {
					BattleCommand command = BattleCommand.ofType(cmd);
					command.setTargetCoordinate(attackable.get().getLocation());
					return command;
				}
				break;
			case BlackMagic:
				List<MagicSpell> availableSpells = currentUnit
						.getCurrentBlackMagicSpells();
				targets = new ArrayList<>(
						battleInfo.getMagicallyAttackableArea(currentUnit)
								.getCurrentArea());
				Optional<BattleCell> mAttackable = getEnemyParty().getMembers()
						.stream().map(u -> battleInfo.getUnitLocation(u))
						.filter(targets::contains).findAny();
				if (mAttackable.isPresent()) {
					BattleCommand command = BattleCommand.ofType(cmd);
					command.setTargetCoordinate(
							mAttackable.get().getLocation());
					command.setMagicSpell(availableSpells
							.get(r.nextInt(availableSpells.size())));
					return command;
				}

				break;
			case EndTurn:
			default:
				return BattleCommand.endTurnCommand();
			}
		}
		// should not reach here anyway
		System.err
				.println("No suitable battle command. Fallback to 'End Turn'");
		return BattleCommand.endTurnCommand();

	}

	@Override
	public void onBattleEnded(boolean winning) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean shouldAnimate() {
		return true;
	}

}
