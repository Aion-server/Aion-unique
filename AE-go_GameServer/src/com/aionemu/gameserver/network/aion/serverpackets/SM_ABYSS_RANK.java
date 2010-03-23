/*
 * This file is part of aion-unique <www.aion-unique.org>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network.aion.serverpackets;

import java.nio.ByteBuffer;

import com.aionemu.gameserver.model.gameobjects.player.AbyssRank;
import com.aionemu.gameserver.model.gameobjects.player.AbyssRank.AbyssRankTemplate;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author Nemiroff
 * 		Date: 25.01.2010
 */
public class SM_ABYSS_RANK extends AionServerPacket
{
	private AbyssRank rank;
	private int currentRankId;
	
	public SM_ABYSS_RANK(AbyssRank rank)
	{
		this.rank = rank;
		this.currentRankId = rank.getRank().getId();
	}

	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		writeQ(buf, rank.getAp()); //curAP
		writeD(buf, currentRankId); //curRank
		writeD(buf, 0); //curRating

		int nextRankId = currentRankId < AbyssRankTemplate.values().length ? currentRankId + 1 : currentRankId;
		writeD(buf, 100 * rank.getAp()/AbyssRank.AbyssRankTemplate.getTemplateById(nextRankId).getRequired()); //exp %

		writeD(buf, rank.getAllKill()); //allKill
		writeD(buf, rank.getMaxRank()); //maxRank

		writeD(buf, 0); //dayKill
		writeQ(buf, 0); //dayAP

		writeD(buf, 0); //weekKill
		writeQ(buf, 0); //weekAP

		writeD(buf, 0); //laterKill
		writeQ(buf, 0); //laterAP

		writeC(buf, 0x00); //unk
	}
}
