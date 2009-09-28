package com.aionemu.packetsamurai.protocol.protocoltree;

import java.util.List;

import com.aionemu.packetsamurai.parser.PartType;

import javolution.util.FastList;

/**
 * This class represents a ProtocolNode, either a {@link PacketFormat} or {@link PacketFamilly}
 * @author Gilles Duboscq
 *
 */
public abstract class ProtocolNode
{
	private List<Integer> _ids = new FastList<Integer>();
	private List<PartType> _idParts = new FastList<PartType>();
	
	public ProtocolNode(int id)
	{
		_ids.add(id);
	}
	
	public ProtocolNode()
	{
		//
	}

	public int getID()
	{
		return _ids.get(_ids.size()-1);
	}
	
	public List<Integer> getIDs()
	{
		return _ids;
	}
	
	public List<PartType> getIdParts()
	{
		return _idParts;
	}
	
	public void addIdPartAtEnd(PartType part)
	{
		_idParts.add(part);
	}
	
	public void addIdPartsAtBegining(List<PartType> parts, List<Integer> values)
	{
		_idParts.addAll(0,parts);
		_ids.addAll(0,values);
		if(this instanceof PacketFamilly)
		{
			for(ProtocolNode n :((PacketFamilly)this).getNodes().values())
			{
				n.addIdPartsAtBegining(parts, new FastList<Integer>());
			}
		}
	}
    
    @Override
    public String toString()
    {
        return "ProtocolNode: ID: "+this.getID();
    }
}