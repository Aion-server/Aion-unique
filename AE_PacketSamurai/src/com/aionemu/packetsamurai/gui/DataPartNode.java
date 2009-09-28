package com.aionemu.packetsamurai.gui;


import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;

import com.aionemu.packetsamurai.parser.datatree.DataTreeNode;
import com.aionemu.packetsamurai.parser.datatree.DataTreeNodeContainer;
import com.aionemu.packetsamurai.parser.datatree.ValuePart;

public class DataPartNode extends DefaultMutableTreeTableNode
{
    private DataTreeNode _node;
    private int _offset;
    private int _length;
    
    public DataPartNode(DataTreeNode node, int offset)
    {
        super();
        _node = node;
        
        if (_node instanceof DataTreeNodeContainer)
        {
            int i = 0;
            for(DataTreeNode n : ((DataTreeNodeContainer)_node).getNodes())
            {
                this.insert(new DataPartNode(n, offset), i++);

                this.setOffset(offset);
                this.setLengtht(_node.getBytesSize()*3 - 1);
                offset += n.getBytesSize()*3;
            }
        }
        else
        {
            this.setOffset(offset);
            this.setLengtht(_node.getBytesSize()*3 - 1);
            offset += this.getLength() + 1;
        }
        
    }
    
    public boolean getAllowsChildren()
    {
        return (_node instanceof DataTreeNodeContainer);
    }
    
    public boolean isLeaf()
    {
        return(_node instanceof ValuePart);
    }
    
    public DataTreeNode getPacketNode()
    {
        return _node;
    }
    
    private void setOffset(int offset)
    {
        _offset = offset;
    }
    
    public int getOffset()
    {
        return _offset;
    }
    
    private void setLengtht(int length)
    {
        _length = length;
    }
    
    public int getLength()
    {
        return _length;
    }
}