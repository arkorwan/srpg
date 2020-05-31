package me.arkorwan.srpg.evaluation;

import java.util.Map;

public class CombinedObjective extends Objective {

	Objective[] subObjectives;
	String combinator;

	@Override
	public double eval(Map<String, BattleResultAggregator> resultMap,
			EvaluationCollector t) {

		FitnessCombinator cmb = FitnessCombinator.valueOf(combinator);

		double result = cmb.identity;
		int count = 0;
		for (Objective o : subObjectives) {
			double raw = o.eval(resultMap, t);
			for (int i = 0; i < o.weight; i++) {
				result = cmb.op.applyAsDouble(result, raw);
			}
			count += o.weight;
		}
		result = cmb.agg.applyAsDouble(result, count);
		t.onObjectiveTraversed(this, result);
		return result;
	}

	@Override
	public void traverse(Traverser t) {
		for (Objective o : subObjectives) {
			o.traverse(t);
		}
		t.onObjectiveTraversed(this);
	}

}
