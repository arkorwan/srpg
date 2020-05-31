package me.arkorwan.srpg.evaluation;

import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;

import me.arkorwan.utils.Common;

public abstract class Objective {

	public static interface Traverser {

		void onObjectiveTraversed(Objective o);

	}

	public static interface EvaluationCollector {

		public void onObjectiveTraversed(Objective o, double result);

	}

	public abstract double eval(Map<String, BattleResultAggregator> resultMap, EvaluationCollector t);

	public abstract void traverse(Traverser t);

	String name;
	int weight = 1;

	static Gson GSON = null;

	static void createGsonSerializer() {

		RuntimeTypeAdapterFactory<Objective> objectiveAdapter = RuntimeTypeAdapterFactory
				.of(Objective.class);
		objectiveAdapter.registerSubtype(AtomicObjective.class, "atomic");
		objectiveAdapter.registerSubtype(CombinedObjective.class, "combined");

		RuntimeTypeAdapterFactory<FitnessFunction> fitnessFunctionAdapter = RuntimeTypeAdapterFactory
				.of(FitnessFunction.class);
		fitnessFunctionAdapter.registerSubtype(ExpectedWinFitness.class,
				"expectedWin");
		fitnessFunctionAdapter.registerSubtype(DamageFractionFitness.class,
				"damageFraction");

		GSON = new GsonBuilder().registerTypeAdapterFactory(objectiveAdapter)
				.registerTypeAdapterFactory(fitnessFunctionAdapter).create();

	}

	static Objective deserialize(String path) {
		if (GSON == null) {
			createGsonSerializer();
		}
		return Common.gsonDeserialize(path, Objective.class, GSON);
	}

	public static void main(String[] args) {

		System.out.println(Objective.deserialize("models/objective.json").name);

	}

}
