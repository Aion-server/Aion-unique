/**
 * This file is part of aion-emu <aion-emu.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-emu is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network.aion.serverpackets;

import java.nio.ByteBuffer;
import java.util.Map;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * In this packet Server is sending Skill Info?
 * 
 * @author -Nemesiss-
 * 
 * modified by ATracer
 * 
 */
public class SM_SKILL_LIST extends AionServerPacket
{

	private Map<Integer, Integer> skillList;


	/**
 	 * Constructs new <tt>SM_SKILL_LIST </tt> packet
 	 */

	public SM_SKILL_LIST(Player player)
 	{
		this.skillList = player.getSkillList().getSkillList();
 	}
	
	/**
	 *  This constructor is used to update player with new asquired skills only 
	 *  
	 * @param skillList
	 */
	public SM_SKILL_LIST(Map<Integer, Integer> skillList)
	{
		this.skillList = skillList;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{

		final int size = skillList.size();
		writeH(buf, size);//skills list size
		
		if (size > 0)
		{
			for (Map.Entry<Integer, Integer> entry : skillList.entrySet())
			{
				writeH(buf, entry.getKey());//id
				writeD(buf, entry.getValue());//lvl
				writeD(buf, 0);//use time? [s]
				writeC(buf, 0);//unk
			}
		}
		writeD(buf, 0);// unk

	}
}