/**
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
package com.aionemu.gameserver.network.aion.clientpackets;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.itemengine.actions.AbstractItemAction;
import com.aionemu.gameserver.itemengine.actions.ItemActions;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;

/**
 * @author Avol
 */
public class CM_USE_ITEM extends AionClientPacket {

	public int uniqueItemId;
	public int type, targetItemId;


	private static final Logger log = Logger.getLogger(CM_USE_ITEM.class);

	public CM_USE_ITEM(int opcode) {
		super(opcode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		uniqueItemId = readD();
		type = readC();
		if (type == 2)
		{
			targetItemId = readD();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl() 
	{
		Player player = getConnection().getActivePlayer();
		Item item = player.getInventory().getItemByObjId(uniqueItemId);
		if(item == null)
		{
			log.warn(String.format("CHECKPOINT: null item use action: %d %d", player.getObjectId(), uniqueItemId));
			return;
		}

		if (QuestEngine.getInstance().onItemUseEvent(new QuestEnv(null, player, 0, 0), item))
			return;

		//check use item multicast delay exploit cast (spam)
		if(player.isCasting())
		{
			//PacketSendUtility.sendMessage(this.getOwner(), "You must wait until cast time finished to use skill again.");
			return;
		}

		Item targetItem = player.getInventory().getItemByObjId(targetItemId);
		if(targetItem == null)
			targetItem = player.getEquipment().getEquippedItemByObjId(targetItemId);

		ItemActions itemActions = item.getItemTemplate().getActions();

		if (itemActions != null)
		{
			for (AbstractItemAction itemAction : itemActions.getItemActions())
			{
				itemAction.act(player, item, targetItem);
			}
		}
	}
}
