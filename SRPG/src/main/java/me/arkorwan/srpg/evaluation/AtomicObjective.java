package me.arkorwan.srpg.evaluation;

import java.util.Map;

public class AtomicObjective extends Objective {

	String generatorRef;
	FitnessFunction function;

	@Override
	public double eval(Map<String, BattleResultAggregator> resultMap,
			EvaluationCollector t) {
		double result = function.eval(resultMap.get(generatorRef));
		t.onObjectiveTraversed(this, result);
		return result;
	}

	@Override
	public void traverse(Traverser t) {
		t.onObjectiveTraversed(this);
	}

}
