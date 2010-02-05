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
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * 
 * @author alexa026
 * 
 */
public class CM_CLOSE_LOOT extends AionClientPacket
{	
	/**
	 * Target object id that client wants to TALK WITH or 0 if wants to unselect
	 */
	@SuppressWarnings("unused")
	private int					targetObjectId;
	@SuppressWarnings("unused")
	private int					unk;
	@SuppressWarnings("unused")
	private int					slot;
	
	/**
	 * Constructs new instance of <tt>CM_CM_REQUEST_DIALOG </tt> packet
	 * @param opcode
	 */
	public CM_CLOSE_LOOT(int opcode)
	{
		super(opcode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl()
	{
		targetObjectId = readD();// empty
		unk = readC(); readC();
		slot = readH();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
		Player player = getConnection().getActivePlayer();
		//TODO this is incorrect - cause this packet called on each equip action
		//Is called when item is dragged to cube
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player,36,0,0));
//		sendPacket(new SM_LOOT_STATUS(targetObjectId,3));
	}
}
