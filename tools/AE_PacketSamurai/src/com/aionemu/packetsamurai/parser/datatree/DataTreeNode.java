package com.aionemu.packetsamurai.parser.datatree;


import com.aionemu.packetsamurai.parser.DataStructure.DataPacketMode;
import com.aionemu.packetsamurai.parser.formattree.Part;

/**
 * 
 * @author Gilles Duboscq
 *
 */
public abstract class DataTreeNode
{
    private DataTreeNodeContainer _container;
    private Part _modelPart;
    private DataPacketMode _mode;
    
    
    public DataTreeNode(DataTreeNodeContainer container, Part part)
    {
        this(container, part, container.getMode());
    }
    
    public DataTreeNode(DataTreeNodeContainer container, Part part, DataPacketMode mode)
    {
        _container = container;
        _modelPart = part;
        _container.addNode(this);
        _mode = mode;
    }
    
    public DataTreeNode()
    {
        this(DataPacketMode.PARSING);
    }
    
    public DataTreeNode(DataPacketMode mode)
    {
        _mode = mode;
    }

    public DataPacketMode getMode()
    {
        return _mode;
    }

    public DataTreeNodeContainer getParentContainer()
    {
        return _container;
    }
    
    public Part getModelPart()
    {
        return _modelPart;
    }
    
    public abstract int getBytesSize();

    public String treeString()
    {
        return "DataPacketNode :S";
    }
}