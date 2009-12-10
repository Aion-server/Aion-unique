/*
 * This file is part of aion-unique <aion-unique.smfnew.com>.
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
import java.util.ArrayList;
import java.util.List;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.questEngine.QuestState;
import com.aionemu.gameserver.questEngine.types.QuestStatus;

/**
 * @author MrPoke
 *
 */
public class SM_QUEST_LIST extends AionServerPacket
{
	
	private List<QuestState> compliteQuestList = new ArrayList<QuestState>();
	private List<QuestState> startedQuestList = new ArrayList<QuestState>();

	public SM_QUEST_LIST(Player player)
	{
		for (QuestState qs : player.getQuestStateList().getAllQuestState())
		{
			if (qs.getStatus() == QuestStatus.COMPLITE)
				compliteQuestList.add(qs);
			else if (qs.getStatus() != QuestStatus.NONE)
				startedQuestList.add(qs);
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		//temp solution to enable teleports
		// TODO remove this as this quests are implemented
		int[] questList = { 2200, 2300, 1130, 1007, 1006, 2008, 2009, 1300 };

		writeH(buf, compliteQuestList.size() + questList.length);
		for (QuestState qs : compliteQuestList)
		{
			writeH(buf, qs.getQuestId());
			writeC(buf, 1);
		}
		
		for(int questId : questList)
		{
			writeH(buf, questId);
			writeC(buf, 1);
		}
		
		writeC(buf, startedQuestList.size());
		for (QuestState qs : startedQuestList) // quest list size ( max is 25 )
		{
			writeH(buf, qs.getQuestId());
			writeH(buf, 0);
		}
		for (QuestState qs : startedQuestList)
		{
			writeC(buf, qs.getStatus().value());
			writeD(buf, qs.getQuestVars().getQuestVars());
			writeC(buf, 0);
		}
	}

}