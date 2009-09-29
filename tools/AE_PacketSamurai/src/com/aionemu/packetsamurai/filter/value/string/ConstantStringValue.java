package com.aionemu.packetsamurai.filter.value.string;

import com.aionemu.packetsamurai.parser.DataStructure;

/**
 * 
 * @author Gilles Duboscq
 *
 */
public class ConstantStringValue extends StringValue
{
    public String _value;
    
    public ConstantStringValue(String val)
    {
        _value = val;
    }

    @Override
    public String getStringValue(DataStructure dp)
    {
        return _value;
    }
    
}