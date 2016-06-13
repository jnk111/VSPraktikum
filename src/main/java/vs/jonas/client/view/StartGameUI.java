package vs.jonas.client.view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.SystemColor;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;

import vs.jonas.client.model.table.tablemodel.PlayerStartGameScreenModel;

public class StartGameUI {
	
	private JFrame frame;
	private JTable playerTable;
	private JButton btnStartGame;
	
	public StartGameUI(){
		frame = new JFrame();
		frame.setSize(500, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		
		JPanel panel = new JPanel();
		panel.setBackground(SystemColor.activeCaption);
		
		frame.getContentPane().add(panel);
		panel.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_1 = new JPanel();
		panel_1.setBackground(SystemColor.activeCaption);
		panel.add(panel_1, BorderLayout.NORTH);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		JLabel lblStartscreenWait = new JLabel("Startscreen - Wait for players...");
		lblStartscreenWait.setFont(new Font("Kristen ITC", Font.PLAIN, 14));
		panel_1.add(lblStartscreenWait, BorderLayout.NORTH);
		
		playerTable = new JTable(new PlayerStartGameScreenModel());
		JScrollPane scrollPane = new JScrollPane(playerTable);
		panel.add(scrollPane, BorderLayout.CENTER);
		
		JPanel panel_2 = new JPanel();
		panel.add(panel_2, BorderLayout.SOUTH);
		panel_2.setLayout(new BorderLayout(0, 0));
		
		btnStartGame = new JButton("Start Game");
		btnStartGame.setBackground(UIManager.getColor("Button.highlight"));
		btnStartGame.setFont(new Font("Kristen ITC", Font.PLAIN, 11));
		panel_2.add(btnStartGame, BorderLayout.NORTH);
	}
	
	public JTable getTable(){
		return playerTable;
	}
	
	public void showUI(){
		frame.setVisible(true);
	}
	
	public static void main(String[] args) {
		new StartGameUI().showUI();
	}

	public JButton getBtnStartGame() {
		return btnStartGame;
	}

	public JFrame getFrame() {
		return frame;
	}
}
