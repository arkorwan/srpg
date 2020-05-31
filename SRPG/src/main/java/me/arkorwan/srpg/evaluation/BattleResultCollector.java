package me.arkorwan.srpg.evaluation;

import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Stream;

import com.google.common.eventbus.Subscribe;
import com.google.common.math.DoubleMath;

import me.arkorwan.srpg.controller.BattleCommand.CommandType;
import me.arkorwan.srpg.controller.BattleController;
import me.arkorwan.srpg.controller.BattleEvents.BattleEndEvent;
import me.arkorwan.srpg.controller.BattleEvents.DamageEvent;
import me.arkorwan.srpg.controller.BattleEvents.UnitActionEvent;
import me.arkorwan.srpg.controller.BattleEvents.UnitDeadEvent;
import me.arkorwan.srpg.models.Unit;

public class BattleResultCollector {

	BattleController battleController;

	public static final int BINS = 10;

	int turns = 0;
	int deadDiff = 0; // positive -> advantageous for player 1
	int damageDiff = 0;// positive -> advantageous for player 1
	int[] damageCounter = new int[BINS];
	double winning = 0;
	boolean stalemate = false;

	double avgMaxHP;

	public BattleResultCollector(BattleController battleController) {
		this.battleController = battleController;

		List<Unit> p1 = battleController.getBattle().getParty1().getMembers();
		List<Unit> p2 = battleController.getBattle().getParty2().getMembers();

		avgMaxHP = Stream.concat(p1.stream(), p2.stream())
				.mapToDouble(u -> u.getHP().getValue()).average().getAsDouble();

		battleController.registerSubscriber(this);
	}

	@Subscribe
	public void handleUnitAction(UnitActionEvent e) {
		if (e.command.getCommandType() == CommandType.EndTurn) {
			turns += 1;
		}
	}

	@Subscribe
	public void handleDeath(UnitDeadEvent e) {

		if (e.party == battleController.getBattle().getParty1()) {
			deadDiff -= 1;
		} else {
			deadDiff += 1;
		}
	}

	@Subscribe
	public void handleDamage(DamageEvent e) {
		int dmg = e.effectiveDamage;
		if (battleController.getBattle().getPartyForUnit(
				e.unit) == battleController.getBattle().getParty2()) {
			dmg = -dmg;
		}
		damageDiff += dmg;

		if (e.isAttack && e.hit) {
			int bin = DoubleMath.roundToInt(
					Math.min(avgMaxHP, Math.abs(e.damage)) * BINS / avgMaxHP,
					RoundingMode.DOWN);
			if (bin >= BINS) {
				bin = BINS - 1;
			}
			damageCounter[bin] += 1;
		}
	}

	@Subscribe
	public void handleEnd(BattleEndEvent e) {
		battleController.unregisterSubscriber(this);
		if (e.winner == battleController.player1()) {
			winning += 1.0;
		} else if (e.winner == null) {
			winning += 0.5;
			stalemate = true;
		}
	}

}
