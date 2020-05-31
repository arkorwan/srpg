package me.arkorwan.srpg.models;

import java.util.ArrayList;
import java.util.List;

/**
 * A magic spell. Can be attacking or healing spell.
 * 
 * @author arkorwan
 *
 */
public class MagicSpell {

	static List<MagicSpell> blackSpells = new ArrayList<>();
	static List<MagicSpell> whiteSpells = new ArrayList<>();

	static {
		for (Elemental e : Elemental.values()) {
			if (e.isBlackMagic()) {
				blackSpells.add(new MagicSpell(e.name() + " 1", 5, 1,
						SpellType.Attack, e));
				blackSpells.add(new MagicSpell(e.name() + " 2", 8, 2,
						SpellType.Attack, e));
				blackSpells.add(new MagicSpell(e.name() + " 3", 10, 3,
						SpellType.Attack, e));
			}
		}

		whiteSpells.add(new MagicSpell("Cure 1", 5, 1, SpellType.Healing,
				Elemental.Holy));
		whiteSpells.add(new MagicSpell("Cure 2", 8, 2, SpellType.Healing,
				Elemental.Holy));
		whiteSpells.add(new MagicSpell("Cure 3", 10, 3, SpellType.Healing,
				Elemental.Holy));
	}

	public static List<MagicSpell> allBlackSpells() {
		return blackSpells;
	}

	public static List<MagicSpell> allWhiteSpells() {
		return whiteSpells;
	}

	public static enum SpellType {
		Attack, Healing;
	}

	String name;
	int cost;
	int power;
	SpellType type;
	Elemental elemental;

	private MagicSpell(String name, int cost, int power, SpellType type,
			Elemental elemental) {
		this.name = name;
		this.cost = cost;
		this.power = power;
		this.type = type;
		this.elemental = elemental;
	}

	public String getName() {
		return name;
	}

	public int cost() {
		return cost;
	}

	public int power() {
		return power;
	}

	public Elemental getElemental() {
		return elemental;
	}

	public SpellType getType() {
		return type;
	}

}
