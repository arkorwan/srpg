package me.arkorwan.srpg.evaluation;

import java.util.function.DoubleBinaryOperator;

public enum FitnessCombinator {

	//@formatter:off
	Max(Math::max, 0, (x, n) -> x), 
	Sum((x, y) -> x + y, 0, (x, n) -> x / n), 
	Product((x, y) -> x * y, 1, (x, n) -> Math.pow(x, 1 / n)), 
	NormalizedProduct((x, y) -> x  * (y + 1d), 1, (x, n) -> Math.pow(x, 1 / n));
	//@formatter:on

	DoubleBinaryOperator op;
	double identity;
	DoubleBinaryOperator agg;

	FitnessCombinator(DoubleBinaryOperator op, double identity,
			DoubleBinaryOperator agg) {
		this.op = op;
		this.identity = identity;
		this.agg = agg;
	}

}