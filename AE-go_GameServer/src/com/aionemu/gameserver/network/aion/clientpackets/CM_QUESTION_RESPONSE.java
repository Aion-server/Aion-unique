/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-emu is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;

/**
 *  Response to SM_QUESTION_WINDOW
 * @author Ben
 */
public class CM_QUESTION_RESPONSE extends AionClientPacket
{
    private int 			questionid;
    private int 			response;
    @SuppressWarnings("unused")
	private int 			senderid;
    
	public CM_QUESTION_RESPONSE(int opcode) 
	{
		super(opcode);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl()
	{
		questionid = readD();
		response = readH();
		readH();
		senderid = readD();
		readD();
		readH();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
		Player player = getConnection().getActivePlayer();
		player.getResponseRequester().respond(questionid, response);
	}

}
