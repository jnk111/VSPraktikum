package vs.jonas.client.view;

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
 * Diese Klasse beschreibt eine Benutzeroberfl�che, 
 * die das Men� f�r Restopoly darstellt. 
 * 
 * Es gibt einen Button f�r "Offene Spiele" und einen f�r
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
		frame.setBounds(100, 100, 300, 200);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		
		contentPane = new JPanel();
		contentPane.setBackground(SystemColor.activeCaption);
		contentPane.setBorder(new EmptyBorder(5, 75, 5, 75));
		frame.setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(4, 0, 0, 5));
		
		JLabel lblRestopoly = new JLabel("Restopoly");
		lblRestopoly.setFont(new Font("Kristen ITC", Font.BOLD, 18));
		lblRestopoly.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblRestopoly);
		
		btnOffeneSpiele = new JButton("Offene Spiele");
		btnOffeneSpiele.setFont(new Font("Kristen ITC", Font.PLAIN, 11));
		contentPane.add(btnOffeneSpiele);
		
		btnSpielErstellen = new JButton("Spiel Erstellen");
		btnSpielErstellen.setFont(new Font("Kristen ITC", Font.PLAIN, 11));
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

	public JFrame getFrame() {
		return frame;
	}
}
