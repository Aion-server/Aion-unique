package com.aionemu.packetsamurai.parser.parttypes;


import com.aionemu.packetsamurai.parser.PartType;
import com.aionemu.packetsamurai.parser.datatree.DataTreeNodeContainer;
import com.aionemu.packetsamurai.parser.datatree.FloatValuePart;
import com.aionemu.packetsamurai.parser.datatree.ValuePart;
import com.aionemu.packetsamurai.parser.formattree.Part;

/**
 * 
 * @author Gilles Duboscq
 *
 */
public class DoublePartType extends PartType
{

    public DoublePartType(String name)
    {
        super(name);
    }

    @Override
    public ValuePart getValuePart(DataTreeNodeContainer parent, Part part)
    {
        return new FloatValuePart(parent, part);
    }

    @Override
    public boolean isBlockType()
    {
        return false;
    }

    @Override
    public boolean isReadableType()
    {
        return true;
    }

    @Override
    public int getTypeByteNumber()
    {
        return 8;
    }
    
}