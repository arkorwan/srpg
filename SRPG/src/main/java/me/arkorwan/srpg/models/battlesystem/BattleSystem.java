package me.arkorwan.srpg.models.battlesystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jenetics.Chromosome;
import org.jenetics.Genotype;
import org.jenetics.IntegerChromosome;
import org.jenetics.IntegerGene;

import me.arkorwan.srpg.models.Unit;
import me.arkorwan.srpg.models.equipment.ConcreteArmour.ArmourType;
import me.arkorwan.srpg.models.equipment.ConcreteWeapon.WeaponType;

public class BattleSystem {

	public DoubleParameterFunctionConfig unitMoveRange = new DoubleParameterFunctionConfig(
			new IntConfig(2, Unit.MAX_CANVAS / 3));
	public SingleParameterFunctionConfig unitSpeed = new SingleParameterFunctionConfig();
	public SingleParameterFunctionConfig unitHP = new SingleParameterFunctionConfig();
	public DoubleParameterFunctionConfig unitMP = new DoubleParameterFunctionConfig();
	public DoubleParameterFunctionConfig unitPhysAttack = new DoubleParameterFunctionConfig();
	public DoubleParameterFunctionConfig unitMagicAttack = new DoubleParameterFunctionConfig();
	public DoubleParameterFunctionConfig unitPhysDefense = new DoubleParameterFunctionConfig();
	public DoubleParameterFunctionConfig unitMagicDefense = new DoubleParameterFunctionConfig();
	public DoubleParameterFunctionConfig unitEvasion = new DoubleParameterFunctionConfig();
	public DoubleParameterFunctionConfig unitRisktaking = new DoubleParameterFunctionConfig();

	public DoubleParameterFunctionConfig weaponPhysEnh = new DoubleParameterFunctionConfig(
			new IntConfig(1, 5));
	public DoubleParameterFunctionConfig armourPhysEnh = new DoubleParameterFunctionConfig(
			new IntConfig(1, 5));

	public IntConfig maxBasicAttribute = new IntConfig(10, 100);
	public DoubleConfig adjustableBasicAttribute = new DoubleConfig(0.01, 0.25);
	public IntConfig maxMaterialAttribute = new IntConfig(10, 100);

	public SingleParameterFunctionConfig eqMagicEnh = new SingleParameterFunctionConfig(
			new IntConfig(1, 5));

	public Map<WeaponType, DoubleConfig> weaponAttackMultipliers = new HashMap<>();
	public DoubleConfig equipmentCarryingRatio = new DoubleConfig(0.25, 4.0);

	public Map<ArmourType, DoubleConfig> meleeDefenseMultipliers = new HashMap<>();
	public Map<ArmourType, DoubleConfig> rangeDefenseMultipliers = new HashMap<>();
	public Map<ArmourType, DoubleConfig> magicDefenseMultipliers = new HashMap<>();

	public DoubleConfig elementalAdvantageMultiplier = new DoubleConfig(1.0,
			100.0);
	public DoubleConfig damageAtkMultiplier = new DoubleConfig(0.1, 10.0);
	public DoubleConfig damageAtkDegree = new DoubleConfig(0.1, 3.0);
	public DoubleConfig damageDiscountDegree = new DoubleConfig(0.1, 3.0);

	public BattleSystem() {
		for (WeaponType w : WeaponType.values()) {
			weaponAttackMultipliers.put(w, new DoubleConfig(1.0, 5.0));
		}
		for (ArmourType w : ArmourType.values()) {
			meleeDefenseMultipliers.put(w, new DoubleConfig(1.0, 5.0));
			rangeDefenseMultipliers.put(w, new DoubleConfig(1.0, 5.0));
			magicDefenseMultipliers.put(w, new DoubleConfig(1.0, 5.0));
		}

	}

	transient BattleSystemConfig[] configs;
	public transient String systemName;

	private BattleSystemConfig[] getConfigs() {
		if (configs == null) {
			configs = new BattleSystemConfig[] { unitMoveRange, unitSpeed,
					unitHP, unitMP, unitPhysAttack, unitMagicAttack,
					unitPhysDefense, unitMagicDefense, unitEvasion,
					unitRisktaking, weaponPhysEnh, armourPhysEnh,
					maxBasicAttribute, adjustableBasicAttribute,
					maxMaterialAttribute, eqMagicEnh, equipmentCarryingRatio,
					elementalAdvantageMultiplier, damageAtkMultiplier,
					damageAtkDegree, damageDiscountDegree };
		}
		return configs;
	}

	public Genotype<IntegerGene> encode() {
		return Genotype.of(getChromosomes());
	}

	public List<IntegerChromosome> getChromosomes() {
		List<IntegerChromosome> chromosomes = new ArrayList<>();
		for (BattleSystemConfig cfg : getConfigs()) {
			chromosomes.addAll(cfg.encode());
		}

		for (WeaponType w : WeaponType.values()) {
			chromosomes.addAll(weaponAttackMultipliers.get(w).encode());
		}
		for (ArmourType a : ArmourType.values()) {
			chromosomes.addAll(meleeDefenseMultipliers.get(a).encode());
			chromosomes.addAll(rangeDefenseMultipliers.get(a).encode());
			chromosomes.addAll(magicDefenseMultipliers.get(a).encode());
		}

		return chromosomes;
	}

	private void decodeConfig(BattleSystemConfig cfg,
			Iterator<Chromosome<IntegerGene>> it) {
		List<Chromosome<IntegerGene>> chr = new ArrayList<>();
		for (int i = 0; i < cfg.numberOfChromosomes(); i++) {
			chr.add(it.next());
		}
		cfg.decode(chr);
	}

	public void decode(Genotype<IntegerGene> data) {

		Iterator<Chromosome<IntegerGene>> it = data.iterator();
		for (BattleSystemConfig cfg : getConfigs()) {
			decodeConfig(cfg, it);
		}

		for (WeaponType w : WeaponType.values()) {
			decodeConfig(weaponAttackMultipliers.get(w), it);
		}
		for (ArmourType a : ArmourType.values()) {
			decodeConfig(meleeDefenseMultipliers.get(a), it);
			decodeConfig(rangeDefenseMultipliers.get(a), it);
			decodeConfig(magicDefenseMultipliers.get(a), it);
		}

	}

}
