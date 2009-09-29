package com.aionemu.packetsamurai.filter.booleanoperator;

import java.util.List;


import com.aionemu.packetsamurai.filter.Condition;
import com.aionemu.packetsamurai.parser.DataStructure;


/**
 * 
 * @author Gilles Duboscq
 *
 */
public interface BooleanOperator
{
    public boolean execute(List<Condition> conditions, DataStructure dp);
}