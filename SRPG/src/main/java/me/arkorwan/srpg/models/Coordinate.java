package me.arkorwan.srpg.models;

/**
 * Coordinate composed of ordered-pair (int, int). Mutable.
 * 
 * @author arkorwan
 *
 */
public class Coordinate {

	public int x;
	public int y;

	private Coordinate() {
	}

	/**
	 * Static factory method.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static Coordinate of(int x, int y) {
		Coordinate coord = new Coordinate();
		coord.x = x;
		coord.y = y;
		return coord;
	}

	public int manhattanDistance(Coordinate other) {
		return Math.abs(this.x - other.x) + Math.abs(this.y - other.y);
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (o instanceof Coordinate) {
			Coordinate co = (Coordinate) o;
			return co.x == this.x && co.y == this.y;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return x * 1000003 + y;
	}

	@Override
	public String toString() {
		return String.format("[%d, %d]", x, y);
	}

}
