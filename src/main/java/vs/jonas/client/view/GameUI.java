package vs.jonas.client.view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.SystemColor;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import vs.jonas.client.model.table.GameFieldTable;
import vs.jonas.client.model.table.tablemodel.GameFieldTableModel;
import vs.jonas.client.model.table.tablemodel.PlayerOverviewTableModel;

public class GameUI {

	private JFrame frame;
	private JPanel contentPane;
	private JTable playerTable;
	private JTable gameFieldTable;
	private JMenuBar menuBar;
	private JMenu mnSpielStarten;
	private JMenu mnHilfe;
	private JMenuItem mntmRegeln;
	private JMenuItem mntmStarten;
	private JSplitPane splitPane_2;
	private JPanel panel;
	private JPanel panel_1;
	private JComboBox<String> aktionen;
	private JButton btnAusfhren;
	private JLabel lblNewLabel;
	private JLabel userLbl;
	private JTextArea eventsConsole;

	/**
	 * Create the frame.
	 */
	public GameUI() {
		frame = new JFrame("Restopoly");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 850, 500);
		frame.setLocationRelativeTo(null);
//		frame.setBackground(SystemColor.blue);
//		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		contentPane = new JPanel();
//		contentPane.setBackground(SystemColor.activeCaption);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		menuBar = new JMenuBar();
//		menuBar.setBackground(SystemColor.activeCaption);
		frame.setJMenuBar(menuBar);
		
		mnSpielStarten = new JMenu("Spiel Starten");
		menuBar.add(mnSpielStarten);
		
		mntmStarten = new JMenuItem("Starten");
		mnSpielStarten.add(mntmStarten);
		
		mnHilfe = new JMenu("Hilfe");
		menuBar.add(mnHilfe);
		
		mntmRegeln = new JMenuItem("Regeln");
		mnHilfe.add(mntmRegeln);
		frame.setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
//		splitPane.setBackground(SystemColor.activeCaption);
		contentPane.add(splitPane, BorderLayout.CENTER);

		JPanel optionsPanel = new JPanel();
//		optionsPanel.setBackground(SystemColor.activeCaption);
		JSplitPane splitPane_1 = new JSplitPane();
		splitPane.setRightComponent(splitPane_1);

		playerTable = new JTable(new PlayerOverviewTableModel());
		JScrollPane playerOverview = new JScrollPane(playerTable);
		JPanel playerPanel = new JPanel(new BorderLayout());
		JLabel playerHeader = new JLabel("********** Spieler **********");
		playerHeader.setFont(new Font("Kristen ITC", Font.PLAIN, 14));
		playerHeader.setHorizontalAlignment(SwingConstants.CENTER);
		playerPanel.add(playerHeader, BorderLayout.NORTH);
		playerPanel.add(playerOverview, BorderLayout.CENTER);
		splitPane_1.setLeftComponent(optionsPanel);
		optionsPanel.setLayout(new BorderLayout(0, 0));
				
		splitPane_2 = new JSplitPane();
		splitPane_2.setOrientation(JSplitPane.VERTICAL_SPLIT);
		optionsPanel.add(splitPane_2);
		
		panel = new JPanel();
		panel.setBorder(new EmptyBorder(10, 60, 10, 60));
		splitPane_2.setLeftComponent(panel);
		panel.setLayout(new GridLayout(0, 1, 0, 0));
		
		userLbl = new JLabel("Username");
		userLbl.setHorizontalTextPosition(SwingConstants.CENTER);
		userLbl.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(userLbl);
		
		aktionen = new JComboBox<String>();
		aktionen.addItem("Wuerfeln");
		aktionen.addItem("Kaufen");
		aktionen.addItem("Verkaufen");
		aktionen.addItem("Ereigniskarte spielen");
		aktionen.addItem("Zug beenden");
		panel.add(aktionen);
		
		btnAusfhren = new JButton("Ausf\u00FChren");
		panel.add(btnAusfhren);
		
		panel_1 = new JPanel();
		splitPane_2.setRightComponent(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		lblNewLabel = new JLabel("Received Events:");
		panel_1.add(lblNewLabel, BorderLayout.NORTH);
		
		eventsConsole = new JTextArea();
		eventsConsole.setForeground(SystemColor.text);
		eventsConsole.setFont(new Font("Monospaced", Font.PLAIN, 13));
		eventsConsole.setBackground(SystemColor.activeCaptionBorder);
		eventsConsole.setEditable(false);
		eventsConsole.append("#Initial Entry");
		panel_1.add(eventsConsole, BorderLayout.CENTER);
		splitPane_2.setDividerLocation(100);
		splitPane.setLeftComponent(playerPanel);
		
		gameFieldTable = new GameFieldTable(new GameFieldTableModel());

		JScrollPane gameFieldScrollPane = new JScrollPane(gameFieldTable);
		JPanel gameFieldPanel = new JPanel(new BorderLayout());
		JLabel gameFieldHeader = new JLabel("********** Spielfeld **********");
		gameFieldHeader.setFont(new Font("Kristen ITC", Font.PLAIN, 14));
		gameFieldHeader.setHorizontalAlignment(SwingConstants.CENTER);
		gameFieldPanel.add(gameFieldHeader, BorderLayout.NORTH);
		
		gameFieldPanel.add(gameFieldScrollPane, BorderLayout.CENTER);
		splitPane_1.setRightComponent(gameFieldPanel);
		splitPane_1.setDividerLocation(300);
		splitPane.setDividerLocation(150);
	}

	public JTable getPlayerTable() {
		return playerTable;
	}
	
	public JTable getGameFIeldTable(){
		return gameFieldTable;
	}

	public void showUI() {
		frame.setVisible(true);
	}
	
	public JComboBox<String> getAktionen(){
		return aktionen;
	}

	public JButton getAktionAusfuehrenBtn(){
		return btnAusfhren;
	}
	
	public JTextArea getEventsConsole(){
		return eventsConsole;
	}
	
	public JLabel getUsernameLbl(){
		return userLbl;
	}
	
	public JMenuItem getSpielStartenMenuItem(){
		return mntmStarten;
	}
	
	public static void main(String[] args) {
		new GameUI().showUI();
	}
}
