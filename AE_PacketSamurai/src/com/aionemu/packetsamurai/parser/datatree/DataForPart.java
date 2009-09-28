package com.aionemu.packetsamurai.parser.datatree;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.packetsamurai.parser.formattree.ForPart;


public class DataForPart extends DataTreeNodeContainer
{
    private List<DataForBlock> _blockList; //list used for type casting
    
    // avoids construction of root ForPart
    @SuppressWarnings("unused")
    private DataForPart()
    {
    }

    public DataForPart(DataTreeNodeContainer container, ForPart part)
    {
        super(container, part);
    }

    @Override
    public void addNode(DataTreeNode node)
    {
        if(node instanceof DataForBlock)
            super.addNode(node);
        else
            throw new IllegalArgumentException("Only DataForBlocks can be added to DataForParts");
        _blockList = null; //invalidate casting list
    }

    @Override
    public List<DataForBlock> getNodes()
    {
        if(_blockList == null)
        {
            // right this is quite slow sompared to just not overriding, plus it kinda duplicates the list (even if it doesnt duplicate the content)
            // but this is the only way i found to make a clean API : getNodes() in DataForPart must have the List<DataForBlock> return type
            // just casting super.getNodes() to List<DataForBlock> will give an unsafe cast warning.
            _blockList = new ArrayList<DataForBlock>();
            for(DataTreeNode node : super.getNodes())
            {
                _blockList.add((DataForBlock) node);
            }
        }
        return _blockList;
    }
    
    @Override
    public String treeString()
    {
        return ((ForPart)this.getModelPart()).treeString();
    }
}