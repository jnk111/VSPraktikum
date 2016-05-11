package vs.malte.models;

public class ServiceDTO
{
    private String name;
    private String description;
    private String service;
    private String uri;
    
    public ServiceDTO()
    {
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public String getService()
    {
        return service;
    }

    public void setService( String service )
    {
        this.service = service;
    }

    public String getUri()
    {
        return uri;
    }

    public void setUri( String uri )
    {
        this.uri = uri;
    }
    
    
    
}
