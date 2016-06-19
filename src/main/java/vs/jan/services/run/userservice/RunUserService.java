package vs.jan.services.run.userservice;

import java.net.UnknownHostException;

import com.mashape.unirest.http.exceptions.UnirestException;

import vs.jan.api.userservice.UserServiceRESTApi;

public class RunUserService {
	public static void main(String[] args) throws UnknownHostException, UnirestException {
		new UserServiceRESTApi();
	}
}
