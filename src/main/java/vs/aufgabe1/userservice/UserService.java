package vs.aufgabe1.userservice;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vs.aufgabe2a.boardsservice.exceptions.InvalidInputException;
import vs.aufgabe2a.boardsservice.exceptions.ResourceNotFoundException;

public class UserService{

	
	private static Map<String, User> users;
	
	public UserService(){
		users = new HashMap<>();
	}

	public void deleteUser(String pathInfo) {
		
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

	public void updateUser(String pathInfo, String name, String uri) {
		
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

	private boolean paramsValid(String name, String uri) {
		return (name != null && uri != null)
						&& !(name.isEmpty() || uri.isEmpty());
	}

	public void createUser(User user) {
		
		if(user != null && user.isValid()){
			users.put(user.getId(), user);
		}else{
			throw new InvalidInputException();
		}
		
	}

	public User getSpecificUser(String pathInfo) {
		User u = users.get(pathInfo);
		if(u != null){
			return u;
		}
		throw new ResourceNotFoundException();
	}

	public List<String> getUserIds() {
		List<String> userIds = new ArrayList<>();
		users.forEach((k, v) -> userIds.add(k));
		return userIds;
	}
	
}
