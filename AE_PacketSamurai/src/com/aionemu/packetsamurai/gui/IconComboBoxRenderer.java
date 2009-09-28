package com.aionemu.packetsamurai.gui;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.aionemu.packetsamurai.gui.images.IconsTable;



@SuppressWarnings("serial")
/**
 * 
 * @author Gilles Duboscq
 * 
 */
public class IconComboBoxRenderer extends JLabel implements ListCellRenderer
{
	private static final ImageIcon[] images = {
		IconsTable.ICON_C,
		IconsTable.ICON_H,
		IconsTable.ICON_D,
		IconsTable.ICON_F,
		IconsTable.ICON_Q,
		IconsTable.ICON_S,
		IconsTable.ICON_S_SMALL,
		IconsTable.ICON_B,
		IconsTable.ICON_X,
		IconsTable.ICON_FOR,
		IconsTable.ICON_SWITCH,};
	public static final String[] types = {"c", "h", "d", "f", "Q", "S", "s", "b", "x", "forblock", "swicthblock"};
	public static final String[] typesButBlocks = {"c", "h", "d", "f", "Q", "S", "s", "b", "x"};
	
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus)
	{
		int selectedIndex = sreachIndex(value);
		if(selectedIndex < 0 || selectedIndex >= types.length)
			selectedIndex = index;
		if(selectedIndex < 0 || selectedIndex >= types.length)
			selectedIndex = 0;

		if (isSelected)
		{
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else
		{
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}
		// Set the icon and text. If icon was null, say so.
		ImageIcon icon = images[selectedIndex];
		setText(types[selectedIndex]);
		setIcon(icon);
		if (icon != null)
		{
			setFont(list.getFont());
		}

		return this;
	}

	private int sreachIndex(Object value)
	{
		if(value instanceof String)
		{
			String str = (String) value;
			int i = 0;
			for(String type : types)
			{
				if(type.equals(str))
					return i;
				i++;
			}
		}
		return -1;
	}

}