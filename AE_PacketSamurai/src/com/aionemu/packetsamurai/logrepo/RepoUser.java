package com.aionemu.packetsamurai.logrepo;

public class RepoUser
{
    private int _id;
    private String _name;
    private long _rights;
    
    public RepoUser(int id, String name, long rights)
    {
        _id = id;
        _name = name;
        _rights = rights;
    }

    public int getId()
    {
        return _id;
    }

    public void setId(int id)
    {
        _id = id;
    }

    public String getName()
    {
        return _name;
    }

    public void setName(String name)
    {
        _name = name;
    }

    public long getRights()
    {
        return _rights;
    }

    public void setRights(long rights)
    {
        _rights = rights;
    }

}