/*
 * This file is part of aion-unique <aionunique.smfnew.com>.
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
package com.aionemu.gameserver.model.quests.operations;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.aionemu.gameserver.model.quests.QuestEngineException;
import com.aionemu.gameserver.model.quests.QuestOperation;
import com.aionemu.gameserver.model.quests.QuestState;

/**
 * @author Blackmouse
 */
public class GiveItemOperation extends QuestOperation
{

	private static final String NAME = "give_item";
	@SuppressWarnings("unused")
	private static int itemCount;
	@SuppressWarnings("unused")
	private static int itemId;
	
	public GiveItemOperation(Node node)
	{
		super(node);
		for (Node innerNode = node.getFirstChild(); innerNode != null; innerNode = innerNode.getNextSibling())
		{
			try
			{
				if ("item".equalsIgnoreCase(innerNode.getNodeName()))
				{
					NamedNodeMap attr = innerNode.getAttributes();
					itemId = Integer.parseInt(attr.getNamedItem("id").getNodeValue());
					itemCount = Integer.parseInt(attr.getNamedItem("count").getNodeValue());
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void doOperate(QuestState state) throws QuestEngineException
	{
		//state.getPlayer().giveItem(itemId, itemCount, false);
	}

	@Override
	public String getName()
	{
		return NAME;
	}
}
