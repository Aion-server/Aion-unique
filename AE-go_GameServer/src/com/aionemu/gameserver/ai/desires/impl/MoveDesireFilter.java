/* This file is part of aion-unique <aion-unique.com>.
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
package com.aionemu.gameserver.ai.desires.impl;

import com.aionemu.gameserver.ai.desires.Desire;
import com.aionemu.gameserver.ai.desires.DesireIteratorFilter;
import com.aionemu.gameserver.ai.desires.MoveDesire;
import com.aionemu.gameserver.controllers.movement.MovementType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MOVE;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 *
 */
public class MoveDesireFilter implements DesireIteratorFilter
{
	private Creature npc;
	public MoveDesireFilter(Creature _npc)
	{
		npc = _npc;
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.ai.desires.DesireIteratorFilter#isOk(com.aionemu.gameserver.ai.desires.Desire)
	 */
	@Override
	public boolean isOk(Desire desire)
	{
		if (npc == null) return false;
		
		if (!npc.canPerformMove()) 
		{
			PacketSendUtility.broadcastPacket(npc, new SM_MOVE(npc, npc.getX(), npc.getY(), npc.getZ(), 0, 0, 0, (byte) 0, MovementType.MOVEMENT_STOP));
			return false;
		}
		return desire instanceof MoveDesire;
	}
}
