package com.aionemu.packetsamurai.parser.datatree;

import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;


import com.aionemu.packetsamurai.parser.DataStructure.DataPacketMode;
import com.aionemu.packetsamurai.parser.formattree.Part;
import com.aionemu.packetsamurai.parser.parttypes.StringPartType.stringType;


/**
 * 
 * @author Gilles Duboscq
 *
 */
public class StringValuePart extends ValuePart
{
    protected String _string;
    protected stringType _type;
    
    public StringValuePart(DataTreeNodeContainer parent, Part part, stringType type)
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
            case s:
                StringBuffer sb = new StringBuffer();
                byte b;
                while ((b = buf.get()) != 0)
                    sb.append((char)b);
                _string = sb.toString();
                size = sb.length()+1;
                break;
            case S:
                StringBuffer sb2 = new StringBuffer();
                char ch;
                while ((ch = buf.getChar()) != 0)
                    sb2.append(ch);
                _string = sb2.toString();
                size = sb2.length()*2+2;
                break;
            case Ss:
                StringBuffer sb3 = new StringBuffer();
				char ch1;
				while ((ch1 = buf.getChar()) != 0)
				{
					sb3.append(ch1);
				}
				_string = sb3.toString();
				size = getBSize();
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
            case S:
                if (_string == null)
                {
                    stream.writeChar('\000');
                }
                else
                {
                    final int len = _string.length();
                    for (int i=0; i < len; i++)
                        stream.writeChar(_string.charAt(i));
                    stream.writeChar('\000');
                }
                break;
            case s:
                if (_string == null)
                {
                    stream.write((byte)0x00);
                }
                else
                {
                    final int len = _string.length();
                    for (int i=0; i < len; i++)
                        stream.write((byte) _string.charAt(i));
                    stream.write((byte)0x00);
                }
                break;
           case Ss:
				for (int i = 0; i < (_string.getBytes().length /2); i++)
				{
					stream.writeChar(_string.charAt(i));
				}
				break;
        }
    }
    
    public String getStringValue()
    {
        return _string;
    }
    
    public void setStringValue(String s)
    {
        if(this.getMode() == DataPacketMode.PARSING)
            throw new IllegalStateException("Can not set value on a Parsing mode Data Packet Tree element");
        _string = s;
    }
    
    
    @Override
    public String getValueAsString()
    {
        return _string;
    }
}