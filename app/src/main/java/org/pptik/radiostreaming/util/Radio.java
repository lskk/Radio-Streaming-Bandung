package org.pptik.radiostreaming.util;

public class Radio
{
    private String name;
    
    private String path;
    
    private boolean info;
    
    public Radio()
    {
        
    }
    
    public Radio(String name, String path, boolean info)
    {
        super();
        this.name = name;
        this.path = path;
        this.info = info;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public boolean isInfo()
    {
        return info;
    }

    public void setInfo(boolean info)
    {
        this.info = info;
    }

    @Override
    public String toString()
    {
        return "Radio [name=" + name + ", path=" + path + ", info=" + info + "]";
    }
    
    
}
