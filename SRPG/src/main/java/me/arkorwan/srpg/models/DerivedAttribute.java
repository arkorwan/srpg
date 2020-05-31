package me.arkorwan.srpg.models;

import me.arkorwan.srpg.models.calculation.Functions;
import me.arkorwan.srpg.models.calculation.NormalizedFunction;

/**
 * A DerivedAttribute is a function of one or more BasicAttributes.
 * 
 * @author arkorwan
 *
 */
public class DerivedAttribute implements Attribute {

	private String name;
	private NormalizedFunction func;
	private int min, max;
	//private double factor = 1.0;

	// for serializer
	@SuppressWarnings("unused")
	private DerivedAttribute() {

	}

	public DerivedAttribute(String name, NormalizedFunction func, int min,
			int max) {
		this.name = name;
		this.func = func;
		this.min = min;
		this.max = max;
	}

	@Override
	public int getValue() {
		return Functions.convertFromNormalized(func.eval(), min, max);
	}

	@Override
	public int getMinValue() {
		return min;
	}

	@Override
	public int getMaxValue() {
		return max;
	}

	@Override
	public String getName() {
		return name;
	}


}
