package me.arkorwan.srpg.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class LoggingDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6629533737889761889L;
	private final JPanel contentPanel = new JPanel();

	JTextArea txtrLogging;

	/**
	 * Create the dialog.
	 */
	public LoggingDialog() {
		setBounds(100, 100, 569, 378);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setVerticalScrollBarPolicy(
					ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			scrollPane.setHorizontalScrollBarPolicy(
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			contentPanel.add(scrollPane, BorderLayout.CENTER);

			txtrLogging = new JTextArea();
			scrollPane.setViewportView(txtrLogging);
			txtrLogging.setLineWrap(true);
			txtrLogging.setEditable(false);
			clearText();
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
				okButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						setVisible(false);
					}
				});
			}
		}
	}

	// warning: not thread safe!
	private static LoggingDialog instance;

	public static LoggingDialog getInstance() {
		if (instance == null) {
			instance = new LoggingDialog();
			instance.setModal(false);
		}
		return instance;
	}

	public LoggingDialog showDialog() {
		setVisible(true);
		// bring to front!
		// http://stackoverflow.com/questions/309023/how-to-bring-a-window-to-the-front
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				toFront();
				repaint();
			}
		});
		return instance;
	}
	
	public void append(String text) {
		txtrLogging.append(text);
	}
	
	public void appendLine(String text) {
		txtrLogging.append(text);
		txtrLogging.append(System.lineSeparator());
		txtrLogging.setCaretPosition(txtrLogging.getDocument().getLength());
	}

	public void clearText() {
		txtrLogging.setText("");
	}
}
