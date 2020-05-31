package me.arkorwan.srpg.models.battlesystem;

import java.util.List;

import org.jenetics.Chromosome;
import org.jenetics.IntegerChromosome;
import org.jenetics.IntegerGene;

import com.google.common.collect.Lists;

import me.arkorwan.srpg.models.Attribute;
import me.arkorwan.srpg.models.DerivedAttribute;
import me.arkorwan.srpg.models.calculation.AbstractShaper;
import me.arkorwan.srpg.models.calculation.CubicBezierShaper;
import me.arkorwan.srpg.models.calculation.NormalizedFunction;

public class SingleParameterFunctionConfig implements BattleSystemConfig {

	public DoubleConfig bz1x, bz1y, bz2x, bz2y;
	public IntConfig max;

	public SingleParameterFunctionConfig(IntConfig max, DoubleConfig bz1x,
			DoubleConfig bz1y, DoubleConfig bz2x, DoubleConfig bz2y) {
		this.bz1x = bz1x;
		this.bz1y = bz1y;
		this.bz2x = bz2x;
		this.bz2y = bz2y;
		this.max = max;
	}

	public SingleParameterFunctionConfig(IntConfig max) {
		this(max, new DoubleConfig(0.0, 1.0), new DoubleConfig(0.0, 1.0),
				new DoubleConfig(0.0, 1.0), new DoubleConfig(0.0, 1.0));
	}

	public SingleParameterFunctionConfig() {
		this(new IntConfig(50, 999));
	}

	public <T extends Attribute & NormalizedFunction> DerivedAttribute createAttribute(
			T attr, String name, int min) {
		AbstractShaper s = new CubicBezierShaper(bz1x.value, bz1y.value,
				bz2x.value, bz2y.value);
		s.setInnerFunction(attr);
		return new DerivedAttribute(name, s, min, max.value);

	}

	@Override
	public List<IntegerChromosome> encode() {
		return Lists.newArrayList(max.encodeAsSingleChromosome(),
				bz1x.encodeAsSingleChromosome(),
				bz1y.encodeAsSingleChromosome(),
				bz2x.encodeAsSingleChromosome(),
				bz2y.encodeAsSingleChromosome());
	}

	@Override
	public void decode(List<? extends Chromosome<IntegerGene>> data) {
		max.decode(data.get(0));
		bz1x.decode(data.get(1));
		bz1y.decode(data.get(2));
		bz2x.decode(data.get(3));
		bz2y.decode(data.get(4));
	}
	
	@Override
	public int numberOfChromosomes() {
		return 5;
	}
}
