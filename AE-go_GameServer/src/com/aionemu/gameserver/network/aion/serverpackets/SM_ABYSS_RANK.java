package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

import java.nio.ByteBuffer;

/**
 * @author Nemiroff
 *         Date: 25.01.2010
 */
public class SM_ABYSS_RANK extends AionServerPacket {
    @Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		writeQ(buf, 0); //curAP
        writeD(buf, 0); //curRank
        writeD(buf, 0); //curRating
        writeD(buf, 0); //exp %
        writeD(buf, 0); //allKill
        writeD(buf, 0); //maxRank
        writeD(buf, 0); //dayKill
        writeQ(buf, 0); //dayAP
        writeD(buf, 0); //weekKill
        writeQ(buf, 0); //weekAP
        writeD(buf, 0); //laterKill
        writeQ(buf, 0); //laterAP
        writeC(buf, 0x00); //unk
	}
}
