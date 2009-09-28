package com.aionemu.packetsamurai.filter.value.string;


import com.aionemu.packetsamurai.filter.value.Value;
import com.aionemu.packetsamurai.parser.DataStructure;

/**
 * 
 * @author Gilles Duboscq
 *
 */
public abstract class StringValue extends Value
{
    public abstract String getStringValue(DataStructure dp);
}