package vs.malte;

import java.util.concurrent.TimeUnit;

import vs.gerriet.service.BankService;
import vs.jan.api.boardservice.BoardRESTApi;
import vs.jan.api.userservice.UserServiceRESTApi;
import vs.malte.example.Example;
import vs.malte.services.GamesServiceAPI;

public class StartUpProject
{
    public static void main( String[] args )
    {
        new BoardRESTApi();
        new UserServiceRESTApi();
        BankService.run();

        new GamesServiceAPI();

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
