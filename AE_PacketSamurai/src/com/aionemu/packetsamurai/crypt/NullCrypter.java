package com.aionemu.packetsamurai.crypt;


import com.aionemu.packetsamurai.protocol.Protocol;
import com.aionemu.packetsamurai.protocol.protocoltree.PacketFamilly.packetDirection;

/**
 * 
 * @author Gilles Duboscq
 *
 */
public class NullCrypter implements ProtocolCrypter
{

	public boolean decrypt(byte[] raw, packetDirection dir)
	{
		return true;
	}

    public void setProtocol(Protocol protocol)
    {
        
    }
}