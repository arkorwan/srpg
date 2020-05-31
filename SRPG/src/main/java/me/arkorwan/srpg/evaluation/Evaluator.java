package me.arkorwan.srpg.evaluation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.beust.jcommander.Parameter;

import me.arkorwan.srpg.ai.SmartPlayer;
import me.arkorwan.srpg.controller.BattleController;
import me.arkorwan.srpg.generators.BattleGenerator;
import me.arkorwan.srpg.models.Battle;
import me.arkorwan.srpg.models.battlesystem.BattleSystem;
import me.arkorwan.utils.Common;

public class Evaluator {

	public static class EvaluationCommand {
		@Parameter(names = "-system", description = "battle system to be evaluated")
		private String battleSystemPath;

		@Parameter(names = "-objective", description = "evaluation objective")
		private String objectivePath;

		@Parameter(names = "-seed", description = "random seed")
		private int randomSeed = 0;

		@Parameter(names = "-count", description = "battle count")
		private int count = 10;

		@Parameter(names = "-turnsLimit", description = "force the game to end in stalemate after this many turns")
		private Integer turnsLimit;

		public void execute() {

			BattleSystem sys = Common.gsonDeserialize(battleSystemPath,
					BattleSystem.class);
			Objective obj = Objective.deserialize(objectivePath);

			run(obj, sys, randomSeed, count, turnsLimit);
		}
	}

	static class GeneratorRefCollector implements Objective.Traverser {

		Set<String> refs = new HashSet<>();

		@Override
		public void onObjectiveTraversed(Objective o) {
			if (o instanceof AtomicObjective) {
				refs.add(((AtomicObjective) o).generatorRef);
			}

		}

	};

	static class FitnessCollector implements Objective.EvaluationCollector {

		@Override
		public void onObjectiveTraversed(Objective o, double result) {
			System.out.println("Objective " + o.name + ":" + result);
		}

	}

	public static void run(Objective obj, BattleSystem sys, int randomSeed,
			int games, Integer turnsLimit) {

		GeneratorRefCollector refCollector = new GeneratorRefCollector();

		obj.traverse(refCollector);
		Map<String, BattleResultAggregator> resultMap = new HashMap<>();
		for (String generatorRef : refCollector.refs) {

			BattleGenerator generator = Common.gsonDeserialize(
					"models/" + generatorRef, BattleGenerator.class);

			generator.setBattleSystem(sys);
			generator.setRandom(new Random(randomSeed));
			generator.setTeamSize(
					Common.getConfigWithDefault("battle.units_per_team", 4));

			BattleResultAggregator agg = new Evaluator().runBattle(generator,
					games, turnsLimit);
			resultMap.put(generatorRef, agg);

			System.out.println("=======================");
			System.out.println(generatorRef);
			System.out.println("runtime(ms) = " + agg.time);

			System.out.println("win pctg. = " + agg.getWinRate());
			System.out.println(
					"Avg. death diff = " + agg.getAverageDeathDifference());
			System.out.println(
					"Avg. damage diff = " + agg.getAverageDamageDifference());
			System.out.println("Avg. turns = " + agg.getAverageGameLength());
			System.out.println("damage percentage distribution = "
					+ Arrays.toString(agg.getAttackDistribution()));
			System.out.println("stalemate pctg. = " + agg.getStalemateRate());

		}

		obj.eval(resultMap, new FitnessCollector());

	}

	public BattleResultAggregator runBattle(BattleGenerator bg, int games,
			Integer turnsLimit) {
		long t = System.currentTimeMillis();

		BattleResultAggregator agg = new BattleResultAggregator();
		for (int i = 0; i < games; i++) {
			Battle b = bg.generate();
			agg.addResult(duel(b, turnsLimit));
		}
		agg.time = System.currentTimeMillis() - t;

		return agg;
	}

	BattleResultCollector duel(Battle battle, Integer turnsLimit) {
		BattleController controller = new BattleController(battle,
				SmartPlayer.getFactory(), SmartPlayer.getFactory());
		if (turnsLimit == null) {
			controller.setNoMaxTurns();
		} else {
			controller.setMaxTurns(turnsLimit);
		}
		BattleResultCollector collector = new BattleResultCollector(controller);
		controller.run(); // execute in this thread!
		return collector;
	}

}
