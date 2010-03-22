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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;

/**
 * @author Mr. Poke
 *
 */
public class CM_CRAFT extends AionClientPacket
{	
	@SuppressWarnings("unused")
	private int unk;
	private int targetTemplateId;
	private int recipeId;
	private int targetObjId;
	
	/**
	 * @param opcode
	 */
	public CM_CRAFT(int opcode)
	{
		super(opcode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl()
	{
		unk = readC();
		targetTemplateId = readD();
		recipeId = readD();
		targetObjId = readD();		
	}
	
	@Override
	protected void runImpl()
	{
		Player player = getConnection().getActivePlayer();
		
		if(player == null || !player.isSpawned())
			return;

		//disallow crafting in shutdown progress..
		if(player.getController().isInShutdownProgress())
			return;
			
		player.getController().startCrafting(targetTemplateId, recipeId, targetObjId);				
	}
}
