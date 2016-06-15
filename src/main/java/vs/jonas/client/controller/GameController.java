package vs.jonas.client.controller;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import com.google.gson.Gson;
import com.mashape.unirest.http.exceptions.UnirestException;

import spark.Spark;
import vs.jonas.client.json.ClientTurn;
import vs.jonas.client.json.Place;
import vs.jonas.client.json.PlayerInformation;
import vs.jonas.client.json.User;
import vs.jonas.client.model.RestopolyClient;
import vs.jonas.client.model.table.tablemodel.GameFieldTableModel;
import vs.jonas.client.model.table.tablemodel.PlayerOverviewTableModel;
import vs.jonas.client.utils.EventTypes;
import vs.jonas.client.view.FieldUI;
import vs.jonas.client.view.GameUI;
import vs.jonas.exceptions.EstateAlreadyOwnedException;
import vs.jonas.services.json.EventData;

/**
 * Diese Klasse implementiert einen Controller für die GamesUI.
 * Er nimmt Nutzeranfragen entgegen und leitet sie weiter.
 * @author Jones
 *
 */
public class GameController {
	
	private GameUI ui;
	private RestopolyClient client;
	private String gameID;
	private final String SLASH_CLIENT = "/client";
	private final String SLASH_TURN = "/turn";
	private final String SLASH_EVENT = "/events";
	private User user;
	private final int PORT = 4779;
	private String ip;
//	private final String PROTOCOL = "http://";
	private Gson gson;
	
	/**
	 * Initialisiert den Controller
	 * @param client Der Client, der mit den Services kommuniziert
	 * @param gameID Die ID des Games
	 * @throws IOException
	 * @throws UnirestException
	 */
	public GameController(RestopolyClient client, String gameID, User user) throws IOException, UnirestException{
		this.client = client;
		this.gameID = gameID;
		this.user = user;
		this.gson = new Gson();
		URL url = new URL("http://checkip.amazonaws.com/");
		BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
		String ipadress = br.readLine();
		this.ip = ipadress;//InetAddress.getLocalHost().getHostAddress();
		
		this.ip = "192.168.255.18";
		
		this.user.setUri("http://" + this.ip + ":" +this.PORT);
		startClientService();
		client.enterGame(this.gameID, this.user);
		initialisiereUI();
	}

	private void startClientService() {	
		Spark.port(this.PORT);
		
		Spark.ipAddress(ip);
		Spark.get(SLASH_CLIENT, (req, res) -> {
			System.out.println("Received request for route /client from: "+req.ip());
			return new Gson().toJson(user);
		});
		
		Spark.post(SLASH_CLIENT + "/:userid" + SLASH_EVENT,"application/json", (req,res)->{
			System.out.println("########Incoming Event");
			handleIncomingEvent(gson.fromJson(req.body(),EventData.class));
			return "";
		});

		Spark.post(SLASH_CLIENT + "/:userid" + SLASH_TURN, "application/json",(req,res) -> {
			ClientTurn turn = gson.fromJson(req.body(), ClientTurn.class);
			System.out.println("########Incoming Turn Message");
			JOptionPane.showMessageDialog(null, "Der Spieler '" + turn.getPlayer() + "' ist jetzt an der Reihe" );
			ladeSpielerInformationen();
			return "";
		});
		
		
	}


	/**
	 * Initialisiert die UI
	 */
	private void initialisiereUI() {
		EventQueue.invokeLater(new Runnable() {	
			public void run() {
				try {
					ui = new GameUI();
					updateGame();
					ui.getUsernameLbl().setText(user.getName());				
					registriereActionListener();
					ui.showUI();
					JOptionPane.showMessageDialog(null, "Auf 'Spiel starten' klicken, wenn Du bereit bist.");
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Hier ist ein Kommunikationsfehler aufgetreten.");
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Registriert die Listener an der UI
	 */
	private void registriereActionListener() {		
		ui.getAktionAusfuehrenBtn().addActionListener(new ActionListener() {
			
			int i=0;
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox<String> aktionen = ui.getAktionen();
				switch(aktionen.getSelectedItem().toString()){
				case "Wuerfeln": rollDice();break;
				case "Kaufen": buy(); break;
				case "Verkaufen": sell(); break;
				case "Ereigniskarte spielen": playChanceCard(); break;
				case "Zug beenden": finishRound();break;
				}
			}
		});
		
		
		ui.getSpielStartenMenuItem().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					ui.getSpielStartenMenuItem().setEnabled(false);
					client.setReady(gameID, user);
					if(client.allPlayersReady(gameID)){
						client.startGame(gameID);
					}
					updateGame();
//					startEventCheckerThread();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		ui.getGameFIeldTable().addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				int row = e.getY() / ui.getGameFIeldTable().getRowHeight();
				GameFieldTableModel model = (GameFieldTableModel)ui.getGameFIeldTable().getModel();
				Place place = model.getPlace(row);
				
				try {
					Place placeWithWholeInformation = client.getPlace(gameID, place.getID());
					if(placeWithWholeInformation.getValue() != 0){
						new FieldUI(placeWithWholeInformation).showUI();
					}
				} catch (IOException | UnirestException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				System.err.println("A Place was selected: " + place);
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {

			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
		
	}
		
	
	private void updateGame() throws Exception{
		ladeSpielerInformationen();
		ladeGameFieldInformationen();
	}
	
	private void rollDice() {
		int i= 0;
		try {
			ImageIcon diceRollImage = new ImageIcon(FieldUI.class.getResource("/dice_roll.gif"));
			JLabel label1 = new JLabel("Roll the Dice, Baby!",diceRollImage, JLabel.CENTER);
			//Set the position of the text, relative to the icon:
			label1.setVerticalTextPosition(JLabel.BOTTOM);
			label1.setHorizontalTextPosition(JLabel.CENTER);
			JOptionPane.showMessageDialog(null,label1);
			int number = client.rollDice(gameID, user);
			JOptionPane.showMessageDialog(null, "Wurfergebnis: " + number);
//			updateGame();
			
		} catch (Exception ex) {
			// TODO Auto-generated catch block
			if(i<3){
				JOptionPane.showMessageDialog(null, "Es ist ein Fehler aufgetreten. Bitte versuchen Sie es nochmal. Möglicherweise sind Sie nur nicht an der Reihe.");
				i++;
			} else{
				JOptionPane.showMessageDialog(null, "Der Fehler konnte nicht behoben werden. Bitte starten sie das Programm neu.");
				ex.printStackTrace();
			}
		} 
	}
	
	private void buy(){
		try {
			client.buyEstate(gameID, user);
			ImageIcon yeahImage = new ImageIcon(FieldUI.class.getResource("/yes.gif"));
			JLabel label1 = new JLabel("So wird's gemacht!",yeahImage, JLabel.CENTER);
			//Set the position of the text, relative to the icon:
			label1.setVerticalTextPosition(JLabel.BOTTOM);
			label1.setHorizontalTextPosition(JLabel.CENTER);
			JOptionPane.showMessageDialog(null,label1);
		} catch (UnirestException | IOException e) {
			e.printStackTrace();
		} catch (EstateAlreadyOwnedException e) {
			JOptionPane.showMessageDialog(null, "Die Straße wurde bereits verkauft.");
		}
		System.err.println("Dummy: Buy");
	}
	
	private void sell(){
		//TODO
		System.err.println("Dummy: Sell");
	}
	
	private void playChanceCard(){
		//TODO
		System.err.println("Dummy: PlayChanceCard");
	}
	
	private void finishRound(){
		//TODO 
		try {
			client.setReady(gameID, user);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Laedt die Spieler-Informationen aller Teilnehmenden Spieler.
	 * @throws Exception
	 */
	private void ladeSpielerInformationen() throws Exception {
		PlayerOverviewTableModel model = (PlayerOverviewTableModel) ui.getPlayerTable().getModel();
		List<PlayerInformation> data = client.getPlayers(gameID);
		model.loadData(data);
	}
	
	/**
	 * Laedt die Spielfeld-Informationen
	 * @throws IOException
	 * @throws UnirestException
	 */
	private void ladeGameFieldInformationen() throws IOException, UnirestException {
		GameFieldTableModel model = (GameFieldTableModel) ui.getGameFIeldTable().getModel();
		List<Place> data = client.getPlaces(gameID);
		model.loadData(data);
		
	}

	/*
	 * Handles the incoming events
	 */
	private void handleIncomingEvent(EventData event) {
		System.err.println("IncomingEvent: " + event);
		JTextArea eventsConsole = ui.getEventsConsole();
		String eventText = "";
		if(event.getType().equals(EventTypes.MOVE_PAWN.getType())){
			eventText = event.getPlayer() + " has moved his pawn.";
		} else if (event.getType().equals(EventTypes.MOVED_TO_JAIL.getType())){
			eventText = event.getPlayer() + " has been sent to jail.";
		} else if (event.getType().equals(EventTypes.VISIT_PLACE.getType())){
			eventText = event.getPlayer() + " has visited " + event.getRessource();
		} else if(event.getType().equals(EventTypes.BUY_PLACE.getType())){
			eventText = event.getPlayer() + " has bought a place.";
		}
//			JOptionPane.showMessageDialog(null, event.getReason());
		eventsConsole.append("Event "+ui.getEventNumber() + ": " + eventText + "\n\n");
		ui.setEventNumber(ui.getEventNumber()+1);
//		eventsConsole.upd
		try {
			updateGame();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
