package com.aionemu.packetsamurai.filter.value.number;

import com.aionemu.packetsamurai.parser.DataStructure;

/**
 * 
 * @author Gilles Duboscq
 *
 */
public abstract class FloatNumberValue extends NumberValue
{
    public abstract double getFloatValue(DataStructure dp);
}