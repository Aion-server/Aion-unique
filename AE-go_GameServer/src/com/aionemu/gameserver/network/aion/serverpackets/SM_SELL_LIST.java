package com.aionemu.gameserver.network.aion.serverpackets;

import java.nio.ByteBuffer;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.model.gameobjects.Npc;

/**
 * Author: Blackmouse
 */
public class SM_SELL_LIST extends AionServerPacket
{
	private Npc _merchant;

	public SM_SELL_LIST(Npc npc)
	{
		_merchant = npc;
	}

	@Override
	public void writeImpl(AionConnection con, ByteBuffer buf)
	{
		writeD(buf, _merchant.getObjectId());
		writeD(buf, 20/**_merchant.getPercentToSell()*/);
	}
}