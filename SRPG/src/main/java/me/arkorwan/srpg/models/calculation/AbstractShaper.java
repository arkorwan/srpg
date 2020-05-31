package me.arkorwan.srpg.models.calculation;

public abstract class AbstractShaper implements NormalizedFunction {

	protected NormalizedFunction inner;

	protected AbstractShaper() {

	}

	public void setInnerFunction(NormalizedFunction f) {
		this.inner = f;
	}

	@Override
	public double eval() {
		if (inner != null) {
			return apply(inner.eval());
		} else {
			return Double.NaN;
		}
	}

	public abstract double apply(double x);
}
