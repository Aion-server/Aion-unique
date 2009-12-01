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
 * Request to create
 * 
 * @author SoulKeeper
 */
public class CM_MACRO_CREATE extends AionClientPacket
{

	/**
	 * Logger
	 */
	private static final Logger	log	= Logger.getLogger(CM_MACRO_CREATE.class);

	/**
	 * Macro number. Fist is 1, second is 2. Starting from 1, not from 0
	 */
	private int					macroPosition;

	/**
	 * XML that represents the macro
	 */
	private String				macroXML;

	@Inject
	private PlayerService playerService;

	/**
	 * Constructs new client packet instance.
	 * @param opcode
	 */
	public CM_MACRO_CREATE(int opcode)
	{
		super(opcode);
	}

	/**
	 * Read macro data
	 */
	@Override
	protected void readImpl()
	{
		macroPosition = readC();
		macroXML = readS();
	}

	/**
	 * Logging
	 */
	@Override
	protected void runImpl()
	{
		log.debug(String.format("Created Macro #%d: %s", macroPosition, macroXML));

		playerService.addMacro(getConnection().getActivePlayer(), macroPosition, macroXML);
		
		sendPacket(SM_MACRO_RESULT.SM_MACRO_CREATED);
	}
}
