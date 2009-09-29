package com.aionemu.packetsamurai.filter;

import java.util.List;


import com.aionemu.packetsamurai.filter.booleanoperator.BooleanOperator;
import com.aionemu.packetsamurai.parser.DataStructure;

import javolution.util.FastList;

/**
 * 
 * @author Gilles Duboscq
 *
 */
public class Condition
{
    private List<Condition> _subConditions;
    private BooleanOperator _operator;
    private boolean _inverted;
    
    protected Condition()
    {
        // only used by the Expression sub-class
    }
    
    public Condition(BooleanOperator op)
    {
        this(op, false);
    }
    
    public Condition(BooleanOperator op, boolean inv)
    {
        _subConditions = new FastList<Condition>();
        _operator = op;
        _inverted = inv;
    }
    
    public Condition(String str)
    {
        //TODO parse str
    }
    
    @Override
    public String toString()
    {
        // TODO this
        return "TODO";
    }

    public boolean evaluate(DataStructure dp)
    {
        return this.isInverted() ^ this.getBooleanOperator().execute(this.getSubCondition(), dp);
    }
    
    public void setInverted(boolean inverted)
    {
        _inverted = inverted;
    }
    
    public boolean isInverted()
    {
        return _inverted;
    }
    
    public BooleanOperator getBooleanOperator()
    {
        return _operator;
    }
    
    public void setOperator(BooleanOperator op)
    {
        _operator = op;
    }
    
    public final List<Condition> getSubCondition()
    {
        return _subConditions;
    }
    
    public final void addSubCondition(Condition cond)
    {
        this.getSubCondition().add(cond);
    }
    
    public final void removeSubCondition(Condition cond)
    {
        this.getSubCondition().remove(cond);
    }
}