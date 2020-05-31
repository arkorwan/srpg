package me.arkorwan.srpg.models.targetarea;

import java.util.Set;

import me.arkorwan.srpg.models.BattleCell;
import me.arkorwan.srpg.models.Coordinate;

public interface TargetArea {
	Set<BattleCell> getArea(Coordinate coord);
	Set<BattleCell> getCurrentArea();
	void clear();
	
}
