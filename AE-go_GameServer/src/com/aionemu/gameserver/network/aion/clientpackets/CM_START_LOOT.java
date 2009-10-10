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
import com.aionemu.gameserver.model.gameobjects.player.DropList;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.stats.PlayerGameStats;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LOOT_ITEMLIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LOOT_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

import org.apache.log4j.Logger;
import java.util.Random;
/**
 * 
 * @author alexa026
 * 
 */
public class CM_START_LOOT extends AionClientPacket
{
	private static final Logger	log	= Logger.getLogger(CM_START_LOOT.class);

	/**
	 * Target object id that client wants to TALK WITH or 0 if wants to unselect
	 */
	private int					targetObjectId;
	private int					unk;
	private int					activePlayer;
	@Inject	
	private World			world;
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
		Player player = getConnection().getActivePlayer();
		PlayerGameStats playerGameStats = player.getGameStats();
		activePlayer = player.getObjectId();

		Random generator = new Random();

		Npc npc = (Npc) world.findAionObject(targetObjectId);
		int monsterId = npc.getTemplate().getNpcId();

		// load items from database.
		DropList dropData = new DropList();
		dropData.getDropList(monsterId);

		int totalItemsCount = dropData.getItemsCount();
		int row = 0;



		int ran = generator.nextInt(100)+1;

		// need to get item name id's from somewhere
		int itemNameId = 2211143 + ran;
		
		if (playerGameStats.getItemId() == 0)
		{
			if (totalItemsCount == 0) {
				//if no item is found for that mob, give item
				playerGameStats.setItemId(100000530);
				playerGameStats.setItemCount(1);
				sendPacket(new SM_LOOT_ITEMLIST(monsterId,targetObjectId, player));
				sendPacket(new SM_LOOT_STATUS(targetObjectId,2));
			} else {
				int itemId = 1;
				int itemMin = 1;
				int itemMax = 1;
				int itemChance = 1;
				int randomCountChance = 1;

				while (totalItemsCount > 0) {
					itemId = dropData.getDropDataItemId(row);
					itemMin = dropData.getDropDataMin(row);
 					itemMax = dropData.getDropDataMax(row);
 					itemChance = dropData.getDropDataChance(row); 

					randomCountChance = (int)Math.random() * (itemMax - itemMin) + itemMin;
			
					totalItemsCount = totalItemsCount-1;
					playerGameStats.setItemId(itemId);
					playerGameStats.setItemCount(randomCountChance);

					row+=1;
				}

				totalItemsCount = dropData.getItemsCount();
				if (totalItemsCount > 0) {
					sendPacket(new SM_LOOT_ITEMLIST(monsterId,targetObjectId, player));
					sendPacket(new SM_LOOT_STATUS(targetObjectId,2));
				}
			}

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
