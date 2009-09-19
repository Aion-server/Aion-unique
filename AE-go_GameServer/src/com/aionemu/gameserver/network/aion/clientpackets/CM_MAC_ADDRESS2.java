package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.network.aion.AionClientPacket;

/**
 * Client sends this only once.
 * 
 * @author Luno
 * 
 */
public class CM_MAC_ADDRESS2 extends AionClientPacket
{
	/**
	 * Constructor
	 * @param opcode
	 */
	public CM_MAC_ADDRESS2(int opcode)
	{
		super(opcode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl()
	{
		int objectId = readD(); // lol NC
		byte[] macAddress = readB(6);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
		// TODO server should response - find out response packet.
	}
}
