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
package com.aionemu.gameserver.itemengine.actions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UPDATE_PLAYER_APPEARANCE;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author IceReaper
 *
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DyeAction")

public class DyeAction extends AbstractItemAction
{

	@XmlAttribute(name = "color")
	protected String color;

	@Override
	public void act(Player player, Item parentItem, Item targetItem)
	{
		if (targetItem.getItemTemplate().isItemDyePermitted())
		{
			int rgb = Integer.parseInt(color, 16);
			int bgra = 0xFF | ((rgb & 0xFF) << 24) | ((rgb & 0xFF00) << 8) | ((rgb & 0xFF0000) >>> 8);
			targetItem.setItemColor(bgra);
			PacketSendUtility.sendPacket(player, new SM_UPDATE_PLAYER_APPEARANCE(player.getObjectId(), player.getEquipment().getEquippedItems()));
			player.getInventory().removeFromBagByObjectId(parentItem.getObjectId(), 1);
		}
	}
}
