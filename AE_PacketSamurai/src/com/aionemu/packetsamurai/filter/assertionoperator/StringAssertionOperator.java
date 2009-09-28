package com.aionemu.packetsamurai.filter.assertionoperator;


import com.aionemu.packetsamurai.filter.value.string.StringValue;
import com.aionemu.packetsamurai.parser.DataStructure;

/**
 * 
 * @author Gilles Duboscq
 *
 */
public interface StringAssertionOperator extends AssertionOperator
{
    public boolean execute(StringValue value1, StringValue value2, DataStructure dp);
}