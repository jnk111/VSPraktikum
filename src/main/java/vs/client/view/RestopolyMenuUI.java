package vs.client.view;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.SystemColor;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * Diese Klasse beschreibt eine Benutzeroberfläche, 
 * die das Menü für Restopoly darstellt. 
 * 
 * Es gibt einen Button für "Offene Spiele" und einen für
 * "Spiel Erstellen".
 * 
 * @author Jones
 *
 */
public class RestopolyMenuUI {

	private JPanel contentPane;
	private JFrame frame;
	private JButton btnOffeneSpiele;
	private JButton btnSpielErstellen;


	/**
	 * Create the frame.
	 */
	public RestopolyMenuUI() {
		frame = new JFrame("Restopoly");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 450, 300);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		
		contentPane = new JPanel();
		contentPane.setBackground(SystemColor.activeCaption);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		frame.setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(4, 0, 0, 0));
		
		JLabel lblRestopoly = new JLabel("Restopoly");
		lblRestopoly.setFont(new Font("Kristen ITC", Font.BOLD, 18));
		lblRestopoly.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblRestopoly);
		
		btnOffeneSpiele = new JButton("Offene Spiele");
		contentPane.add(btnOffeneSpiele);
		
		btnSpielErstellen = new JButton("Spiel Erstellen");
		contentPane.add(btnSpielErstellen);
	}

	public JButton getBtnOffeneSpiele() {
		return btnOffeneSpiele;
	}
	
	public JButton getBtnSpielErstellen() {
		return btnSpielErstellen;
	}
	
	public void showUI(){
		frame.setVisible(true);
	}
}
