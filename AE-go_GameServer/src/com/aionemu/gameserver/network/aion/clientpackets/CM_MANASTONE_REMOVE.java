/*
 * This file is part of aion-unique <aion-unique.org>.
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

import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.services.ItemService;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * @author ATracer
 */
public class CM_MANASTONE_REMOVE extends AionClientPacket
{
	@Inject
	private World world;
	@Inject
	private ItemService itemService;
	
	private int npcObjId;
	private int itemObjId;
	private int slotNum;
	/**
	 * @param opcode
	 */
	public CM_MANASTONE_REMOVE(int opcode)
	{
		super(opcode);
	}

	@Override
	protected void readImpl()
	{
		this.npcObjId = readD();
		this.itemObjId = readD();
		this.slotNum = readC();
	}

	@Override
	protected void runImpl()
	{
		AionObject npc = world.findAionObject(npcObjId);
		Player player = getConnection().getActivePlayer();
		
		if(npc != null)
		{
			itemService.removeManastone(player, itemObjId, slotNum);
		}
	}

}
