package me.arkorwan.srpg.models.equipment;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import me.arkorwan.srpg.models.BasicAttribute.Type;
import me.arkorwan.srpg.models.DerivedAttribute;
import me.arkorwan.srpg.models.Elemental;
import me.arkorwan.srpg.models.battlesystem.BattleSystem;

/**
 * Default implementation of Equipment
 * 
 * @author arkorwan
 *
 */
public abstract class ConcreteEquipment implements Equipment {

	protected String name;

	protected Map<MaterialAttribute.Type, MaterialAttribute> materials = new HashMap<>();
	protected Elemental elemental = Elemental.None;

	protected DerivedAttribute physicalEnhancement;
	protected DerivedAttribute magicalEnhancement;

	// for serializer
	protected ConcreteEquipment() {

	}

	protected ConcreteEquipment(BattleSystem sys, String name, double s,
			double p, double d, double c, double e) {
		this.name = name;
		int max = sys.maxMaterialAttribute.value;

		materials.put(MaterialAttribute.Type.Strength,
				new MaterialAttribute(MaterialAttribute.Type.Strength, s, max));
		materials.put(MaterialAttribute.Type.Plasticity, new MaterialAttribute(
				MaterialAttribute.Type.Plasticity, p, max));
		materials.put(MaterialAttribute.Type.Density,
				new MaterialAttribute(MaterialAttribute.Type.Density, d, max));
		materials.put(MaterialAttribute.Type.Craftsmanship,
				new MaterialAttribute(MaterialAttribute.Type.Craftsmanship, c,
						max));
		materials.put(MaterialAttribute.Type.Enchantment, new MaterialAttribute(
				MaterialAttribute.Type.Enchantment, e, max));

		initPhysicalEnhancement(sys);
		initMagicalEnhancement(sys);

	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Optional<MaterialAttribute> getAttribute(
			MaterialAttribute.Type type) {
		MaterialAttribute attr = materials.get(type);
		return (attr == null) ? Optional.empty() : Optional.of(attr);
	}

	public void setElemental(Elemental e) {
		this.elemental = e;
	}

	@Override
	public Elemental getElemental() {
		return this.elemental;
	}

	@Override
	public Map<Type, Integer> effects() {
		return Collections.emptyMap();
	}

	public abstract double volume();

	@Override
	public double getWeight() {
		return volume()
				* materials.get(MaterialAttribute.Type.Density).getValue();
	}

	protected abstract void initPhysicalEnhancement(BattleSystem sys);

	protected void initMagicalEnhancement(BattleSystem sys) {
		magicalEnhancement = sys.eqMagicEnh.createAttribute(
				materials.get(MaterialAttribute.Type.Enchantment),
				"Magical Enhancement", 1);
	}

	@Override
	public double magicalPower() {
		return magicalEnhancement.getValue();
	}

	@Override
	public String getInformation() {
		String effects = effects().entrySet().stream()
				.filter(kv -> kv.getValue() != 0).map(kv -> {
					double n = kv.getValue();
					return String.format("%.2fx %s", n, kv.getKey());
				}).collect(Collectors.joining(","));
		if (effects.isEmpty()) {
			return String.format("%s (%s)", getName(), getElemental().name());
		} else {
			return String.format("%s (%s) (%s)", getName(), effects,
					getElemental().name());
		}
	}
}
