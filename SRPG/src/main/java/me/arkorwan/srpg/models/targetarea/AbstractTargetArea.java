package me.arkorwan.srpg.models.targetarea;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import me.arkorwan.srpg.models.BattleCell;
import me.arkorwan.srpg.models.BattleInfoReader;
import me.arkorwan.srpg.models.Coordinate;
import me.arkorwan.srpg.models.Unit;

public abstract class AbstractTargetArea implements TargetArea {

	protected Unit unit;
	protected BattleInfoReader battleInfo;

	protected Map<Coordinate, Set<BattleCell>> cellsMap = new HashMap<>();

	public AbstractTargetArea(Unit unit, BattleInfoReader battleInfo) {
		this.unit = unit;
		this.battleInfo = battleInfo;
	}

	public Set<BattleCell> getArea(Coordinate coord) {
		if (!cellsMap.containsKey(coord)) {
			cellsMap.put(coord, calculateArea(coord));
		}
		return cellsMap.get(coord);
	}

	public Set<BattleCell> getCurrentArea() {
		return getArea(battleInfo.getUnitLocation(unit).getLocation());
	}

	public void clear() {
		cellsMap.clear();
	}

	protected abstract HashSet<BattleCell> calculateArea(Coordinate coord);

}
