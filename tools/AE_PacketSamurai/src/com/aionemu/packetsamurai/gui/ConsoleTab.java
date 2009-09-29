/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.aionemu.packetsamurai.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * @author Ulysses R. Ribeiro
 *
 */
@SuppressWarnings("serial")
public class ConsoleTab extends JPanel
{
	private GridBagConstraints _cons = new GridBagConstraints();
	private GridBagLayout _layout = new GridBagLayout();
	
    private JTextArea _textPane;
    
	public ConsoleTab()
	{
		this.setLayout(_layout);
		_cons.fill = GridBagConstraints.BOTH;
		_cons.weightx = 1;
		_cons.weighty = 1;
		_cons.insets = new Insets(5,5,5,5);
		_textPane = new JTextArea();
		_textPane.setEditable(false);
		_textPane.setAutoscrolls(true);
		this.add(new JScrollPane(_textPane,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED),_cons);
	}

	public void addText(String text)
	{
        _textPane.append(text+"\n");
	}

}
