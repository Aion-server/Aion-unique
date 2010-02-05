/**
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

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.group.PlayerGroup;
import com.aionemu.gameserver.network.aion.AionClientPacket;

/**
 * @author Lyahim
 */
public class CM_GROUP_DISTRIBUTION extends AionClientPacket
{
	private int amount;
	
	private static final Logger	log	= Logger.getLogger(PlayerGroup.class);
	
	public CM_GROUP_DISTRIBUTION(int opcode)
	{
		super(opcode);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl()
	{
		amount = readD();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
		Player player = getConnection().getActivePlayer();
		PlayerGroup pg = null;
		
		if(player != null)
			pg = player.getPlayerGroup();
		if(pg != null)
		{
			int rewardcount = pg.size() - 1;
		
			if(rewardcount <= amount)
			{
				int reward = amount/rewardcount;
				
				Iterator it = pg.getGroupMemberIterator();
				while(it.hasNext())
				{
					Player member = (Player)it.next();
					
					if(member.equals(player))
						member.getInventory().decreaseKinah(amount);
					else
						member.getInventory().increaseKinah(reward);
				}
			}			
		}
	}
}
