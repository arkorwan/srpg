package me.arkorwan.srpg.ui;

import java.awt.EventQueue;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;

import me.arkorwan.srpg.models.battlesystem.BattleSystem;
import me.arkorwan.utils.Common;

public class BattleStarter extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BattleStarter frame = new BattleStarter();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	static Map<String, String> systems = new HashMap<>();

	static {
		String path = Common.getConfigWithDefault("battle_systems", ".");
		try {
			Files.walk(Paths.get(path)).filter(f -> Files.isRegularFile(f))
					.forEach(f -> {
						String name = f.getFileName().toString();
						if (name.endsWith(".json")) {
							systems.put(name.substring(0, name.length() - 5),
									f.toString());
						}

					});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the frame.
	 */
	public BattleStarter() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 362, 246);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblBattleSystem = new JLabel("Battle system");
		lblBattleSystem.setBounds(37, 16, 111, 16);
		contentPane.add(lblBattleSystem);

		JComboBox<String> cmbBattleSystem = new JComboBox<>();
		for (String s : systems.keySet()) {
			cmbBattleSystem.addItem(s);
		}
		cmbBattleSystem.setBounds(166, 12, 153, 27);
		cmbBattleSystem.setSelectedIndex(0);
		contentPane.add(cmbBattleSystem);

		JLabel lblPartySize = new JLabel("Party size");
		lblPartySize.setBounds(37, 57, 111, 16);
		contentPane.add(lblPartySize);

		JSlider sliderPartySize = new JSlider();
		sliderPartySize.setMajorTickSpacing(1);
		sliderPartySize.setSnapToTicks(true);
		sliderPartySize.setPaintLabels(true);
		sliderPartySize.setValue(4);
		sliderPartySize.setMinimum(1);
		sliderPartySize.setMaximum(4);
		sliderPartySize.setBounds(166, 58, 153, 41);
		contentPane.add(sliderPartySize);

		JButton btnStart = new JButton("Next >>");
		btnStart.setBounds(37, 130, 282, 29);
		btnStart.addActionListener(e -> {

			PartySelector selector = new PartySelector(this);
			String systemName = cmbBattleSystem.getSelectedItem().toString();
			BattleSystem sys = Common.gsonDeserialize(systems.get(systemName),
					BattleSystem.class);
			sys.systemName = systemName;
			int teamSize = sliderPartySize.getValue();
			selector.setup(sys, teamSize);
			selector.setVisible(true);
			this.setVisible(false);
		});
		contentPane.add(btnStart);

	}
}
