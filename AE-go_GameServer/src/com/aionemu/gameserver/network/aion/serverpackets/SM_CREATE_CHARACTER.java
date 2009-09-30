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

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerItemsDAO;
import com.aionemu.gameserver.model.account.PlayerAccountData;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerItems;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.PlayerInfo;
import com.aionemu.gameserver.network.aion.Version;

/**
 * This packet is response for CM_CREATE_CHARACTER
 * 
 * @author Nemesiss, AEJTester
 * 
 */
public class SM_CREATE_CHARACTER extends PlayerInfo
{
	/** If response is ok */
	public static final int	RESPONSE_OK				= 0x00;
	
	
	public static final int	FAILED_TO_CREATE_THE_CHARACTER = 1;
	/** Failed to create the character due to world db error */
	public static final int RESPONSE_DB_ERROR = 2;
	/** The number of characters exceeds the maximum allowed for the server */
	public static final int RESPONSE_SERVER_LIMIT_EXCEEDED = 4;
	/** Invalid character name */
	public static final int	RESPONSE_INVALID_NAME	= 5;
	/** The name includes forbidden words */
	public static final int RESPONSE_FORBIDDEN_CHAR_NAME= 9;
	/** A character with that name already exists */
	public static final int	RESPONSE_NAME_ALREADY_USED			= 10;
	/** The name is already reserved */
	public static final int RESPONSE_NAME_RESERVED = 11;
	/** You cannot create characters of other races in the same server */
	public static final int RESPONSE_OTHER_RACE = 12;

	

	/**
	 * response code
	 */
	private final int		responseCode;

	/**
	 * Newly created player.
	 */
	private final PlayerAccountData	player;

	/**
	 * Constructs new <tt>SM_CREATE_CHARACTER </tt> packet
	 * 
	 * @param accPlData
	 *            playerAccountData of player that was created
	 * @param responseCode
	 *            response code (invalid nickname, nickname is already taken, ok)
	 */

	public SM_CREATE_CHARACTER(PlayerAccountData accPlData, int responseCode)
	{
		this.player = accPlData;
		this.responseCode = responseCode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		writeD(buf, responseCode);

		if(responseCode == RESPONSE_OK)
		{
			PlayerItems playerItems = new PlayerItems();
			switch (player.getPlayerCommonData().getPlayerClass().getClassId())
			{
				case 0: //WARRIOR
					playerItems.setRshard(100000094);// item id - Mace for Practice
					playerItems.setArmor(110500003);// item id - Leather Armor for Practice
					playerItems.setPants(113500001);// item id - Leather Leg Armor for Practice
				break;
				case 3: //SCOUT		
					playerItems.setRshard(100200112);// item id - Mace for Practice
					playerItems.setArmor(110300015);// item id - Leather Armor for Practice
					playerItems.setPants(113300005);// item id - Leather Leg Armor for Practice
				break;
				case 6: //MAGE
					playerItems.setRshard(100600034);// item id - Mace for Practice
					playerItems.setArmor(110100009);// item id - Leather Armor for Practice
					playerItems.setPants(113100005);// item id - Leather Leg Armor for Practice	
				break;
				case 9: //PRIEST			
					playerItems.setRshard(100100011);// item id - Mace for Practice					
					playerItems.setArmor(110300292);// item id - Leather Armor for Practice
					playerItems.setPants(113300278);// item id - Leather Leg Armor for Practice		
				break;
			}
			DAOManager.getDAO(PlayerItemsDAO.class).addStartingItems(player.getPlayerCommonData(),playerItems);
			playerItems=DAOManager.getDAO(PlayerItemsDAO.class).loadItems(player.getPlayerCommonData().getPlayerObjId());
			writePlayerInfo(buf, player, playerItems); // if everything is fine, all the character's data should be sent
		}
		else
		{
			writeB(buf, new byte[448]); // if something is wrong, only return code should be sent in the packet
		}
	}
}
