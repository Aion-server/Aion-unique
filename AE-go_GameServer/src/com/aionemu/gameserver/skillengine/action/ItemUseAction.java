/*
 * This file is part of aion-unique <aion-unique.com>.
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
package com.aionemu.gameserver.skillengine.action;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Inventory;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UPDATE_ITEM;
import com.aionemu.gameserver.skillengine.model.Env;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ItemUseAction")
public class ItemUseAction extends Action
{
	@XmlAttribute(required = true)
    protected int itemid;
	
	@XmlAttribute(required = true)
    protected int count;
	
	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.skillengine.action.Action#act(com.aionemu.gameserver.skillengine.model.Env)
	 */
	@Override
	public void act(Env env)
	{
		Player player = (Player) env.getEffector();
		Inventory inventory = player.getInventory();
		Item item = inventory.removeFromBag(itemid, count);
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
