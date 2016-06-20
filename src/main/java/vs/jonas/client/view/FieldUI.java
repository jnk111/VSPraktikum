package vs.jonas.client.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.SystemColor;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;

import vs.jonas.client.json.Place;

public class FieldUI {

	private JFrame frame;
	private JButton btnHausKaufen;
	private JButton btnHypothekAufnehmen;
	private JButton btnKaufen;
	
	public FieldUI(Place place, boolean isClient){
		frame = new JFrame(place.getName());
		frame.getContentPane().setBackground(SystemColor.inactiveCaption);
		frame.setSize(300, 450);
		frame.setLocation(500,100);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//		frame.setAlwaysOnTop(true);
		JLabel lblNewLabel = new JLabel(place.getName());
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblNewLabel.setForeground(Color.BLACK);
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		frame.getContentPane().add(lblNewLabel, BorderLayout.NORTH);
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setDividerSize(0);
		splitPane.setBackground(SystemColor.activeCaption);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		panel.setBackground(SystemColor.activeCaption);
		splitPane.setLeftComponent(panel);
		panel.setLayout(new BorderLayout(0, 0));
		
		ImageIcon fieldImage = new ImageIcon(FieldUI.class.getResource("/house.gif"));
		JLabel lblNewLabel_1 = new JLabel(fieldImage); // Todo ImageFinder
//		lblNewLabel_1.setIcon(new ImageIcon(FieldUI.class.getResource("/vs/jonas/client/assets/monopolyKopf.png")));
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblNewLabel_1, BorderLayout.CENTER);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBackground(SystemColor.inactiveCaption);
		splitPane.setRightComponent(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_2 = new JPanel();
		panel_2.setBackground(SystemColor.inactiveCaption);
		panel_1.add(panel_2);
		panel_2.setLayout(new GridLayout(0, 2, 0, 0));
		
		JLabel lblOwner = new JLabel("Owner:");
		panel_2.add(lblOwner);
		lblOwner.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblOwner.setForeground(Color.BLACK);
		lblOwner.setHorizontalAlignment(SwingConstants.CENTER);
		JLabel lblOwnerresult = new JLabel(place.getOwner());
		panel_2.add(lblOwnerresult);
		lblOwnerresult.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblOwnerresult.setForeground(Color.BLACK);
		
		JLabel lblValue = new JLabel("Value:");
		panel_2.add(lblValue);
		lblValue.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblValue.setForeground(Color.BLACK);
		lblValue.setHorizontalAlignment(SwingConstants.CENTER);
		JLabel lblValueresult = new JLabel(place.getValue()+" Rubel");
		panel_2.add(lblValueresult);
		lblValueresult.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblValueresult.setForeground(Color.BLACK);
		
		JLabel lblRent = new JLabel("Rent:");
		panel_2.add(lblRent);
		lblRent.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblRent.setForeground(Color.BLACK);
		lblRent.setHorizontalAlignment(SwingConstants.CENTER);
		JLabel lblRentresult = new JLabel(place.getRent()+" Rubel");
		panel_2.add(lblRentresult);
		lblRentresult.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblRentresult.setForeground(Color.BLACK);
		
		JLabel lblHouses = new JLabel("Houses:");
		panel_2.add(lblHouses);
		lblHouses.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblHouses.setForeground(Color.BLACK);
		lblHouses.setHorizontalAlignment(SwingConstants.CENTER);
		JLabel lblHousesresult = new JLabel(place.getHouses()+"");
		panel_2.add(lblHousesresult);
		lblHousesresult.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblHousesresult.setForeground(Color.BLACK);
		lblHousesresult.setHorizontalAlignment(SwingConstants.LEFT);
		
		JLabel lblCost = new JLabel("Cost:");
		panel_2.add(lblCost);
		lblCost.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblCost.setForeground(Color.BLACK);
		lblCost.setHorizontalAlignment(SwingConstants.CENTER);
		JLabel lblCostresult = new JLabel(place.getCost()+" Rubel");
		panel_2.add(lblCostresult);
		lblCostresult.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblCostresult.setForeground(Color.BLACK);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBackground(SystemColor.inactiveCaption);
		panel_1.add(panel_3, BorderLayout.SOUTH);
		
		btnHausKaufen = new JButton("Haus Kaufen");
		btnHypothekAufnehmen = new JButton("Hypothek Aufnehmen");
		btnKaufen = new JButton("Kaufen");
		
		if(isClient){
			panel_3.add(btnHausKaufen);
			panel_3.add(btnHypothekAufnehmen);
		} else{
			panel_3.add(btnKaufen);
		}
		
		
		splitPane.setDividerLocation(200);
	}
	
	/** Returns an ImageIcon, or null if the path was invalid. */
	protected ImageIcon createImageIcon(String path, String description) {
	    URL imgURL = getClass().getResource(path);
	    if (imgURL != null) {
	        return new ImageIcon(imgURL, description);
	    } else {
	        System.err.println("Couldn't find file: " + path);
	        return null;
	    }
	}
	
	public void showUI(){
		frame.setVisible(true);
	}

	public JButton getBtnHausKaufen() {
		return btnHausKaufen;
	}

	public JButton getBtnHypothekAufnehmen() {
		return btnHypothekAufnehmen;
	}

	public JButton getBtnKaufen() {
		return btnKaufen;
	}

	public JFrame getFrame() {
		return frame;
	}
	
}
