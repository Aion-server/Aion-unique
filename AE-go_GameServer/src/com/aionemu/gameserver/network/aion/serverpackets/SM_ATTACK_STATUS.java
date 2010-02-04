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

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * 
 * @author alexa026
 * @author ATracer
 * 
 */
public class SM_ATTACK_STATUS extends AionServerPacket
{
    private Creature creature;
    private TYPE type;
    private int skillId;
    private int value;
 
    
    public static enum TYPE
    {
    	REGULAR(5),
    	DAMAGE(7),
    	HP(9),
    	MP(19);
    	
    	private int value;
    	
    	private TYPE(int value)
    	{
    		this.value = value;
    	}
    	
    	public int getValue()
    	{
    		return this.value;
    	}
    }
	
	public SM_ATTACK_STATUS(Creature creature, TYPE type, int skillId, int value)
	{
		this.creature = creature;
		this.type = type;
		this.skillId = skillId;
		this.value = value;
	}
	
	public SM_ATTACK_STATUS(Creature creature, int value)
	{
		this.creature = creature;
		this.type = TYPE.REGULAR;
		this.skillId = 0;
	}

	/**
	 * {@inheritDoc} ddchcc
	 */
	
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{		
		writeD(buf, creature.getObjectId());
		switch(type)
		{
			case DAMAGE:
				writeD(buf, -value);
				break;
			default:
				writeD(buf, value);
		}		
		writeC(buf, type.getValue());
		writeC(buf, creature.getLifeStats().getHpPercentage());
		writeH(buf, skillId);
		writeC(buf, 0x9B);
	}	
}
