package me.arkorwan.srpg.evaluation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import org.jenetics.IntegerGene;
import org.jenetics.engine.EvolutionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.arkorwan.srpg.evaluation.Objective.Traverser;

public class EvaluationResultCollector
		implements Consumer<EvolutionResult<IntegerGene, Double>> {

	static final Logger logger = LoggerFactory
			.getLogger(EvaluationResultCollector.class);

	BufferedWriter bw;
	BufferedWriter bw2;
	long currentGen = 1;

	public EvaluationResultCollector(String path, Objective obj)
			throws IOException {
		new File(path).mkdirs();
		bw = new BufferedWriter(new FileWriter(path + "log.csv", true));
		bw.write("Generation,Best Fitness,Worst Fitness,Time\n");
		bw2 = new BufferedWriter(new FileWriter(path + "data.csv", true));

		bw2.write("Generation");
		obj.traverse(new Traverser() {
			@Override
			public void onObjectiveTraversed(Objective o) {
				try {
					bw2.write(',');
					bw2.write(o.name);
				} catch (IOException e) {
				}
			}

		});
		bw2.newLine();
	}

	@Override
	public void accept(EvolutionResult<IntegerGene, Double> er) {
		long gen = er.getGeneration();
		if (currentGen <= gen) {
			currentGen = gen + 1;
		}
		double bestFitness = er.getBestFitness();
		double worstFitness = er.getWorstFitness();
		long evolveTime = er.getDurations().getEvolveDuration().toMillis();
		logger.info("finished generation {}", gen);
		try {
			bw.write(String.format("%d,%.3f,%.3f,%d\n", gen, bestFitness,
					worstFitness, evolveTime));
			bw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void logIndividualFitness(List<Double> fitnesses) {
		try {
			synchronized (bw2) {
				bw2.write(String.valueOf(currentGen));
				for (double d : fitnesses) {
					bw2.write(String.format(",%.6f", d));
				}
				bw2.newLine();
				bw2.flush();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void close() throws IOException {
		bw.close();
		bw2.close();
	}

}
