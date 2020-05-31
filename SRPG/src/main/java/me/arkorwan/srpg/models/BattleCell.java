package me.arkorwan.srpg.models;

import java.util.Optional;

/**
 * A cell in a battlefield.
 * 
 * @author arkorwan
 *
 */
public class BattleCell {

	Coordinate location;
	int height;
	boolean isVoid;

	Optional<Unit> unit;

	// for serializer
	@SuppressWarnings("unused")
	private BattleCell() {

	}

	public BattleCell(Coordinate c, int h) {
		this.location = c;
		this.height = h;
		this.unit = Optional.empty();
		this.isVoid = false;
	}

	public Coordinate getLocation() {
		return location;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int h) {
		height = h;
	}

	public void setUnit(Unit u) {
		this.unit = Optional.of(u);
	}

	public void clearUnit() {
		this.unit = Optional.empty();
	}

	public Optional<Unit> getUnit() {
		return unit;
	}

	public void setToVoid(boolean v) {
		this.isVoid = v;
	}

	public boolean isVoid() {
		return isVoid;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (other instanceof BattleCell) {
			return this.location.equals(((BattleCell) other).location);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return -location.hashCode();
	}

}
