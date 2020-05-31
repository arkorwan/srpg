package me.arkorwan.srpg.models;

/**
 * An attribute captures a certain aspect of a Unit or an Equipment by
 * describing the name, minimum, and maximum values.
 * 
 * @author arkorwan
 *
 */
public interface Attribute {

	public interface AttrType {
	}

	String getName();

	int getValue();

	int getMinValue();

	int getMaxValue();
}
