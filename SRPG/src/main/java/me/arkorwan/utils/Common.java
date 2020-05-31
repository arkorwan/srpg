package me.arkorwan.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class Common {

	private Common() {
	}

	public static List<Integer> randomPermutation(int excMax, Random rand) {
		List<Integer> cellList = IntStream.range(0, excMax).boxed()
				.collect(Collectors.toList());
		Collections.shuffle(cellList, rand);
		return cellList;
	}

	public static List<Double> randomDoublePartition(int count, double average,
			double deviation, Random rand) {

		double[] randomDoubles = rand.doubles(count).toArray();
		double streamSum = 0;
		for (double d : randomDoubles) {
			streamSum += d;
		}
		double streamAverage = streamSum / count;
		double maxDeviation = 0;
		for (double d : randomDoubles) {
			double dev = Math.abs(d - streamAverage);
			if (dev > maxDeviation) {
				maxDeviation = dev;
			}
		}
		double scale = deviation / maxDeviation;

		List<Double> result = new ArrayList<>(count);
		for (double d : randomDoubles) {
			result.add((d - streamAverage) * scale + average);
		}
		return result;
	}

	static Config conf = ConfigFactory.load();

	@SuppressWarnings("unchecked")
	public static <T> T getConfigWithDefault(String path, T def) {
		if (conf.hasPath(path)) {
			if (def instanceof Integer) {
				return (T) Integer.valueOf(conf.getInt(path));
			} else if (def instanceof Double) {
				return (T) Double.valueOf(conf.getDouble(path));
			} else if (def instanceof Boolean) {
				return (T) Boolean.valueOf(conf.getBoolean(path));
			} else if (def instanceof String) {
				return (T) conf.getString(path);
			} else {
				return (T) conf.getObject(path);
			}
		} else {
			System.err.println(String
					.format("warning:: configuration for %s not found.", path));
			return def;
		}

	}

	// from http://stackoverflow.com/a/326440/1823254
	public static String readFile(String path, Charset encoding)
			throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

	static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public static void gsonSerialize(Object object, String path) {
		String json = GSON.toJson(object);

		// ensure directory exists
		new File(path).getParentFile().mkdirs();

		try (PrintWriter out = new PrintWriter(path)) {
			out.println(json);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static <T> T gsonDeserialize(String path, Class<T> clazz,
			Gson deserializer) {
		try {
			return deserializer.fromJson(
					Common.readFile(path, Charset.defaultCharset()), clazz);
		} catch (JsonSyntaxException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static <T> T gsonDeserialize(String path, Class<T> clazz) {
		return gsonDeserialize(path, clazz, GSON);
	}

	public static double scalarProduct(double[] a1, double[] a2) {
		double result = 0.0;
		for (int i = 0; i < a1.length && i < a2.length; i++) {
			result += a1[i] * a2[i];
		}
		return result;
	}

	// http://math.stackexchange.com/a/466248/58442
	public static double squareFittingSize(double x, double y, int n) {

		double px = Math.ceil(Math.sqrt(n * x / y));
		double sx, sy;
		if (Math.floor(px * y / x) * px < n) {
			sx = y / Math.ceil(px * y / x);
		} else {
			sx = x / px;
		}

		double py = Math.ceil(Math.sqrt(n * y / x));
		if (Math.floor(py * x / y) * py < n) {
			sy = x / Math.ceil(x * py / y);
		} else {
			sy = y / py;
		}

		return Math.max(sx, sy);
	}

}
