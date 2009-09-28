package com.aionemu.packetsamurai.parser.datatree;

import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;


import com.aionemu.packetsamurai.parser.DataStructure.DataPacketMode;
import com.aionemu.packetsamurai.parser.formattree.Part;


/**
 * 
 * @author Gilles Duboscq
 *
 */
public class FloatValuePart extends ValuePart
{
    private float _float;

    public FloatValuePart(DataTreeNodeContainer parent, Part part)
    {
        super(parent, part);
    }
    
    @Override
    public void parse(ByteBuffer buf)
    {
        if(this.getMode() == DataPacketMode.FORGING)
            throw new IllegalStateException("Can not parse on a Forging mode Data Packet Tree element");
        _float = buf.getFloat();
        // sets the raw bytes
        _bytes = new byte[4];
        buf.position(buf.position()-4);
        buf.get(_bytes);
    }
    
    @Override
    public void forge(DataOutput stream) throws IOException
    {
        if(this.getMode() == DataPacketMode.PARSING)
            throw new IllegalStateException("Can not call forge on a Parsing mode Data Packet Tree element");
        stream.writeDouble(_float);
    }
    
    public void setFloatValue(float d)
    {
        if(this.getMode() == DataPacketMode.PARSING)
            throw new IllegalStateException("Can not set value on a Parsing mode Data Packet Tree element");
        _float = d;
    }
    
    public float getFloatValue()
    {
        return _float;
    }
    
    @Override
    public String getValueAsString()
    {
        return String.valueOf(_float);
    }
}