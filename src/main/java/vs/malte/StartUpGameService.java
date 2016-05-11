package vs.malte;

import vs.jan.services.boardservice.BoardRESTApi;
import vs.jan.services.boardservice.BoardService;
import vs.malte.services.GamesService;

public class StartUpGameService
{
    public static void main( String[] args )
    {
        new BoardRESTApi();
        new GamesService();
    }
}
