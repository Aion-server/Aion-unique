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
package com.aionemu.gameserver.questEngine.operations;

import org.w3c.dom.NamedNodeMap;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Inventory;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UPDATE_ITEM;
import com.aionemu.gameserver.questEngine.Quest;
import com.aionemu.gameserver.questEngine.QuestEngineException;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Blackmouse
 */
public class TakeItemOperation extends QuestOperation
{

	private static final String NAME = "take_item";
	private final int itemCount;
	private final int itemId;
	
	public TakeItemOperation(NamedNodeMap attr, Quest quest)
	{
		super(attr, quest);
		itemId = Integer.parseInt(attr.getNamedItem("item_id").getNodeValue());
		itemCount = Integer.parseInt(attr.getNamedItem("count").getNodeValue());
	}

	@Override
	protected void doOperate(Player player) throws QuestEngineException
	{
		int count = itemCount;
		Inventory inventory = player.getInventory();
		if (itemCount < 0)
		{
			Item tmp = inventory.getItemByItemId(itemId);
			if (tmp != null)
				count = tmp.getItemCount();
		}
		Item item = inventory.removeFromBag(itemId, count);
		if(item == null)
		{
			return;
		}
		if(item.getItemCount() > 0)
		{
			PacketSendUtility.sendPacket(player, new SM_UPDATE_ITEM(item));
		}
		else
		{
			PacketSendUtility.sendPacket(player, new SM_DELETE_ITEM(item.getObjectId()));
		}
	}

	@Override
	public String getName()
	{
		return NAME;
	}
}
