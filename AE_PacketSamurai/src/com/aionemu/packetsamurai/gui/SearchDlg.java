package com.aionemu.packetsamurai.gui;
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

import java.awt.Color;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;


import com.aionemu.packetsamurai.PacketSamurai;
import com.aionemu.packetsamurai.parser.PartType;
import com.aionemu.packetsamurai.parser.datatree.IntValuePart;
import com.aionemu.packetsamurai.parser.datatree.StringValuePart;
import com.aionemu.packetsamurai.parser.formattree.ForPart;
import com.aionemu.packetsamurai.parser.formattree.Part;
import com.aionemu.packetsamurai.protocol.Protocol;
import com.aionemu.packetsamurai.protocol.protocoltree.PacketFamilly;
import com.aionemu.packetsamurai.protocol.protocoltree.PacketFormat;
import com.aionemu.packetsamurai.protocol.protocoltree.ProtocolNode;
import com.aionemu.packetsamurai.session.DataPacket;
import com.aionemu.packetsamurai.session.GameSessionViewer;

import javolution.util.FastList;

/**
 * @author Ulysses R. Ribeiro
 *
 */
@SuppressWarnings("serial")
public class SearchDlg extends JDialog
{
	private GridBagConstraints _cons = new GridBagConstraints();
	private GridBagLayout _layout = new GridBagLayout();
	
	private JComboBox _packetSelect;
	private JComboBox _partSelect;
	private JComboBox _operatorSelect;
	private JTextField _valueField;
	private JLabel _bottomMessage;
	private JButton _searchButton;
	
	private static final String[] MATH_OPERATORS = {"==", "!=", ">", ">=", "<", "<="};
	private static final String[] STRING_OPERATORS = {"==", "!="};
	
	private List<PacketFormat> _formats;
	
	private PacketFormat _currentFormat;
	private Part _currentPart;
	private int _currentIndex;
	private boolean _stringPart;
	
	
	public SearchDlg(Frame owner)
	{
		this.setSize(400,150);
		this.setLocationRelativeTo(owner);
		this.setLayout(_layout);
		this.setTitle("Search");
		
		//get all the packets
		getAllFormatsName();

		_cons.insets = new Insets(5,5,5,5);
		_cons.anchor = GridBagConstraints.FIRST_LINE_START;
		_cons.fill = GridBagConstraints.NONE;
		_cons.weighty = 0.2;
		_cons.weightx = 0.75;
		_cons.gridwidth = 3;
		_packetSelect = new JComboBox();
		for (PacketFormat format : _formats)
		{
			_packetSelect.addItem(format.getOpcodeStr()+" "+format.getName());
		}
		_packetSelect.setEditable(false);
		_packetSelect.setSelectedIndex(0);
		_packetSelect.setActionCommand("PacketSelected");
		_packetSelect.addActionListener(new SearchActionListener());
		this.add(_packetSelect,_cons);
		
		_cons.fill = GridBagConstraints.HORIZONTAL;
		_cons.weightx = 0.4;
		_cons.weighty = 0.2;
		_cons.gridy = 1;
		_cons.gridwidth = 1;
		_partSelect = new JComboBox();
		_partSelect.setEditable(false);
		_partSelect.setActionCommand("PartSelected");
		_partSelect.addActionListener(new SearchActionListener());
		this.add(_partSelect,_cons);
		
		_cons.gridx = 1;
		_cons.weightx = 0.2;
		_operatorSelect = new JComboBox();
		_operatorSelect.setEditable(false);
		_operatorSelect.setActionCommand("OperatorSelected");
		_operatorSelect.addActionListener(new SearchActionListener());
		this.add(_operatorSelect,_cons);
		
		_cons.gridx = 2;
		_cons.weightx = 0.5;
		_valueField = new JTextField();
		this.add(_valueField,_cons);
		
		_cons.gridx = 0;
		_cons.gridy = 2;
		_cons.gridwidth = 2;
		_cons.weightx = 0.75;
		_bottomMessage = new JLabel();
		_bottomMessage.setForeground(Color.RED);
		this.add(_bottomMessage,_cons);
		
		_cons.gridx = 2;
		_cons.weightx = 0.75;
		_cons.anchor = GridBagConstraints.LAST_LINE_END;
		_cons.gridwidth = 1;
		_searchButton = new JButton("Search");
		_searchButton.setActionCommand("Search");
		_searchButton.addActionListener(new SearchActionListener());
		this.add(_searchButton,_cons);
		_currentIndex = 0;
		this.setVisible(true);
        this.setAlwaysOnTop(true);
		_stringPart = false;
	}
	
	public class SearchActionListener implements ActionListener
	{

		public void actionPerformed(ActionEvent evt) 
		{
			String actionCmd = evt.getActionCommand();
			if (actionCmd.equals("PacketSelected"))
			{
				_currentIndex = 0;
				_currentFormat = _formats.get(_packetSelect.getSelectedIndex());
				_partSelect.removeAllItems();
				//_partSelect.setSelectedIndex(-1);
				_currentPart = null;
				_partSelect.addItem("<none>");
				for (Part part : _currentFormat.getDataFormat().getMainBlock().getParts())
				{
					if (part instanceof ForPart)
						continue;
					_partSelect.addItem(part.getName());
				}
			}
			else if (actionCmd.equals("PartSelected"))
			{
				_currentIndex = 0;
                String name = (String)_partSelect.getSelectedItem();
                if(name == null)
                    return;
				
				_currentPart = _currentFormat.getDataFormat().getMainBlock().getPartByName(name);
                if(_currentPart == null)
                    return;
				
				if ((!_stringPart || _operatorSelect.getItemCount() <= 0) && (_currentPart.getType() == PartType.S || _currentPart.getType() == PartType.s))
				{
					_operatorSelect.removeAllItems();
					_stringPart = true;
					for (String str : STRING_OPERATORS)
					{
						_operatorSelect.addItem(str);
					}
					_operatorSelect.setSelectedIndex(0);
				}
				else if((_stringPart || _operatorSelect.getItemCount() <= 0) && !(_currentPart.getType() == PartType.S || _currentPart.getType() == PartType.s))
				{
					_operatorSelect.removeAllItems();
					_stringPart = false;
					for (String str : MATH_OPERATORS)
					{
						_operatorSelect.addItem(str);
					}
					_operatorSelect.setSelectedIndex(0);
				}
			}
            else if(actionCmd.equals("OperatorSelected"))
            {
                _currentIndex = 0;
            }
			else if (actionCmd.equals("Search"))
			{
				_bottomMessage.setText("");
				int index = search(_currentIndex);
				System.out.println("Search return: "+index);
				if (index >= 0)
				{
					JTable pt = ((Main) PacketSamurai.getUserInterface()).getViewerTab().getCurrentViewPane().getPacketTable();
					pt.setAutoscrolls(true);
					pt.getSelectionModel().setSelectionInterval(index, index);
					pt.scrollRectToVisible(pt.getCellRect(index, 0, true));
					_currentIndex = index+1;
				}
				else
				{
					_bottomMessage.setText("None found!");
				}
			}
			//Main.getInstance().getPacketTableModel()
		}
	}
	
	public int search(int startIndex)
	{
		GameSessionViewer gameSessionViewer = ((Main) PacketSamurai.getUserInterface()).getViewerTab().getCurrentViewPane().getGameSessionViewer();
		if (gameSessionViewer == null)
			return -1;
		
		List<DataPacket> packets = gameSessionViewer.getAllPackets();
		if (packets == null)
			return -1;
		
		PacketFormat format;
		int size = packets.size();
		System.out.println("Search Size: "+size);
		for (int i = startIndex; i < size; i++)
		{
			DataPacket gp = packets.get(i); 
			format = gp.getPacketFormat();
			if (format != null)
			{
				if (format == _currentFormat)
				{
					if (_currentPart != null)
					{
						switch (_operatorSelect.getSelectedIndex())
						{
						case 0: // ==
							if ((_currentPart.getType() != PartType.S) && (_currentPart.getType() != PartType.s))
							{
								try
								{
									int value = Integer.decode(_valueField.getText());
									int partValue = ((IntValuePart)gp.getRootNode().getPartByName(_currentPart.getName())).getIntValue();
									System.out.println(value+" == "+partValue);
									if (value == partValue)
										return i;
								}
								catch (NumberFormatException nfe)
								{
									//nfe.printStackTrace();
								}
							}
							else
							{
								if (((StringValuePart)gp.getRootNode().getPartByName(_currentPart.getName())).getStringValue().equalsIgnoreCase(_valueField.getText()))
									return i;
							}
							break;
						case 1: // !=
							if ((_currentPart.getType() != PartType.S) && (_currentPart.getType() != PartType.s))
							{
								try
								{
									int value = Integer.decode(_valueField.getText());
                                    int partValue = ((IntValuePart)gp.getRootNode().getPartByName(_currentPart.getName())).getIntValue();
									System.out.println(value+" != "+partValue);
									if (value != partValue)
										return i;
								}
								catch (NumberFormatException nfe)
								{
									//nfe.printStackTrace();
								}
							}
							else
							{
								if (!((StringValuePart)gp.getRootNode().getPartByName(_currentPart.getName())).getStringValue().equalsIgnoreCase(_valueField.getText()))
									return i;
							}
							break;
						case 2: // >
							try
							{
								int value = Integer.decode(_valueField.getText());
                                int partValue = ((IntValuePart)gp.getRootNode().getPartByName(_currentPart.getName())).getIntValue();
								if (partValue > value)
									return i;
							}
							catch (NumberFormatException nfe)
							{
								//nfe.printStackTrace();
							}
							break;
						case 3: // >=
							try
							{
								int value = Integer.decode(_valueField.getText());
                                int partValue = ((IntValuePart)gp.getRootNode().getPartByName(_currentPart.getName())).getIntValue();
								if (partValue >= value)
									return i;
							}
							catch (NumberFormatException nfe)
							{
								//nfe.printStackTrace();
							}
							break;
						case 4: // <
							try
							{
								int value = Integer.decode(_valueField.getText());
                                int partValue = ((IntValuePart)gp.getRootNode().getPartByName(_currentPart.getName())).getIntValue();
								if (partValue < value)
									return i;
							}
							catch (NumberFormatException nfe)
							{
								//nfe.printStackTrace();
							}
							break;
						case 5: // <=
							try
							{
								int value = Integer.decode(_valueField.getText());
                                int partValue = ((IntValuePart)gp.getRootNode().getPartByName(_currentPart.getName())).getIntValue();
								if (partValue <= value)
									return i;
							}
							catch (NumberFormatException nfe)
							{
								//nfe.printStackTrace();
							}
							break;
						}
					}
					else
					{
						return i;
					}
				}
			}
		}
		
		return -1;
	}
	
	public void setCurrentSearchIndex(int i)
	{
		_currentIndex = i;
	}
	
	private void getAllFormatsName()
	{
		_formats = new FastList<PacketFormat>();
        GameSessionViewer viewer = ((Main) PacketSamurai.getUserInterface()).getViewerTab().getCurrentViewPane().getGameSessionViewer();
        if(viewer == null)
            return;

        Protocol currentProto = viewer.getSession().getProtocol();
		getAllFormatsName(currentProto.getServerPacketsFamilly(),true);
		getAllFormatsName(currentProto.getClientPacketsFamilly(),false);
	}
	
	private void getAllFormatsName(PacketFamilly packetFamilly, boolean fromServer)
	{
		for (ProtocolNode node : packetFamilly.getNodes().values())
		{
			if (node instanceof PacketFamilly)
			{
				System.out.println("Packetfamilly: "+((PacketFamilly)node).getID());
				getAllFormatsName((PacketFamilly)node,fromServer);
			}
			else
			{
				_formats.add((PacketFormat)node);
			}
		}
	}
}
