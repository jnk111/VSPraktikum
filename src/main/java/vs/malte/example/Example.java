package vs.malte.example;

import vs.malte.example.json.CreateGameExDTO;
import vs.malte.example.json.UserDTO;
import vs.malte.services.HttpService;

public class Example
{
    /**
     * Legt zwei Spiele mit jeweils 3 Spielern an und setzt diese auf ready
     */
    public static void runExample01(String GameServiceIP)
    {
        CreateGameExDTO cg = new CreateGameExDTO();
        cg.setName( "game01" );
        HttpService.post( GameServiceIP + "/games", cg );
        cg.setName( "game02" );
        HttpService.post( GameServiceIP + "/games", cg );
        UserDTO user = new UserDTO();

        user.setUser( "klaus" );
        HttpService.post( GameServiceIP + "/games/game01/players", user );

        user.setUser( "fin" );
        HttpService.post( GameServiceIP + "/games/game01/players", user );

        user.setUser( "tidus" );
        HttpService.post( GameServiceIP + "/games/game01/players" , user );

        HttpService.put( GameServiceIP + "/games/game01/players/klaus/ready", null );
        HttpService.put( GameServiceIP + "/games/game01/players/fin/ready", null );
        HttpService.put( GameServiceIP + "/games/game01/players/tidus/ready", null );
        
        HttpService.put( GameServiceIP + "/games/game01/status", null );
    }
}
