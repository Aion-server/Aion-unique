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
package com.aionemu.gameserver.network.aion.serverpackets;

import java.nio.ByteBuffer;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/** 
 * Opens a yes/no question window on the client.
 * Question based on the code given, defined in client_strings.xml
 * 
 * @author Ben
 *
 */
public class SM_QUESTION_WINDOW extends AionServerPacket
{
	/**
	 * Asks for confirmation of a friend. Takes as one parameter, the name of the person who added you
	 */
	public static final int STR_BUDDYLIST_ADD_BUDDY_REQUETS = 0x0DBEE9;
	public static final int STR_EXCHANGE_DO_YOU_ACCEPT_EXCHANGE = 0x15f91;
	public static final int STR_EXCHANGE_HE_REJECTED_EXCHANGE = 0x13D782;// TODO: make it a simple box, not a question.
	
	int code;
	String[] params;
	/**
	 * Creates a new <tt>SM_QUESTION_WINDOW<tt> packet
	 * @param code The string code to display, found in client_strings.xml
	 * @param params The parameters for the string, if any
	 */
	public SM_QUESTION_WINDOW(int code, String... params) {
		this.code = code;
		this.params = params;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		
		writeD(buf, code);
		for (String string : params) 
			writeS(buf, string);

		
	}

}
