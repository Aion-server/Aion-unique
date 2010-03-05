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
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Equipment;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UPDATE_PLAYER_APPEARANCE;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * 
 * @author Avol modified by ATracer
 */
public class CM_EQUIP_ITEM extends AionClientPacket
{
	public int	slotRead;
	public int	itemUniqueId;
	public int	action;

	public CM_EQUIP_ITEM(int opcode)
	{
		super(opcode);
	}

	@Override
	protected void readImpl()
	{
		action = readC(); // 0/1 = equip/unequip
		slotRead = readD();
		itemUniqueId = readD();
	}

	@Override
	protected void runImpl()
	{
		final Player activePlayer = getConnection().getActivePlayer();
		Equipment equipment = activePlayer.getEquipment();
		boolean operationResult = false;

		if(!RestrictionsManager.canChangeEquip(activePlayer))
			return;
		
		switch(action)
		{
			case 0:
				operationResult = equipment.equipItem(itemUniqueId, slotRead);
				break;
			case 1:
				operationResult = equipment.unEquipItem(itemUniqueId, slotRead);
				break;
			case 2:
				operationResult = equipment.switchHands();
				break;
		}

		if(operationResult)
		{
			PacketSendUtility.broadcastPacket(activePlayer, new SM_UPDATE_PLAYER_APPEARANCE(activePlayer.getObjectId(),
				equipment.getEquippedItems()), true);
		}

	}
}
