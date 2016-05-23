package vs.malte.json;

import vs.malte.models.Components;
import vs.malte.models.ServiceList;

public class GameDTO
{
        private String id;
        private String name;
        private String players;
        private ServiceList services;
        private Components components;
        
        public GameDTO()
        {
        }

        public String getId()
        {
            return id;
        }

        public void setId( String id )
        {
            this.id = id;
        }

        public String getName()
        {
            return name;
        }

        public void setName( String name )
        {
            this.name = name;
        }

        public String getPlayers()
        {
            return players;
        }

        public void setPlayers( String players )
        {
            this.players = players;
        }

        public ServiceList getServices()
        {
            return services;
        }

        public void setServices( ServiceList services )
        {
            this.services = services;
        }

        public Components getComponents()
        {
            return components;
        }

        public void setComponents( Components components )
        {
            this.components = components;
        }
        
        
}
