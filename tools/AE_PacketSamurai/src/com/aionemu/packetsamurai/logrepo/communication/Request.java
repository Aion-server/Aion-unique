package com.aionemu.packetsamurai.logrepo.communication;

import java.util.List;

import javolution.util.FastList;

/**
 * 
 * @author Gilles Duboscq
 *
 */
public class Request
{
    private String _type;
    private int _id;
    private List<RequestPart> _parts = new FastList<RequestPart>();
    
    protected Request(int id, String type)
    {
        _id = id;
        _type = type;
    }
    
    public int getId()
    {
        return _id;
    }
    
    public String getType()
    {
        return _type;
    }
    
    public void setType(String type)
    {
        _type = type;
    }
    
    public List<RequestPart> getParts()
    {
        return _parts;
    }
    
    public boolean addPart(RequestPart part)
    {
        return _parts.add(part);
    }
    
    public boolean addPart(String name, String value)
    {
        return this.addPart(new RequestPart(name, value));
    }
    
    public boolean removePart(RequestPart part)
    {
        return _parts.remove(part);
    }
}