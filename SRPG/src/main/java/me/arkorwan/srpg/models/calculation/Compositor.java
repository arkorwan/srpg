package me.arkorwan.srpg.models.calculation;

import java.util.function.Function;

/**
 * A NormalizedFunction composed of two functions.
 * 
 * @author arkorwan
 *
 */
public class Compositor implements NormalizedFunction {

	public static class Builder {

		private Function<Double, SerializableOperator.Binary> func;
		private double param = Double.NaN;

		private Builder(Function<Double, SerializableOperator.Binary> func) {
			this.func = func;
		};

		public Builder withParameter(double param) {
			this.param = param;
			return this;
		}

		public Compositor build() {
			return new Compositor(func.apply(param));
		}

	}

	private SerializableOperator.Binary func;
	private NormalizedFunction left, right;

	public static Builder prepare(
			Function<Double, SerializableOperator.Binary> f) {
		return new Builder(f);
	}

	private Compositor(SerializableOperator.Binary func) {
		this.func = func;
	}

	public double apply(double x, double y) {
		return func.applyAsDouble(x, y);
	}

	public void setOperands(NormalizedFunction left, NormalizedFunction right) {
		this.left = left;
		this.right = right;
	}

	@Override
	public double eval() {
		return apply(left.eval(), right.eval());
	}

}
