package me.arkorwan.srpg.evaluation;

import java.io.File;
import java.io.IOException;

import org.jenetics.GaussianMutator;
import org.jenetics.Genotype;
import org.jenetics.IntegerGene;
import org.jenetics.Population;
import org.jenetics.SinglePointCrossover;
import org.jenetics.TournamentSelector;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.engine.EvolutionStream;
import org.jenetics.engine.limit;
import org.jenetics.util.Factory;
import org.jenetics.util.IO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.Parameter;

import me.arkorwan.srpg.models.battlesystem.BattleSystem;
import me.arkorwan.utils.Common;

public class EvolutionEngine {

	public static class GACommand {

		@Parameter(names = "-size", description = "population size")
		private Integer size = 30;

		@Parameter(names = "-population", description = "path to the file containing starting population")
		private String population;

		@Parameter(names = "-count", description = "battles per evaluation")
		private int battlesCount = 10;

		@Parameter(names = "-seed", description = "random seed")
		private int randomSeed = 10;

		@Parameter(names = "-maxGen", description = "max number of generations")
		private int maxGen = 100;

		@Parameter(names = "-steadyGen", description = "number of steady generations")
		private int steadyGen = 20;

		@Parameter(names = "-combinator", description = "fitness combinator function")
		private String combinator;

		@Parameter(names = "-turnsLimit", description = "force the game to end in stalemate after this many turns")
		private Integer turnsLimit;

		@Parameter(names = "-objective")
		private String objective = "models/objective.json";

		public void execute() {
			if (population == null && size == null) {
				throw new IllegalArgumentException(
						"One of -size or -population must not be null");
			}

			try {
				new EvolutionEngine(objective, combinator, population, size,
						battlesCount, randomSeed, maxGen, steadyGen, turnsLimit)
								.run();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	static final Logger logger = LoggerFactory.getLogger(EvolutionEngine.class);

	Population<IntegerGene, Double> population;
	int populationSize;
	int battles;
	int randomSeed;
	int maxGenerations;
	int steadyGenerations;
	Integer turnsLimit;
	Objective obj;

	FitnessCalculator fitness;

	@SuppressWarnings("unchecked")
	EvolutionEngine(String objective, String combinator, String populationPath,
			Integer populationSize, int battles, int randomSeed,
			int maxGenerations, int steadyGenerations, Integer turnsLimit)
					throws IOException {

		population = null;
		if (populationPath != null) {
			population = (Population<IntegerGene, Double>) IO.jaxb
					.read(populationPath);
			// overwrite size
			this.populationSize = population.size();
		} else {
			this.populationSize = populationSize;
		}

		this.battles = battles;
		this.randomSeed = randomSeed;
		this.maxGenerations = maxGenerations;
		this.steadyGenerations = steadyGenerations;
		this.turnsLimit = turnsLimit;
		this.obj = loadObjective(objective, combinator);

		fitness = new FitnessCalculator();
	}

	Engine<IntegerGene, Double> createEngine() throws IOException {

		Factory<Genotype<IntegerGene>> factory = new BattleSystem().encode();
		Engine<IntegerGene, Double> engine = Engine
				.builder(g -> fitness.getFitness(obj, g, randomSeed, battles,
						turnsLimit), factory)
				.populationSize(populationSize).minimizing()
				.survivorsSelector(new TournamentSelector<>(4))
				.alterers(new SinglePointCrossover<>(0.3),
						new GaussianMutator<>(0.5))
				.build();
		return engine;
	}

	static Objective loadObjective(String objective, String combinator) {
		Objective obj = Objective.deserialize(objective);
		if (combinator != null && obj instanceof CombinedObjective) {
			((CombinedObjective) obj).combinator = combinator;
		}
		return obj;
	}

	public void run() throws IOException {

		logger.info("Start evolution");

		long now = System.currentTimeMillis();
		Engine<IntegerGene, Double> engine = createEngine();
		EvaluationResultCollector evc = new EvaluationResultCollector(
				String.format("./ga/%d/", now), obj);
		fitness.setCollector(evc);

		EvolutionStream<IntegerGene, Double> stream;
		if (population == null) {
			logger.info("Start from random population");
			stream = engine.stream();
		} else {
			logger.info("Start from given population");
			stream = engine.stream(population, 1);
		}

		EvolutionResult<IntegerGene, Double> best = stream
				.limit(limit.bySteadyFitness(steadyGenerations))
				.limit(limit.byFixedGeneration(maxGenerations)).peek(evc)
				.collect(EvolutionResult.toBestEvolutionResult());

		BattleSystem bestSystem = new BattleSystem();
		bestSystem.decode(best.getBestPhenotype().getGenotype());
		Common.gsonSerialize(bestSystem,
				String.format("./ga/%d/bestsys.json", now));

		try {
			IO.jaxb.write(best.getPopulation(),
					new File(String.format("./ga/%d/population.xml", now)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		evc.close();
		fitness.setCollector(null);
		logger.info("Evolution runtime: {}", System.currentTimeMillis() - now);
	}

}
