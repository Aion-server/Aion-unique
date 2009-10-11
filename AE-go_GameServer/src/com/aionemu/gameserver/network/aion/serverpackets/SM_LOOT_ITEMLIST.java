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
package com.aionemu.gameserver.network.aion.serverpackets;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.model.gameobjects.player.DropList;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.stats.PlayerGameStats;
import java.util.Random;

/**
 * 
 * @author alexa026, Avol
 * 
 */
public class SM_LOOT_ITEMLIST extends AionServerPacket
{
	private int	targetObjectId;
	private int	count;
	private int	itemid;
	private int	itemChance;
	private int	itemCount;
	private int	monsterId;
	private int	playerObjectId;
	private int	chance[];
	private int	droppedItemId[];
	private int	droppedItemQuanty[];
	Player player;
	

	private static final Logger	log	= Logger.getLogger(SM_LOOT_ITEMLIST.class);

	public SM_LOOT_ITEMLIST(int monsterId, int targetObjectId, Player player)
	{
		this.monsterId = monsterId;
		this.targetObjectId = targetObjectId;
		this.player = player;
	}

	/**
	 * {@inheritDoc} dc
	 */
	
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{	
		PlayerGameStats playerGameStats = player.getGameStats();	
		
		int itemid;
		int itemChance = 1;
		int itemMin;
		int itemMax;
		int count;
		int droppedItemCount = 0;

		DropList dropData = new DropList();
		dropData.getDropList(monsterId);
		itemCount = dropData.getItemsCount();

		Random r = new Random();

		chance = new int[500];
		droppedItemId = new int[500];
		droppedItemQuanty = new int[500];

		int row = 0;
		int arrayLenght = 0;
		for(int i = 0; i < itemCount; i++)
		{	
			itemChance = dropData.getDropDataChance(row);

			if (itemChance == 1) {
				chance[row] = 0;
			} else {
				chance[row] = r.nextInt(itemChance); //itemChance-1
			}
			if (chance[row]==0) {
				arrayLenght++;
			}
			row++;
		}
		
		playerGameStats.setItemIdArrayLenght(arrayLenght);
		

		row = 0;
		for(int i = 0; i < itemCount; i++)
		{	
			if (chance[row] == 0) {
				droppedItemCount++;
				droppedItemId[droppedItemCount-1] = dropData.getDropDataItemId(row);

				itemMin = dropData.getDropDataMin(row);
				itemMax = dropData.getDropDataMax(row);

				if (itemMax == 1) {
					droppedItemQuanty[droppedItemCount-1] = 1;
				} else {
					int add = itemMax - itemMin;
					int dropedQuanty = r.nextInt(add);
					if (dropedQuanty > 0) {
						dropedQuanty--;
					}
					droppedItemQuanty[droppedItemCount-1] = itemMin + dropedQuanty;
				}

				playerGameStats.setItemIdArray(dropData.getDropDataItemId(droppedItemCount-1),droppedItemCount-1);
				playerGameStats.setItemCountArray(droppedItemQuanty[droppedItemCount-1],droppedItemCount-1);

			}
			
			row++;
		}
			

		writeD(buf, targetObjectId);
		writeH(buf, droppedItemCount);

		row = 0;

		for(int i = 0; i < droppedItemCount; i++)
		{
			writeD(buf, droppedItemId[row]);
			writeD(buf, droppedItemQuanty[row]); //count
			writeH(buf, 0);
			writeC(buf, 0);

			log.info(String.format("item quanty: %s", droppedItemQuanty[row]));

			row+=1;
		}
		writeH(buf, 0);
	}	
}
