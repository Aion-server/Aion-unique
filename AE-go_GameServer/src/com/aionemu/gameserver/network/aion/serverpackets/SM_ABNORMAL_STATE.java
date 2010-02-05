/*
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
package com.aionemu.gameserver.network.aion.serverpackets;

import java.nio.ByteBuffer;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.skillengine.model.Effect;

/**
 * @author Avol, ATracer
 * 
 */
public class SM_ABNORMAL_STATE extends AionServerPacket
{
	public int action;
	private Effect[] effects;
	
	public SM_ABNORMAL_STATE(Effect[] effects)
	{
		this.effects = effects;
	}

	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		writeD(buf, 0); // abnormal TODO
		writeH(buf, effects.length); 
		
		for(Effect effect : effects)
		{
			writeD(buf, effect.getEffectorId());
			writeH(buf, effect.getSkillId());
			writeH(buf, effect.getSkillLevel());
			writeD(buf, effect.getElapsedTime());
		}
	}
}