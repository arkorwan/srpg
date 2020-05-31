package me.arkorwan.srpg.models.equipment;

import java.util.HashMap;
import java.util.Map;

import me.arkorwan.srpg.models.battlesystem.BattleSystem;

public class ConcreteArmour extends ConcreteEquipment implements Armour {

	public static enum ArmourType {

		//@formatter:off
		Plate(5.0),
		Hide(2.5),
		Robe(1.0);
		//@formatter:on

		double volume;

		ArmourType(double volume) {
			this.volume = volume;
		}
	}

	ArmourType type;
	Map<AttackType, Double> multipliers = new HashMap<>();

	double multiplierAgainstMelee;
	double multiplierAgainstRange;
	double multiplierAgainstMagic;

	// for serializer
	@SuppressWarnings("unused")
	private ConcreteArmour() {
		super();
	}

	public ConcreteArmour(BattleSystem sys, ArmourType type, String name,
			double s, double p, double d, double c, double e) {
		super(sys, String.format("%s of %s", type.name(), name), s, p, d, c, e);
		this.type = type;
		multipliers.put(AttackType.Melee,
				sys.meleeDefenseMultipliers.get(type).value);
		multipliers.put(AttackType.Range,
				sys.rangeDefenseMultipliers.get(type).value);
		multipliers.put(AttackType.Magic,
				sys.magicDefenseMultipliers.get(type).value);
	}

	public ConcreteArmour(BattleSystem sys, ArmourType type, String name,
			Map<MaterialAttribute.Type, Double> attributes) {
		this(sys, type, name, attributes.get(MaterialAttribute.Type.Strength),
				attributes.get(MaterialAttribute.Type.Plasticity),
				attributes.get(MaterialAttribute.Type.Density),
				attributes.get(MaterialAttribute.Type.Craftsmanship),
				attributes.get(MaterialAttribute.Type.Enchantment));
	}

	@Override
	public EqType getEquipmentType() {
		return EqType.Armour;
	}

	@Override
	public int defensePower() {
		return physicalEnhancement.getValue();
	}

	@Override
	public double volume() {
		return type.volume;
	}

	@Override
	protected void initPhysicalEnhancement(BattleSystem sys) {
		physicalEnhancement = sys.armourPhysEnh.createAttribute(
				materials.get(MaterialAttribute.Type.Plasticity),
				materials.get(MaterialAttribute.Type.Craftsmanship),
				"Physical Enhancement", 1);
	}

	@Override
	public String getInformation() {
		return String.format("%s %dx DEF", super.getInformation(),
				defensePower());
	}

	public ArmourType getArmourType() {
		return type;
	}

	@Override
	public double defenseMultiplier(AttackType against) {
		return multipliers.get(against);
	}
}
