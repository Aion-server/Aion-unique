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

package com.aionemu.gameserver.network.aion.serverpackets;

import java.nio.ByteBuffer;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * System message packet.
 * 
 * @author -Nemesiss-
 * @author EvilSpirit
 * @author Luno :D
 */
public class SM_SYSTEM_MESSAGE extends AionServerPacket
{

	/**
	 * Coordinates of current location: %WORLDNAME0 Region, X=%1 Y=%2 Z=%3
	 *
	 * @param worldId       id of the world
	 * @param x             x coordinate
	 * @param y             y coordinate
	 * @param z             z coordinate
	 * @return  Message instance.
	 */
	public static SM_SYSTEM_MESSAGE CURRENT_LOCATION(int worldId, float x, float y, float z)
	{
		return new SM_SYSTEM_MESSAGE(230038,worldId, x, y, z);
	}
	
	/**
	 * Busy in game
	 *
	 * @return
	 */
	public static final SM_SYSTEM_MESSAGE BUDDYLIST_BUSY = new SM_SYSTEM_MESSAGE(900847);

	/**
	 * %0 is not playing the game
	 *
	 * @param playerName    Player name.
	 * @return Message instance.
	 */
	public static SM_SYSTEM_MESSAGE PLAYER_IS_OFFLINE(String playerName)
	{
		return new SM_SYSTEM_MESSAGE(1300627, playerName);
	}
	
	/**
	 * The remaining playing time is %0.
	 * 
	 * @param playTime play time
	 * @return Message instance.
	 */
	public static SM_SYSTEM_MESSAGE REMAINING_PLAYING_TIME(int playTime)
	{
		return new SM_SYSTEM_MESSAGE(1300719, playTime);
	}
	
	/**
	 *  You are dead
	 */
	public static SM_SYSTEM_MESSAGE DIE = new SM_SYSTEM_MESSAGE(1340000);
	
	/**
	 * 
	 */
	public static SM_SYSTEM_MESSAGE REVIVE = new SM_SYSTEM_MESSAGE(1300738);
	/**
	 * 
	 */
	public static SM_SYSTEM_MESSAGE EXP(String _exp)
	{
		return new SM_SYSTEM_MESSAGE(1370002, _exp);
	}
	
	/**
	 * Your Friends List is full
	 */
	public static final SM_SYSTEM_MESSAGE BUDDYLIST_LIST_FULL = new SM_SYSTEM_MESSAGE(1300887);
	
	/**
	 * The character is not on your Friends List.
	 */
	public static final SM_SYSTEM_MESSAGE BUDDYLIST_NOT_IN_LIST = new SM_SYSTEM_MESSAGE(1300889);
	
	/**
	 * You cannot block a character who is currently on your Friends List.
	 * @return
	 */
	public static SM_SYSTEM_MESSAGE BLOCKLIST_NO_BUDDY = new SM_SYSTEM_MESSAGE(1300891);
	/**
	 * Character already in block list
	 */
	public static SM_SYSTEM_MESSAGE BLOCKLIST_ALREADY_BLOCKED = new SM_SYSTEM_MESSAGE(1300894);

	/**
	 * The character is not blocked.
	 */
	public static SM_SYSTEM_MESSAGE BLOCKLIST_NOT_BLOCKED = new SM_SYSTEM_MESSAGE(1300897);
	
	/**
	 * %0 has blocked you.
	 */
	public static SM_SYSTEM_MESSAGE YOU_ARE_BLOCKED_BY(String blocker)
	{
		return new SM_SYSTEM_MESSAGE(1300628, blocker);
	}
	
	/**
	 * Your accumulated play time is %0 hour(s) %1 minute(s). Your accumulated rest time is %2 hour(s) %3 minute(s).
	 * 
	 * @param onlineHours       accumulated online hours
	 * @param onlineMinutes     accumulated online minutes
	 * @param restHours         accumulated rest hours
	 * @param restMinutes       accumulated rest minutes
	 * @return Message instance.
	 */
	public static SM_SYSTEM_MESSAGE ACCUMULATED_TIME(int onlineHours, int onlineMinutes, int restHours, int restMinutes)
	{
		return new SM_SYSTEM_MESSAGE(1390141, onlineHours, onlineMinutes, restHours, restMinutes);
	}

	private final int		code;
	private final Object[]	params;

	/**
	 * Constructs new <tt>SM_SYSTEM_MESSAGE </tt> packet
	 * 
	 * @param code
	 *            operation code, take it from SM_SYSTEM_MESSAGE public static values
	 * @param params
	 */
	private SM_SYSTEM_MESSAGE(int code, Object... params)
	{
		this.code = code;
		this.params = params;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		writeH(buf, 0x13); // unk
		writeD(buf, 0x00); // unk
		writeD(buf, code); // msg id
		writeC(buf, params.length); // count

		for(Object param : params)
		{
			writeS(buf, String.valueOf(param));
		}
	}
}
