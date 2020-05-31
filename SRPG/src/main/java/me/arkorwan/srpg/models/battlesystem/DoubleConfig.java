package me.arkorwan.srpg.models.battlesystem;

import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

import org.jenetics.Chromosome;
import org.jenetics.IntegerChromosome;
import org.jenetics.IntegerGene;

import com.google.common.math.DoubleMath;

public class DoubleConfig implements BattleSystemConfig {

	public double value, min, max;

	public DoubleConfig(double min, double max) {
		this.min = min;
		this.max = max;
		value = min + (max - min) / 2.0;
	}

	static final int RESOLUTION = 100;

	@Override
	public List<IntegerChromosome> encode() {
		return Collections.singletonList(encodeAsSingleChromosome());
	}

	int round(double v) {
		return DoubleMath.roundToInt(v * RESOLUTION, RoundingMode.HALF_EVEN);
	}

	public IntegerChromosome encodeAsSingleChromosome() {
		return IntegerChromosome
				.of(IntegerGene.of(round(value), round(min), round(max)));
	}

	public void decode(Chromosome<IntegerGene> data) {
		this.value = (data.getGene(0).intValue() * 1.0 / RESOLUTION);
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
