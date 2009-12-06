/*
 * This file is part of aion-unique <aion-unique.com>.
 *
 * aion-unique is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-unique is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.questEngine.conditions;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.w3c.dom.NamedNodeMap;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.Quest;
import com.aionemu.gameserver.questEngine.QuestEngineException;

/**
 * @author MrPoke
 */
public class NpcIdCondition extends QuestCondition
{
	private static final String NAME = "npc_id";
	private final List<Integer> npcsId = new ArrayList<Integer>();

	public NpcIdCondition(NamedNodeMap attr, Quest quest)
	{
		super(attr, quest);
		StringTokenizer st = new StringTokenizer(attr.getNamedItem("values").getNodeValue(), ",");
		int tokenCount = st.countTokens();
		for (int i=0; i<tokenCount; i++)
		{
			Integer value = Integer.decode(st.nextToken().trim());
			this.npcsId.add(value);
		}
	}

	@Override
	public String getName()
	{
		return NAME;
	}
	
	@Override
	protected boolean doCheck(Player player, int data) throws QuestEngineException
	{
		int id = 0;
		VisibleObject creature = player.getTarget();
		if (creature != null && creature instanceof Npc)
		{
			id = ((Npc)creature).getNpcId();
		}
		int npcId = npcsId.get(0);
		switch (getOp())
		{
			case EQUAL:
				return id == npcId;
			case GREATER:
				return id > npcId;
			case GREATER_EQUAL:
				return id >= npcId;
			case LESSER:
				return id < npcId;
			case LESSER_EQUAL:
				return id <= npcId;
			case NOT_EQUAL:
				return id != npcId;
			case IN:
				return npcsId.contains(id);
			case NOT_IN:
				return !(npcsId.contains(id));
			default:
				return false;
		}
	}
}
