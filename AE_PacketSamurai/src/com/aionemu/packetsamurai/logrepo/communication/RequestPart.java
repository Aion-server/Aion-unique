package com.aionemu.packetsamurai.logrepo.communication;

/**
 * 
 * @author Gilles Duboscq
 *
 */
public class RequestPart
{
    private String _name;
    private String _value;
    
    public RequestPart(String name, String value)
    {
        _name = name;
        _value = value;
    }
    
    public String getName()
    {
        return _name;
    }
    
    public String getValue()
    {
        return _value;
    }
}