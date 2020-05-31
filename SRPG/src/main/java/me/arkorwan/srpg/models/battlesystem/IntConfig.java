package me.arkorwan.srpg.models.battlesystem;

import java.util.Collections;
import java.util.List;

import org.jenetics.Chromosome;
import org.jenetics.IntegerChromosome;
import org.jenetics.IntegerGene;

public class IntConfig implements BattleSystemConfig {

	public int value, min, max;

	public IntConfig(int min, int max) {
		this.min = min;
		this.max = max;
		value = min + (max - min) / 2;
	}

	@Override
	public List<IntegerChromosome> encode() {
		return Collections.singletonList(encodeAsSingleChromosome());
	}

	public IntegerChromosome encodeAsSingleChromosome() {
		return IntegerChromosome.of(IntegerGene.of(value, min, max));
	}

	public void decode(Chromosome<IntegerGene> data) {
		this.value = data.getGene(0).intValue();
	}

	@Override
	public void decode(List<? extends Chromosome<IntegerGene>> data) {
		decode(data.get(0));
	}

	@Override
	public int numberOfChromosomes() {
		return 1;
	}

}
