package com.aionemu.packetsamurai.gui.protocoleditor;

import javax.swing.JComboBox;

import com.aionemu.packetsamurai.gui.IconComboBoxRenderer;


/**
 * 
 * @author Gilles Duboscq
 *
 */
@SuppressWarnings("serial")
public class PartTypeComboBox extends JComboBox
{
	public int offset;
	
	public PartTypeComboBox()
	{
		super(IconComboBoxRenderer.typesButBlocks);
	}
	
	public void setBounds(int x, int y, int w, int h)
	{
        
		System.out.println("reshaping, offset = "+offset);
	    int newX = Math.max(x, offset);
	    super.setBounds(newX, y, w - (newX - x), h);
	}
}