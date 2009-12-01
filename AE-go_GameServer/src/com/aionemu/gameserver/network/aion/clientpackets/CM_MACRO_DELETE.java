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

import org.apache.log4j.Logger;

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MACRO_RESULT;
import com.aionemu.gameserver.services.PlayerService;
import com.google.inject.Inject;

/**
 * Packet that is responsible for macro deletion.<br>
 * Client sends id in the macro list.<br>
 * For instance client has 4 macros and we are going to delete macro #3.<br>
 * Client sends request to delete macro #3.<br>
 * And macro #4 becomes macro #3.<br>
 * So we have to use a list to store macros properly.
 * 
 * @author SoulKeeper
 */
public class CM_MACRO_DELETE extends AionClientPacket
{

	/**
	 * Logger
	 */
	private static final Logger	log	= Logger.getLogger(CM_MACRO_DELETE.class);

	/**
	 * Macro id that has to be deleted
	 */
	private int					macroPosition;

	@Inject
	private PlayerService playerService;
	
	/**
	 * Constructs new client packet instance.
	 * @param opcode
	 */
	public CM_MACRO_DELETE(int opcode)
	{
		super(opcode);
	}

	/**
	 * Reading macro id
	 */
	@Override
	protected void readImpl()
	{
		macroPosition = readC();
	}

	/**
	 * Logging
	 */
	@Override
	protected void runImpl()
	{
		log.debug("Request to delete macro #" + macroPosition);

		playerService.removeMacro(getConnection().getActivePlayer(), macroPosition);
		
		sendPacket(SM_MACRO_RESULT.SM_MACRO_DELETED);
	}
}
