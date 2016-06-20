package vs.jonas.client.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.mashape.unirest.http.exceptions.UnirestException;

import vs.jonas.client.json.Place;
import vs.jonas.client.json.User;
import vs.jonas.client.model.RestopolyClient;
import vs.jonas.client.view.FieldUI;
import vs.jonas.exceptions.EstateAlreadyOwnedException;

public class FieldController {

	private RestopolyClient client;
	private FieldUI ui;
	private Place place;
	private User user;
	private String gameID;
	
	public FieldController(RestopolyClient client, Place place, User user, String gameID) {
		super();
		this.client = client;
		this.place = place;
		this.user = user;
		this.gameID = gameID;
		this.ui = new FieldUI(place, user.getPlayerUri().equals(place.getOwner()));
		registriereActionListener();
		this.ui.showUI();
	}

	private void registriereActionListener() {
		ui.getBtnKaufen().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					client.buyEstate(gameID, place, user);
				} catch (UnirestException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (EstateAlreadyOwnedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		ui.getBtnHausKaufen().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					client.buyEstate(gameID, place, user);
				} catch (UnirestException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (EstateAlreadyOwnedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		ui.getBtnHypothekAufnehmen().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					client.takeHypothecaryCredit(place.getBroker(),user.getPlayerUri());
				} catch (UnirestException e1) {
					//TODO
					e1.printStackTrace();
				}
			}
		});
	}
	
	
}
