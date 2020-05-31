package me.arkorwan.srpg.ai;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import me.arkorwan.srpg.controller.BattleCommand;
import me.arkorwan.srpg.controller.BattleCommand.CommandType;
import me.arkorwan.srpg.models.BattleCell;
import me.arkorwan.srpg.models.BattleInfoReader;
import me.arkorwan.srpg.models.MagicSpell;
import me.arkorwan.srpg.models.Party;
import me.arkorwan.srpg.models.Player;
import me.arkorwan.srpg.models.Unit;
import me.arkorwan.srpg.models.calculation.Damage;
import me.arkorwan.srpg.models.targetarea.TargetArea;
import me.arkorwan.utils.Common;

/**
 * 
 * A purposeful AI player. The AI tries to make a useful action (attack an enemy
 * , heal its own party) if it can. It also tries to move itself into the best
 * position available. A potential function based on expected effect on HP is
 * used to assess how good an action or a cell is.
 * 
 * @author arkorwan
 *
 */
public class SmartPlayer extends Player {

	private static Player.Factory factory = new Player.Factory() {

		@Override
		public String playerTypeName() {
			return "AI player";
		}

		@Override
		protected Player create() {
			return new SmartPlayer();
		}
	};

	public static Player.Factory getFactory() {
		return factory;
	}

	private static class CommandPotential
			implements Comparable<CommandPotential> {

		BattleCommand actionCommand = null;
		double potential = 0;
		int moves = 0; // the number of moves needed to execute this command

		@Override
		public int compareTo(CommandPotential o) {
			return Double.compare(potential, o.potential);
		}
	}

	Map<Unit, AllCellsMoveFinder> moveFinders = new HashMap<>();

	// hold the remaining commands for the current turn.
	LinkedList<BattleCommand> currentCommands = new LinkedList<>();

	@Override
	protected void init(BattleInfoReader battleInfo, Party party) {
		super.init(battleInfo, party);
		initMoveFinder(party.getMembers());
		initMoveFinder(getEnemyParty().getMembers());
	}

	private void initMoveFinder(List<Unit> party) {
		for (Unit u : party) {
			moveFinders.put(u,
					new AllCellsMoveFinder(u, battleInfo.getField()));
		}
	}

	@Override
	public BattleCommand selectPlay(Unit currentUnit,
			List<CommandType> commands) {

		if (currentCommands.isEmpty()) {
			currentCommands = createPlays(currentUnit, commands);
		}

		return currentCommands.removeFirst();
	}

	/**
	 * Create a list of commands for this turn.
	 * 
	 * @param commands
	 * @return
	 */
	private LinkedList<BattleCommand> createPlays(Unit u,
			List<CommandType> commands) {

		LinkedList<BattleCommand> result = new LinkedList<>();

		// get all the best commands to attack each enemy.
		Stream<CommandPotential> attacks = Stream.empty();
		if (commands.contains(CommandType.Attack)
				|| commands.contains(CommandType.BlackMagic)) {
			attacks = getEnemyParty().getMembers().stream()
					.map(enemy -> maxDamagePotential(u, enemy,
							battleInfo.getUnitLocation(u),
							battleInfo.getUnitLocation(enemy)));
		}

		// get all the best commands to heal each friendly units, including the
		// current unit.
		Stream<CommandPotential> heals = Stream.empty();
		if (commands.contains(CommandType.WhiteMagic)) {
			heals = getParty().getMembers().stream()
					.map(friend -> maxHealingPotential(u, friend,
							battleInfo.getUnitLocation(u),
							battleInfo.getUnitLocation(friend)));
		}

		// find the best command that can be executed in 0 or 1 move.
		Optional<CommandPotential> bestAction = Stream.concat(attacks, heals)
				.filter(cs -> cs.moves == 0 || cs.moves == 1)
				.max(CommandPotential::compareTo);

		if (bestAction.isPresent()) {

			if (bestAction.get().moves == 0) {
				// execute the action, then move to the best position.
				result.add(bestAction.get().actionCommand);
				addBestMove(u, battleInfo.getVisitableArea(u).getCurrentArea(),
						result);
			} else {
				// list all the positions that allow the action to be executed,
				// then find the best one
				CommandPotential cs = bestAction.get();
				BattleCell targetCell = battleInfo.getField().cellForCoordinate(
						cs.actionCommand.getTargetCoordinate());
				List<BattleCell> moveCandidates = battleInfo.getVisitableArea(u)
						.getCurrentArea().stream()
						.filter(c -> attackTargetArea(u,
								cs.actionCommand.getCommandType())
										.getArea(c.getLocation())
										.contains(targetCell))
						.collect(Collectors.toList());

				// move to that position, then execute the action
				addBestMove(u, moveCandidates, result);
				result.add(cs.actionCommand);
			}
		} else {
			// if no useful action is found, just move to the best position
			addBestMove(u, battleInfo.getVisitableArea(u).getCurrentArea(),
					result);
		}

		// always end your turn!
		result.add(BattleCommand.endTurnCommand());
		return result;

	}

	/**
	 * Find the best position among the given candidates, and add a Move command
	 * to the given command list, but only if the best candidate has better
	 * potential than the current position.
	 * 
	 * @param u
	 * @param candidates
	 * @param resultList
	 */
	private void addBestMove(Unit u, Collection<BattleCell> candidates,
			List<BattleCommand> resultList) {
		// initialize the potential with the current position
		double potential = cellPotential(u, battleInfo.getUnitLocation(u));
		BattleCell bestCell = null;
		for (BattleCell cell : candidates) {
			// skip if the cell is occupied
			if (cell.getUnit().isPresent()) {
				continue;
			}
			double f = cellPotential(u, cell);
			if (f > potential) {
				potential = f;
				bestCell = cell;
			}
		}

		if (bestCell != null) {
			BattleCommand moveCommand = BattleCommand.ofType(CommandType.Move);
			moveCommand.setTargetCoordinate(bestCell.getLocation());
			resultList.add(moveCommand);
		}
	}

	private TargetArea attackTargetArea(Unit u, CommandType t) {
		if (t == CommandType.Attack) {
			return battleInfo.getPhysicallyAttackableArea(u);
		} else if (t == CommandType.BlackMagic || t == CommandType.WhiteMagic) {
			return battleInfo.getMagicallyAttackableArea(u);
		}
		return null;
	}

	static final double step_mult = Common
			.getConfigWithDefault("ai_player.fitness.step_multiplier", 0.3);
	static final double blackmagic_mult = Common.getConfigWithDefault(
			"ai_player.fitness.blackmagic_multiplier", 0.9);
	static final double whitemagic_mult = Common.getConfigWithDefault(
			"ai_player.fitness.whitemagic_multiplier", 2.0);
	static final double defense_mult = Common.getConfigWithDefault(
			"ai_player.fitness.defense_multiplier", 0.001);
	static final boolean prefer_blackmagic = Common
			.getConfigWithDefault("ai_player.prefer_blackmagic", true);

	/**
	 * A cell potential is measured assuming that the current unit is on the
	 * cell. It is calculated by taking the potential of the best action,
	 * subtracted by the (weighted) sum of the potential of the best attack by
	 * each enemy on this cell, added by the (weighted) sum of the potential of
	 * the best healing spell by each friendly units on this cell.
	 * 
	 * @param cell
	 * @return
	 */
	private double cellPotential(Unit u, BattleCell cell) {

		double potential = 0;

		// maximum damage to enemies or heal to friends

		Optional<CommandPotential> attackDamage = getEnemyParty().getMembers()
				.stream()
				.map(enemy -> maxDamagePotential(u, enemy, cell,
						battleInfo.getUnitLocation(enemy)))
				.max(CommandPotential::compareTo);
		if (attackDamage.isPresent()) {
			potential = attackDamage.get().potential;
		}

		Optional<CommandPotential> heal = getParty().getMembers().stream()
				.map(friend -> maxHealingPotential(u, friend, cell,
						battleInfo.getUnitLocation(friend)))
				.max(CommandPotential::compareTo);
		if (heal.isPresent()) {
			double f = heal.get().potential;
			if (f > potential) {
				potential = f;
			}
		}

		// sum damaged by enemies
		for (Unit enemy : getEnemyParty().getMembers()) {
			BattleCell enemyCell = battleInfo.getUnitLocation(enemy);
			potential -= defense_mult
					* maxDamagePotential(enemy, u, enemyCell, cell).potential;
		}

		// sum heal by friends
		for (Unit friend : getParty().getMembers()) {
			if (friend != u) {
				BattleCell friendCell = battleInfo.getUnitLocation(friend);
				potential += defense_mult * maxHealingPotential(friend, u,
						friendCell, cell).potential;
			}
		}

		return potential;
	}

	/**
	 * The expected damage is the base damage times the probability of it being
	 * effective
	 * 
	 * @param calc
	 * @return
	 */
	private double getExpectedDamage(Damage calc) {
		return calc.getBaseDamage() * (1.0 - calc.getEvasion());
	}

	/**
	 * The command and moves required to cause maximum damage potential by a
	 * unit to another.
	 * 
	 * @param attacker
	 * @param target
	 * @param attackerCell
	 * @param targetCell
	 * @return
	 */
	private CommandPotential maxDamagePotential(Unit attacker, Unit target,
			BattleCell attackerCell, BattleCell targetCell) {

		CommandPotential result;

		// physical
		double pDmg = getExpectedDamage(battleInfo.getDamageCalculator()
				.physicalDamage(attacker, target, null));

		result = expectedPotential(attacker, target, attackerCell, targetCell,
				pDmg, battleInfo.getPhysicallyAttackableArea(attacker));
		result.actionCommand = BattleCommand.ofType(CommandType.Attack);

		// magical
		double mDmg = 0;
		MagicSpell spell = null;

		for (MagicSpell sp : attacker.getCurrentBlackMagicSpells()) {
			double v = getExpectedDamage(battleInfo.getDamageCalculator()
					.magicalDamage(attacker, target, null, sp))
					* blackmagic_mult;
			if (v > mDmg) {
				mDmg = v;
				spell = sp;
			}
		}

		if (spell != null) {
			BattleCommand cmd = BattleCommand.ofType(CommandType.BlackMagic);
			cmd.setMagicSpell(spell);
			CommandPotential magicCs = expectedPotential(attacker, target,
					attackerCell, targetCell, mDmg,
					battleInfo.getMagicallyAttackableArea(attacker));
			if (prefer_blackmagic || magicCs.potential > result.potential) {
				result = magicCs;
				result.actionCommand = cmd;
			}
		}

		result.actionCommand.setTargetCoordinate(targetCell.getLocation());
		return result;
	}

	/**
	 * The command and moves required to cause maximum healing potential by a
	 * unit to another.
	 * 
	 * @param healer
	 * @param target
	 * @param healerCell
	 * @param targetCell
	 * @param canMove
	 * @return
	 */
	private CommandPotential maxHealingPotential(Unit healer, Unit target,
			BattleCell healerCell, BattleCell targetCell) {
		CommandPotential result = new CommandPotential();
		result.potential = 0;
		result.moves = -1;
		MagicSpell spell = null;
		for (MagicSpell sp : healer.getCurrentWhiteMagicSpells()) {
			double v = getExpectedDamage(battleInfo.getDamageCalculator()
					.healingDamage(healer, null, sp)) * whitemagic_mult;
			v *= 1.0 - (target.getCurrentHP() * 1.0
					/ (target.getHP().getValue()));
			if (v > result.potential) {
				result.potential = v;
				spell = sp;
			}
		}

		if (spell != null) {
			BattleCommand cmd = BattleCommand.ofType(CommandType.WhiteMagic);
			cmd.setMagicSpell(spell);
			cmd.setTargetCoordinate(targetCell.getLocation());
			CommandPotential magicCs = expectedPotential(healer, target,
					healerCell, targetCell, result.potential,
					battleInfo.getMagicallyAttackableArea(healer));
			if (magicCs.potential >= result.potential) {
				result = magicCs;
				result.actionCommand = cmd;
			}
		}

		return result;
	}

	private CommandPotential expectedPotential(Unit attacker, Unit target,
			BattleCell attackerCell, BattleCell targetCell,
			double initialDamage, TargetArea initialArea) {

		CommandPotential cs = new CommandPotential();
		cs.potential = 0;
		cs.moves = -1;

		if (initialArea.getArea(attackerCell.getLocation())
				.contains(targetCell)) {
			cs.potential = initialDamage;
			cs.moves = 0;
		} else {
			double dmg = initialDamage;
			AllCellsMoveFinder finder = moveFinders.get(attacker);
			int moves = 0;
			boolean found = false;
			while (!found) {
				moves += 1;
				dmg *= step_mult;
				Set<BattleCell> cells = finder.cellsWithTurns(attackerCell,
						moves);
				if (cells.isEmpty()) {
					cs.potential = 0;
					cs.moves = -1;
					break;
				}
				for (BattleCell cell : cells) {
					if (!cell.getUnit().isPresent() && initialArea
							.getArea(cell.getLocation()).contains(targetCell)) {
						found = true;
						cs.potential = dmg;
						cs.moves = moves;
					}
				}
			}

		}
		return cs;
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
