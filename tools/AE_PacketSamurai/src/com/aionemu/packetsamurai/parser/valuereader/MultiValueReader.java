package com.aionemu.packetsamurai.parser.valuereader;

import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLabel;

import javolution.util.FastMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.aionemu.packetsamurai.PacketSamurai;
import com.aionemu.packetsamurai.parser.datatree.IntValuePart;
import com.aionemu.packetsamurai.parser.datatree.ValuePart;

/**
 * 
 * @author Gilles Duboscq
 *
 */
public class MultiValueReader implements Reader
{
    private Map<Integer, Case> _cases = new FastMap<Integer, Case>();
    private Case _default;
    private Class<?> _enum;
    private String _enumName;
    
    public boolean loadReaderFromXML(Node n)
    {
        NamedNodeMap attrs = n.getAttributes();
        Node atr = attrs.getNamedItem("enum");
        if(atr != null)
        {
            _enumName = atr.getNodeValue();
            // get enum class
        }
        for(Node subNode = n.getFirstChild(); subNode != null; subNode = subNode.getNextSibling())
        {
            if("case".equals(subNode.getNodeName()))
            {
                attrs = subNode.getAttributes();
                atr = attrs.getNamedItem("val");
                if(atr == null)
                {
                    PacketSamurai.getUserInterface().log("Error while reading MultiValue reader : no val atribute");
                    return false;
                }
                if("default".equals(atr.getNodeValue()))
                {
                    atr = attrs.getNamedItem("display");
                    String disp = atr.getNodeValue();
                    String enumValue = null;
                    if(_enum != null)
                    {
                        atr = attrs.getNamedItem("enumval");
                        if(atr == null)
                        {
                            PacketSamurai.getUserInterface().log("Warning: reading MultiValue reader : no enumval attribute while reader has an enum : disabling enum for this reader");
                            _enum = null;
                        }
                        else
                        {
                            enumValue = atr.getNodeValue();
                        }
                    }
                    _default = new Case(disp, enumValue);
                    continue;
                }
                int caseId = 0;
                try
                {
                    caseId = Integer.decode(atr.getNodeValue());
                }
                catch (NumberFormatException e)
                {
                    PacketSamurai.getUserInterface().log("Error while reading MultiValue reader : val should be a number");
                    return false;
                }
                atr = attrs.getNamedItem("display");
                String disp = atr.getNodeValue();
                String enumValue = null;
                if(_enum != null)
                {
                    atr = attrs.getNamedItem("enumval");
                    if(atr == null)
                    {
                        PacketSamurai.getUserInterface().log("Warning: reading MultiValue reader : no enumval attribute while reader has an enum : disabling enum for this reader");
                        _enum = null;
                    }
                    else
                    {
                        enumValue = atr.getNodeValue();
                    }
                }
                _cases.put(caseId, new Case(caseId, disp, enumValue));
            }
            else
            {
                //wtf!
            }
        }
        return true;
    }

    public String read(ValuePart part)
    {
        if(!(part instanceof IntValuePart))
            throw new IllegalStateException("A MultiValueReader must be providen an IntValuePart");
        Case c = _cases.get(((IntValuePart)part).getIntValue());
        if(c == null)
            c = _default;
        if(c == null)
            return "Unknown case";
        if(c.display != null)
            return c.display;
        return "";
    }
    
    public JComponent readToComponent(ValuePart part)
    {
        return new JLabel(this.read(part));
    }

    // XXX this is dirty, we should see if we can do something without having dirty unchecked type casting
    @SuppressWarnings("unchecked")
    public <T extends Enum<T>> T getEnum(ValuePart part)
    {
        if(_enum == null)
            throw new IllegalStateException("getEnum can not be called on a MultiValueReader that doesnt supports enums");
        if(!(part instanceof IntValuePart))
            throw new IllegalStateException("A MultiValueReader must be providen an IntValuePart");
        Case c = _cases.get(((IntValuePart)part).getIntValue());
        return Enum.valueOf((Class<T>)_enum, c.enumValue);
    }

    public boolean saveReaderToXML(Element element, Document doc)
    {
        if(this.supportsEnum())
            element.setAttribute("enum", _enumName);
        for(Case c : _cases.values())
        {
            Element caseElement = doc.createElement("case");
            caseElement.setAttribute("val", Integer.toString(c.id));
            caseElement.setAttribute("display", c.display);
            if(this.supportsEnum())
                caseElement.setAttribute("enumval", c.enumValue);
            element.appendChild(caseElement);
        }
        return true;
    }

    public boolean supportsEnum()
    {
        return (_enum != null);
    }
    
    private class Case
    {
        public int id;
        public boolean defaultCase;
        public String display;
        public String enumValue;
        
        public Case(int i, String dis, String enname)
        {
            id = i;
            display = dis;
            enumValue = enname;
            defaultCase = false;
        }
        
        public Case(String dis, String enname)
        {
            id = 0;
            display = dis;
            enumValue = enname;
            defaultCase = true;
        }
    }
    
}