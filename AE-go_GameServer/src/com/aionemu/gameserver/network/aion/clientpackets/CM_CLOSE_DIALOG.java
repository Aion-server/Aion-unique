/*
 * This file is part of aion-unique <aionunique.smfnew.com>.
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

import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LOOKATOBJECT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TARGET_UPDATE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

public class CM_CLOSE_DIALOG extends AionClientPacket
{
	/**
	* Target object id that client wants to TALK WITH or 0 if wants to unselect
	*/
	private int	targetObjectId;

	@Inject
	private World world;

	/**
	* Constructs new instance of <tt>CM_CM_REQUEST_DIALOG </tt> packet
	* @param opcode
	*/
	public CM_CLOSE_DIALOG(int opcode)
	{
		super(opcode);
	}

	/**
	* {@inheritDoc}
	*/
	@Override
	protected void readImpl()
	{
		targetObjectId = readD();
	}

	/**
	* {@inheritDoc}
	*/
	@Override
	protected void runImpl()
	{
		AionObject targetObject = world.findAionObject(targetObjectId);
		Player player = getConnection().getActivePlayer();

		if(targetObject == null || player == null)
			return;

		if(targetObject instanceof Npc)
		{
			if(((Npc) targetObject).getTarget() == player)
			{
				((Npc) targetObject).setTarget(null);
				PacketSendUtility.broadcastPacket((Npc) targetObject, new SM_TARGET_UPDATE((Npc) targetObject));
			}

			PacketSendUtility.broadcastPacket((Npc) targetObject,
				new SM_LOOKATOBJECT(targetObjectId, 0, ((Npc) targetObject).getHeading()));
		}
	}
}
