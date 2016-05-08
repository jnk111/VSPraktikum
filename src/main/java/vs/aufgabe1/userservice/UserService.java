package vs.aufgabe1.userservice;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vs.aufgabe2a.boardsservice.exceptions.InvalidInputException;
import vs.aufgabe2a.boardsservice.exceptions.ResourceNotFoundException;

public class UserService{

	/*
	 * Mapping User-Uri -> User
	 */
	private static Map<String, User> users;
	
	/**
	 * Default-Konstruktor
	 */
	public UserService(){
		users = new HashMap<>();
	}

	/**
	 * Loescht einen User aus der UserListe
	 * @param pathInfo
	 * 				Die URI des Users
	 * @throws ResourceNotFoundException
	 * 				User wurde nicht gefunden
	 */
	public synchronized void deleteUser(String pathInfo) 
			throws ResourceNotFoundException{
		
		User u = users.get(pathInfo);
		if(u != null){
			System.out.println(pathInfo);
			System.out.println(users.toString());
			users.remove(pathInfo);
			System.out.println(users.toString());
			return;
		}
		throw new ResourceNotFoundException();
	}

	/**
	 * Verandert Informationen zu einem User (z. B. Name oder Client-Uri)
	 * @param pathInfo
	 * 				Die Uri des Users
	 * @param name
	 * 				Der Name des Users
	 * @param uri
	 * 				Die Client-Uri des Users
	 * @throws InvalidInputException
	 * 				Paramter nicht richtig angegeben
	 * 	
	 */
	public synchronized void updateUser(String pathInfo, String name, String uri) 
			throws InvalidInputException{
		
		if(paramsValid(name, uri)){
			User u = users.get(pathInfo);
			if(u != null){
				u.setName(name);
				u.setUri(uri);
			}else{
				throw new ResourceNotFoundException();
			}
		}else{
			throw new InvalidInputException();
		}		
	}

	/**
	 * Prueft, ob die uebergeben Parameter um Userinfomrationen zu veraendern, gueltig sind
	 * @param name
	 * 				Der Name des Users
	 * @param uri
	 * 				Die Client-Uri des Users
	 * @return
	 * 				<code>true</code> : gueltig
	 * 				<code>false</code> : ungueltig
	 */
	private boolean paramsValid(String name, String uri) {
		return (name != null && uri != null)
						&& !(name.isEmpty() || uri.isEmpty());
	}

	/**
	 * Legt den uebergebenen User an
	 * @param user	
	 * 				Der anzulegende User
	 * @throws InvalidInputException
	 * 					Der Uebergebene User enthaelt nicht alle noetigen Informationen
	 */
	public synchronized void createUser(User user) 
			throws InvalidInputException {
		
		if(user != null && user.isValid()){
			users.put(user.getId(), user);
		}else{
			throw new InvalidInputException();
		}
	}

	
	/**
	 * Gibt einen bestimmten User als Json-DTO zurueck
	 * @param pathInfo
	 * 				Die Uri des Users
	 * @return User
	 * 				Der User, welcher noch in einen JSON-String konvertiert wird
	 * @throws ResourceNotFoundException
	 * 				Der User wurde nicht gefunden
	 */
	public User getSpecificUser(String pathInfo) 
			throws ResourceNotFoundException {
		
		User u = users.get(pathInfo);
		if(u != null){
			return u;
		}
		throw new ResourceNotFoundException();
	}

	/**
	 * Die eine Liste der URIs der angemeldeten User zurueck
	 * @return List<String>
	 * 					Die URIs der angemeldeten User
	 * 
	 */
	public List<String> getUserIds() {
		List<String> userIds = new ArrayList<>();
		users.forEach((k, v) -> userIds.add(k));
		return userIds;
	}
	
}
