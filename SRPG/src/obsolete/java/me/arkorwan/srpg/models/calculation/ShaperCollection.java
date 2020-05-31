package me.arkorwan.srpg.models.calculation;

import java.util.HashMap;
import java.util.Map;

public class ShaperCollection {

	private static Map<String, Shaper.Builder> shapers = new HashMap<>();
	private static Map<String, Compositor.Builder> compositors = new HashMap<>();

	static {

		registerShaper("linear", Shaper.prepare());
		registerShaper("power",
				Shaper.prepare().addParam((k, x) -> Math.pow(x, k), 2.0));
		registerShaper("root",
				Shaper.prepare().addParam((k, x) -> Math.pow(x, 1.0 / k), 2.0));
		registerShaper("exp", Shaper.prepare().addParam(
				(k, x) -> (Math.pow(k, x) - 1.0) / (k - 1.0), Math.exp(2)));
		registerShaper("logistic", Shaper.prepare()
				.addParam((k, x) -> ShaperCollection.normalizedLogistic(k, x)));
		registerShaper("cutoff", Shaper.prepare()
				.addParam((k, x) -> x > k ? (x - k) / (1.0 - k) : 0.0, 0.5));

		registerCompositor("mult",
				Compositor.prepare(k -> (x1, x2) -> x1 * x2));
		registerCompositor("weight",
				Compositor.prepare(α -> (x1, x2) -> α * x1 + (1.0 - α) * x2));
	}

	private static void registerShaper(String key, Shaper.Builder builder) {
		builder.build();
		shapers.put(key, builder);
	}

	private static void registerCompositor(String key,
			Compositor.Builder compositor) {
		compositors.put(key, compositor);
	}
	
	public static Shaper getShaper(String key) {
		return shapers.get(key).build();
	}

	public static Shaper getShaper(String key, double... params) {
		Shaper.Builder b = shapers.get(key);
		for (int i = 0; i < params.length; i++) {
			b.setParameter(i, params[i]);
		}
		return b.build();
	}

	public static Compositor getCompositor(String key, double param) {
		Compositor.Builder c = compositors.get(key);
		if (!Double.isNaN(param)) {
			c.withParameter(param);
		}
		return c.build();
	}

	/**
	 * A logistic function centred at (0.5, 0.5) and has both 0 and 1 as
	 * fixpoints
	 * 
	 * @param x
	 * @param k
	 * @return
	 */
	public static double normalizedLogistic(double k, double x) {
		double k2x = Math.pow(k, 2.0 * x - 1.0);
		return (k2x * k - 1.0) / ((k2x + 1.0) * (k - 1.0));
	}
}
