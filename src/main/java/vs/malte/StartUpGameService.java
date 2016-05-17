package vs.malte;

import java.util.concurrent.TimeUnit;

import vs.jan.services.boardservice.BoardRESTApi;
import vs.jan.services.boardservice.BoardService;
import vs.jan.services.userservice.UserServiceRESTApi;
import vs.malte.example.Example;
import vs.malte.services.GamesService;

public class StartUpGameService
{
    public static void main( String[] args )
    {
        new BoardRESTApi();
        new GamesService();
        new UserServiceRESTApi();
        
        try
        {
            TimeUnit.SECONDS.sleep( 1 );
        }
        catch ( InterruptedException e )
        {
            e.printStackTrace();
        }
        
        Example.runExample01("http://localhost:4567");
    }
}
