package vs.aufgabe2a.gamesservice.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import vs.aufgabe2a.gamesservice.DTO.CreateUserDTO;

public class HttpService
{
    public static int post( String URL, Object body ) throws IOException
    {
        return connect( "POST", URL, body );
    }

    public static int connect( String method, String URL, Object body )
    {
        int result = 500;   // ERROR TODO
        
        try
        {
            URL url = new URL( URL );
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod( method );

            connection.setDoOutput( true );

            String json = new Gson().toJson( body );
            System.out.println( json );

            if ( json != null )
            {
                connection.getOutputStream().write( json.getBytes() );
            }

            connection.connect();
            result = connection.getResponseCode();
        }
        catch ( IOException e )
        {
            System.err.println( "Beim Posten ist was schief gelaufen" );  // TODO: Fehlerbehandlung
            e.printStackTrace();
        }
        
        return result;
    }
}
