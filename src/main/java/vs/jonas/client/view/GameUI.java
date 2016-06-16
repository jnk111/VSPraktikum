package vs.jonas.client.view;

import java.awt.BorderLayout;
import java.awt.Color;
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
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

import vs.jonas.client.model.table.GameFieldTable;
import vs.jonas.client.model.table.renderer.PlayerTableCellRenderer;
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
	private int receivedEventNumber;
	
	private static final Color GAMEFIELD_COLOR = new Color(204, 255, 204);
	private static final Color TABLEHEADER_COLOR = new Color(200, 255, 190);

	/**
	 * Create the frame.
	 */
	public GameUI() {
		frame = new JFrame("Restopoly");
		frame.setBackground(SystemColor.activeCaption);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 850, 500);
		frame.setLocationRelativeTo(null);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		contentPane = new JPanel();
		contentPane.setBackground(SystemColor.inactiveCaption);
		contentPane.setOpaque(false);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		menuBar = new JMenuBar();
		menuBar.setBackground(SystemColor.activeCaption);
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
		splitPane.setDividerSize(0);
		splitPane.setBackground(SystemColor.inactiveCaption);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		contentPane.add(splitPane, BorderLayout.CENTER);

		JPanel optionsPanel = new JPanel();
		optionsPanel.setBackground(SystemColor.inactiveCaption);
		JSplitPane splitPane_1 = new JSplitPane();
		splitPane_1.setDividerSize(0);
		splitPane_1.setBackground(SystemColor.inactiveCaption);
		splitPane.setRightComponent(splitPane_1);

		playerTable = new JTable(new PlayerOverviewTableModel());
		playerTable.getTableHeader().setOpaque(false);
		playerTable.getTableHeader().setBackground(SystemColor.inactiveCaption);
		playerTable.setOpaque(false);
		playerTable.setDefaultRenderer(String.class, new PlayerTableCellRenderer());
		
		JScrollPane playerOverview = new JScrollPane(playerTable);
		playerOverview.getViewport().setBackground(SystemColor.inactiveCaption);
		playerOverview.setBackground(SystemColor.inactiveCaption);
		JPanel playerPanel = new JPanel(new BorderLayout());
		playerPanel.setBackground(SystemColor.inactiveCaption);
		JLabel playerHeader = new JLabel("********** Spieler **********");
		playerHeader.setForeground(Color.BLACK);
		playerHeader.setFont(new Font("Kristen ITC", Font.PLAIN, 14));
		playerHeader.setHorizontalAlignment(SwingConstants.CENTER);
		playerPanel.add(playerHeader, BorderLayout.NORTH);
		playerPanel.add(playerOverview, BorderLayout.CENTER);
		splitPane_1.setLeftComponent(optionsPanel);
		optionsPanel.setLayout(new BorderLayout(0, 0));
				
		splitPane_2 = new JSplitPane();
		splitPane_2.setDividerSize(0);
		splitPane_2.setBackground(SystemColor.inactiveCaption);
		splitPane_2.setOrientation(JSplitPane.VERTICAL_SPLIT);
		optionsPanel.add(splitPane_2);
		
		panel = new JPanel();
		panel.setBackground(SystemColor.inactiveCaption);
		panel.setBorder(new EmptyBorder(10, 100, 10, 100));
		splitPane_2.setLeftComponent(panel);
		panel.setLayout(new GridLayout(0, 1, 0, 5));
		
		userLbl = new JLabel("Username");
		userLbl.setForeground(Color.BLACK);
		userLbl.setFont(new Font("Kristen ITC", Font.BOLD, 14));
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
		panel_1.setBackground(SystemColor.activeCaption);
		splitPane_2.setRightComponent(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		lblNewLabel = new JLabel("Received Events:");
		lblNewLabel.setFont(new Font("Kristen ITC", Font.PLAIN, 11));
		lblNewLabel.setForeground(Color.BLACK);
		lblNewLabel.setBorder(new EmptyBorder(10, 0, 5, 0));
		lblNewLabel.setBackground(SystemColor.activeCaption);
		panel_1.add(lblNewLabel, BorderLayout.NORTH);
		
		receivedEventNumber = 0; // Initial Value
		eventsConsole = new JTextArea();
		eventsConsole.setForeground(SystemColor.text);
		eventsConsole.setFont(new Font("Monospaced", Font.PLAIN, 13));
		eventsConsole.setBackground(new Color(70, 130, 180));
		eventsConsole.setEditable(false);
		eventsConsole.append("#Initial Entry\n");
		eventsConsole.setLineWrap(true);
		eventsConsole.setWrapStyleWord(true);
		
		DefaultCaret caret = (DefaultCaret)eventsConsole.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		JScrollPane textPane = new JScrollPane(eventsConsole);
		textPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		textPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
//		textPane.setAutoscrolls(true);
		panel_1.add(textPane, BorderLayout.CENTER);
		splitPane_2.setDividerLocation(100);
		splitPane.setLeftComponent(playerPanel);
		
		gameFieldTable = new GameFieldTable(new GameFieldTableModel());
		gameFieldTable.getTableHeader().setOpaque(false);
		gameFieldTable.getTableHeader().setBackground(TABLEHEADER_COLOR);

		JScrollPane gameFieldScrollPane = new JScrollPane(gameFieldTable);
		gameFieldScrollPane.getViewport().setBackground(GAMEFIELD_COLOR);
		JPanel gameFieldPanel = new JPanel(new BorderLayout());
		gameFieldPanel.setBackground(SystemColor.inactiveCaption);
		JLabel gameFieldHeader = new JLabel("********** Spielfeld **********");
		gameFieldHeader.setBackground(SystemColor.activeCaption);
		gameFieldHeader.setForeground(Color.BLACK);
		gameFieldHeader.setFont(new Font("Kristen ITC", Font.PLAIN, 14));
		gameFieldHeader.setHorizontalAlignment(SwingConstants.CENTER);
		gameFieldPanel.add(gameFieldHeader, BorderLayout.NORTH);
		
		gameFieldPanel.add(gameFieldScrollPane, BorderLayout.CENTER);
		splitPane_1.setRightComponent(gameFieldPanel);
		splitPane_1.setDividerLocation(450);
		splitPane.setDividerLocation(120);
	}

	public JTable getPlayerTable() {
		return playerTable;
	}
	
	public JTable getGameFieldTable(){
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
	
	public int getEventNumber(){
		return receivedEventNumber;
	}
	
	public void setEventNumber(int number){
		this.receivedEventNumber = number;
	}

	public JFrame getFrame() {
		return frame;
	}
}
