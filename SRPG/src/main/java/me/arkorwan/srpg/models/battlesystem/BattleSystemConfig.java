package me.arkorwan.srpg.models.battlesystem;

import java.util.List;

import org.jenetics.Chromosome;
import org.jenetics.IntegerChromosome;
import org.jenetics.IntegerGene;

public interface BattleSystemConfig {

	List<IntegerChromosome> encode();

	void decode(List<? extends Chromosome<IntegerGene>> data);
	
	int numberOfChromosomes();

}
