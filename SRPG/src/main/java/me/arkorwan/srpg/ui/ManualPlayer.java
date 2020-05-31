package me.arkorwan.srpg.ui;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import me.arkorwan.srpg.controller.BattleCommand;
import me.arkorwan.srpg.controller.BattleCommand.CommandType;
import me.arkorwan.srpg.models.Player;
import me.arkorwan.srpg.models.Unit;

public class ManualPlayer extends Player {

	private static Player.Factory factory = new Player.Factory() {

		@Override
		public String playerTypeName() {
			return "Manual Control";
		}

		@Override
		protected Player create() {
			return new ManualPlayer();
		}
	};

	public static Player.Factory getFactory() {
		return factory;
	}

	BattleCanvas canvas;
	List<CommandType> currentCommands;
	BattleCommand responseCommand;

	CountDownLatch latch;

	@Override
	public BattleCommand selectPlay(Unit currentUnit,
			List<CommandType> commands) {
		currentCommands = commands;

		latch = new CountDownLatch(1);
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return responseCommand;
	}

	void setPlay(BattleCommand command) {
		responseCommand = command;
		if (latch != null) {
			latch.countDown();
		}
	}

	@Override
	public void onBattleEnded(boolean winning) {
		// TODO Auto-generated method stub

	}

	List<CommandType> getCurrentCommands() {
		return currentCommands;
	}

	@Override
	public boolean shouldAnimate() {
		return false;
	}

}
