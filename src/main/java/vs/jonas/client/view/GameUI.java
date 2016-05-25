package vs.jonas.client.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.SystemColor;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import vs.jonas.client.model.tablemodel.PlayerOverviewTableModel;

public class GameUI {

	private JFrame frame;
	private JPanel contentPane;
	private JTable playerTable;
	private JButton btnSpielzugBeenden;
	private JButton btnJailbreak;
	private JButton btnHausKaufen;
	private JButton btnHausVerkaufen;
	private JButton btnWrfeln;
	private JLabel lblKontostand;
	private JSplitPane gameOverviewPanel;
	private JScrollPane currentFieldPanel;
	private JScrollPane gameFieldPanel;
	private JLabel lblUsername;

	/**
	 * Create the frame.
	 */
	public GameUI() {
		frame = new JFrame("Restopoly");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 850, 500);
		contentPane = new JPanel();
		contentPane.setBackground(SystemColor.activeCaption);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		frame.setLocationRelativeTo(null);
		frame.setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		JSplitPane splitPane = new JSplitPane();
		splitPane.setBackground(SystemColor.activeCaption);
		contentPane.add(splitPane, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		panel.setBackground(SystemColor.activeCaption);
		splitPane.setLeftComponent(panel);
		panel.setLayout(new GridLayout(8, 0, 0, 0));

		lblUsername = new JLabel("Username");
		lblUsername.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblUsername.setBackground(SystemColor.activeCaption);
		lblUsername.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblUsername);

		lblKontostand = new JLabel("Kontostand:");
		lblKontostand.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblKontostand);

		btnWrfeln = new JButton("W\u00FCrfeln");
		panel.add(btnWrfeln);

		btnJailbreak = new JButton("Jailbreak");
		panel.add(btnJailbreak);

		btnHausKaufen = new JButton("Haus Kaufen");
		panel.add(btnHausKaufen);

		btnHausVerkaufen = new JButton("Haus Verkaufen");
		panel.add(btnHausVerkaufen);

		btnSpielzugBeenden = new JButton("Spielzug beenden");
		panel.add(btnSpielzugBeenden);

		JButton btnAusloggen = new JButton("Ausloggen");
		panel.add(btnAusloggen);

		JSplitPane splitPane_1 = new JSplitPane();
		splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setRightComponent(splitPane_1);

		playerTable = new JTable(new PlayerOverviewTableModel());
		JScrollPane playerOverview = new JScrollPane(playerTable);
		splitPane_1.setLeftComponent(playerOverview);
		
		gameOverviewPanel = new JSplitPane();
		splitPane_1.setRightComponent(gameOverviewPanel);
		
		currentFieldPanel = new JScrollPane();
		gameOverviewPanel.setLeftComponent(currentFieldPanel);
		
		gameFieldPanel = new JScrollPane();
		gameOverviewPanel.setRightComponent(gameFieldPanel);
		gameOverviewPanel.setDividerLocation(200);
		splitPane_1.setDividerLocation(150);
		splitPane.setDividerLocation(150);
	}

	public JTable getPlayerTable() {
		return playerTable;
	}

	public JButton getBtnSpielzugBeenden() {
		return btnSpielzugBeenden;
	}

	public JButton getBtnJailbreak() {
		return btnJailbreak;
	}

	public JButton getBtnHausKaufen() {
		return btnHausKaufen;
	}

	public JButton getBtnHausVerkaufen() {
		return btnHausVerkaufen;
	}

	public JButton getBtnWrfeln() {
		return btnWrfeln;
	}

	public JLabel getLblKontostand() {
		return lblKontostand;
	}
	
	public JLabel getUsername(){
		return lblUsername;
	}

	public void showUI() {
		frame.setVisible(true);
	}

}
