package me.arkorwan.srpg.evaluation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.arkorwan.utils.Common;

public abstract class FitnessFunction {

	static final Logger logger = LoggerFactory.getLogger(FitnessFunction.class);

	String name;
	int weight;

	FitnessFunction(String name) {
		this.name = name;
	}

	String getName() {
		return name;
	}

	abstract double eval(BattleResultAggregator agg);

	static int unitsPerTeam = Common
			.getConfigWithDefault("battle.units_per_team", 4);

}

class ExpectedWinFitness extends FitnessFunction {

	double expectedWinRate;

	ExpectedWinFitness() {
		super("Expected Win");
	}

	double eval(BattleResultAggregator agg) {
		double winRate = agg.getWinRate();
		if (winRate > 1.0 || winRate < 0.0) {
			logger.error("win rate out of range: {}", winRate);
		}
		return Math.abs(winRate - expectedWinRate);
	}
}

class DamageFractionFitness extends FitnessFunction {

	double[] damageFractionPenalties;

	DamageFractionFitness() {
		super("Expected Damage Fraction");
	}

	double eval(BattleResultAggregator agg) {
		return Common.scalarProduct(agg.getAttackDistribution(),
				damageFractionPenalties);
	}
}