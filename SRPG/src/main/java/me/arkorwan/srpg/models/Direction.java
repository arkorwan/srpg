package me.arkorwan.srpg.models;

/**
 * The 4 cardinal directions.
 * 
 * @author arkorwan
 *
 */
public enum Direction {

	North(0, -1), East(1, 0), South(0, 1), West(-1, 0);

	int dx, dy;

	Direction(int dx, int dy) {
		this.dx = dx;
		this.dy = dy;
	}
}
