package vs.client.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.SystemColor;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import vs.client.model.tablemodel.GameInformationTableModel;

/**
 * Diese Klasse beschreibt eine Benutzeroberfl�che, die daf�r da ist, dass sich
 * der Benutzer alle offenen Spiele anschauen kann.
 * 
 * @author Jones
 *
 */
public class OffeneSpieleUI {

	private JPanel contentPane;
	private JFrame frame;
	private JTable offeneSpieleTable; //
	private JButton btnBeitreten;
	private JLabel lblAuswahl;

	/**
	 * Initialisierung
	 */
	public OffeneSpieleUI() {
		frame = new JFrame("Restopoly - Offene Spiele");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setBounds(100, 100, 450, 300);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);

		contentPane = new JPanel();
		contentPane.setBackground(SystemColor.activeCaption);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		frame.setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		JSplitPane splitPane = new JSplitPane();
		splitPane.setBackground(SystemColor.activeCaption);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		contentPane.add(splitPane);
		splitPane.setDividerLocation(200);

		JPanel auswahlPanel = new JPanel();
		auswahlPanel.setBackground(SystemColor.activeCaption);
		splitPane.setLeftComponent(auswahlPanel);
		auswahlPanel.setLayout(new BorderLayout(0, 0));

		GameInformationTableModel model = new GameInformationTableModel();
		offeneSpieleTable = new JTable(model);
		offeneSpieleTable.setCellSelectionEnabled(true);
	    ListSelectionModel cellSelectionModel = offeneSpieleTable.getSelectionModel();
	    cellSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		offeneSpieleTable.setBackground(SystemColor.activeCaption);
		JScrollPane scrollPane = new JScrollPane(offeneSpieleTable);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		auswahlPanel.add(scrollPane);

		JPanel bestaetigungsPanel = new JPanel();
		bestaetigungsPanel.setBackground(SystemColor.inactiveCaption);
		splitPane.setRightComponent(bestaetigungsPanel);
		bestaetigungsPanel.setLayout(new GridLayout(0, 3, 0, 0));

		JLabel lblNewLabel = new JLabel("Auswahl:");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		bestaetigungsPanel.add(lblNewLabel);

		lblAuswahl = new JLabel("");
		lblAuswahl.setHorizontalAlignment(SwingConstants.CENTER);
		bestaetigungsPanel.add(lblAuswahl);

		btnBeitreten = new JButton("Beitreten");
		bestaetigungsPanel.add(btnBeitreten);
	}

	public JTable getOffeneSpieleTable() {
		return offeneSpieleTable;
	}

	public JButton getBtnBeitreten() {
		return btnBeitreten;
	}

	public JLabel getLblAuswahl() {
		return lblAuswahl;
	}

	public void showUI() {
		frame.setVisible(true);
	}

}
