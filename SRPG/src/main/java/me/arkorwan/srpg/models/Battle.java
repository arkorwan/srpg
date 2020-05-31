package me.arkorwan.srpg.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import me.arkorwan.srpg.models.calculation.DamageCalculator;
import me.arkorwan.srpg.models.targetarea.MagicallyAttackableTargetArea;
import me.arkorwan.srpg.models.targetarea.PhysicallyAttackableTargetArea;
import me.arkorwan.srpg.models.targetarea.TargetArea;
import me.arkorwan.srpg.models.targetarea.VisitableTargetArea;

/**
 * An instance of this class describes a complete snapshot of a battle
 * environment.
 * 
 * @author arkorwan
 *
 */
public class Battle implements BattleInfoReader {

	BattleField field;
	Party party1, party2;
	Random rand;
	DamageCalculator dmgCalculator;

	Map<Unit, BattleCell> unitLocations;
	transient Map<Unit, TargetArea> visitableAreas = new HashMap<>();
	transient Map<Unit, TargetArea> pAttackAreas = new HashMap<>();
	transient Map<Unit, TargetArea> mAttackAreas = new HashMap<>();
	Map<Unit, Party> parties = new HashMap<>();
	Unit currentUnit;

	// for serializer
	@SuppressWarnings("unused")
	private Battle() {

	}

	public Battle(int width, int length, Random rand,
			DamageCalculator dmgCalculator) {

		this.field = new BattleField(width, length);
		this.rand = rand;
		this.dmgCalculator = dmgCalculator;
		this.party1 = new Party();
		this.party2 = new Party();
		this.unitLocations = new HashMap<>();
	}

	@Override
	public Random getRandom() {
		return rand;
	}

	@Override
	public DamageCalculator getDamageCalculator() {
		return dmgCalculator;
	}

	public void placeUnitInParty1(Unit u, Coordinate position) {
		placeUnit(u, position, party1);
	}

	public void placeUnitInParty2(Unit u, Coordinate position) {
		placeUnit(u, position, party2);
	}

	public void placeUnit(Unit u, Coordinate position, Party p) {
		BattleCell c = field.cellForCoordinate(position);
		if(c.unit.isPresent()){
			removeUnit(c.unit.get());
		}
		c.setUnit(u);
		unitLocations.put(u, c);
		parties.put(u, p);
		p.addUnit(u);
	}

	public void removeUnit(Unit unit) {
		BattleCell cell = unitLocations.remove(unit);
		cell.clearUnit();
		Party party = parties.get(unit);
		if (party != null) {
			party.removeUnit(unit);
			parties.remove(unit);
		}
	}

	@Override
	public Optional<Party> getWinningParty() {
		if (party1.getMembers().isEmpty()) {
			return Optional.of(party2);
		} else if (party2.getMembers().isEmpty()) {
			return Optional.of(party1);
		} else {
			return Optional.empty();
		}
	}

	@Override
	public BattleField getField() {
		return field;
	}

	@Override
	public Party getParty1() {
		return party1;
	}

	@Override
	public Party getParty2() {
		return party2;
	}

	@Override
	public Party getPartyForUnit(Unit u) {
		return parties.get(u);
	}

	@Override
	public BattleCell getUnitLocation(Unit u) {
		return unitLocations.get(u);
	}

	public void setUnitLocation(Unit u, BattleCell targetCell) {
		BattleCell currentCell = getUnitLocation(u);
		currentCell.clearUnit();
		targetCell.setUnit(u);
		unitLocations.put(u, targetCell);
	}

	@Override
	public TargetArea getVisitableArea(Unit u) {
		if (!visitableAreas.containsKey(u)) {
			visitableAreas.put(u, new VisitableTargetArea(u, this));
		}
		return visitableAreas.get(u);
	}

	@Override
	public TargetArea getPhysicallyAttackableArea(Unit u) {
		if (!pAttackAreas.containsKey(u)) {
			pAttackAreas.put(u, new PhysicallyAttackableTargetArea(u, this));
		}
		return pAttackAreas.get(u);
	}

	@Override
	public TargetArea getMagicallyAttackableArea(Unit u) {
		if (!mAttackAreas.containsKey(u)) {
			mAttackAreas.put(u, new MagicallyAttackableTargetArea(u, this));
		}
		return mAttackAreas.get(u);
	}

}
