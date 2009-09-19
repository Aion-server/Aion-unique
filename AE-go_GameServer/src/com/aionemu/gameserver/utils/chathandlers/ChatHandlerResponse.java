/**
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

package com.aionemu.gameserver.utils.chathandlers;

/**
 * Every {@link ChatHandler} as a result returns object of ChatHandlerResponse type. Objects of this class contains dual
 * information:
 * <ul>
 * <li>(maybe) Transformed message in accessible by {@link #getMessage()}</li>
 * <li>information whether handler blocked this message (it means, that it won't be sent to client(s)</li>
 * </ul>
 * 
 * @author Luno
 * 
 */
public class ChatHandlerResponse
{
	/** Single instance of <tt>ChatHandlerResponse</tt> representing response with blocked message */
	public static final ChatHandlerResponse	BLOCKED_MESSAGE	= new ChatHandlerResponse(true, "");

	private boolean							messageBlocked;
	private String							message;

	/**
	 * 
	 * @param messageBlocked
	 * @param message
	 */
	public ChatHandlerResponse(boolean messageBlocked, String message)
	{
		this.messageBlocked = messageBlocked;
		this.message = message;
	}

	/**
	 * A message (maybe) changed by handler.
	 * 
	 * @return a message
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * 
	 * @return if true, it means that handler blocked sending this message to client.
	 */
	public boolean isBlocked()
	{
		return messageBlocked;
	}
}
