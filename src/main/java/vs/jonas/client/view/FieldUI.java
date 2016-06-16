package vs.jonas.client.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;

import vs.jonas.client.json.Place;
import java.awt.SystemColor;
import java.awt.Color;
import java.awt.Font;

public class FieldUI {

	private JFrame frame;
	
	public FieldUI(Place place){
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
		panel_1.setLayout(new GridLayout(0, 2, 5, 5));
		
		JLabel lblOwner = new JLabel("Owner:");
		lblOwner.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblOwner.setForeground(Color.BLACK);
		lblOwner.setHorizontalAlignment(SwingConstants.CENTER);
		panel_1.add(lblOwner);
		JLabel lblOwnerresult = new JLabel(place.getOwner());
		lblOwnerresult.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblOwnerresult.setForeground(Color.BLACK);
		panel_1.add(lblOwnerresult);
		
		JLabel lblValue = new JLabel("Value:");
		lblValue.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblValue.setForeground(Color.BLACK);
		lblValue.setHorizontalAlignment(SwingConstants.CENTER);
		panel_1.add(lblValue);
		JLabel lblValueresult = new JLabel(place.getValue()+" Rubel");
		lblValueresult.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblValueresult.setForeground(Color.BLACK);
		panel_1.add(lblValueresult);
		
		JLabel lblRent = new JLabel("Rent:");
		lblRent.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblRent.setForeground(Color.BLACK);
		lblRent.setHorizontalAlignment(SwingConstants.CENTER);
		panel_1.add(lblRent);
		JLabel lblRentresult = new JLabel(place.getRent()+" Rubel");
		lblRentresult.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblRentresult.setForeground(Color.BLACK);
		panel_1.add(lblRentresult);
		
		JLabel lblHouses = new JLabel("Houses:");
		lblHouses.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblHouses.setForeground(Color.BLACK);
		lblHouses.setHorizontalAlignment(SwingConstants.CENTER);
		panel_1.add(lblHouses);
		JLabel lblHousesresult = new JLabel(place.getHouses()+"");
		lblHousesresult.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblHousesresult.setForeground(Color.BLACK);
		lblHousesresult.setHorizontalAlignment(SwingConstants.LEFT);
		panel_1.add(lblHousesresult);
		
		JLabel lblCost = new JLabel("Cost:");
		lblCost.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblCost.setForeground(Color.BLACK);
		lblCost.setHorizontalAlignment(SwingConstants.CENTER);
		panel_1.add(lblCost);
		JLabel lblCostresult = new JLabel(place.getCost()+" Rubel");
		lblCostresult.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblCostresult.setForeground(Color.BLACK);
		panel_1.add(lblCostresult);
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
}
