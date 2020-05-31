package me.arkorwan.srpg.models.calculation;

/**
 * A collection of predefined functions.
 * 
 * @author arkorwan
 *
 */
public final class Functions {

	public static int convertFromNormalized(double v, int min, int max) {
		return min + (int) Math.round(v * (max - min));
	}
	/*
	 * public static double normalize(int n, int min, int max) { return (n -
	 * min) * 1.0 / (max - min); }
	 */
}
