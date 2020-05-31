package me.arkorwan.srpg.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.arkorwan.srpg.controller.BattleCommand.CommandType;
import me.arkorwan.srpg.models.Battle;
import me.arkorwan.srpg.models.BattleCell;
import me.arkorwan.srpg.models.Coordinate;
import me.arkorwan.srpg.models.MagicSpell;
import me.arkorwan.srpg.models.Party;
import me.arkorwan.srpg.models.Player;
import me.arkorwan.srpg.models.Unit;
import me.arkorwan.srpg.models.calculation.Damage;
import me.arkorwan.srpg.models.targetarea.TargetArea;

/**
 * An instance of this class controls the flow of the battle.
 * 
 * @author arkorwan
 *
 */
public class BattleController implements Runnable {

	static final Logger logger = LoggerFactory
			.getLogger(BattleController.class);

	Battle battle;

	boolean hasMoved;
	boolean hasActed;
	Player p1, p2, currentPlayer;

	BattleEvents events = new BattleEvents();

	PlaySequence sequence;

	OptionalInt maxTurns = OptionalInt.empty();

	public BattleController(Battle battle, Player.Factory factory1,
			Player.Factory factory2) {
		this.battle = battle;
		this.p1 = factory1.createInstance(battle, battle.getParty1());
		this.p2 = factory2.createInstance(battle, battle.getParty2());
		this.sequence = new PlaySequence(battle);
	}

	public Battle getBattle() {
		return battle;
	}

	public void setMaxTurns(int m) {
		this.maxTurns = OptionalInt.of(m);
	}

	public void setNoMaxTurns() {
		this.maxTurns = OptionalInt.empty();
	}

	public void registerSubscriber(Object subscriber) {
		events.register(subscriber);
	}

	public void unregisterSubscriber(Object subscriber) {
		events.unregister(subscriber);
	}

	/**
	 * The list of command types that the current unit can make.
	 * 
	 * @return
	 */
	private List<CommandType> getCurrentCommands() {
		List<CommandType> result = new ArrayList<>();

		Unit unit = getCurrentUnit();
		// a unit can move once per turn
		if (!hasMoved) {
			result.add(CommandType.Move);
		}
		// a unit can do an action once per turn
		if (!hasActed) {
			// attack is always a possible action
			result.add(CommandType.Attack);

			if (!unit.getCurrentBlackMagicSpells().isEmpty()) {
				result.add(CommandType.BlackMagic);
			}
			if (!unit.getCurrentWhiteMagicSpells().isEmpty()) {
				result.add(CommandType.WhiteMagic);
			}

		}
		result.add(CommandType.EndTurn);
		return result;

	}

	/**
	 * Execute a move command
	 * 
	 * @param cmd
	 * @return
	 */
	private boolean executeMove(BattleCommand cmd) {
		Coordinate target = cmd.getTargetCoordinate();
		if (!hasMoved && target != null) {
			Optional<Runnable> exec = moveCurrentUnit(target);
			if (exec.isPresent()) {
				events.postActionTargetArea(currentPlayer, battle
						.getVisitableArea(getCurrentUnit()).getCurrentArea(),
						cmd.getCommandType());
				exec.get().run();
				events.postUnitAction(currentPlayer, getCurrentUnit(), cmd);
				hasMoved = true;
				return true;
			}
		}
		return false;
	}

	private Optional<Runnable> moveCurrentUnit(Coordinate target) {
		BattleCell targetCell = battle.getField().cellForCoordinate(target);
		if (targetCell.getUnit().isPresent()
				|| !battle.getVisitableArea(getCurrentUnit()).getCurrentArea()
						.contains(targetCell)) {
			return Optional.empty();
		}

		Runnable r = new Runnable() {
			@Override
			public void run() {
				battle.setUnitLocation(getCurrentUnit(), targetCell);
			}
		};

		return Optional.of(r);
	}

	/**
	 * Execute an attack (physical or magical) command.
	 * 
	 * @param cmd
	 * @param spell
	 * @return
	 */
	private boolean executeAttack(BattleCommand cmd, MagicSpell spell) {

		TargetArea attackableCells = (spell != null)
				? battle.getMagicallyAttackableArea(getCurrentUnit())
				: battle.getPhysicallyAttackableArea(getCurrentUnit());
		Coordinate target = cmd.getTargetCoordinate();
		if (!hasActed && attackableCells.getCurrentArea().stream()
				.anyMatch(cell -> cell.getLocation().equals(target))) {

			Optional<Runnable> exec = makeCurrentUnitStrike(target, spell);

			if (exec.isPresent()) {
				events.postActionTargetArea(currentPlayer,
						attackableCells.getCurrentArea(), cmd.getCommandType());
				exec.get().run();
				events.postUnitAction(currentPlayer, getCurrentUnit(), cmd);
				hasActed = true;
				return true;
			}

		}
		return false;
	}

	private Optional<Runnable> makeCurrentUnitStrike(Coordinate target,
			MagicSpell spell) {
		BattleCell targetCell = battle.getField().cellForCoordinate(target);
		if (!targetCell.getUnit().isPresent()) {
			return Optional.empty();
		}

		Unit targetUnit = targetCell.getUnit().get();

		Damage dmgCalc = (spell != null)
				? battle.getDamageCalculator().magicalDamage(getCurrentUnit(),
						targetUnit, battle.getRandom(), spell)
				: battle.getDamageCalculator().physicalDamage(getCurrentUnit(),
						targetUnit, battle.getRandom());
		Optional<Integer> damage = dmgCalc.getActualDamage();
		Runnable r = new Runnable() {

			@Override
			public void run() {
				if (spell != null) {
					getCurrentUnit().adjustCurrentMP(-spell.cost());
				}

				boolean hit = damage.isPresent();
				if (hit) {
					int realDamage = targetUnit.adjustCurrentHP(-damage.get());
					events.postDamage(targetUnit, damage.get(), realDamage,
							true, true, battle);
				} else {
					events.postDamage(targetUnit, 0, 0, false, true, battle);
				}
				if (targetUnit.getCurrentHP() == 0) {
					Party party = battle.getPartyForUnit(targetUnit);
					battle.removeUnit(targetUnit);
					events.postUnitDead(targetUnit, party);
				}
			}

		};

		return Optional.of(r);

	}

	/**
	 * Execute healing command.
	 * 
	 * @param cmd
	 * @return
	 */
	private boolean executeHeal(BattleCommand cmd) {
		TargetArea targetCells = battle
				.getMagicallyAttackableArea(getCurrentUnit());
		Coordinate target = cmd.getTargetCoordinate();
		if (!hasActed && targetCells.getCurrentArea().stream()
				.anyMatch(cell -> cell.getLocation().equals(target))) {

			Optional<Runnable> exec = makeCurrentUnitHeal(target,
					cmd.getMagicSpell());

			if (exec.isPresent()) {
				events.postActionTargetArea(currentPlayer,
						targetCells.getCurrentArea(), cmd.getCommandType());
				exec.get().run();
				events.postUnitAction(currentPlayer, getCurrentUnit(), cmd);
				hasActed = true;
				return true;
			}
		}
		return false;
	}

	private Optional<Runnable> makeCurrentUnitHeal(Coordinate target,
			MagicSpell spell) {
		BattleCell targetCell = battle.getField().cellForCoordinate(target);
		if (!targetCell.getUnit().isPresent()) {
			return Optional.empty();
		}

		Unit targetUnit = targetCell.getUnit().get();

		Damage dmgCalc = battle.getDamageCalculator()
				.healingDamage(getCurrentUnit(), battle.getRandom(), spell);
		Optional<Integer> damage = dmgCalc.getActualDamage();
		Runnable r = new Runnable() {

			@Override
			public void run() {
				getCurrentUnit().adjustCurrentMP(-spell.cost());
				boolean hit = damage.isPresent();
				if (hit) {
					int realHeal = targetUnit.adjustCurrentHP(damage.get());
					events.postDamage(targetUnit, damage.get(), realHeal, true,
							false, battle);
				} else {
					events.postDamage(targetUnit, 0, 0, false, false, battle);
				}
			}

		};

		return Optional.of(r);

	}

	/**
	 * End the current turn
	 */
	private void endTurn() {
		hasMoved = false;
		hasActed = false;
		sequence.nextTurn();
	}

	/**
	 * Start the control loop in a new thread
	 */
	public void start() {
		ExecutorService ex = Executors.newSingleThreadExecutor();
		ex.execute(this);
		ex.shutdown();
	}

	/**
	 * The current player
	 * 
	 * @return
	 */
	public Player currentPlayer() {
		return currentPlayer;
	}

	public Player player1() {
		return p1;
	}

	public Player player2() {
		return p2;
	}

	public Unit getCurrentUnit() {
		return sequence.getCurrentUnit();
	}

	@Override
	public void run() {
		this.currentPlayer = null;
		initializeBattle();
		endTurn();

		int turnsLeft = maxTurns.isPresent() ? maxTurns.getAsInt() : -1;

		while (!battle.getWinningParty().isPresent() && turnsLeft-- != 0) {
			logger.trace("turns left: {}", turnsLeft);
			currentPlayer = battle.getPartyForUnit(getCurrentUnit()) == battle
					.getParty1() ? p1 : p2;
			boolean done = false;
			while (!done) {
				BattleCommand command = currentPlayer.selectPlay(
						getCurrentUnit(),
						Collections.unmodifiableList(getCurrentCommands()));
				logger.trace("command {} selected.", command.getCommandType());
				switch (command.getCommandType()) {
				case Move:
					executeMove(command);
					break;
				case Attack:
					executeAttack(command, null);
					break;
				case BlackMagic:
					executeAttack(command, command.getMagicSpell());
					break;
				case WhiteMagic:
					executeHeal(command);
					break;
				case EndTurn:
					Unit current = getCurrentUnit();
					endTurn();
					events.postUnitAction(currentPlayer, current, command);
					done = true;
					break;
				}
			}

		}
		Optional<Party> winning = battle.getWinningParty();
		if (winning.isPresent()) {
			if (p1.getParty() == winning.get()) {
				events.postBattleEnded(p1);
			} else {
				events.postBattleEnded(p2);
			}
		} else {
			logger.trace("Stalemate.");
			events.postBattleEnded(null);
		}

	}

	void initializeBattle() {
		sequence.initQueue();
	}

}
