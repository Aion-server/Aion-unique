package com.aionemu.packetsamurai.parser.formattree;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.aionemu.packetsamurai.parser.PartType;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * 
 * @author Gilles Duboscq
 *
 */
public class SwitchPart extends Part
{
    private Map<Integer, SwitchCaseBlock> _casesMap = new FastMap<Integer,SwitchCaseBlock>();
    private SwitchCaseBlock _default;
    private int _switchId;

    public SwitchPart(int id, String analyzerName) 
    {
        super(PartType.swicthBlock, -1, "SwitchPart", "","", analyzerName);
        this.setSwitchId(id);
    }

    public SwitchCaseBlock getCase(int switchCase)
    {
        SwitchCaseBlock c = _casesMap.get(new Integer(switchCase));
        if(c == null)
            c= this.getDefaultCase();
        return c;
    }

    public Map<Integer,SwitchCaseBlock> getSwitchMap()
    {
        return Collections.unmodifiableMap(_casesMap);
    }

    public void setSwitchId(int id)
    {
        _switchId = id;
        if(this.getContainingFormat() != null)
            this.getContainingFormat().triggerFormatChanged();
    }

    public int getSwitchId()
    {
        return _switchId;
    }

    /**
     * switches can not have an id
     */
    public int getId()
    {
        return -1;
    }

    public List<Integer> getCasesIds()
    {
        FastList<Integer> cases = new FastList<Integer>();
        for(Entry<Integer,SwitchCaseBlock> entry : _casesMap.entrySet())
        {
            cases.add(entry.getKey());
        }
        return cases;
    }

    public List<SwitchCaseBlock> getCases()
    {
        FastList<SwitchCaseBlock> cases = new FastList<SwitchCaseBlock>();
        for(Entry<Integer,SwitchCaseBlock> entry : _casesMap.entrySet())
        {
            cases.add(entry.getValue());
        }
        return cases;
    }

    public List<SwitchCaseBlock> getCases(boolean includeDefault)
    {
        List<SwitchCaseBlock> cases = getCases();
        if(includeDefault && _default != null)
            cases.add(_default);
        return cases;
    }

    public void addCase(SwitchCaseBlock iCase)
    {
        iCase.setParentContainer(this.getParentContainer()); // this can NOT be root
        iCase.setContainingFormat(this.getContainingFormat());
        if(iCase.isDefault())
            _default = iCase;
        else
            _casesMap.put(iCase.getSwitchCase(), iCase);
        if(this.getContainingFormat() != null)
            this.getContainingFormat().triggerFormatChanged();
    }

    public Part getTestPart()
    {
        return this.getParentContainer().getPacketPartByIdInScope(this.getSwitchId(),this);
    }

    public String treeString()
    {
        Part pp = getTestPart();
        if (pp != null)
        {
            return "Switch.. : "+pp.getName();
        }
        return "Switch..";
    }

    public boolean removeCase(int switchCase)
    {
        if(_casesMap.remove(new Integer(switchCase)) != null)
        {
            if(this.getContainingFormat() != null)
                this.getContainingFormat().triggerFormatChanged();
            return true;
        }
        if(_default.getSwitchCase() == switchCase)
        {
            _default = null;
            if(this.getContainingFormat() != null)
                this.getContainingFormat().triggerFormatChanged();
            return true;
        }
        return false;
    }

    public boolean removeCase(SwitchCaseBlock sCase)
    {
        return removeCase(sCase.getSwitchCase());
    }

    public void setDefaultCase(SwitchCaseBlock dcase)
    {
        dcase.setParentContainer(this.getParentContainer()); // this can NOT be root
        dcase.setContainingFormat(this.getContainingFormat());
        dcase.setDefault(true);
        _default = dcase;
        if(this.getContainingFormat() != null)
            this.getContainingFormat().triggerFormatChanged();
    }

    public SwitchCaseBlock getDefaultCase()
    {
        return _default;
    }
    
    @Override
    public void setParentContainer(PartContainer pc)
    {
        super.setParentContainer(pc);
        for(SwitchCaseBlock block : getCases())
        {
            block.setParentContainer(pc);
        }
    }

    @Override
    public void setContainingFormat(Format format)
    {
        super.setContainingFormat(format);
        for(SwitchCaseBlock block : getCases())
        {
            block.setContainingFormat(format);
        }
    }
    
    
}