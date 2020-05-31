package me.arkorwan.srpg.controller;

import java.util.PriorityQueue;

import me.arkorwan.srpg.models.Battle;
import me.arkorwan.srpg.models.Unit;
import me.arkorwan.utils.Pair;

public class PlaySequence {

	PriorityQueue<Pair<Unit, Double>> turnQueue;
	double timeOffset;
	Unit currentUnit;

	Battle battle;

	public PlaySequence(Battle battle) {
		this.battle = battle;

		this.turnQueue = new PriorityQueue<>(10,
				PlaySequence::compareUnitSpeed);
		this.timeOffset = 0;
	}

	public void initQueue() {
		for (Unit u : battle.getParty1().getMembers()) {
			turnQueue.add(Pair.of(u, 1.0 / u.getActualSpeed()));
		}
		for (Unit u : battle.getParty2().getMembers()) {
			turnQueue.add(Pair.of(u, 1.0 / u.getActualSpeed()));
		}
	}

	static int compareUnitSpeed(Pair<Unit, Double> u1, Pair<Unit, Double> u2) {
		return Double.compare(u1.second, u2.second);
	}

	public void nextTurn() {
		Pair<Unit, Double> firstPair;
		do {
			firstPair = turnQueue.poll();
		} while (battle.getUnitLocation(firstPair.first) == null);

		currentUnit = firstPair.first;
		timeOffset = firstPair.second;
		turnQueue.add(Pair.of(currentUnit,
				timeOffset + 1.0 / currentUnit.getActualSpeed()));

		if (timeOffset > 1.0) {
			PriorityQueue<Pair<Unit, Double>> newQueue = new PriorityQueue<>(10,
					PlaySequence::compareUnitSpeed);
			while (!turnQueue.isEmpty()) {
				Pair<Unit, Double> p = turnQueue.poll();
				newQueue.add(Pair.of(p.first, p.second - timeOffset));
			}
			turnQueue = newQueue;
		}
	}

	public Unit getCurrentUnit() {
		return currentUnit;
	}
}
