package me.arkorwan.srpg.generators;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import me.arkorwan.srpg.generators.constraints.AttributeConstraint;
import me.arkorwan.srpg.generators.constraints.QuotaConstraint;
import me.arkorwan.srpg.generators.constraints.QuotaConstraint.Quota;
import me.arkorwan.srpg.models.Elemental;
import me.arkorwan.srpg.models.battlesystem.BattleSystem;
import me.arkorwan.srpg.models.equipment.Equipment;
import me.arkorwan.srpg.models.equipment.MaterialAttribute;
import me.arkorwan.utils.NameGenerator;

public abstract class EquipmentGenerator<T extends Equipment>
		extends Generator<T> {

	static NameGenerator nameGenerator = new NameGenerator(new Random());

	double averageAttributeValues = 0.5;
	double deviation = 0.3;
	Set<AttributeConstraint<MaterialAttribute.Type>> attributeConstraints;
	QuotaConstraint<Elemental> elementalConstraint;
	transient Quota<Elemental> qe;
	transient boolean peekMode = false;

	public EquipmentGenerator(BattleSystem sys, Random r) {
		super(sys, r);
	}

	public void setAttributeConstraints(
			Set<AttributeConstraint<MaterialAttribute.Type>> attributeConstraints) {
		this.attributeConstraints = attributeConstraints;
	}

	public void setElementalConstraint(QuotaConstraint<Elemental> el) {
		this.elementalConstraint = el;
	}

	public void refreshQuotas() {
		qe = QuotaConstraint.createFreshQuota(elementalConstraint);
	}

	public void setPeekMode(boolean b) {
		this.peekMode = b;
	}
	
	@Override
	public T generate() {

		Set<AttributeConstraint<MaterialAttribute.Type>> attrConstraints = attributeConstraints == null
				? Collections.emptySet() : attributeConstraints;

		AttributesDistributor<MaterialAttribute.Type> distributor = new AttributesDistributor<>();

		Map<MaterialAttribute.Type, Double> attributes = distributor.distribute(
				averageAttributeValues, deviation,
				Arrays.asList(MaterialAttribute.Type.values()), attrConstraints,
				rand);
		Elemental element = qe.getRandomItem(rand, Elemental.blackElementals(), peekMode);

		return createEquipment(nameGenerator.getName(), attributes, element);
	}

	protected abstract T createEquipment(String name,
			Map<MaterialAttribute.Type, Double> attributes, Elemental element);

	@Override
	protected Generator<?>[] downstream() {
		return new Generator[0];
	}
}
