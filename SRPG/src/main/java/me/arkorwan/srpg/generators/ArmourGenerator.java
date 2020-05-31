package me.arkorwan.srpg.generators;

import java.util.Map;
import java.util.Random;

import me.arkorwan.srpg.models.Elemental;
import me.arkorwan.srpg.models.battlesystem.BattleSystem;
import me.arkorwan.srpg.models.equipment.Armour;
import me.arkorwan.srpg.models.equipment.ConcreteArmour;
import me.arkorwan.srpg.models.equipment.ConcreteArmour.ArmourType;
import me.arkorwan.srpg.models.equipment.MaterialAttribute.Type;

public class ArmourGenerator extends EquipmentGenerator<Armour> {

	ArmourType armourType;

	public ArmourGenerator(BattleSystem sys, Random r) {
		super(sys, r);
	}

	@Override
	protected Armour createEquipment(String name, Map<Type, Double> attributes,
			Elemental element) {

		ConcreteArmour a = new ConcreteArmour(sys, armourType, name,
				attributes);
		a.setElemental(element);
		return a;
	}

}
