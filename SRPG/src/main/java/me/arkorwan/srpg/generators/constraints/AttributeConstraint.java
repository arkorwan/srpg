package me.arkorwan.srpg.generators.constraints;

import me.arkorwan.srpg.models.Attribute;

public class AttributeConstraint<T extends Attribute.AttrType>
		implements Constraint {

	T type;
	double min;
	double max;

	public AttributeConstraint(T type, double min, double max) {
		this.min = min;
		this.max = max;
		this.type = type;
	}

	public T getType() {
		return type;
	}
	
	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}

}
