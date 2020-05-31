package me.arkorwan.srpg.ui;

import me.arkorwan.srpg.models.Unit;

public enum FFTSpriteTile implements Tile {

	BlueWarrior(0, 0), BlueRanger(1, 0), BlueMage(2, 0), BlueCleric(3,
			0), RedWarrior(0,
					1), RedRanger(1, 1), RedMage(2, 1), RedCleric(3, 1);

	int x, y;

	FFTSpriteTile(int x, int y) {
		this.x = x;
		this.y = y;
	}

	static FFTSpriteTile tileFromUnit(Unit u, boolean team1) {

		switch (u.getJob()) {
		case Ranger:
			return team1 ? BlueRanger : RedRanger;
		case Mage:
			return team1 ? BlueMage : RedMage;
		case Cleric:
			return team1 ? BlueCleric : RedCleric;
		case Warrior:
		default:
			return team1 ? BlueWarrior : RedWarrior;
		}
	}

	@Override
	public int tileSize() {
		return 32;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}
}
