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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Inventory;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UPDATE_ITEM;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.utils.PacketSendUtility;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TakeItemOperation")
public class TakeItemOperation
    extends QuestOperation
{

    @XmlAttribute(name = "item_id", required = true)
    protected int itemId;
    @XmlAttribute(required = true)
    protected int count;

	@Override
	public void doOperate(QuestEnv env)
	{
		int itemCount = count;
		Player player = env.getPlayer();
		Inventory inventory = player.getInventory();
		if (count < 0)
		{
			Item tmp = inventory.getItemByItemId(itemId);
			if (tmp != null)
				itemCount = tmp.getItemCount();
		}
		Item item = inventory.removeFromBag(itemId, itemCount);
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
}
