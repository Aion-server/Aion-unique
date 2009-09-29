package com.aionemu.packetsamurai.logrepo.communication;

import java.util.List;

/**
 * 
 * @author Gilles Duboscq
 *
 */
public class RequestForPart extends RequestPart
{
    private List<RequestPart> _parts;
    
    public RequestForPart(String name)
    {
        super(name, null);
    }
    
    public void addPart(RequestPart part)
    {
        _parts.add(part);
    }
    
    public List<RequestPart> getSubParts()
    {
        return _parts;
    }
}