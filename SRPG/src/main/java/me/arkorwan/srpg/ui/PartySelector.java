package me.arkorwan.srpg.ui;

import static me.arkorwan.srpg.SerializerHelper.cloneEntity;

import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import me.arkorwan.srpg.ai.SmartPlayer;
import me.arkorwan.srpg.controller.BattleController;
import me.arkorwan.srpg.generators.BattleGenerator;
import me.arkorwan.srpg.models.Battle;
import me.arkorwan.srpg.models.BattleCell;
import me.arkorwan.srpg.models.Unit;
import me.arkorwan.srpg.models.battlesystem.BattleSystem;
import me.arkorwan.utils.Common;

public class PartySelector extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	JFrame parent;

	UnitDisplayCanvas udcAI;
	UnitDisplayCanvas udcChoices;

	private JLabel lblName, lblUnitDetails;

	UnitInformationPrinter unitPrinter;

	/**
	 * Create the frame.
	 */
	public PartySelector(JFrame parent) {
		this.parent = parent;
		setResizable(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 616, 540);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblAiParty = new JLabel("AI Party");
		lblAiParty.setBounds(22, 22, 72, 15);
		contentPane.add(lblAiParty);

		JLabel lblChooseYourParty = new JLabel(
				"Click to choose your party members");
		lblChooseYourParty.setBounds(22, 160, 293, 15);
		contentPane.add(lblChooseYourParty);

		lblUnitDetails = new JLabel("details");
		lblUnitDetails.setBounds(440, 69, 162, 366);
		contentPane.add(lblUnitDetails);

		lblName = new JLabel("Name");
		lblName.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		lblName.setHorizontalAlignment(SwingConstants.CENTER);
		lblName.setBounds(440, -4, 162, 62);
		contentPane.add(lblName);

		unitPrinter = new UnitInformationPrinter(lblName, lblUnitDetails);
		unitPrinter.clearDisplayedUnit();

		udcAI = new UnitDisplayCanvas(unitPrinter);
		udcAI.setBounds(22, 44, 400, 80);
		contentPane.add(udcAI);

		udcChoices = new UnitDisplayCanvas(unitPrinter);
		udcChoices.setBounds(22, 187, 400, 236);
		contentPane.add(udcChoices);

		JButton btnStart = new JButton("Start >>");
		btnStart.setBounds(214, 444, 208, 54);
		contentPane.add(btnStart);
		btnStart.addActionListener(e -> this.startBattle());

		JButton btnReroll = new JButton("Reroll all  ↻");
		btnReroll.setBounds(22, 444, 180, 25);
		contentPane.add(btnReroll);
		btnReroll.addActionListener(e -> this.reroll());

		JButton btnGetNewCandiates = new JButton("Get new candiates  ↻");
		btnGetNewCandiates.setBounds(22, 473, 180, 25);
		contentPane.add(btnGetNewCandiates);
		btnGetNewCandiates.addActionListener(e -> this.generateCandidates());

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				ended();
			}
		});
	}

	void ended() {
		if (parent != null) {
			parent.setVisible(true);
		}
		dispose();
	}

	BattleSystem sys;
	BattleGenerator battleGen;
	int teamSize;

	Battle battle;
	
	public void setup(BattleSystem sys, int teamSize) {
		this.sys = sys;
		this.teamSize = teamSize;
		battleGen = Common.gsonDeserialize(
				Common.getConfigWithDefault("generator", ""),
				BattleGenerator.class);
		battleGen.setBattleSystem(sys);
		battleGen.setRandom(ThreadLocalRandom.current());
		battleGen.setTeamSize(teamSize);

		generateBattle();
	}

	void startBattle() {

		if (udcChoices.selections != teamSize) {
			JOptionPane.showMessageDialog(this, String
					.format("Please select a party of %d members.", teamSize));
		} else {

			// replace party members
			Unit[] oldMembers = battle.getParty1().getMembers()
					.toArray(new Unit[0]);
			List<Unit> newMembers = udcChoices.getSelectedUnits();

			for (int i = 0; i < teamSize; i++) {
				BattleCell cell = battle.getUnitLocation(oldMembers[i]);
				battle.placeUnitInParty1(newMembers.get(i), cell.getLocation());
			}

			BattleController controller = new BattleController(
					cloneEntity(battle), ManualPlayer.getFactory(),
					SmartPlayer.getFactory());
			BattleViewer viewer = new BattleViewer(this, controller, sys.systemName);
			viewer.show();
			this.setVisible(false);
		}

	}

	void generateBattle() {
		battleGen.refreshQuotas();
		battle = battleGen.generate();
		udcAI.setUnits(0, battle.getParty2().getMembers(), false);
		generateCandidates();
	}

	void generateCandidates() {
		battleGen.refreshQuotas();
		List<Unit> choices = battleGen.generateFullUnits(10, true, true);
		udcChoices.setUnits(teamSize, choices, true);
		udcChoices.repaint();
	}

	void reroll() {
		generateBattle();
		udcAI.repaint();
		udcChoices.repaint();
	}
}
