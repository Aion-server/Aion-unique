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

import org.w3c.dom.NamedNodeMap;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.Quest;
import com.aionemu.gameserver.questEngine.QuestEngineException;

/**
 * @author Blackmouse
 */

public class PcInventoryCondition extends QuestCondition
{
	private static final String NAME = "pc_inventory";
	private final int itemId;
	private final int itemCount;

	public PcInventoryCondition(NamedNodeMap attr, Quest quest)
	{
		super(attr, quest);
		itemId = Integer.parseInt(attr.getNamedItem("item_id").getNodeValue());
		itemCount = Integer.parseInt(attr.getNamedItem("count").getNodeValue());
		
	}

	@Override
	protected boolean doCheck(Player player, int data) throws QuestEngineException
	{
		Item item = player.getInventory().getItemByItemId(itemId);
		int _count = 0;
		if (item != null)
			_count = player.getInventory().getItemByItemId(itemId).getItemCount();
		System.out.println("Item count: "+_count);
		switch (getOp())
		{
			case EQUAL:
				return _count == itemCount;
			case GREATER:
				return _count > itemCount;
			case GREATER_EQUAL:
				return _count >= itemCount;
			case LESSER:
				return _count < itemCount;
			case LESSER_EQUAL:
				return _count <= itemCount;
			case NOT_EQUAL:
				return _count != itemCount;
			default:
				return false;
		}
	}

	@Override
	public String getName()
	{
		return NAME;
	}
}
