package me.arkorwan.srpg.models.calculation;

import java.util.ArrayList;
import java.util.List;

/**
 * A Shaper function is a NormalizedFunction with constraints f(0.0) = 0.0 and
 * f(1.0) = 1.0
 * 
 * @author arkorwan
 *
 */

public class Shaper extends AbstractShaper {

	public static class Builder {

		private List<Double> params = new ArrayList<>();
		private List<SerializableOperator.Binary> funcs = new ArrayList<>();

		private Builder() {
		};

		public Builder addParam(SerializableOperator.Binary func,
				double param) {
			params.add(param);
			funcs.add(func);
			return this;
		}

		public Builder addParam(SerializableOperator.Binary func) {
			return addParam(func, Double.NaN);
		}

		public void setParameter(int index, double value) {
			params.set(index, value);
		}

		public Shaper build(SerializableOperator.Unary f) {

			for (int i = 0; i < params.size(); i++) {
				SerializableOperator.Binary op = funcs.get(i);
				double param = params.get(i);
				SerializableOperator.Unary g = x -> op.applyAsDouble(param, x);
				f = g.compose(f);
			}

			return new Shaper(f);
		}

		public Shaper build() {
			return build(x -> x);
		}

	}

	private SerializableOperator.Unary func;

	static final double e = 0.00001;

	public static Builder prepare() {
		return new Builder();
	}

	private Shaper(SerializableOperator.Unary func) {
		if (func == null) {
			throw new IllegalStateException("function cannot be null");
		}
		if (Math.abs(func.applyAsDouble(0.0)) > e
				|| Math.abs(func.applyAsDouble(1.0) - 1.0) > e) {
			throw new IllegalArgumentException(
					"function needs to have 0.0 and 1.0 as fixpoints.");
		}
		this.func = func;
	}

	@Override
	public double apply(double x) {
		return func.applyAsDouble(x);
	}
}
