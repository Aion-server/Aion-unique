package com.aionemu.packetsamurai.parser.valuereader;

import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLabel;

import javolution.util.FastMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.aionemu.packetsamurai.parser.datatree.IntValuePart;
import com.aionemu.packetsamurai.parser.datatree.ValuePart;

/**
 * 
 * @author Gilles Duboscq
 *
 */
public class BitSetReader implements Reader
{
    private Map<Integer,Bit> _bits = new FastMap<Integer, Bit>();
    
    public boolean loadReaderFromXML(Node n)
    {
        for(Node subNode = n.getFirstChild(); subNode != null; subNode = subNode.getNextSibling())
        {
            if("bit".equals(subNode.getNodeName()))
            {
                NamedNodeMap attrs = subNode.getAttributes();
                Node atr = attrs.getNamedItem("num");
                if(atr == null)
                {
                    //wrong
                    return false;
                }
                int bitId = 0;
                boolean inverted = false;
                try
                {
                    bitId = Integer.parseInt(atr.getNodeValue());
                }
                catch (NumberFormatException e)
                {
                    //wrong
                    return false;
                }
                atr = attrs.getNamedItem("inverted");
                if(atr != null)
                {
                    inverted = Boolean.parseBoolean(atr.getNodeValue());
                }
                atr = attrs.getNamedItem("display");
                _bits.put(bitId, new Bit(bitId, atr.getNodeValue(),inverted));
            }
            else
            {
                //wtf??
            }
        }
        return true;
    }

    public String read(ValuePart part)
    {
        if(!(part instanceof IntValuePart)) // this should be detected at loading anyway, we have to give the part type to the load function so it can check it
        {
            return "";
        }
        int val = ((IntValuePart)part).getIntValue(); // XXX this should be long to acept Q
        StringBuilder sb = new StringBuilder();
        int size = part.getType().getTypeByteNumber()*8;
        for(int i = 0; i<size; i++)
        {
            Bit b = _bits.get(i);
            if(b != null)
            {
                sb.append(b.name+":"+Boolean.toString((val&i) != 0)+" ");
            }
        }
        return sb.toString();
    }
    
    public JComponent readToComponent(ValuePart part)
    {
        return new JLabel(this.read(part));
    }
    
    private class Bit
    {
        int number;
        String name;
        boolean inverted;
        
        public Bit(int num, String n, boolean i)
        {
            number = num;
            name = n;
            inverted = i;
        }
    }

    public boolean saveReaderToXML(Element element, Document doc)
    {
        for (Bit b : _bits.values())
        {
            Element bit = doc.createElement("bit");
            bit.setAttribute("num", Integer.toString(b.number));
            bit.setAttribute("display", b.name);
            if(b.inverted)
                bit.setAttribute("inverted", "true");
            element.appendChild(bit);
        }
        return false;
    }

    public boolean supportsEnum()
    {
        return false;
    }

    public <T extends Enum<T>> T getEnum(ValuePart part)
    {
        return null;
    }
}