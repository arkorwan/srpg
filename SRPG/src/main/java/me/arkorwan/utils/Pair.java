package me.arkorwan.utils;

public class Pair<T, U> {

	public final T first;
	public final U second;

	private Pair(T x, U y) {
		first = x;
		second = y;
	}

	public static <T, U> Pair<T, U> of(T t, U u) {
		return new Pair<T, U>(t, u);
	}

}
