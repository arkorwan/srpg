package me.arkorwan.srpg.generators;

import java.util.Random;

import me.arkorwan.srpg.models.battlesystem.BattleSystem;

public abstract class Generator<T> {

	transient protected Random rand;
	transient protected BattleSystem sys;

	public Generator(BattleSystem sys, Random r) {
		this.sys = sys;
		this.rand = r;
	}

	public abstract T generate();

	public void setRandom(Random r) {
		this.rand = r;
		for (Generator<?> g : downstream()) {
			g.setRandom(r);
		}
	}

	public void setBattleSystem(BattleSystem sys) {
		this.sys = sys;
		for (Generator<?> g : downstream()) {
			g.setBattleSystem(sys);
		}
	}

	protected abstract Generator<?>[] downstream();

}
