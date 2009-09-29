package com.aionemu.packetsamurai.parser.formattree;

import java.util.List;


import com.aionemu.packetsamurai.PacketSamurai;
import com.aionemu.packetsamurai.parser.PartType;


/**
 * 
 * @author Ulysses R. Ribeiro
 */
public class ForPart extends Part
{
    private PartContainer _modelBlock;
    private int _forId = -1;

	public ForPart(int id)
	{
		this(id, "");
	}
    
    public ForPart(int id, String analyzerName)
    {
        super(PartType.forBlock);
        this.setAnalyzerName(analyzerName);
        this.setForId(id);
        _modelBlock = new PartContainer(PartType.block);
    }
	
    public PartContainer getModelBlock()
    {
        return _modelBlock;
    }
    
	public void addPart(Part part)
	{
        this.getModelBlock().addPart(part);
        part.setContainingFormat(this.getContainingFormat());
        part.setParentContainer(this.getParentContainer()); // this can NOT be root
        if(this.getContainingFormat() != null)
            this.getContainingFormat().triggerFormatChanged();
	}

	public void addParts(List<Part> parts)
	{
        this.getModelBlock().addParts(parts);
        if(this.getContainingFormat() != null)
            this.getContainingFormat().triggerFormatChanged();
	}
	
    public Part getPartById(int id)
    {
        return _modelBlock.getPacketPartById(id);
    }
	
	public String treeString()
	{
        Part pp;
		if ((pp = this.getParentContainer().getPacketPartByIdInScope(this.getForId(),this)) != null)
		{
			return "For.. : '"+pp.getName()+"'";
		}
        PacketSamurai.getUserInterface().log("ForSize Part id "+this.getForId()+" not found");
	    return "For..";
	}
    
    public int getForId()
    {
        return _forId;
    }
    
	public void setForId(int id)
	{
		_forId = id;
        if(this.getContainingFormat() != null)
            this.getContainingFormat().triggerFormatChanged();
	}

	public boolean addPartAfter(Part part, Part afterPart)
	{
		return _modelBlock.addPartAfter(part, afterPart);
	}

	public boolean removePart(Part part)
	{
        if(this.getContainingFormat() != null)
            this.getContainingFormat().triggerFormatChanged();
		return _modelBlock.removePart(part);
	}
    
    /**
     * for can not have an id
     */
    public int getId()
    {
        return -1;
    }
    
    @Override
    public void setParentContainer(PartContainer pc)
    {
        super.setParentContainer(pc);
        _modelBlock.setParentContainer(pc);
    }
    
    @Override
    public void setContainingFormat(Format format)
    {
        super.setContainingFormat(format);
        _modelBlock.setContainingFormat(format);
    }
}