/**
 * 
 */
package com.aionemu.packetsamurai.parser.valuereader;

import javax.swing.JComponent;
import javax.swing.JLabel;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.aionemu.packetsamurai.parser.datatree.IntValuePart;
import com.aionemu.packetsamurai.parser.datatree.ValuePart;

/**
 * @author Ulysses R. Ribeiro
 *
 */
public class BooleanReader implements Reader
{

    public <T extends Enum<T>> T getEnum(ValuePart part)
    {
        return null;
    }

    public boolean loadReaderFromXML(Node n)
    {
        return true;
    }

    public String read(ValuePart part)
    {
        if (part instanceof IntValuePart)
        {
            return (((IntValuePart)part).getIntValue() == 1 ? "true" : "false");
        }
        return "";
    }

    public JComponent readToComponent(ValuePart part)
    {
        return new JLabel(this.read(part));
    }

    public boolean saveReaderToXML(Element element, Document doc)
    {
        return true;
    }

    public boolean supportsEnum()
    {
        return false;
    }

}
