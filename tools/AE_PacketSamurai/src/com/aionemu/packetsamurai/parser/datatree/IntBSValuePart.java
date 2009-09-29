package com.aionemu.packetsamurai.parser.datatree;

import com.aionemu.packetsamurai.parser.formattree.Part;
import com.aionemu.packetsamurai.parser.parttypes.IntPartType.intType;

public class IntBSValuePart extends IntValuePart
{
	public IntBSValuePart(DataTreeNodeContainer parent, Part part, intType type)
	{
		super(parent, part, type);
	}

	public int getBitCount()
	{
		return Integer.bitCount(getIntValue());
	}

    @Override
    public String getValueAsString()
    {
        return String.valueOf(this.getBitCount());
    }
}
