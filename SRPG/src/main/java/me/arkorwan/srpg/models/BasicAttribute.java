package me.arkorwan.srpg.models;

/**
 * Basic unit attributes.
 * 
 * @author arkorwan
 *
 */
public class BasicAttribute extends ModifiableAttribute {

	public static enum Type implements AttrType {
		STR("Strength"), CON("Constitution"), INT("Intelligence"), WIS(
				"Wisdom"), DEX("Dexterity");

		String name;

		Type(String name) {
			this.name = name;
		}
	}

	static final int MIN = 1;
	
	int max;
	int adjustable;
	
	private Type type;

	// for serializer
	@SuppressWarnings("unused")
	private BasicAttribute() {
		super();
	}

	public BasicAttribute(Type type, double normalizedValue, int max, int adjustable) {
		super(normalizedValue, MIN, max, MIN + adjustable, max - adjustable);
		this.type = type;
		this.max = max;
		this.adjustable = adjustable;
	}

	@Override
	public String getName() {
		return type.name;
	}

}
