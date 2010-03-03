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
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * 
 * @author xavier
 * 
 */
public class CM_DUEL_REQUEST extends AionClientPacket
{
	/**
	 * Target object id that client wants to start duel with
	 */
	private int		objectId;
	@Inject
	private World	world;

	/**
	 * Constructs new instance of <tt>CM_DUEL_REQUEST</tt> packet
	 * 
	 * @param opcode
	 */
	public CM_DUEL_REQUEST(int opcode)
	{
		super(opcode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl()
	{
		objectId = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activePlayer = getConnection().getActivePlayer();
		AionObject target = world.findAionObject(objectId);
		
		if(target == null)
			return;
		
		if (target instanceof Player) {
			Player targetPlayer = (Player)target;
			targetPlayer.getController().onDuelRequest(activePlayer);
			activePlayer.getController().confirmDuelWith(targetPlayer);
		} else{
			sendPacket(SM_SYSTEM_MESSAGE.DUEL_PARTNER_INVALID(target.getName()));
		}
	}
}
