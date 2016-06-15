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

public class FieldUI {

	private JFrame frame;
	
	public FieldUI(Place place){
		frame = new JFrame(place.getName());
		frame.setSize(300, 450);
		frame.setLocation(500,100);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//		frame.setAlwaysOnTop(true);
		JLabel lblNewLabel = new JLabel(place.getName());
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		frame.getContentPane().add(lblNewLabel, BorderLayout.NORTH);
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		splitPane.setLeftComponent(panel);
		panel.setLayout(new BorderLayout(0, 0));
		
		ImageIcon fieldImage = new ImageIcon(FieldUI.class.getResource("/monopoly.jpg"));
		JLabel lblNewLabel_1 = new JLabel(fieldImage); // Todo ImageFinder
//		lblNewLabel_1.setIcon(new ImageIcon(FieldUI.class.getResource("/vs/jonas/client/assets/monopolyKopf.png")));
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblNewLabel_1, BorderLayout.CENTER);
		
		JPanel panel_1 = new JPanel();
		splitPane.setRightComponent(panel_1);
		panel_1.setLayout(new GridLayout(0, 2, 5, 5));
		
		JLabel lblOwner = new JLabel("Owner:");
		lblOwner.setHorizontalAlignment(SwingConstants.CENTER);
		panel_1.add(lblOwner);
		JLabel lblOwnerresult = new JLabel(place.getOwner());
		panel_1.add(lblOwnerresult);
		
		JLabel lblValue = new JLabel("Value:");
		lblValue.setHorizontalAlignment(SwingConstants.CENTER);
		panel_1.add(lblValue);
		JLabel lblValueresult = new JLabel(place.getValue()+"");
		panel_1.add(lblValueresult);
		
		JLabel lblRent = new JLabel("Rent:");
		lblRent.setHorizontalAlignment(SwingConstants.CENTER);
		panel_1.add(lblRent);
		JLabel lblRentresult = new JLabel(place.getRent()+"");
		panel_1.add(lblRentresult);
		
		JLabel lblHouses = new JLabel("Houses:");
		lblHouses.setHorizontalAlignment(SwingConstants.CENTER);
		panel_1.add(lblHouses);
		JLabel lblHousesresult = new JLabel(place.getHouses()+"");
		lblHousesresult.setHorizontalAlignment(SwingConstants.LEFT);
		panel_1.add(lblHousesresult);
		
		JLabel lblCost = new JLabel("Cost:");
		lblCost.setHorizontalAlignment(SwingConstants.CENTER);
		panel_1.add(lblCost);
		JLabel lblCostresult = new JLabel(place.getCost()+"");
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