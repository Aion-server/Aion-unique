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
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.services.LegionService;
import com.google.inject.Inject;

/**
 * @author Simple
 * 
 */
public class CM_LEGION_MODIFY_EMBLEM extends AionClientPacket
{
	/** Legion based information **/
	@Inject
	private LegionService	legionService;

	/** Emblem related information **/
	private int				legionId;
	private int				emblemId;
	private int				red;
	private int				green;
	private int				blue;

	/**
	 * @param opcode
	 */
	public CM_LEGION_MODIFY_EMBLEM(int opcode)
	{
		super(opcode);
	}

	@Override
	protected void readImpl()
	{
		legionId = readD();
		emblemId = readH();
		readC(); // 0xFF color something?
		red = readC();
		green = readC();
		blue = readC();
	}

	@Override
	protected void runImpl()
	{
		Player activePlayer = getConnection().getActivePlayer();

		if(activePlayer.isLegionMember())
			legionService.storeLegionEmblem(activePlayer, legionId, emblemId, red, green, blue);
	}
}
