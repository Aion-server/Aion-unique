package com.aionemu.packetsamurai.filter;

import com.aionemu.packetsamurai.parser.DataStructure;

/**
 * 
 * @author Gilles Duboscq
 *
 */
public class ConstantExpression extends Expression
{
    private boolean _value;
    
    public ConstantExpression(boolean val)
    {
        _value = val;
    }
    
    @Override
    public boolean evaluate(DataStructure dp)
    {
        return _value;
    }
}