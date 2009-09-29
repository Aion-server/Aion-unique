package com.aionemu.packetsamurai.parser.formattree;

import java.util.Collections;
import java.util.List;

import com.aionemu.packetsamurai.parser.PartType;

import javolution.util.FastTable;

/**
 * @author Ulysses R. Ribeiro
 *
 */
public class PartContainer extends Part
{
    private List<Part> _parts = new FastTable<Part>();
    private boolean _lengthComputed;
    private boolean _constantLength;
    private int _length;
    
    public PartContainer(PartType type, boolean isRoot)
    {
        super(type, isRoot);
        _lengthComputed = false;
        _length = 0;
    }
    
    public PartContainer(PartType type)
    {
        this(type, false);
    }
    
    public void addPart(Part part)
    {
        // transmit containing format & container
        part.setContainingFormat(this.getContainingFormat());
        part.setParentContainer(this);
        
        _parts.add(part);
        invalidateLength();
    }
    
    public boolean addPartAfter(Part part, Part afterPart)
    {
        int i = 1;
        boolean found = false;
        for(Part ipart : _parts)
        {
            if(afterPart == ipart)
            {
                found = true;
                break;
            }
            if(ipart instanceof PartContainer && ((PartContainer)ipart).addPartAfter(part, afterPart))
            {
                return true;
            }
            if(ipart instanceof ForPart && ((ForPart)ipart).addPartAfter(part, afterPart))
            {
                return true;
            }
            else if(ipart instanceof SwitchPart)
            {
                for(SwitchCaseBlock sCase : ((SwitchPart)ipart).getCases(true))
                {
                    if(sCase.addPartAfter(part, afterPart))
                    {
                        return true;
                    }
                }
            }
            i++;
        }
        if(found)
        {
            _parts.add(i, part);
            invalidateLength();
        }
        return found;
    }
    
    public boolean removePart(Part part)
    {
        boolean found = false;
        if(_parts.remove(part))
        {
            found = true;
        }
        else
        {
            for(Part p : _parts)
            {
                if(p instanceof ForPart)
                {
                    found = ((ForPart)p).getModelBlock().removePart(part);
                }
                else if(p instanceof SwitchPart)
                {
                    for(SwitchCaseBlock scb : ((SwitchPart)p).getCases(true))
                    {
                        found = scb.removePart(part);
                        if(found)
                            break;
                    }
                }
                else if(p instanceof PartContainer)
                {
                    found = ((PartContainer)p).removePart(part);
                }
                if(found)
                    break;
            }
        }
        if(found)
            invalidateLength();
        return found;
    }
    
    public Part getPart(int index)
    {
        return _parts.get(index);
    }
    
    public void addParts(List<Part> parts)
    {
        for (Part part : parts)
        {
            this.addPart(part);
        }
    }
    
    /**
     * This returns an unmodifiable list of th parts in this container
     * @return a List of the parts in this container
     */
    public List<Part> getParts()
    {
        return Collections.unmodifiableList(_parts);
    }
    
    public int size()
    {
        return this.getParts().size();
    }
    
    /**
     * Searchs in this Part Container for a part with the given id, if not found
     * it will search into any Part Containers inside this one and so on, until the lowest level of containers is reached
     * 
     * @param id The Id of the PacketPart to be retrieved
     * @return The PacketPart with the given Id if found, null otherwise
     */
    public Part getPacketPartById(int id)
    {
        for(Part part : this.getParts())
        {
            if(part instanceof PartContainer)
            {
                Part test = ((PartContainer) part).getPacketPartById(id);
                if(test != null)
                    return test;
            }
            else if(part instanceof ForPart)
            {
                Part test = ((ForPart) part).getModelBlock().getPacketPartById(id);
                if(test != null)
                    return test;
            }
            else if(part instanceof SwitchPart)
            {
                for(SwitchCaseBlock scb : ((SwitchPart)part).getCases(true))
                {
                    Part test = scb.getPacketPartById(id);
                    if(test != null)
                        return test;
                }
            }
            if(id == part.getId())
                return part;
        }
        return null;
    }
    
    /**
     * Searchs in this Part Container for a part with the given id, before the <code>who</code> part to ensure that the returned part is in the scopte of <code>who</code>, if not found
     * it will search up into Part Containers above this one and so on, until the root level of containers is reached
     * 
     * @param id The Id of the PacketPart to be retrieved
     * @param who ensures that this part should be able to see the returned part
     * @return The PacketPart with the given Id if found, null otherwise
     */
    public Part getPacketPartByIdInScope(int id, Part who)
    {
        for(Part part : this.getParts())
        {
            if(id == part.getId())
                return part;
            if(part == who) // dont go after myself
                break;
        }
        if(!this.isRoot())
            return this.getParentContainer().getPacketPartByIdInScope(id, who);
        return null;
    }
    
    /**
     * Searchs in this Part Container for a part with the given analyzerName, if not found
     * it will search into any Part Containers inside this one and so on, until the lowest level of containers is reached
     * 
     * @param analyzerName the analyzer name to look for
     * @return The PacketPart with the given analyzerName or null if not found.
     */
    public Part getPartByAnalyzerName(String analyzerName)
    {
        for(Part part : this.getParts())
        {
            if(part instanceof PartContainer)
            {
                Part test = ((PartContainer) part).getPartByAnalyzerName(analyzerName);
                if(test != null)
                    return test;
            }
            else if(part instanceof ForPart)
            {
                Part test = ((ForPart) part).getModelBlock().getPartByAnalyzerName(analyzerName);
                if(test != null)
                    return test;
            }
            else if(part instanceof SwitchPart)
            {
                for(SwitchCaseBlock scb : ((SwitchPart)part).getCases(true))
                {
                    Part test = scb.getPartByAnalyzerName(analyzerName);
                    if(test != null)
                        return test;
                }
            }
            if(analyzerName.equals(part.getName()))
                return part;
        }
        return null;
    }
    
    public Part getPartByName(String name)
    {
        if(name == null)
            throw new NullPointerException("Name was null in getPartByName");
        for(Part part : this.getParts())
        {
            if(part instanceof PartContainer)
            {
                Part test = ((PartContainer) part).getPartByName(name);
                if(test != null)
                    return test;
            }
            else if(part instanceof ForPart)
            {
                Part test = ((ForPart) part).getModelBlock().getPartByName(name);
                if(test != null)
                    return test;
            }
            else if(part instanceof SwitchPart)
            {
                for(SwitchCaseBlock scb : ((SwitchPart)part).getCases(true))
                {
                    Part test = scb.getPartByName(name);
                    if(test != null)
                        return test;
                }
            }
            if(name.equals(part.getName()))
                return part;
        }
        return null;
    }
    
    public boolean hasConstantLength()
    {
        if(!_lengthComputed)
        {
            computeLength();
        }
        return _constantLength;
    }
    
    public int getLength()
    {
        if(!_lengthComputed)
        {
            computeLength();
        }
        return _length;
    }
    
    public void invalidateLength()
    {
        _lengthComputed = false;
        if(!this.isRoot())
            getParentContainer().invalidateLength();
        if(this.getContainingFormat() != null)
            this.getContainingFormat().triggerFormatChanged();
    }

    private void computeLength()
    {
        _length = 0;
        int typeLength;
        for(Part part : _parts)
        {
            typeLength = part.getType().getTypeByteNumber();
            if(typeLength == 0)
            {
                _length = 0;
                _constantLength = false;
                break;
            }
            _length += typeLength;
        }
        _lengthComputed = true;
    }
}
