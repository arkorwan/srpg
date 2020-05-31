package me.arkorwan.srpg.evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jenetics.Genotype;
import org.jenetics.IntegerGene;

import me.arkorwan.srpg.evaluation.Evaluator.GeneratorRefCollector;
import me.arkorwan.srpg.generators.BattleGenerator;
import me.arkorwan.srpg.models.battlesystem.BattleSystem;
import me.arkorwan.utils.Common;

public class FitnessCalculator {

	EvaluationResultCollector collector = null;

	public void setCollector(EvaluationResultCollector collector) {
		this.collector = collector;
	}

	static class ValueCollector implements Objective.EvaluationCollector {

		List<Double> values = new ArrayList<>();

		@Override
		public void onObjectiveTraversed(Objective o, double result) {
			values.add(result);
		}
	};

	public double getFitness(Objective obj, Genotype<IntegerGene> instance,
			int randomSeed, int battles, Integer turnsLimit) {
		GeneratorRefCollector refCollector = new GeneratorRefCollector();
		obj.traverse(refCollector);
		Map<String, BattleResultAggregator> resultMap = new HashMap<>();
		for (String generatorRef : refCollector.refs) {

			BattleGenerator generator = Common.gsonDeserialize(
					"models/" + generatorRef, BattleGenerator.class);

			BattleSystem sys = new BattleSystem();
			sys.decode(instance);

			generator.setBattleSystem(sys);
			generator.setRandom(new Random(randomSeed));
			generator.setTeamSize(
					Common.getConfigWithDefault("battle.units_per_team", 4));
			BattleResultAggregator agg = new Evaluator().runBattle(generator,
					battles, turnsLimit);
			resultMap.put(generatorRef, agg);

		}

		ValueCollector vCollector = new ValueCollector();
		double result = obj.eval(resultMap, vCollector);
		if (collector != null) {
			collector.logIndividualFitness(vCollector.values);

		}
		return result;

	}

}
