package me.arkorwan.srpg.evaluation;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BattleResultAggregator {

	static final Logger logger = LoggerFactory
			.getLogger(BattleResultAggregator.class);

	int sumTurn = 0;
	int sumDamageDiff = 0;
	int sumDeathDiff = 0;
	double sumWinning = 0;
	private int[] damageCounter = new int[BattleResultCollector.BINS];
	int sumEffectiveAttacks = 0;
	int stalemates = 0;

	int games = 0;
	long time;

	public void addResult(BattleResultCollector collector) {
		sumWinning += collector.winning;
		sumTurn += collector.turns;
		sumDamageDiff += collector.damageDiff;
		sumDeathDiff += collector.deadDiff;
		for (int i = 0; i < BattleResultCollector.BINS; i++) {
			int d = collector.damageCounter[i];
			sumEffectiveAttacks += d;
			damageCounter[i] += d;
		}
		if (collector.stalemate) {
			stalemates += 1;
		}
		games += 1;
		logger.trace("{}: {}", sumWinning, games);

	}

	public double getWinRate() {
		return sumWinning * 1.0 / games;
	}

	public double getAverageGameLength() {
		return sumTurn * 1.0 / games;
	}

	public double getAverageDeathDifference() {
		return sumDeathDiff * 1.0 / games;
	}

	public double getAverageDamageDifference() {
		return sumDamageDiff * 1.0 / games;
	}

	public double[] getAttackDistribution() {
		return Arrays.stream(damageCounter)
				.mapToDouble(i -> i * 1.0 / sumEffectiveAttacks).toArray();
	}

	public double getStalemateRate() {
		return stalemates * 1.0 / games;
	}
}
