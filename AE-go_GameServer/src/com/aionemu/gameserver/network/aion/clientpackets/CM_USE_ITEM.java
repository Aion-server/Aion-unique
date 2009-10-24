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

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.model.gameobjects.player.Inventory;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.itemengine.ItemEngine;
import com.aionemu.gameserver.model.templates.ItemTemplate;
import com.aionemu.gameserver.model.items.ItemStorage;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.itemengine.ItemTemplateLoader;
/**
 * 
 * @author Avol
 * 
 */
public class CM_USE_ITEM extends AionClientPacket
{
	public int uniqueItemId;

	private static final Logger log = Logger.getLogger(CM_USE_ITEM.class);

	public CM_USE_ITEM(int opcode)
	{
		super(opcode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl()
	{
		uniqueItemId = readD();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
		Player player = getConnection().getActivePlayer();
		
		ItemEngine itemEngine = new ItemEngine();
		itemEngine.setItem(uniqueItemId, player);
		itemEngine.useItem();

		sendPacket(SM_SYSTEM_MESSAGE.USE_ITEM(itemEngine.getItemName()));
	}
}
