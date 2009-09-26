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

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_BUYLIST;
import java.util.Random;
/**
 * 
 * @author alexa026
 * 
 */
public class CM_DIALOG_SELECT extends AionClientPacket
{
	/**
	* Target object id that client wants to TALK WITH or 0 if wants to unselect
	*/
	private int					targetObjectId;
	private int					unk1;
	private int					unk2;
	/**
	* Constructs new instance of <tt>CM_CM_REQUEST_DIALOG </tt> packet
	* @param opcode
	*/
	public CM_DIALOG_SELECT(int opcode)
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
		unk1 = readH(); //total no of choice
		unk2 = readH(); //maybe answer 1
	}

	/**
	* {@inheritDoc}
	*/
	@Override
	protected void runImpl()
	{
		//Player player = getConnection().getActivePlayer();
		//if(player == null)
		//	return;
		Random generator = new Random();
		int randomlist = generator.nextInt(5)+ 1;
		
		if (unk1 ==2)
		sendPacket(new SM_BUYLIST(targetObjectId,51201,28+randomlist));
	}
}
