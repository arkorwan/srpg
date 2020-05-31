package me.arkorwan.srpg.models;

import java.util.Optional;
import java.util.Random;

import me.arkorwan.srpg.models.calculation.DamageCalculator;
import me.arkorwan.srpg.models.targetarea.TargetArea;

/**
 * A read-only interface for Battle.
 * 
 * @author arkorwan
 *
 */
public interface BattleInfoReader {

	Optional<Party> getWinningParty();

	BattleField getField();

	Party getParty1();

	Party getParty2();

	Party getPartyForUnit(Unit u);

	BattleCell getUnitLocation(Unit u);

	Random getRandom();

	DamageCalculator getDamageCalculator();

	TargetArea getVisitableArea(Unit u);

	TargetArea getPhysicallyAttackableArea(Unit u);

	TargetArea getMagicallyAttackableArea(Unit u);
}
