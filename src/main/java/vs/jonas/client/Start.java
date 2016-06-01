package vs.jonas.client;

import java.io.IOException;

import javax.swing.JOptionPane;

import com.mashape.unirest.http.exceptions.UnirestException;

import vs.jonas.client.controller.MenuController;
import vs.jonas.client.json.User;
import vs.jonas.client.model.RestopolyClient;
import vs.jonas.services.services.YellowPagesService;

public class Start {
	public static void main(String[] args) throws IOException, UnirestException {
		YellowPagesService yellowPagesService = new YellowPagesService(true);
		
		
		String username = "";
		
		while(username != null && username.equals("")){
			username = JOptionPane.showInputDialog(null, "Bitte wählen Sie einen Usernamen.");
			if(username != null && username.equals("") && username.equals("user")){
				JOptionPane.showMessageDialog(null, "Dieser Username ist nicht gültig. Der Username darf nicht leer sein.");
			}
		}
		
		if(username == null){
			JOptionPane.showMessageDialog(null, "Wir respektieren Ihre Entscheidung.\n "
					+ "Falls Sie es sich anders überlegen, starten Sie das Programm bitte erneut.");
		} else{
			
			try {
				RestopolyClient client = new RestopolyClient(yellowPagesService, new User(username.toLowerCase()));
				new MenuController(client);
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Ein Fehler ist aufgetreten. Bitte wählen Sie einen anderen Usernamen. ");
			}
		}
		
	}
}
