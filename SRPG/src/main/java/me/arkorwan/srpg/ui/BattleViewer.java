package me.arkorwan.srpg.ui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.google.common.eventbus.Subscribe;

import me.arkorwan.srpg.controller.BattleController;
import me.arkorwan.srpg.controller.BattleEvents;

public class BattleViewer {

	private JFrame frame, parent;

	private static BattleViewer instance;

	private JLabel lblName, lblUnitDetails;

	private LoggingDialog logger = new LoggingDialog();

	BattleController controller;

	UnitInformationPrinter unitPrinter;

	/**
	 * Create the application.
	 */
	public BattleViewer(JFrame parent, BattleController controller,
			String battleSystemName) {
		instance = this;
		this.controller = controller;
		initialize(controller, battleSystemName);
		this.parent = parent;
		controller.registerSubscriber(this);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(BattleController controller, String systemName) {
		frame = new JFrame();
		frame.setBounds(100, 100, 800, 600);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 600, 198, 0 };
		gridBagLayout.rowHeights = new int[] { 578, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0,
				Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		frame.getContentPane().setLayout(gridBagLayout);

		// somehow just by adding this label, there's no noticeable loading
		// delay anymore.
		JLabel loadingLabel = new JLabel("", new ImageIcon("ajax-loader.gif"),
				JLabel.CENTER);
		frame.getContentPane().add(loadingLabel);

		BattleCanvas worldCanvas = new BattleCanvas();
		worldCanvas.controller = controller;

		GridBagConstraints gbc_worldCanvas = new GridBagConstraints();
		gbc_worldCanvas.fill = GridBagConstraints.BOTH;
		gbc_worldCanvas.insets = new Insets(0, 0, 0, 5);
		gbc_worldCanvas.gridx = 0;
		gbc_worldCanvas.gridy = 0;
		frame.getContentPane().add(worldCanvas, gbc_worldCanvas);

		JPanel panel = new JPanel();
		panel.setLayout(null);
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 0;
		frame.getContentPane().add(panel, gbc_panel);

		lblUnitDetails = new JLabel("details");
		lblUnitDetails.setBounds(20, 150, 159, 390);
		panel.add(lblUnitDetails);

		lblName = new JLabel("Name");
		lblName.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		lblName.setHorizontalAlignment(SwingConstants.CENTER);
		lblName.setBounds(20, 76, 159, 62);
		panel.add(lblName);

		JButton btnViewLog = new JButton("View log");
		btnViewLog.setBounds(6, 50, 187, 29);
		btnViewLog.addActionListener(e -> logger.showDialog());
		panel.add(btnViewLog);

		unitPrinter = new UnitInformationPrinter(lblName, lblUnitDetails);

		JLabel lblBattleSystem = new JLabel(
				String.format("<<battle system: %s>>", systemName));
		lblBattleSystem.setHorizontalAlignment(SwingConstants.CENTER);
		lblBattleSystem.setBounds(6, 18, 187, 16);
		panel.add(lblBattleSystem);
		unitPrinter.clearDisplayedUnit();

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				ended();
			}
		});
	}

	public void show() {
		frame.setVisible(true);
	}

	void ended() {
		if (parent != null) {
			parent.setVisible(true);
		}
		frame.dispose();
	}

	public static BattleViewer instance() {
		return instance;
	}

	@Subscribe
	public void handleDamage(BattleEvents.DamageEvent e) {

		if (e.isAttack) {
			if (e.hit) {
				logLine(String.format("%s took %d damage.", e.unit.getName(),
						-e.effectiveDamage));
			} else {
				logLine("But the target evaded the attack.");
			}
		} else {
			if (e.hit) {
				logLine(String.format("%s regained %d HP.", e.unit.getName(),
						e.effectiveDamage));
			} else {
				logLine("It failed.");
			}
		}

	}

	@Subscribe
	public void handleUnitAction(BattleEvents.UnitActionEvent e) {

		StringBuilder s = new StringBuilder();
		s.append('[').append(e.unit.getName()).append(']');

		switch (e.command.getCommandType()) {
		case Move:
			s.append(" moved to ").append(e.command.getTargetCoordinate());
			logLine(s.toString());
			break;
		case Attack:
			s.append(" made physical attack.");
			log(s.toString());
			break;
		case BlackMagic:
		case WhiteMagic:
			s.append(" cast the spell ")
					.append(e.command.getMagicSpell().getName()).append(".");
			log(s.toString());
			break;
		case EndTurn:
			s.append("'s turn has ended.");
			logLine(s.toString());
			logLine();
			break;
		}

	}

	@Subscribe
	public void handleBattleEnded(BattleEvents.BattleEndEvent e) {
		logLine("Battle has ended.");
		controller.unregisterSubscriber(this);
		if (e.winner == controller.player1()) {
			JOptionPane.showMessageDialog(frame, "You won!");
		} else {
			JOptionPane.showMessageDialog(frame,
					"You losed hahaha what a n00b!");
		}
		ended();
	}

	public void log(String text) {
		logger.append(text);
	}

	public void logLine(String line) {
		logger.appendLine(line);
	}

	public void logLine() {
		logger.appendLine("");
	}

	public void logClear() {
		logger.clearText();
	}
}