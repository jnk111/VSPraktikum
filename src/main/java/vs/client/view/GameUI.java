package vs.client.view;

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

public class GameUI {

	private JFrame frame;
	private JPanel contentPane;
	private JTable playerTable;
	
	/**
	 * Create the frame.
	 */
	public GameUI() {
		frame = new JFrame("Restopoly");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 650, 500);
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
		
		JLabel lblUsername = new JLabel("Username");
		lblUsername.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblUsername.setBackground(SystemColor.activeCaption);
		lblUsername.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblUsername);
		
		JLabel lblKontostand = new JLabel("Kontostand:");
		lblKontostand.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblKontostand);
		
		JButton btnWrfeln = new JButton("W\u00FCrfeln");
		panel.add(btnWrfeln);
		
		JButton btnJailbreak = new JButton("Jailbreak");
		panel.add(btnJailbreak);
		
		JButton btnHausKaufen = new JButton("Haus Kaufen");
		panel.add(btnHausKaufen);
		
		JButton btnHausVerkaufen = new JButton("Haus Verkaufen");
		panel.add(btnHausVerkaufen);
		
		JButton btnSpielzugBeenden = new JButton("Spielzug beenden");
		panel.add(btnSpielzugBeenden);
		
		JButton btnAusloggen = new JButton("Ausloggen");
		panel.add(btnAusloggen);
		
		JSplitPane splitPane_1 = new JSplitPane();
		splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setRightComponent(splitPane_1);
		
		JPanel board = new JPanel();
		splitPane_1.setRightComponent(board);
		board.setLayout(new GridLayout(1, 0, 0, 0));
		
		
		JScrollPane playerOverview = new JScrollPane();
		splitPane_1.setLeftComponent(playerOverview);
		splitPane_1.setDividerLocation(150);
		splitPane.setDividerLocation(150);
	}
	
	public void showUI() {
		frame.setVisible(true);
	}

}
