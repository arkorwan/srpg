package me.arkorwan.srpg.models;

import me.arkorwan.srpg.models.calculation.Functions;
import me.arkorwan.srpg.models.calculation.NormalizedFunction;

public abstract class ModifiableAttribute
		implements Attribute, NormalizedFunction {

	private double value;
	private int min;
	private int max;
	private double xMinNormal;
	private double normalRange;
	private int modifier = 0;

	// for serialization
	protected ModifiableAttribute() {

	}

	protected ModifiableAttribute(double x, int yMin, int yMax, int yMinNormal,
			int yMaxNormal) {
		this(x, yMin, yMax, (yMinNormal - yMin) * 1.0 / (yMax - yMin),
				(yMaxNormal - yMin) * 1.0 / (yMax - yMin));
	}

	protected ModifiableAttribute(double x, int yMin, int yMax,
			double xMinNormal, double xMaxNormal) {
		this.normalRange = xMaxNormal - xMinNormal;
		// scale x to [xminnormal, xmaxnormal]
		this.value = x * (normalRange) + xMinNormal;
		this.min = yMin;
		this.max = yMax;
		this.xMinNormal = xMinNormal;
	}

	@Override
	public double eval() {
		return value;
	}

	public double unscaledValue() {
		return (value - xMinNormal) / normalRange;
	}

	@Override
	public int getValue() {
		int v = Functions.convertFromNormalized(value, min, max) + modifier;
		if (v > max) {
			return max;
		} else if (v < min) {
			return min;
		} else {
			return v;
		}
	}

	@Override
	public int getMinValue() {
		return min;
	}

	@Override
	public int getMaxValue() {
		return max;
	}

	public void adjustModifier(int adj) {
		this.modifier += adj;
	}

}
