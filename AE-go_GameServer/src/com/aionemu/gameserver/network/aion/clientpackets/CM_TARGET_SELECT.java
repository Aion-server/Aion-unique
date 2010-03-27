/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 * aion-emu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-emu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TARGET_SELECTED;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TARGET_UPDATE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * Client Sends this packet when /Select NAME is typed.<br>
 * I believe it's the same as mouse click on a character.<br>
 * If client want's to select target - d is object id.<br>
 * If client unselects target - d is 0;
 * 
 * @author SoulKeeper, Sweetkr
 */
public class CM_TARGET_SELECT extends AionClientPacket
{
	/**
	 * Target object id that client wants to select or 0 if wants to unselect
	 */
	private int	targetObjectId;
	private int	type;

	@Inject
	private World	world;

	/**
	 * Constructs new client packet instance.
	 * @param opcode
	 */
	public CM_TARGET_SELECT(int opcode)
	{
		super(opcode);
	}

	/**
	 * Read packet.<br>
	 * d - object id;
	 * c - selection type;
	 */
	@Override
	protected void readImpl()
	{
		targetObjectId = readD();
		type = readC();
	}

	/**
	 * Do logging
	 */
	@Override
	protected void runImpl()
	{
		Player player = getConnection().getActivePlayer();
		if(player == null)
			return;

		AionObject obj = world.findAionObject(targetObjectId);
		if(obj != null && obj instanceof VisibleObject)
		{
			if(type == 1)
			{
				if(((VisibleObject) obj).getTarget() == null)
					return;
				player.setTarget(((VisibleObject) obj).getTarget());
			}
			else
			{
				player.setTarget(((VisibleObject) obj));
			}
			sendPacket(new SM_TARGET_SELECTED(player));
			PacketSendUtility.broadcastPacket(player, new SM_TARGET_UPDATE(player));
		}
	}
}
