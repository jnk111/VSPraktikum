package vs.malte;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ServiceArray
{
    public List<String> services;
    
    public ServiceArray()
    {
        services = new ArrayList<>();
    }

    public List<String> getServices()
    {
        return services;
    }

    public void setServices( List<String> services )
    {
        this.services = services;
    }
}
