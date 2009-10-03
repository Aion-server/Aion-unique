package com.aionemu.gameserver.network.aion.serverpackets;

import java.nio.ByteBuffer;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * In this packet Server is sending Inventory Info?
 * 
 * @author -Nemesiss-
 * @updater alexa026
 * 
 */
public class SM_INVENTORY_INFO extends AionServerPacket
{
	/**
	 * Constructs new <tt>SM_INVENTORY_INFO </tt> packet
	 */
	public SM_INVENTORY_INFO()
	{
		//inventory will be set here
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		writeD(buf, 0x01); //1.5.x**
		writeH(buf, 0x00);
	}
}
