package me.arkorwan.srpg.models;

import java.util.HashMap;
import java.util.Map;

public enum Race implements StatusEffector {

	//@formatter:off
	Human (100,  0, 0, 0, 0, 0), 
	Elf   ( 80, -1,-2, 1, 0, 2),
	Dwarf (120,  1, 1,-2, 1,-1), 
	Gnome ( 90, -2, 0, 2,-1, 1), 
	Troll (200,  2, 2,-1,-1,-2), 
	Kobold(150, -1, 1,-1, 0, 1),
	Fairy ( 50, -2,-1, 1, 2, 0), 
	Goblin( 80,  2,-2, 0,-1, 1);
	//@formatter:on

	Map<BasicAttribute.Type, Integer> effects = new HashMap<>();
	int weight;

	Race(int weight, int str, int con, int intel, int wis, int dex) {
		this.weight = weight;
		if (str != 0) {
			effects.put(BasicAttribute.Type.STR, str);
		}
		if (con != 0) {
			effects.put(BasicAttribute.Type.CON, con);
		}
		if (intel != 0) {
			effects.put(BasicAttribute.Type.INT, intel);
		}
		if (wis != 0) {
			effects.put(BasicAttribute.Type.WIS, wis);
		}
		if (dex != 0) {
			effects.put(BasicAttribute.Type.DEX, dex);
		}
	}

	@Override
	public Map<BasicAttribute.Type, Integer> effects() {
		return effects;
	}
	
	public int getWeight(){
		return weight;
	}
}
