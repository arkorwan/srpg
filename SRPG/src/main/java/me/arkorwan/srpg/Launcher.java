package me.arkorwan.srpg;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import me.arkorwan.srpg.evaluation.Evaluator.EvaluationCommand;
import me.arkorwan.srpg.evaluation.EvolutionEngine.GACommand;
import me.arkorwan.srpg.ui.BattleStarter;
import me.arkorwan.srpg.ui.BattleObserver.ObserverCommand;

public class Launcher {

	public static void main(String[] args) {

		if (args.length == 0) {
			BattleStarter.main(args);
		} else {

			Object command = new Object();
			JCommander jc = new JCommander(command);

			EvaluationCommand evalCommand = new EvaluationCommand();
			jc.addCommand("eval", evalCommand);
			GACommand gaCommand = new GACommand();
			jc.addCommand("ga", gaCommand);
			ObserverCommand observeCommand = new ObserverCommand();
			jc.addCommand("observe", observeCommand);
			try {
				jc.parse(args);
			} catch (ParameterException e) {
				jc.usage("eval");
				jc.usage("ga");
				jc.usage("observe");
				throw e;
			}
			switch (jc.getParsedCommand()) {
			case "eval":
				evalCommand.execute();
				break;
			case "ga":
				gaCommand.execute();
				break;
			case "observe":
				observeCommand.execute();
				break;
			}
		}

	}

}
