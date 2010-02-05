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
/**
 * 
 * @author alexa026, Avol, ATracer
 * 
 */
public class CM_ATTACK extends AionClientPacket
{
	/**
	 * Target object id that client wants to TALK WITH or 0 if wants to unselect
	 */
	private int					targetObjectId;
	// TODO: Question, are they really needed?
	@SuppressWarnings("unused")
	private int					attackno;
	@SuppressWarnings("unused")
	private int					time;
	@SuppressWarnings("unused")
	private int					type;
	@SuppressWarnings("unused")
	private long                exp;
	@SuppressWarnings("unused")
	private long                maxexp;
	@SuppressWarnings("unused")
	private int					at;

	public CM_ATTACK(int opcode)
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
		attackno = readC();// empty
		time = readH();// empty
		type = readC();// empty
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{

		Player player = getConnection().getActivePlayer();
		if(!player.getLifeStats().isAlreadyDead())
		{
			player.getController().attackTarget(targetObjectId);
		}
	}
}
