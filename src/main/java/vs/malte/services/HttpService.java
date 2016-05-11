package vs.malte.services;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;

public class HttpService
{
    public static int post( String URL, Object body )
    {
        return connect( "POST", URL, body );
    }

    private static int connect( String method, String URL, Object body )
    {
        int result = 500;   // ERROR TODO
        
        try
        {
            URL url = new URL( URL );
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod( method );

            connection.setDoOutput( true );

            String json = new Gson().toJson( body );

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
