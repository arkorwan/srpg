package me.arkorwan.srpg.models.equipment;

import me.arkorwan.srpg.models.Attribute;
import me.arkorwan.srpg.models.calculation.Functions;
import me.arkorwan.srpg.models.calculation.NormalizedFunction;

public class MaterialAttribute implements Attribute, NormalizedFunction {

	public static enum Type implements AttrType {
		Strength, Density, Plasticity, Craftsmanship, Enchantment;
	}

	public static final int MIN = 1;
	Type type;
	double value;
	int max;

	// for serializer
	@SuppressWarnings("unused")
	private MaterialAttribute() {

	}

	public MaterialAttribute(Type type, double normalizedValue, int max) {
		this.type = type;
		this.value = normalizedValue;
		this.max = max;
	}

	@Override
	public String getName() {
		return type.name();
	}

	@Override
	public int getValue() {
		return Functions.convertFromNormalized(value, MIN, max);
	}

	@Override
	public int getMinValue() {
		return MIN;
	}

	@Override
	public int getMaxValue() {
		return max;
	}

	@Override
	public double eval() {
		return value;
	}

}
