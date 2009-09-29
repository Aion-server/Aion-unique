package com.aionemu.packetsamurai.session.packetbuffer;

import com.aionemu.packetsamurai.protocol.Protocol;

/**
 * 
 * @author Gilles Duboscq
 *
 */
public interface PacketBuffer
{
    /**
     * provides the PacketBuffer with a ref to the protocol with which it is used
     * @param p
     */
    public void setProtocol(Protocol p);
    
    /**
     * @return the size in bytes of the data of a wholly available packet, 0 if no packet is completly avaialable
     */
    public int nextAvaliablePacket();
    
    /**
     * Fills the byte arrays providen with the data of a packet.
     * Once this is called, the packet returned must be consumed
     * @param header
     * @param data
     */
    public void getNextPacket(byte[] header, byte[] data);
    
    /**
     * Provides raw data from the stream to the PacketBuffer
     * @param data
     */
    public void addData(byte[] data);
}