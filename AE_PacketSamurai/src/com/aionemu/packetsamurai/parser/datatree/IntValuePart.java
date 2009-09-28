package com.aionemu.packetsamurai.parser.datatree;

import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;


import com.aionemu.packetsamurai.parser.DataStructure.DataPacketMode;
import com.aionemu.packetsamurai.parser.formattree.Part;
import com.aionemu.packetsamurai.parser.parttypes.IntPartType.intType;


/**
 * 
 * @author Gilles Duboscq
 *
 */
public class IntValuePart extends ValuePart
{
    private int _int;
    intType _type;
    
    public IntValuePart(DataTreeNodeContainer parent, Part part, intType type)
    {
        super(parent, part);
        _type = type;
    }
    
    @Override
    public void parse(ByteBuffer buf)
    {
        if(this.getMode() == DataPacketMode.FORGING)
            throw new IllegalStateException("Can not parse on a Forging mode Data Packet Tree element");
        int pos = buf.position();
        int size = 0;
        switch(_type)
        {
            case c:
                _int = buf.get();
                size = 1;
                break;
            case h:
                _int = buf.getShort();
                size = 2;
                break;
            case d:
                _int = buf.getInt();
                size = 4;
                break;
        }
        // sets the raw bytes
        _bytes = new byte[size];
        buf.position(pos);
        buf.get(_bytes);
    }
    
    @Override
    public void forge(DataOutput stream) throws IOException
    {
        if(this.getMode() == DataPacketMode.PARSING)
            throw new IllegalStateException("Can not call forge on a Parsing mode Data Packet Tree element");
        switch(_type)
        {
            case c:
                stream.write((byte) _int);
                break;
            case h:
                stream.writeChar((char) _int);
                break;
            case d:
                stream.writeInt(_int);
                break;
        }
    }
    
    public int getIntValue()
    {
        return _int;
    }
    
    public void setIntValue(int i)
    {
        if(this.getMode() == DataPacketMode.PARSING)
            throw new IllegalStateException("Can not set value on a Parsing mode Data Packet Tree element");
        _int = i;
    }
    
    
    @Override
    public String getValueAsString()
    {
        return String.valueOf(this.getIntValue());
    }
}