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
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.stats.PlayerGameStats;

/**
 * 
 * @author alexa026, Avol, Corrected by Metos
 * 
 */
public class SM_LOOT_ITEMLIST extends AionServerPacket
{
	
	private int	targetObjectId;
	private int	monsterId;
	private Player player;
	private int [][] finaloot;
	private int nbloot;

	private static final Logger	log	= Logger.getLogger(SM_LOOT_ITEMLIST.class);

	public SM_LOOT_ITEMLIST (int monsterId, int targetObjectId, Player player, int [][] finaloot, int nbloot) {
		this.monsterId = monsterId;
		this.targetObjectId = targetObjectId;
		this.player = player;
		this.finaloot = finaloot;
		this.nbloot = nbloot;
	}

	/**
	 * {@inheritDoc} dc
	 */
	
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf) {
		PlayerGameStats playerGameStats = player.getGameStats();
		
		playerGameStats.setItemIdArrayLenght(nbloot);
		for (int i = 0; i < nbloot; i++) {
			playerGameStats.setItemIdArray(finaloot[i][0], i);
			playerGameStats.setItemCountArray(finaloot[i][1], i);
		}
		
		writeD(buf, targetObjectId);
		writeH(buf, nbloot);
		for(int i = 0; i < nbloot; i++) {
			writeD(buf, finaloot[i][0]);
			writeD(buf, finaloot[i][1]);
			writeH(buf, 0);
			writeC(buf, 0);
		}
		writeH(buf, 0);
		
	}	
}