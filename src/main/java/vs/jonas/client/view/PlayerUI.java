package vs.jonas.client.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.net.URL;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import vs.jonas.client.json.Place;
import vs.jonas.client.model.Player;

public class PlayerUI {


	private JFrame frame;
	
	public PlayerUI(Player player){
		frame = new JFrame(player.getName());
		frame.setSize(300, 450);
		frame.setLocation(500,100);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//		frame.setAlwaysOnTop(true);
		JLabel lblNewLabel = new JLabel(player.getName());
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		frame.getContentPane().add(lblNewLabel, BorderLayout.NORTH);
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setDividerSize(0);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);
		
		JSplitPane dataPane = new JSplitPane();
		dataPane.setDividerSize(0);
		dataPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setLeftComponent(dataPane);
		
		JPanel panel = new JPanel();
		dataPane.setLeftComponent(panel);
		panel.setLayout(new BorderLayout(0, 0));
		
		ImageIcon fieldImage = new ImageIcon(FieldUI.class.getResource("/stick_man.gif"));
		JLabel lblNewLabel_1 = new JLabel(fieldImage); // Todo ImageFinder
//		lblNewLabel_1.setIcon(new ImageIcon(FieldUI.class.getResource("/vs/jonas/client/assets/monopolyKopf.png")));
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblNewLabel_1, BorderLayout.CENTER);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new EmptyBorder(10, 0, 10, 0));
		dataPane.setRightComponent(panel_1);
		panel_1.setLayout(new GridLayout(0, 2, 5, 5));
		
		JLabel lblOwner = new JLabel("Kontostand:");
		lblOwner.setHorizontalAlignment(SwingConstants.CENTER);
		panel_1.add(lblOwner);
		JLabel lblOwnerresult = new JLabel(player.getAccount());
		panel_1.add(lblOwnerresult);
		
		JLabel lblValue = new JLabel("Average DiceRoll:");
		lblValue.setHorizontalAlignment(SwingConstants.CENTER);
		panel_1.add(lblValue);
		JLabel lblValueresult = new JLabel(player.getAverageDiceRoll()+"%");
		panel_1.add(lblValueresult);
		
		JPanel panel_2 = new JPanel();
		splitPane.setRightComponent(panel_2);
		panel_2.setLayout(new BorderLayout(0, 0));
		
		JLabel lblGrundstckeImBesitz = new JLabel("Grundst\u00FCcke im Besitz:");
		lblGrundstckeImBesitz.setBorder(new EmptyBorder(5, 0, 5, 0));
		panel_2.add(lblGrundstckeImBesitz, BorderLayout.NORTH);
		
		List<Place> places = player.getPlaces();
		String[] placeData = new String[places.size()];
		for(int i=0; i<places.size(); i++){
			Place place = places.get(i);
			placeData[i] = place.getID();
		}
		JList<String> list = new JList<>(placeData);
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list.setVisibleRowCount(-1);
		
		JScrollPane jlistPane = new JScrollPane(list);
		panel_2.add(jlistPane, BorderLayout.CENTER);
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
