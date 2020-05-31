package me.arkorwan.srpg.ui;

import java.awt.EventQueue;
import java.nio.file.Paths;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.beust.jcommander.Parameter;

import me.arkorwan.srpg.ai.SmartPlayer;
import me.arkorwan.srpg.controller.BattleController;
import me.arkorwan.srpg.generators.BattleGenerator;
import me.arkorwan.srpg.models.Battle;
import me.arkorwan.srpg.models.Player;
import me.arkorwan.srpg.models.battlesystem.BattleSystem;
import me.arkorwan.utils.Common;

public class BattleObserver extends JFrame {

	public static class ObserverCommand {
		@Parameter(names = "-system", description = "battle system")
		private String battleSystemPath;

		public void execute() {
			BattleSystem sys = Common.gsonDeserialize(battleSystemPath,
					BattleSystem.class);
			sys.systemName = Paths.get(battleSystemPath).getFileName()
					.toString();
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						BattleObserver frame = new BattleObserver(sys);
						frame.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	Player.Factory[] playerFactories = new Player.Factory[] {
			ManualPlayer.getFactory(), SmartPlayer.getFactory() };

	/**
	 * Create the frame.
	 */
	public BattleObserver(BattleSystem sys) {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblPlayer1 = new JLabel("Player 1");
		lblPlayer1.setBounds(126, 79, 61, 16);
		contentPane.add(lblPlayer1);

		JComboBox<Player.Factory> cmbPlayer1 = new JComboBox<>(playerFactories);
		cmbPlayer1.setBounds(199, 75, 211, 27);
		cmbPlayer1.setSelectedIndex(0);
		contentPane.add(cmbPlayer1);

		JLabel lblPlayer2 = new JLabel("Player 2");
		lblPlayer2.setBounds(126, 120, 61, 16);
		contentPane.add(lblPlayer2);

		JComboBox<Player.Factory> cmbPlayer2 = new JComboBox<>(playerFactories);
		cmbPlayer2.setBounds(199, 116, 211, 27);
		cmbPlayer2.setSelectedIndex(0);
		contentPane.add(cmbPlayer2);

		JButton btnStart = new JButton("Start");
		btnStart.setBounds(126, 193, 284, 29);
		btnStart.addActionListener(e -> {

			BattleGenerator generator = Common.gsonDeserialize(
					Common.getConfigWithDefault("generator", ""),
					BattleGenerator.class);
			generator.setRandom(ThreadLocalRandom.current());
			generator.setBattleSystem(sys);
			generator.setTeamSize(
					Common.getConfigWithDefault("battle.units_per_team", 4));
			Battle battle = generator.generate();
			BattleController controller = new BattleController(battle,
					cmbPlayer1.getItemAt(cmbPlayer1.getSelectedIndex()),
					cmbPlayer2.getItemAt(cmbPlayer2.getSelectedIndex()));
			BattleViewer viewer = new BattleViewer(null, controller,
					sys.systemName);
			viewer.show();
			this.setVisible(false);
		});
		contentPane.add(btnStart);

	}
}