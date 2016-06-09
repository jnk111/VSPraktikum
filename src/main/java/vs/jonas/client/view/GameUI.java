package vs.jonas.client.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
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

import vs.jonas.client.model.table.GameFieldTable;
import vs.jonas.client.model.table.tablemodel.GameFieldTableModel;
import vs.jonas.client.model.table.tablemodel.PlayerOverviewTableModel;

public class GameUI {

	private JFrame frame;
	private JPanel contentPane;
	private JTable playerTable;
	private JTable gameFieldTable;
	// Wir brauchen folgende Aktionen: Wuerfeln, Jailbreak, Haus kaufen, Haus verkaufen, Handeln, Hypothek aufnehmen, 
	private JButton btnSpielzugBeenden;
	private JButton btnJailbreak;
	private JButton btnHausKaufen;
	private JButton btnHausVerkaufen;
	private JButton btnWrfeln;
	private JLabel lblUsername;
	private JButton btnStarten;

	/**
	 * Create the frame.
	 */
	public GameUI() {
		frame = new JFrame("Restopoly");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 850, 500);
//		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
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
		
		btnStarten = new JButton("Starten");
		panel.add(btnStarten);

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
		JPanel playerPanel = new JPanel(new BorderLayout());
		JLabel playerHeader = new JLabel("********** Spieler **********");
		playerHeader.setFont(new Font("Kristen ITC", Font.PLAIN, 14));
		playerHeader.setHorizontalAlignment(SwingConstants.CENTER);
		playerPanel.add(playerHeader, BorderLayout.NORTH);
		playerPanel.add(playerOverview, BorderLayout.CENTER);
		splitPane_1.setLeftComponent(playerPanel);
		
		gameFieldTable = new GameFieldTable(new GameFieldTableModel());

		JScrollPane gameFieldScrollPane = new JScrollPane(gameFieldTable);
		JPanel gameFieldPanel = new JPanel(new BorderLayout());
		JLabel gameFieldHeader = new JLabel("********** Spielfeld **********");
		gameFieldHeader.setFont(new Font("Kristen ITC", Font.PLAIN, 14));
		gameFieldHeader.setHorizontalAlignment(SwingConstants.CENTER);
		gameFieldPanel.add(gameFieldHeader, BorderLayout.NORTH);
		
		gameFieldPanel.add(gameFieldScrollPane, BorderLayout.CENTER);
		splitPane_1.setRightComponent(gameFieldPanel);
		splitPane_1.setDividerLocation(150);
		splitPane.setDividerLocation(150);
	}

	public JTable getPlayerTable() {
		return playerTable;
	}
	
	public JTable getGameFIeldTable(){
		return gameFieldTable;
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

	public JButton getBtnStarten() {
		return btnStarten;
	}
	
	public JLabel getUserLabel(){
		return lblUsername;
	}

	public void showUI() {
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		new GameUI().showUI();
	}
}
