package me.arkorwan.srpg.models;

import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.math.DoubleMath;

import me.arkorwan.srpg.models.battlesystem.BattleSystem;
import me.arkorwan.srpg.models.equipment.Armour;
import me.arkorwan.srpg.models.equipment.ConcreteArmour;
import me.arkorwan.srpg.models.equipment.ConcreteWeapon;
import me.arkorwan.srpg.models.equipment.EmptyEquipment;
import me.arkorwan.srpg.models.equipment.Equipment;
import me.arkorwan.srpg.models.equipment.Equipment.EqType;
import me.arkorwan.srpg.models.equipment.Weapon;

/**
 * A unit is a 'character' in the battle.
 * 
 * @author arkorwan
 *
 */
public class Unit {

	String name;

	Job job;

	Race race;

	Map<BasicAttribute.Type, BasicAttribute> basics = new HashMap<>();
	DerivedAttribute moveRange, speed, hp, mp, physAtk, physDef, magicAtk,
			magicDef, evasion, courage;
	int currentHP, currentMP;
	double equipmentCarryingRatio;

	Map<EqType, Equipment> equipments = new HashMap<>();

	// for serializer
	@SuppressWarnings("unused")
	private Unit() {

	}

	public Unit(BattleSystem sys, double s, double i, double w, double d,
			double c) {

		int maxAttr = sys.maxBasicAttribute.value;
		int adjAttr = DoubleMath.roundToInt(
				sys.adjustableBasicAttribute.value * maxAttr,
				RoundingMode.HALF_EVEN);

		basics.put(BasicAttribute.Type.STR, new BasicAttribute(
				BasicAttribute.Type.STR, s, maxAttr, adjAttr));
		basics.put(BasicAttribute.Type.INT, new BasicAttribute(
				BasicAttribute.Type.INT, i, maxAttr, adjAttr));
		basics.put(BasicAttribute.Type.WIS, new BasicAttribute(
				BasicAttribute.Type.WIS, w, maxAttr, adjAttr));
		basics.put(BasicAttribute.Type.DEX, new BasicAttribute(
				BasicAttribute.Type.DEX, d, maxAttr, adjAttr));
		basics.put(BasicAttribute.Type.CON, new BasicAttribute(
				BasicAttribute.Type.CON, c, maxAttr, adjAttr));

		setJob(Job.Warrior);
		setRace(Race.Human);

		for (EqType eqt : EqType.values()) {
			equipments.put(eqt, EmptyEquipment.Instance);
		}

		moveRange = sys.unitMoveRange.createAttribute(dexterity(), strength(),
				"Move Range", 1);
		speed = sys.unitSpeed.createAttribute(dexterity(), "Speed", 1);
		hp = sys.unitHP.createAttribute(constitution(), "HP", 50);
		mp = sys.unitMP.createAttribute(wisdom(), intelligence(), "MP", 0);
		physAtk = sys.unitPhysAttack.createAttribute(strength(), constitution(),
				"Physical ATK", 1);
		physDef = sys.unitPhysDefense.createAttribute(constitution(),
				strength(), "Physical DEF", 1);
		magicAtk = sys.unitMagicAttack.createAttribute(intelligence(),
				strength(), "Magical ATK", 1);
		magicDef = sys.unitMagicDefense.createAttribute(wisdom(),
				constitution(), "Magical DEF", 1);
		evasion = sys.unitEvasion.createAttribute(intelligence(), dexterity(),
				"Evasion", 1);
		courage = sys.unitRisktaking.createAttribute(wisdom(), dexterity(),
				"Courage", 1);

		currentHP = hp.getValue();
		currentMP = mp.getValue();
		equipmentCarryingRatio = sys.equipmentCarryingRatio.value;

	}

	public Unit(BattleSystem sys, Map<BasicAttribute.Type, Double> attributes) {
		this(sys, attributes.get(BasicAttribute.Type.STR),
				attributes.get(BasicAttribute.Type.INT),
				attributes.get(BasicAttribute.Type.WIS),
				attributes.get(BasicAttribute.Type.DEX),
				attributes.get(BasicAttribute.Type.CON));
	}
	
	public void setName(String name) {
		Objects.requireNonNull(name);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setJob(Job job) {
		this.job = job;
	}

	public Job getJob() {
		return job;
	}

	public void setRace(Race race) {
		Objects.requireNonNull(race);
		if (this.race != null) {
			adjustBasicStats(this.race, false);
		}
		this.race = race;
		adjustBasicStats(this.race, true);

	}

	public Race getRace() {
		return race;
	}

	private void adjustBasicStats(StatusEffector effector, boolean added) {
		int multiplier = added ? 1 : -1;
		for (Map.Entry<BasicAttribute.Type, Integer> kv : effector.effects()
				.entrySet()) {
			basics.get(kv.getKey()).adjustModifier(multiplier * kv.getValue());
		}
	}

	public static final int MAX_CANVAS = 16;

	public BasicAttribute strength() {
		return basics.get(BasicAttribute.Type.STR);
	}

	public BasicAttribute intelligence() {
		return basics.get(BasicAttribute.Type.INT);
	}

	public BasicAttribute wisdom() {
		return basics.get(BasicAttribute.Type.WIS);
	}

	public BasicAttribute dexterity() {
		return basics.get(BasicAttribute.Type.DEX);
	}

	public BasicAttribute constitution() {
		return basics.get(BasicAttribute.Type.CON);
	}

	public BasicAttribute getBasicAttribute(BasicAttribute.Type type) {
		return basics.get(type);
	}

	public Attribute getHP() {
		return hp;
	}

	public int getCurrentHP() {
		return currentHP;
	}

	public int adjustCurrentHP(int delta) {
		int t = currentHP + delta;
		if (t > hp.getValue()) {
			t = hp.getValue();
		} else if (t < 0) {
			t = 0;
		}
		int trueDelta = t - currentHP;
		currentHP = t;
		return trueDelta;
	}

	public Attribute getMP() {
		return mp;
	}

	public int getCurrentMP() {
		return currentMP;
	}

	public int adjustCurrentMP(int delta) {
		int t = currentMP + delta;
		if (t > mp.getValue()) {
			t = mp.getValue();
		} else if (t < 0) {
			t = 0;
		}
		int trueDelta = t - currentMP;
		currentMP = t;
		return trueDelta;
	}

	public Attribute getPhysicalAttack() {
		return physAtk;
	}

	public Attribute getPhysicalDefense() {
		return physDef;
	}

	public Attribute getMagicalAttack() {
		return magicAtk;
	}

	public Attribute getMagicalDefense() {
		return magicDef;
	}

	public Attribute getEvasion() {
		return evasion;
	}

	public Attribute getCourage() {
		return courage;
	}

	public Attribute getMoveRange() {
		return moveRange;
	}

	public Attribute getSpeed() {
		return speed;
	}

	public double getWeightThreshold() {
		return equipmentCarryingRatio * race.weight;
	}

	public double getSpeedModifier() {
		double eqWeight = equipments.values().stream()
				.mapToDouble(eq -> eq.getWeight()).sum();
		double threshold = getWeightThreshold();

		if (eqWeight > threshold) {
			// (0.5, 1.0]
			return (threshold + eqWeight) / (2.0 * eqWeight);
		} else {
			return 1.0;
		}
	}

	public int getActualSpeed() {
		return (int) (getSpeed().getValue() * getSpeedModifier());
	}

	public void equip(Equipment equipment) {

		if (equipment.getEquipmentType() == EqType.Unclassifiable) {
			return;
		}

		if (equipment instanceof ConcreteWeapon) {
			if (((ConcreteWeapon) equipment)
					.naturalAttackingType() != job.atkType) {
				return;
			}
		} else if (equipment instanceof ConcreteArmour) {
			if (((ConcreteArmour) equipment)
					.getArmourType() != job.armourType) {
				return;
			}
		}

		if (equipments.containsKey(equipment.getEquipmentType())) {
			unequip(equipment.getEquipmentType());
		}

		adjustBasicStats(equipment, true);
		equipments.put(equipment.getEquipmentType(), equipment);

	}

	public void unequip(EqType eqType) {
		Objects.requireNonNull(eqType);
		Equipment eq = equipments.get(eqType);
		if (eq.getEquipmentType() == EqType.Unclassifiable) {
			return;
		}

		adjustBasicStats(eq, false);
		equipments.put(eqType, EmptyEquipment.Instance);
	}

	public Weapon getWeapon() {
		return (Weapon) equipments.get(EqType.Weapon);
	}

	public Armour getArmour() {
		return (Armour) equipments.get(EqType.Armour);
	}

	public List<MagicSpell> getCurrentBlackMagicSpells() {

		if (getJob() != Job.Mage) {
			return Collections.emptyList();
		}

		return MagicSpell.allBlackSpells().stream()
				.filter(sp -> sp.getElemental() == getWeapon().getElemental()
						&& sp.cost() <= getCurrentMP())
				.collect(Collectors.toList());

	}

	public List<MagicSpell> getCurrentWhiteMagicSpells() {

		if (getJob() != Job.Cleric) {
			return Collections.emptyList();
		}

		return MagicSpell.allWhiteSpells().stream()
				.filter(sp -> sp.cost() <= getCurrentMP())
				.collect(Collectors.toList());

	}

}
