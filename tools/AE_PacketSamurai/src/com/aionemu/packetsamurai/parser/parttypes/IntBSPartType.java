package com.aionemu.packetsamurai.parser.parttypes;

import com.aionemu.packetsamurai.parser.datatree.DataTreeNodeContainer;
import com.aionemu.packetsamurai.parser.datatree.IntBSValuePart;
import com.aionemu.packetsamurai.parser.datatree.IntValuePart;
import com.aionemu.packetsamurai.parser.datatree.ValuePart;
import com.aionemu.packetsamurai.parser.formattree.Part;
import com.aionemu.packetsamurai.parser.parttypes.IntPartType.intType;

/**
 * @author -Nemesiss-
 *
 */
public class IntBSPartType extends IntPartType
{
	public IntBSPartType(String name, intType type)
	{
		super(name, type);
	}

    @Override
    public ValuePart getValuePart(DataTreeNodeContainer parent, Part part)
    {
        return new IntBSValuePart(parent, part, _type);
    }

}
