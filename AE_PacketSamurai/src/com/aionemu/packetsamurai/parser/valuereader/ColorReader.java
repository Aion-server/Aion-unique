package com.aionemu.packetsamurai.parser.valuereader;

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JPanel;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.aionemu.packetsamurai.parser.datatree.IntValuePart;
import com.aionemu.packetsamurai.parser.datatree.ValuePart;

/**
 * 
 * @author Gilles Duboscq
 *
 */
public class ColorReader implements Reader
{
    public boolean loadReaderFromXML(Node n)
    {
        return true;
    }

    public String read(ValuePart part)
    {
        return part.getHexDump();
    }
    
    public JComponent readToComponent(ValuePart part)
    {
        if(!(part instanceof IntValuePart))
            throw new IllegalStateException("A ColorReader must be providen an IntValuePart");
        int color = ((IntValuePart)part).getIntValue();
        int r = (color  & 0x000000ff); //save red
        color = (color  & 0xff00ff00) | ((color & 0x00ff0000) >> 0x10); //swap red and blue
        color  = (color  & 0xff00ffff) | (r << 0x10);
        JPanel panel = new JPanel();
        panel.setBackground(new Color(color));
        return panel;
    }

    public <T extends Enum<T>> T getEnum(ValuePart part)
    {
        return null;
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