/*
 * This file is part of aion-unique <www.aion-unique.com>.
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
package com.aionemu.gameserver.itemengine;
import org.apache.log4j.Logger;

import com.aionemu.gameserver.itemengine.itemeffects.consumables.potions.HpPotion;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
/**
 * @author Avol
 * 
 */

public class ItemActionSelecter
{
	public int type;
	public int value;
	public int value2;
	public int timerEnd;
	public int timerInterval;
	public int itemId;
	public int itemObjId;
	public int abnormal_id;

	/** type list:
	*   HP_POTION(1)
	*/

	private static final Logger log = Logger.getLogger(ItemTemplateLoader.class);

	/**
	* set type, value, value2, timerEnd, timerInterval
	*/

	public ItemActionSelecter(int type, int value, int value2, int timerEnd, int timerInterval, int itemObjId, int itemId, int effect) 
	{
		this.type = type;
		this.value = value;
		this.value2 = value2;
		this.timerEnd = timerEnd;
		this.timerInterval = timerInterval;
		this.itemObjId = itemObjId;
		this.itemId = itemId;
		this.abnormal_id = effect;
	}

	/**
	* execute item effect by type. (item effect types are in actionSelecter)
	*/

	public void execute(Player player)
	{
		if (type==1) 
		{
			HpPotion effect = new HpPotion();
			effect.execute(value, timerEnd, timerInterval, abnormal_id, player);
			PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, itemId), true);
		} 
		else
		{
			PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, itemId), true);
			log.info("Unkown item effect type: " + type);
		}

	}
	
}
