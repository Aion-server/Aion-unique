package com.aionemu.packetsamurai.parser.datatree;

import com.aionemu.packetsamurai.parser.formattree.Part;
import com.aionemu.packetsamurai.parser.parttypes.IntPartType.intType;

public class IntBCValuePart extends IntValuePart
{
	public IntBCValuePart(DataTreeNodeContainer parent, Part part, intType type)
	{
		super(parent, part, type);
	}

    public int getBitCount()
    {
        return bitCount(getIntValue());
    }

    private static int bitCount(int v) {
        v = (v & 0x5555) + ((v & 0xAAAA) >>> 1);
        v = (v & 0x3333) + ((v & 0xCCCC) >>> 2);
        v = (v & 0x0F0F) + ((v & 0xF0F0) >>> 4);
        v = (v & 0x00FF) + ((v & 0xFF00) >>> 8);
        return v;
    }

    @Override
    public String getValueAsString()
    {
        return String.valueOf(this.getBitCount());
    }
}
