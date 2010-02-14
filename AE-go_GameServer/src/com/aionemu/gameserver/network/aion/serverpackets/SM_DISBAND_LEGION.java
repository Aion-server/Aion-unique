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
package com.aionemu.gameserver.network.aion.serverpackets;

import java.nio.ByteBuffer;
import java.util.Date;

import com.aionemu.gameserver.configs.LegionConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * 
 * @author Simple
 * 
 */
public class SM_DISBAND_LEGION extends AionServerPacket
{
	private Player	player;
	private Date	disbandLegionTime;

	public SM_DISBAND_LEGION(Player player)
	{
		this.player = player;
		this.disbandLegionTime = new Date();
	}

	@Override
	public void writeImpl(AionConnection con, ByteBuffer buf)
	{
		writeD(buf, player.getObjectId()); // char ID
		writeC(buf, player.getCommonData().getPlayerClass().getClassId());
		writeD(buf, player.getLevel());
		writeD(buf, player.getPosition().getMapId());
		writeD(buf, 0x00);
		writeH(buf, 0x4FD7);
		writeS(buf, disbandLegionTime.getTime() + LegionConfig.LEGION_DISBAND_TIME + "");
	}
}