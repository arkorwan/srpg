package me.arkorwan.srpg.models.calculation;

import java.io.Serializable;
import java.util.function.DoubleBinaryOperator;

public interface SerializableOperator {

	/*
	 * public static interface Unary extends DoubleUnaryOperator, Serializable {
	 * 
	 * default Unary compose(Unary before) { return v ->
	 * applyAsDouble(before.applyAsDouble(v)); }
	 * 
	 * }
	 */
	public static interface Binary extends DoubleBinaryOperator, Serializable {

	}

}
