/**
 * This file is part of aion-unique <aion-unique.smfnew.com>.
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

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.stats.PlayerGameStats;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LOOT_ITEMLIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LOOT_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE;
import com.aionemu.gameserver.network.aion.AionClientPacket;

import java.util.Random;
/**
 * 
 * @author alexa026
 * 
 */
public class CM_START_LOOT extends AionClientPacket
{
	/**
	 * Target object id that client wants to TALK WITH or 0 if wants to unselect
	 */
	private int					targetObjectId;
	private int					unk;

	/**
	 * Constructs new instance of <tt>CM_CM_REQUEST_DIALOG </tt> packet
	 * @param opcode
	 */
	public CM_START_LOOT(int opcode)
	{
		super(opcode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl()
	{
		targetObjectId = readD();// empty
		unk = readC();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
		Random generator = new Random();
		int ran = generator.nextInt(100)+1;
		int itemId = 169400039 + ran;
		int itemNameId = 2211143 + ran;
		
		Player player = getConnection().getActivePlayer();
		PlayerGameStats playerGameStats = player.getGameStats();
		if (playerGameStats.getItemId() == 0)
		{
			playerGameStats.setItemId(itemId);
			playerGameStats.setItemNameId(itemNameId);
		
			sendPacket(new SM_LOOT_ITEMLIST(targetObjectId,itemId,1));	
			sendPacket(new SM_LOOT_STATUS(targetObjectId,2));
			sendPacket(new SM_EMOTION(targetObjectId,35,0));
		}else
		{
			//sendPacket(new SM_LOOT_ITEMLIST(targetObjectId,itemId,1));	
			sendPacket(new SM_LOOT_STATUS(targetObjectId,3));
			sendPacket(new SM_DELETE((Creature) player.getTarget()));
			playerGameStats.setItemId(0);
		}
		
	}
}
