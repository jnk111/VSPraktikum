package vs.jonas.client.controller;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import com.google.gson.Gson;
import com.mashape.unirest.http.exceptions.UnirestException;

import spark.Spark;
import vs.gerriet.api.Events;
import vs.gerriet.json.event.SubscriptionRegisterData;
import vs.jonas.client.json.ClientTurn;
import vs.jonas.client.json.Place;
import vs.jonas.client.json.PlayerInformation;
import vs.jonas.client.json.PlayerResponse;
import vs.jonas.client.json.User;
import vs.jonas.client.model.MyCallback;
import vs.jonas.client.model.Player;
import vs.jonas.client.model.RestopolyClient;
import vs.jonas.client.model.ShowMessageThread;
import vs.jonas.client.model.table.MyTableMouseListener;
import vs.jonas.client.model.table.tablemodel.GameFieldTableModel;
import vs.jonas.client.model.table.tablemodel.PlayerOverviewTableModel;
import vs.jonas.client.utils.EventTypes;
import vs.jonas.client.utils.JailReasonGenerator;
import vs.jonas.client.view.FieldUI;
import vs.jonas.client.view.GameUI;
import vs.jonas.exceptions.NotExpectedStatusCodeException;
import vs.jonas.exceptions.PlayerDoesNotHaveTheMutexException;
import vs.jonas.exceptions.PlayerHasAlreadyRolledTheDiceException;
import vs.jonas.services.json.EventData;

/**
 * Diese Klasse implementiert einen Controller f�r die GamesUI.
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
	private int PORT = 4777;
	private String ip;
	private final String HTTP_PROTOCOL = "http://";
	private final String HTTPS_PROTOCOL = "https://";
	private Gson gson;
	
	/**
	 * Initialisiert den Controller
	 * @param client Der Client, der mit den Services kommuniziert
	 * @param gameID Die ID des Games
	 * @throws UnirestException
	 * @throws NotExpectedStatusCodeException 
	 * @throws  
	 */
	public GameController(RestopolyClient client, String gameID, User user) throws UnirestException, NotExpectedStatusCodeException{
		this.client = client;
		this.gameID = gameID;
		this.user = user;
		this.gson = new Gson();
//		try {
//			URL url = new URL("http://checkip.amazonaws.com/");
//			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
//			String ipadress = br.readLine();
//			this.ip = ipadress;//
//			System.out.println("IP über InetAdress.getLocalHost: " +InetAddress.getLocalHost().getHostAddress());
//			System.out.println("IP-Adresse:" + this.ip);
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		this.ip = JOptionPane.showInputDialog("IP Andresse angeben: (z.b. localhost oder 192.168.99.100");
		String port = JOptionPane.showInputDialog("Port angeben: (z.B. 4777");
		if(this.ip == null || port == null){
			ui.getFrame().dispose();
			JOptionPane.showMessageDialog(null, "Einige Angaben waren leer. Das Programm wurde beendet.");
		}
		this.PORT = Integer.valueOf(port);
//		this.ip = "192.168.255.18"; // im Docker-VPN 
//		this.ip = "141.22.34.15"; // amazonaws im Docker VPN
//		this.ip = "169.254.41.219"; // im Docker-VPN InetAdress.getLocalHost().getHostAdress()
//		this.ip = 172.0.0.3 // den IPService fragen
		
		this.user.setUri(HTTP_PROTOCOL + this.ip + ":" +this.PORT + SLASH_CLIENT + "/" + user.getName());
		startClientService();
		client.enterGame(this.gameID, this.user);
		initialisiereUI();
	}

	private void startClientService() {	
		Spark.port(this.PORT);
		
		Spark.ipAddress(ip);
		
		Spark.get("", (req,res) -> {
			return "Hello from " + user.getName();
		});
		
		Spark.get(SLASH_CLIENT, (req, res) -> {
			System.out.println("Received request for route /client from: "+req.ip());
			return new Gson().toJson(user);
		});
		
		Spark.post(SLASH_CLIENT + "/:userid" + SLASH_EVENT,"application/json", (req,res)->{
			handleIncomingEvent(gson.fromJson(req.body(),EventData.class));
			return "";
		});

		Spark.post(SLASH_CLIENT + "/:userid" + SLASH_TURN, "application/json",(req,res) -> {
			ClientTurn turn = gson.fromJson(req.body(), ClientTurn.class);
			ui.getEventsConsole().append("**********************************************\n" + turn.getPlayer()  + " ist jetzt an der Reihe.\n"
					+ "**********************************************\n\n");
			if(turn.getPlayer().equals(user.getPlayerUri())){
				new ShowMessageThread("Du bist an der Reihe!").start();
			}
			ladeSpielerInformationen();
			return "";
		});
		
		Events events = new Events();
		String[] eventData = {
				EventTypes.MOVE_PAWN.getType(),
				EventTypes.GAME_STARTED.getType(),
				EventTypes.BUY_HOUSE.getType(),
				EventTypes.BUY_PLACE.getType(),
				EventTypes.GOT_MONEY_ALL_PLAYERS.getType(),
				EventTypes.MOVED_OVER_GO.getType(),
				EventTypes.VISIT_PLACE.getType(),
				EventTypes.MUTEX_CHANGE.getType(),
				EventTypes.MOVED_TO_JAIL.getType(),
				EventTypes.GOT_MONEY_FROM_BANK.getType(),
				EventTypes.PAY_RENT.getType(),
				EventTypes.PAYED_TAX.getType(),
				EventTypes.TAKE_HYPO.getType(),
				EventTypes.TRADE_PLACE.getType(),
				EventTypes.TRADE_REQ.getType(),
				EventTypes.CANNOT_BUY_HOUSE.getType()
				};
		events.addSubscription(new SubscriptionRegisterData(gameID, user.getUri(), eventData));
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
					JOptionPane.showMessageDialog(null, "Auf 'Spiel Starten' klicken, wenn Du bereit bist.");
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Ein Kommunikationsfehler ist aufgetreten.");
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Registriert die Listener an der UI
	 */
	private void registriereActionListener() {				
		ui.getLblWrfeln().addMouseListener(new MyTableMouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				rollDice();
			}
		});
		
		ui.getLblPlayerOverview().addMouseListener(new MyTableMouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				try {
					Player player = client.getPlayer(gameID,user.getPlayerUri());
					new PlayerController(client,player,gameID,user);
					System.err.println("A Player was selected: " + player);			
				} catch (UnirestException e1) {
					e1.printStackTrace();
				}				
			}
		});
		
		ui.getLblSpielzugBeenden().addMouseListener(new MyTableMouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				finishRound();
			}
		});
		
		ui.getSpielStartenMenuItem().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					ui.getSpielStartenMenuItem().setEnabled(false);
					client.setReady(gameID, user);
//					if(client.allPlayersReady(gameID)){
//						client.startGame(gameID);
//						JOptionPane.showMessageDialog(null, "Das Spiel wurde gestartet.");
//					}
					updateGame();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		ui.getPlayerTable().addMouseListener(new MyTableMouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				int row = e.getY() / ui.getPlayerTable().getRowHeight();
				PlayerOverviewTableModel model = (PlayerOverviewTableModel)ui.getPlayerTable().getModel();
				PlayerInformation playerInformation = model.getPlayerInformation(row);
				
				try {
					Player player = client.getPlayerWithWholeInformation(gameID,playerInformation);
					new PlayerController(client,player,gameID,user);
					System.err.println("A Player was selected: " + player);			
				} catch (UnirestException e1) {
					e1.printStackTrace();
				}
				
			}
		});
		
		ui.getGameFieldTable().addMouseListener(new MyTableMouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				int row = e.getY() / ui.getGameFieldTable().getRowHeight();
				GameFieldTableModel model = (GameFieldTableModel)ui.getGameFieldTable().getModel();
				Place place = model.getPlace(row);
				
				try {
					Place placeWithWholeInformation = client.getPlace(place.getID());
					if(placeWithWholeInformation.getValue() != 0){
						new FieldController(client,placeWithWholeInformation,user,gameID);
					}
				} catch (UnirestException e1) {
					e1.printStackTrace();
				}
				
				System.err.println("A Place was selected: " + place);
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
			JLabel label1 = new JLabel(diceRollImage, JLabel.CENTER);
			label1.setVerticalTextPosition(JLabel.BOTTOM);
			label1.setHorizontalTextPosition(JLabel.CENTER);
			JOptionPane.showMessageDialog(null,label1);
			int number = client.rollDice(gameID, user);
			JOptionPane.showMessageDialog(null, "Wurfergebnis: " + number);
		} catch(PlayerHasAlreadyRolledTheDiceException ex){
			JOptionPane.showMessageDialog(null, "So nicht, Freundchen. Du hast bereits gewürfelt!");
		} catch(PlayerDoesNotHaveTheMutexException ex){
			JOptionPane.showMessageDialog(null, "Ich weiß, das Leben ist hart, aber du bist noch nicht and der Reihe.");
		} catch (Exception ex) {
			ex.printStackTrace();
			if(i<3){
				JOptionPane.showMessageDialog(null, "Es ist ein Fehler aufgetreten. Bitte versuchen Sie es nochmal.");
				i++;
			} else{
				JOptionPane.showMessageDialog(null, "Der Fehler konnte nicht behoben werden. Bitte starten sie das Programm neu.");
				ex.printStackTrace();
			}
		} 
	}
	
	private void finishRound(){
		try {
			client.setReady(gameID, user);
		} catch (Exception e) {
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
	 * @throws UnirestException
	 */
	private void ladeGameFieldInformationen() throws IOException, UnirestException {
		GameFieldTableModel model = (GameFieldTableModel) ui.getGameFieldTable().getModel();
		List<Place> data = client.getPlaces(gameID);
		model.loadData(data);
		
	}

	/*
	 * Handles the incoming events
	 */
	private void handleIncomingEvent(EventData event) {
		System.err.println("IncomingEvent: " + event);
		JTextArea eventsConsole = ui.getEventsConsole();
		
		if(event.getType().equals(EventTypes.GAME_STARTED.getType())){
			System.out.println("************** GAME STARTED *****************");
			eventsConsole.append("**********************************************\n"
					+ "Das Spiel wurde gestartet.\n"
					+ "**********************************************\n\n");
			try {
				PlayerResponse response = client.getPlayerWithMutex(gameID);
				new ShowMessageThread(response.getPawn() + " schnappt sich die Wuerfel vor allen anderen und darf beginnen.").start();
			} catch (UnirestException e) {
				e.printStackTrace();
			}
		} else{
			String eventText = "";
			if(event.getType().equals(EventTypes.MOVE_PAWN.getType())){
				eventText = event.getPlayer() + " hat seine Spielfigur bewegt.";
			} else if (event.getType().equals(EventTypes.MOVED_TO_JAIL.getType())){
				eventText = event.getPlayer() + " " +JailReasonGenerator.getRandomReason();
			} else if (event.getType().equals(EventTypes.VISIT_PLACE.getType())){
				eventText = event.getPlayer() + " ist auf dem Feld " + event.getRessource() + " gelandet.";
			} else if(event.getType().equals(EventTypes.BUY_PLACE.getType())){
				eventText = event.getPlayer() + " hat ein Grundstueck gekauft.";
				if(event.getPlayer().equals(user.getPlayerUri())){
					ImageIcon yeahImage = new ImageIcon(GameController.class.getResource("/yes.gif"));
					JLabel label1 = new JLabel("So wird's gemacht!",yeahImage, JLabel.CENTER);
					label1.setVerticalTextPosition(JLabel.BOTTOM);
					label1.setHorizontalTextPosition(JLabel.CENTER);
					new ShowMessageThread(label1).start();
				}
			} else if(event.getType().equals(EventTypes.GOT_MONEY_ALL_PLAYERS.getType())){
				eventText = event.getPlayer() + " wurde von seinen Mitspielern mit Geld ueberschuettet.";
			} else if(event.getType().equals(EventTypes.GOT_MONEY_FROM_BANK.getType())){
				eventText = event.getPlayer() + " hat Geld von der Bank bekommen.";
			} else if(event.getType().equals(EventTypes.PAY_RENT.getType())){
				eventText = event.getPlayer() + " hat die Miete bezahlt.";
			} else if(event.getType().equals(EventTypes.CANNOT_BUY_PLACE.getType())){
				eventText = event.getPlayer() + " konnte das Grundstueck nicht kaufen.";
			} else if(event.getType().equals(EventTypes.CANNOT_PAY_RENT.getType())){
				eventText = event.getPlayer() + " konnte seine Miete nicht bezahlen. Monopoly macht Spaß, oder nicht?";
			} else if(event.getType().equals(EventTypes.TRADE_PLACE.getType())){
				eventText = event.getPlayer() + " hat ein Grundstueck verkauft.";
			} else if(event.getType().equals(EventTypes.MUTEX_CHANGE.getType())){
				eventText = "Der Mutex wurde dem nächsten Spieler übergeben.";
			} else if(event.getType().equals(EventTypes.TRADE_REQ.getType())){
				String grundstück = event.getRessource();
				new ShowMessageThread(event.getPlayer() + " möchte ihr Grundstück " 
				+ grundstück + " erwerben. Sind Sie damit einverstanden?", new MyCallback() {
					
					@Override
					public void onReceive(Integer value) {
						if(value == JOptionPane.YES_OPTION){
							new ShowMessageThread("TODO NOT IMPLEMENTED").start();
						}
					}
				}).start();
			}
			else{
				eventText = "Unimplemented: " + event + " Ausgeloest durch: " + event.getPlayer();
			}
			eventsConsole.append("Event "+ui.getEventNumber() + ": " + eventText + "\n\n");
			ui.setEventNumber(ui.getEventNumber()+1);
		}
		try {
			updateGame();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
