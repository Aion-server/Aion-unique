package com.aionemu.packetsamurai.filter.assertionoperator;


import com.aionemu.packetsamurai.filter.value.number.NumberValue;
import com.aionemu.packetsamurai.parser.DataStructure;

/**
 * 
 * @author Gilles Duboscq
 *
 */
public interface NumberAssertionOperator extends AssertionOperator
{
    public boolean execute(NumberValue value1, NumberValue value2, DataStructure dp);
}