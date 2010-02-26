package com.aionemu.gameserver.network.aion.serverpackets;

import java.nio.ByteBuffer;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author Nemiroff
 *         Date: 17.02.2010
 */
public class SM_ABYSS_RANK_UPDATE extends AionServerPacket{
    private Player player;
    private int rankId;

    public SM_ABYSS_RANK_UPDATE(Player player)
	{
		this.player = player;
		this.rankId = player.getAbyssRank().getRank().getId();
	}

    @Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		writeD(buf, player.getObjectId());
        writeD(buf, rankId);
	}
}
