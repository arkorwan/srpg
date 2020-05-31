package me.arkorwan.srpg.models.equipment;

import java.util.Map;

import me.arkorwan.srpg.models.battlesystem.BattleSystem;

public class ConcreteWeapon extends ConcreteEquipment implements Weapon {

	public static enum WeaponType {

		//@formatter:off
		Sword(1, 1,  2,  1, 0.1,  true,  AttackType.Melee, AttackType.Melee),
		Lance(1, 2,  3,  2, 0.4,  true,  AttackType.Melee, AttackType.Melee), 
		Bow  (2, 5, -1, -1, 0.25, false, AttackType.Range, AttackType.Range),
		Staff(1, 1,  2,  1, 0.2,  true,  AttackType.Magic, AttackType.Melee);
		//@formatter:on

		int minRange, maxRange, lowerRange, higherRange;
		double volume;
		boolean isDirect;
		AttackType naturalType, physicalType;

		WeaponType(int minRange, int maxRange, int lowerRange, int higherRange,
				double volume, boolean isDirect, AttackType naturalType,
				AttackType physicalType) {
			this.minRange = minRange;
			this.maxRange = maxRange;
			this.lowerRange = lowerRange;
			this.higherRange = higherRange;
			this.volume = volume;
			this.isDirect = isDirect;
			this.naturalType = naturalType;
			this.physicalType = physicalType;
		}

		public AttackType naturalAttackingType() {
			return naturalType;
		}

		public AttackType physicalAttackingType() {
			return physicalType;
		}
	}

	WeaponType type;
	double atkMultiplier;

	// for serializer
	@SuppressWarnings("unused")
	private ConcreteWeapon() {
		super();
	}

	public ConcreteWeapon(BattleSystem sys, WeaponType type, String name,
			double s, double p, double d, double c, double e) {
		super(sys, String.format("%s of %s", type.name(), name), s, p, d, c, e);
		this.type = type;
		atkMultiplier = sys.weaponAttackMultipliers.get(type).value;
	}

	public ConcreteWeapon(BattleSystem sys, WeaponType type, String name,
			Map<MaterialAttribute.Type, Double> attributes) {
		this(sys, type, name, attributes.get(MaterialAttribute.Type.Strength),
				attributes.get(MaterialAttribute.Type.Plasticity),
				attributes.get(MaterialAttribute.Type.Density),
				attributes.get(MaterialAttribute.Type.Craftsmanship),
				attributes.get(MaterialAttribute.Type.Enchantment));
	}

	@Override
	public double attackPower() {
		return physicalEnhancement.getValue() * atkMultiplier;
	}

	@Override
	public int attackMinRange() {
		return type.minRange;
	}

	@Override
	public int attackMaxRange() {
		return type.maxRange;
	}

	@Override
	public int attackLowerRange() {
		return type.lowerRange;
	}

	@Override
	public int attackHigherRange() {
		return type.higherRange;
	}

	@Override
	public boolean isRangeDirect() {
		return type.isDirect;
	}

	@Override
	public EqType getEquipmentType() {
		return EqType.Weapon;
	}

	@Override
	public double volume() {
		return type.volume;
	}

	@Override
	protected void initPhysicalEnhancement(BattleSystem sys) {
		physicalEnhancement = sys.armourPhysEnh.createAttribute(
				materials.get(MaterialAttribute.Type.Strength),
				materials.get(MaterialAttribute.Type.Craftsmanship),
				"Physical Enhancement", 1);
	}

	@Override
	public String getInformation() {
		return String.format("%s %.2fx ATK", super.getInformation(),
				attackPower());
	}

	@Override
	public AttackType naturalAttackingType() {
		return type.naturalAttackingType();
	}

	@Override
	public AttackType physicalAttackingType() {
		return type.physicalAttackingType();
	}

}
