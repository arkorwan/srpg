package me.arkorwan.srpg.generators;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import me.arkorwan.srpg.models.Battle;
import me.arkorwan.srpg.models.BattleCell;
import me.arkorwan.srpg.models.BattleField;
import me.arkorwan.srpg.models.Party;
import me.arkorwan.srpg.models.Unit;
import me.arkorwan.srpg.models.battlesystem.BattleSystem;
import me.arkorwan.srpg.models.calculation.DamageCalculator;
import me.arkorwan.utils.Common;

public class BattleGenerator extends Generator<Battle> {

	UnitGenerator party1Generator, party2Generator;
	WeaponGenerator weapon1Generator, weapon2Generator;
	ArmourGenerator armour1Generator, armour2Generator;

	transient int unitsPerTeam;

	public BattleGenerator(BattleSystem sys, Random r) {
		super(sys, r);
	}

	public BattleSystem getBattleSystem() {
		return sys;
	}

	public Random getRandom() {
		return rand;
	}

	public void setTeamSize(int n) {
		this.unitsPerTeam = n;
	}

	public void refreshQuotas() {
		party1Generator.refreshQuotas();
		weapon1Generator.refreshQuotas();
		armour1Generator.refreshQuotas();
		party2Generator.refreshQuotas();
		weapon2Generator.refreshQuotas();
		armour2Generator.refreshQuotas();
	}

	public void setTeamGenerators(boolean team1, UnitGenerator u,
			WeaponGenerator w, ArmourGenerator a) {

		if (team1) {
			this.party1Generator = u;
			this.weapon1Generator = w;
			this.armour1Generator = a;
		} else {
			this.party2Generator = u;
			this.weapon2Generator = w;
			this.armour2Generator = a;
		}
	}

	public UnitGenerator getUnitGenerator(boolean team1) {
		return team1 ? party1Generator : party2Generator;
	}

	@Override
	public Battle generate() {

		int width = Common.getConfigWithDefault("battlefield.dimension.x", 10);
		int length = Common.getConfigWithDefault("battlefield.dimension.y", 10);

		Battle b = new Battle(width, length, new Random(rand.nextLong()),
				new DamageCalculator(sys.elementalAdvantageMultiplier.value,
						sys.damageAtkMultiplier.value,
						sys.damageAtkDegree.value,
						sys.damageDiscountDegree.value));
		do {
			randomizeField(b.getField());
		} while (!validateField(b.getField(), unitsPerTeam));

		generateParties(b, unitsPerTeam);
		return b;
	}

	private void generateParties(Battle battle, int unitsPerTeam) {

		refreshQuotas();

		int allowSize = 2 * battle.getField().getLength();

		int fieldSize = battle.getField().getLength()
				* battle.getField().getWidth();

		List<Integer> team1pos = Common.randomPermutation(allowSize, rand)
				.stream().map(k -> fieldSize - 1 - k)
				.collect(Collectors.toList());
		List<Integer> team2pos = Common.randomPermutation(allowSize, rand);

		placeUnits(battle, team1pos,
				generateFullUnits(unitsPerTeam, true, false),
				battle.getParty1());
		placeUnits(battle, team2pos,
				generateFullUnits(unitsPerTeam, false, false),
				battle.getParty2());

	}

	public List<Unit> generateFullUnits(int n, boolean team1,
			boolean isPeeking) {

		List<Unit> us = new ArrayList<>();

		UnitGenerator ug = team1 ? party1Generator : party2Generator;
		WeaponGenerator wg = team1 ? weapon1Generator : weapon2Generator;
		ArmourGenerator ag = team1 ? armour1Generator : armour2Generator;

		ug.setPeekMode(isPeeking);
		wg.setPeekMode(isPeeking);
		ag.setPeekMode(isPeeking);

		for (int i = 0; i < n; i++) {
			Unit u = ug.generate();
			wg.job = u.getJob();
			u.equip(wg.generate());
			ag.armourType = u.getJob().getArmourType();
			u.equip(ag.generate());
			us.add(u);
		}
		return us;
	}

	private void placeUnits(Battle battle, List<Integer> positions,
			List<Unit> units, Party party) {
		int u = 0;
		for (Integer k : positions) {
			BattleCell cell = battle.getField().cellForId(k);
			if (!cell.isVoid()) {
				battle.placeUnit(units.get(u), cell.getLocation(), party);
				if (++u == units.size()) {
					break;
				}
			}

		}
	}

	private static final int UNREACHABLE_HEIGHT = 1000000;

	private void randomizeField(BattleField field) {
		int size = field.getLength() * field.getWidth();
		double pitsRatio = Common.getConfigWithDefault("battlefield.pit_rate",
				0.25);
		int targetGround = (int) Math.round(size * (1.0 - pitsRatio));

		field.forEachCell(c -> c.setHeight(0));
		field.forEachCell(c -> c.setToVoid(false));

		int lowHeight = size + 1;
		BattleCell cell = field.cellForCoordinate(field.getWidth() / 2,
				field.getLength() / 2);
		cell.setHeight(lowHeight);
		targetGround--;

		List<BattleCell> fringe = new ArrayList<>();
		fringe.addAll(field.adjacent(cell.getLocation()));
		for (BattleCell b : fringe) {
			b.setHeight(-1);
		}

		while (fringe.size() > 0 && targetGround > 0) {

			int i = rand.nextInt(fringe.size());
			cell = fringe.get(i);
			if (i < fringe.size() - 1) {
				fringe.set(i, fringe.remove(fringe.size() - 1));
			} else {
				fringe.remove(fringe.size() - 1);
			}

			List<Integer> newCellHeights = new ArrayList<>();

			for (BattleCell c : field.adjacent(cell.getLocation())) {
				if (c.getHeight() == 0) {
					c.setHeight(-1);
					fringe.add(c);
				} else if (c.getHeight() > 0) {
					newCellHeights.add(c.getHeight());
					newCellHeights.add(c.getHeight() - 1);
					newCellHeights.add(c.getHeight() + 1);

				}
			}

			int randomHeight = newCellHeights
					.get(rand.nextInt(newCellHeights.size()));
			cell.setHeight(randomHeight);
			if (randomHeight < lowHeight) {
				lowHeight = randomHeight;
			}
			targetGround -= 1;

		}

		int offset = lowHeight - 1;
		field.forEachCell(c -> {
			if (c.getHeight() <= 0) {
				c.setHeight(UNREACHABLE_HEIGHT);
				c.setToVoid(true);
			} else {
				c.setHeight(c.getHeight() - offset);
			}
		});

	}

	private boolean validateField(BattleField field, int units) {
		int n = 2 * field.getLength();
		int fieldSize = field.getLength() * field.getWidth();
		int count = units;
		for (int i = 0; i < n; i++) {
			if (!field.cellForId(i).isVoid()) {
				if (--count == 0) {
					break;
				}
			}
		}
		if (count > 0) {
			return false;
		}
		count = units;
		for (int i = 0; i < n; i++) {
			if (!field.cellForId(fieldSize - i - 1).isVoid()) {
				if (--count == 0) {
					break;
				}
			}
		}
		return count == 0;
	}

	@Override
	protected Generator<?>[] downstream() {
		return new Generator[] { party1Generator, party2Generator,
				weapon1Generator, weapon2Generator, armour1Generator,
				armour2Generator };
	}
}
