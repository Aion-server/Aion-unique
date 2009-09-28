package com.aionemu.packetsamurai.parser.datatree;

import com.aionemu.packetsamurai.parser.formattree.PartContainer;

/**
 * 
 * @author Gilles Duboscq
 *
 */
public class DataForBlock extends DataTreeNodeContainer
{
    private int _iteration;
    private int _size;
    
    // avoids construction of root ForBlock
    @SuppressWarnings("unused")
    private DataForBlock()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    public DataForBlock(DataTreeNodeContainer container, PartContainer part, int iteration, int size)
    {
        super(container, part);
        _iteration = iteration;
        _size = size;
    }
    
    @Override
    public String treeString()
    {
        return "Iteration "+(_iteration+1)+"/"+_size;
        
    }
}