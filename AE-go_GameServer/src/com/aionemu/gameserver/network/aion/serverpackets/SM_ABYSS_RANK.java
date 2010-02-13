package com.aionemu.gameserver.network.aion.serverpackets;

import java.nio.ByteBuffer;

import com.aionemu.gameserver.model.gameobjects.player.AbyssRank;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author Nemiroff
 *         Date: 25.01.2010
 */
public class SM_ABYSS_RANK extends AionServerPacket {
	
	private AbyssRank rank;
	
	
	public SM_ABYSS_RANK(AbyssRank rank)
	{
		this.rank = rank;
	}

	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		writeQ(buf, rank.getAp()); //curAP
        writeD(buf, rank.getRank().getId()); //curRank
        writeD(buf, 0); //curRating
        writeD(buf, 0); //exp %
        writeD(buf, 0); //allKill
        writeD(buf, 1); //maxRank
        writeD(buf, 0); //dayKill
        writeQ(buf, 0); //dayAP
        writeD(buf, 0); //weekKill
        writeQ(buf, 0); //weekAP
        writeD(buf, 0); //laterKill
        writeQ(buf, 0); //laterAP
        writeC(buf, 0x00); //unk
	}
}
