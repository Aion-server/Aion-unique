package com.aionemu.packetsamurai.gui;


import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableNode;

import com.aionemu.packetsamurai.parser.datatree.DataTreeNode;
import com.aionemu.packetsamurai.parser.datatree.ValuePart;

/**
 * @author Ulysses R. Ribeiro
 *
 */
class PacketViewTableModel extends DefaultTreeTableModel
{
	private static final String[] columnNames = {
		"Name",
		"Value",
		"Lookup"
	};
    
    public PacketViewTableModel(TreeTableNode root)
    {
        super(root);
    }
    
    @Override
    public int getColumnCount()
    {
        return columnNames.length;
    }
    
    @Override
    public String getColumnName(int col)
    {
        return columnNames[col];
    }
    
    public Object getValueAt(Object node, int column)
    {
        DataTreeNode part = ((DataPartNode)node).getPacketNode();
        switch(column)
        {
            case 0:
                return part.treeString();
            case 1:
                if(part instanceof ValuePart)
                {
                    return ((ValuePart)part).getValueAsString(); 
                }
                return "";
            case 2:
                if(part instanceof ValuePart)
                {
                    return ((ValuePart)part).readValueToComponent();
                }
                return "";
            default:
                return "";
        }
    }
}

