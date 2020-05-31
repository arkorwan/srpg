package me.arkorwan.srpg.ai;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.concurrent.ThreadLocalRandom;

import org.testng.annotations.Test;

import com.google.common.collect.Lists;

import me.arkorwan.srpg.controller.BattleCommand;
import me.arkorwan.srpg.controller.BattleCommand.CommandType;
import me.arkorwan.srpg.generators.UnitGenerator;
import me.arkorwan.srpg.models.Battle;
import me.arkorwan.srpg.models.Coordinate;
import me.arkorwan.srpg.models.Unit;
import me.arkorwan.srpg.models.battlesystem.BattleSystem;
import me.arkorwan.srpg.models.calculation.DamageCalculator;

public class TestSmartPlayer {

	UnitGenerator g = new UnitGenerator(new BattleSystem(),
			ThreadLocalRandom.current());

	@Test
	public void testPlayerName() {

		assertEquals(SmartPlayer.getFactory().playerTypeName(), "AI player");
		assertEquals(SmartPlayer.getFactory().toString(), "AI player");

	}

	@Test
	public void testPlayerSetup() {
		Battle b = new Battle(3, 3, ThreadLocalRandom.current(),
				new DamageCalculator(2, 1, 1, 1));

		Unit a1 = g.generate();
		Unit b1 = g.generate();

		b.placeUnitInParty1(a1, Coordinate.of(0, 0));
		b.placeUnitInParty2(b1, Coordinate.of(2, 2));

		SmartPlayer p = (SmartPlayer) SmartPlayer.getFactory().createInstance(b,
				b.getParty1());

		assertSame(p.getParty(), b.getParty1());
		assertSame(p.getEnemyParty(), b.getParty2());

		assertTrue(p.currentCommands.isEmpty());

		BattleCommand cmd = p.selectPlay(a1,
				Lists.newArrayList(CommandType.Move, CommandType.EndTurn));

		if (cmd.getCommandType() == CommandType.Move) {
			assertEquals(p.currentCommands.size(), 1);
			assertEquals(p.currentCommands.get(0).getCommandType(),
					CommandType.EndTurn);
		} else {
			assertTrue(p.currentCommands.isEmpty());
			assertEquals(cmd.getCommandType(), CommandType.EndTurn);
		}

	}

}
