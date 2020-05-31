package me.arkorwan.srpg.models.targetarea;

import java.util.HashSet;
import java.util.Set;

import me.arkorwan.srpg.models.BattleCell;
import me.arkorwan.srpg.models.BattleInfoReader;
import me.arkorwan.srpg.models.Coordinate;
import me.arkorwan.srpg.models.Unit;
import me.arkorwan.srpg.models.equipment.Weapon;

public class PhysicallyAttackableTargetArea extends AbstractTargetArea {

	public PhysicallyAttackableTargetArea(Unit unit,
			BattleInfoReader battleInfo) {
		super(unit, battleInfo);
	}

	@Override
	protected HashSet<BattleCell> calculateArea(Coordinate coord) {
		Weapon w = unit.getWeapon();
		Set<BattleCell> result = battleInfo.getField().cellInRange(
				w.isRangeDirect(), coord, w.attackMaxRange(),
				dh -> w.verticalAttackAllowed(dh), (c0, c1) -> 1);
		result.removeIf(c -> c.isVoid());

		if (w.attackMinRange() < w.attackMaxRange()) {
			Set<BattleCell> toRemove = battleInfo.getField().cellInRange(
					w.isRangeDirect(), coord, w.attackMinRange() - 1,
					dh -> true, (c0, c1) -> 1);
			result.removeAll(toRemove);
		}

		return new HashSet<>(result);
	}

}
