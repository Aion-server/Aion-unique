package com.aionemu.packetsamurai.filter.value.number;

import com.aionemu.packetsamurai.parser.DataStructure;

/**
 * 
 * @author Gilles Duboscq
 *
 */
public abstract class IntegerNumberValue extends NumberValue
{
    public abstract long getIntegerValue(DataStructure dp);
}