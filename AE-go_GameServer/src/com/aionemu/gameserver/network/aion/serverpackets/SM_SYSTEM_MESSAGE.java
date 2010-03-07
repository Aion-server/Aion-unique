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

import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.network.aion.SystemMessageId;

/**
 * System message packet.
 * 
 * @author -Nemesiss-
 * @author EvilSpirit
 * @author Luno :D
 * @author Avol!
 * @author Simple :) 
 */
public class SM_SYSTEM_MESSAGE extends AionServerPacket
{

	/**
	 * Coordinates of current location: %WORLDNAME0 Region, X=%1 Y=%2 Z=%3
	 * 
	 * @param worldId
	 *            id of the world
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @param z
	 *            z coordinate
	 * @return Message instance.
	 */
	public static SM_SYSTEM_MESSAGE CURRENT_LOCATION(int worldId, float x, float y, float z)
	{
		return new SM_SYSTEM_MESSAGE(230038, worldId, x, y, z);
	}

	/**
	 * Busy in game
	 */
	public static final SM_SYSTEM_MESSAGE	BUDDYLIST_BUSY	= new SM_SYSTEM_MESSAGE(900847);

	/**
	 * %0 is not playing the game
	 * 
	 * @param playerName
	 *            Player name.
	 * @return Message instance.
	 */
	public static SM_SYSTEM_MESSAGE PLAYER_IS_OFFLINE(String playerName)
	{
		return new SM_SYSTEM_MESSAGE(1300627, playerName);
	}

	/**
	 * You used item
	 */
	public static SM_SYSTEM_MESSAGE USE_ITEM(DescriptionId itemDescId)
	{
		return new SM_SYSTEM_MESSAGE(1300423, itemDescId);
	}

	public static SM_SYSTEM_MESSAGE REQUEST_TRADE(String playerName)
	{
		return new SM_SYSTEM_MESSAGE(1300353, playerName);
	}

	/**
	 * The remaining playing time is %0.
	 * 
	 * @param playTime
	 *            play time
	 * @return Message instance.
	 */
	public static SM_SYSTEM_MESSAGE REMAINING_PLAYING_TIME(int playTime)
	{
		return new SM_SYSTEM_MESSAGE(1300719, playTime);
	}

	/**
	 * You are dead
	 */
	public static SM_SYSTEM_MESSAGE	DIE		= new SM_SYSTEM_MESSAGE(1340000);

	/**
	 * 
	 */
	public static SM_SYSTEM_MESSAGE	REVIVE	= new SM_SYSTEM_MESSAGE(1300738);

	/**
	 * 
	 */
	public static SM_SYSTEM_MESSAGE EXP(String _exp)
	{
		return new SM_SYSTEM_MESSAGE(1370002, _exp);
	}

	public static SM_SYSTEM_MESSAGE Gather_Success(String _value)
	{
		return new SM_SYSTEM_MESSAGE(1330058, _value);
	}

	/**
	 * Your Requested player to trade
	 */
	public static final SM_SYSTEM_MESSAGE	REQUEST_TRADE			= new SM_SYSTEM_MESSAGE(1300353);

	/**
	 * Your Friends List is full
	 */
	public static final SM_SYSTEM_MESSAGE	BUDDYLIST_LIST_FULL		= new SM_SYSTEM_MESSAGE(1300887);

	/**
	 * The character is not on your Friends List.
	 */
	public static final SM_SYSTEM_MESSAGE	BUDDYLIST_NOT_IN_LIST	= new SM_SYSTEM_MESSAGE(1300889);

	/**
	 * The server is due to shut down in %0 seconds. Please quit the game.
	 */
	public static SM_SYSTEM_MESSAGE SERVER_SHUTDOWN(int seconds)
	{
		return new SM_SYSTEM_MESSAGE(1300642, Integer.toString(seconds));
	}

	/**
	 * You cannot block a character who is currently on your Friends List.
	 */
	public static SM_SYSTEM_MESSAGE	BLOCKLIST_NO_BUDDY			= new SM_SYSTEM_MESSAGE(1300891);

	/**
	 * Character already in block list
	 */
	public static SM_SYSTEM_MESSAGE	BLOCKLIST_ALREADY_BLOCKED	= new SM_SYSTEM_MESSAGE(1300894);

	/**
	 * The character is not blocked.
	 */
	public static SM_SYSTEM_MESSAGE	BLOCKLIST_NOT_BLOCKED		= new SM_SYSTEM_MESSAGE(1300897);

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
	 * @param onlineHours
	 *            accumulated online hours
	 * @param onlineMinutes
	 *            accumulated online minutes
	 * @param restHours
	 *            accumulated rest hours
	 * @param restMinutes
	 *            accumulated rest minutes
	 * @return Message instance.
	 */
	public static SM_SYSTEM_MESSAGE ACCUMULATED_TIME(int onlineHours, int onlineMinutes, int restHours, int restMinutes)
	{
		return new SM_SYSTEM_MESSAGE(1390141, onlineHours, onlineMinutes, restHours, restMinutes);
	}

	/**
	 * %0 has challenged you to a duel.
	 */
	public static SM_SYSTEM_MESSAGE DUEL_ASKED_BY(String player)
	{
		return new SM_SYSTEM_MESSAGE(1301065, player);
	}

	/**
	 * You challenged %0 to a duel.
	 */
	public static SM_SYSTEM_MESSAGE DUEL_ASKED_TO(String player)
	{
		return new SM_SYSTEM_MESSAGE(1300094, player);
	}

	/**
	 * %0 rejects your duel request
	 */
	public static SM_SYSTEM_MESSAGE DUEL_REJECTED_BY(String player)
	{
		return new SM_SYSTEM_MESSAGE(1300097, player);
	}

	/**
	 * You won the duel against %0.
	 */
	public static SM_SYSTEM_MESSAGE DUEL_YOU_WON_AGAINST(String player)
	{
		return new SM_SYSTEM_MESSAGE(1300098, player);
	}

	/**
	 * You lost the duel against %0.
	 */
	public static SM_SYSTEM_MESSAGE DUEL_YOU_LOST_AGAINST(String player)
	{
		return new SM_SYSTEM_MESSAGE(1300099, player);
	}

	public static SM_SYSTEM_MESSAGE	DUEL_START	= new SM_SYSTEM_MESSAGE(1300770);

	public static SM_SYSTEM_MESSAGE	DUEL_END	= new SM_SYSTEM_MESSAGE(1300771);

	/**
	 * Starting the duel with %0.
	 */
	public static SM_SYSTEM_MESSAGE DUEL_STARTING_WITH(String player)
	{
		return new SM_SYSTEM_MESSAGE(1300777, player);
	}

	/**
	 * You declined %0's challenge for a duel.
	 */
	public static SM_SYSTEM_MESSAGE DUEL_REJECT_DUEL_OF(String player)
	{
		return new SM_SYSTEM_MESSAGE(1301064, player);
	}

	/**
	 * %0 has withdrawn the challenge for a duel.
	 */
	public static SM_SYSTEM_MESSAGE DUEL_CANCEL_DUEL_BY(String player)
	{
		return new SM_SYSTEM_MESSAGE(1300134, player);
	}

	/**
	 * You have withdrawn the challenge to %0 for a duel.
	 */
	public static SM_SYSTEM_MESSAGE DUEL_CANCEL_DUEL_WITH(String player)
	{
		return new SM_SYSTEM_MESSAGE(1300135, player);
	}

	/**
	 * You cannot duel with %0.
	 */
	public static SM_SYSTEM_MESSAGE DUEL_PARTNER_INVALID(String partner)
	{
		return new SM_SYSTEM_MESSAGE(1300091, partner);
	}

	/**
	 * Group System Messages
	 */
	public static SM_SYSTEM_MESSAGE REQUEST_GROUP_INVITE(String player)
	{
		return new SM_SYSTEM_MESSAGE(1300173, player);
	}

	public static SM_SYSTEM_MESSAGE REJECT_GROUP_INVITE(String player)
	{
		return new SM_SYSTEM_MESSAGE(1300161, player);
	}

	public static SM_SYSTEM_MESSAGE PLAYER_IN_ANOTHER_GROUP(String player)
	{
		return new SM_SYSTEM_MESSAGE(1300169, player);
	}

	public static SM_SYSTEM_MESSAGE INVITED_PLAYER_OFFLINE()
	{
		return new SM_SYSTEM_MESSAGE(1300159);
	}

	public static SM_SYSTEM_MESSAGE MEMBER_LEFT_GROUP(String player)
	{
		return new SM_SYSTEM_MESSAGE(1300168, player);
	}

	public static SM_SYSTEM_MESSAGE DISBAND_GROUP()
	{
		return new SM_SYSTEM_MESSAGE(1300167);
	}

	public static SM_SYSTEM_MESSAGE YOU_LEFT_GROUP()
	{
		return new SM_SYSTEM_MESSAGE(1300043);
	}

	public static SM_SYSTEM_MESSAGE SELECTED_TARGET_DEAD()
	{
		return new SM_SYSTEM_MESSAGE(1300044);
	}

	public static SM_SYSTEM_MESSAGE DURING_FLYING_PATH_NOT_LEFT_GROUP()
	{
		return new SM_SYSTEM_MESSAGE(1300047);
	}

	public static SM_SYSTEM_MESSAGE FULL_GROUP()
	{
		return new SM_SYSTEM_MESSAGE(1300152);
	}

	public static SM_SYSTEM_MESSAGE CHANGE_GROUP_LEADER()
	{
		return new SM_SYSTEM_MESSAGE(1300155);
	}

	public static SM_SYSTEM_MESSAGE ONLY_GROUP_LEADER_CAN_INVITE()
	{
		return new SM_SYSTEM_MESSAGE(1300160);
	}

	public static SM_SYSTEM_MESSAGE CANNOT_INVITE_YOURSELF()
	{
		return new SM_SYSTEM_MESSAGE(1300162);
	}

	public static SM_SYSTEM_MESSAGE CANNOT_INVITE_BECAUSE_YOU_DEAD()
	{
		return new SM_SYSTEM_MESSAGE(1300163);
	}

	public static SM_SYSTEM_MESSAGE INVITED_ANOTHER_GROUP_MEMBER(String player)
	{
		return new SM_SYSTEM_MESSAGE(1300169);
	}

	public static SM_SYSTEM_MESSAGE INVITED_YOUR_GROUP_MEMBER(String player)
	{
		return new SM_SYSTEM_MESSAGE(1300170);
	}

	public static SM_SYSTEM_MESSAGE CANT_INVITE_OTHER_RACE()
	{
		return new SM_SYSTEM_MESSAGE(1300188);
	}

	public static SM_SYSTEM_MESSAGE LEVEL_NOT_ENOUGH_FOR_SEARCH(String level)
	{
		return new SM_SYSTEM_MESSAGE(1400341, level);
	}

	public static SM_SYSTEM_MESSAGE LEVEL_NOT_ENOUGH_FOR_WHISPER(String level)
	{
		return new SM_SYSTEM_MESSAGE(1310004, level);
	}

	public static SM_SYSTEM_MESSAGE SOUL_HEALED()
	{
		return new SM_SYSTEM_MESSAGE(1300674);
	}

	public static SM_SYSTEM_MESSAGE DONT_HAVE_RECOVERED_EXP()
	{
		return new SM_SYSTEM_MESSAGE(1300682);
	}

	/**
	 * Legion messages.
	 */
	/** NPC TOO FAR messages **/
	public static SM_SYSTEM_MESSAGE LEGION_DISPERSE_TOO_FAR_FROM_NPC()
	{
		// You are too far from the NPC to disband the Legion.
		return new SM_SYSTEM_MESSAGE(1300305);
	}

	public static SM_SYSTEM_MESSAGE LEGION_CREATE_TOO_FAR_FROM_NPC()
	{
		// You are too far from the NPC to create a Legion.
		return new SM_SYSTEM_MESSAGE(1300229);
	}

	/** Incorrect target / user offline **/
	public static SM_SYSTEM_MESSAGE LEGION_INCORRECT_TARGET()
	{
		return new SM_SYSTEM_MESSAGE(1300627);
	}

	/** Announcement related **/
	public static SM_SYSTEM_MESSAGE LEGION_DISPLAY_ANNOUNCEMENT(String announcement, long unixTime, int type)
	{
		return new SM_SYSTEM_MESSAGE(1400019, announcement, unixTime, type);
	}

	/** Done messages **/
	public static SM_SYSTEM_MESSAGE LEGION_WRITE_NOTICE_DONE()
	{
		// The Legion Announcement has been modified.
		return new SM_SYSTEM_MESSAGE(1300277);
	}

	/** Player online/kicked/left/joined **/
	public static SM_SYSTEM_MESSAGE LEGION_MEMBER_ONLINE(String charName)
	{
		return new SM_SYSTEM_MESSAGE(1400133, charName);
	}

	public static SM_SYSTEM_MESSAGE NEW_MEMBER_JOINED(String charName)
	{
		// %0 has joined your Legion.
		return new SM_SYSTEM_MESSAGE(1300260, charName);
	}

	public static SM_SYSTEM_MESSAGE LEGION_MEMBER_LEFT(String charName)
	{
		// %0 has left the Legion.
		return new SM_SYSTEM_MESSAGE(900699, charName);
	}

	public static SM_SYSTEM_MESSAGE LEGION_NEW_MASTER()
	{
		// %0 was appointed as the new Legion Brigade General.
		return new SM_SYSTEM_MESSAGE(900701);
	}

	/** Requests and their response **/
	public static SM_SYSTEM_MESSAGE SEND_INVITE_REQUEST(String charName)
	{
		// You have sent a Legion invitation to %0.
		return new SM_SYSTEM_MESSAGE(1300258, charName);
	}

	public static SM_SYSTEM_MESSAGE REJECTED_INVITE_REQUEST(String charName)
	{
		// %0 has declined your Legion invitation.
		return new SM_SYSTEM_MESSAGE(1300259, charName);
	}

	/** Name related messages **/
	public static SM_SYSTEM_MESSAGE LEGION_CREATE_INVALID_NAME()
	{
		// That name is invalid. Please try another..
		return new SM_SYSTEM_MESSAGE(1300228);
	}

	public static SM_SYSTEM_MESSAGE LEGION_CREATE_NAME_EXISTS()
	{
		// That name is invalid. Please try another.
		return new SM_SYSTEM_MESSAGE(1300233);
	}

	public static SM_SYSTEM_MESSAGE LEGION_WRITE_INTRO_DONE()
	{
		// Your Character Information has been modified.
		return new SM_SYSTEM_MESSAGE(1300282);
	}

	/** Legion update related **/
	public static SM_SYSTEM_MESSAGE LEGION_LEVEL_UP(int legionLevel)
	{
		// The Legion was leveled up to %0.
		return new SM_SYSTEM_MESSAGE(900700, legionLevel);
	}

	public static SM_SYSTEM_MESSAGE LEGION_CHANGE_LEVEL_CANT_LEVEL_UP()
	{
		// The Legion is already at the highest level.
		return new SM_SYSTEM_MESSAGE(1300316);
	}

	public static SM_SYSTEM_MESSAGE LEGION_CHANGED_EMBLEM()
	{
		return new SM_SYSTEM_MESSAGE(1390137);
	}

	/** Reponse to checks - CREATION **/
	public static SM_SYSTEM_MESSAGE LEGION_CREATE_ALREADY_MEMBER()
	{
		// You cannot create a Legion as you are already a member of another Legion.
		return new SM_SYSTEM_MESSAGE(1300232);
	}

	public static SM_SYSTEM_MESSAGE LEGION_CREATE_NOT_ENOUGH_KINAH()
	{
		// You do not have enough Kinah to create a Legion.
		return new SM_SYSTEM_MESSAGE(1300231);
	}

	public static SM_SYSTEM_MESSAGE LEGION_CREATE_LAST_DAY_CHECK()
	{
		// You cannot create a new Legion as the grace period between creating Legions has not expired.
		return new SM_SYSTEM_MESSAGE(1300234);
	}

	public static SM_SYSTEM_MESSAGE LEGION_CREATED(String legionName)
	{
		// The %0 Legion has been created.
		return new SM_SYSTEM_MESSAGE(1300235, legionName);
	}

	/** Reponse to checks - LEVEL UP **/
	public static SM_SYSTEM_MESSAGE LEGION_CHANGE_LEVEL_NOT_ENOUGH_POINT()
	{
		// You do not have enough Contribution Points.
		return new SM_SYSTEM_MESSAGE(1300317);
	}

	public static SM_SYSTEM_MESSAGE LEGION_CHANGE_LEVEL_NOT_ENOUGH_MEMBER()
	{
		// Your Legion does not have enough members.
		return new SM_SYSTEM_MESSAGE(1300318);
	}

	public static SM_SYSTEM_MESSAGE LEGION_CHANGE_LEVEL_NOT_ENOUGH_KINAH()
	{
		// You do not have enough Kinah.
		return new SM_SYSTEM_MESSAGE(1300319);
	}

	/** Reponse to checks - INVITE **/
	public static SM_SYSTEM_MESSAGE LEGION_TARGET_BUSY()
	{
		// The target is busy and cannot be invited at the moment.
		return new SM_SYSTEM_MESSAGE(1300325);
	}

	public static SM_SYSTEM_MESSAGE LEGION_CANT_INVITE_WHILE_DEAD()
	{
		// You cannot issue a Legion invitation while you are dead.
		return new SM_SYSTEM_MESSAGE(1300250);
	}

	public static SM_SYSTEM_MESSAGE LEGION_CAN_NOT_INVITE_SELF()
	{
		// You cannot invite yourself to a Legion.
		return new SM_SYSTEM_MESSAGE(1300254);
	}

	public static SM_SYSTEM_MESSAGE LEGION_HE_IS_MY_GUILD_MEMBER(String charName)
	{
		// %0 is already a member of your Legion.
		return new SM_SYSTEM_MESSAGE(1300255, charName);
	}

	public static SM_SYSTEM_MESSAGE LEGION_HE_IS_OTHER_GUILD_MEMBER(String charName)
	{
		// %0 is a member of another Legion.
		return new SM_SYSTEM_MESSAGE(1300256, charName);
	}

	public static SM_SYSTEM_MESSAGE LEGION_CAN_NOT_ADD_MEMBER_ANY_MORE()
	{
		// There is no room in the Legion for more members.
		return new SM_SYSTEM_MESSAGE(1300257);
	}

	public static SM_SYSTEM_MESSAGE LEGION_NO_USER_TO_INVITE()
	{
		// There is no user to invite to your Legion.
		return new SM_SYSTEM_MESSAGE(1300253);
	}

	/** Reponse to checks - LEAVE **/
	public static SM_SYSTEM_MESSAGE LEGION_CANT_LEAVE_BEFORE_CHANGE_MASTER()
	{
		// You cannot leave your Legion unless you transfer Brigade General authority to someone else.
		return new SM_SYSTEM_MESSAGE(1300238);
	}

	/** Reponse to checks - KICK **/
	public static SM_SYSTEM_MESSAGE LEGION_CANT_KICK_YOURSELF()
	{
		// You cannot kick yourself out from a Legion.
		return new SM_SYSTEM_MESSAGE(1300243);
	}

	public static SM_SYSTEM_MESSAGE LEGION_KICKED_BY(String charName)
	{
		// You have been kicked out from the %0 Legion.
		return new SM_SYSTEM_MESSAGE(1300246, charName);
	}

	public static SM_SYSTEM_MESSAGE LEGION_CANT_KICK_BRIGADE_GENERAL()
	{
		// You cannot kick out the Legion Brigade General.
		return new SM_SYSTEM_MESSAGE(1300249);
	}

	/** Reponse to checks - CHANGE RANK **/
	public static SM_SYSTEM_MESSAGE LEGION_CHANGE_MEMBER_RANK_DONT_HAVE_RIGHT()
	{
		// You cannot change the ranks of Legion members because you are not the Legion Brigade General.
		return new SM_SYSTEM_MESSAGE(1300262);
	}

	public static SM_SYSTEM_MESSAGE LEGION_CHANGE_MEMBER_RANK_ERROR_SELF()
	{
		// The Legion Brigade General cannot change its own rank.
		return new SM_SYSTEM_MESSAGE(1300263);
	}

	public static SM_SYSTEM_MESSAGE LEGION_CHANGE_MEMBER_RANK_NO_USER()
	{
		// There is no one to change rank.
		return new SM_SYSTEM_MESSAGE(1300264);
	}

	/** Reponse to checks - APPOINT BRIGADE GENERAL **/
	public static SM_SYSTEM_MESSAGE LEGION_CHANGE_MASTER_ERROR_SELF()
	{
		// You are already the Legion Brigade General
		return new SM_SYSTEM_MESSAGE(1300271);
	}

	public static SM_SYSTEM_MESSAGE LEGION_CHANGE_MASTER_NO_SUCH_USER()
	{
		// You cannot transfer your Brigade General authority to an offline user.
		return new SM_SYSTEM_MESSAGE(1300270);
	}

	public static SM_SYSTEM_MESSAGE LEGION_CHANGE_MASTER_SENT_OFFER_MSG_TO_HIM(String charName)
	{
		// You nominated %0 as the next Legion Brigade General.
		return new SM_SYSTEM_MESSAGE(1300330, charName);
	}

	public static SM_SYSTEM_MESSAGE LEGION_CHANGE_MASTER_SENT_CANT_OFFER_WHEN_HE_IS_QUESTION_ASKED()
	{
		// You cannot request the selected player to become the Legion Brigade General.
		return new SM_SYSTEM_MESSAGE(1300331);
	}

	public static SM_SYSTEM_MESSAGE LEGION_CHANGE_MASTER_HE_DECLINE_YOUR_OFFER(String charName)
	{
		// %0 has declined to become the Legion Brigade General.
		return new SM_SYSTEM_MESSAGE(1300332, charName);
	}

	/** Reponse to checks - DISBAND **/
	public static SM_SYSTEM_MESSAGE LEGION_DISPERSE_ONLY_MASTER_CAN_DISPERSE()
	{
		// You have no authority to disband the Legion.
		return new SM_SYSTEM_MESSAGE(1300300);
	}

	public static SM_SYSTEM_MESSAGE LEGION_DISPERSE_REQUESTED(int unixTime)
	{
		// The Brigade General has requested to disband the Legion. The expected time of disbanding is %DATETIME0.
		return new SM_SYSTEM_MESSAGE(1300303, unixTime);
	}

	public static SM_SYSTEM_MESSAGE LEGION_DISPERSE_ALREADY_REQUESTED()
	{
		// You have already requested to disband the Legion.
		return new SM_SYSTEM_MESSAGE(1300304);
	}

	public static SM_SYSTEM_MESSAGE LEGION_WAREHOUSE_CANT_USE_WHILE_DISPERSE()
	{
		// You cannot use the Legion warehouse during the disbandment waiting period.
		return new SM_SYSTEM_MESSAGE(1300333);
	}

	public static SM_SYSTEM_MESSAGE LEGION_DISPERSE_CANT_DISPERSE_GUILD_STORE_ITEM_IN_WAREHOUSE()
	{
		// You cannot disband your Legion while you have items left in the Legion warehouse.
		return new SM_SYSTEM_MESSAGE(1390212);
	}

	/**
	 * Legion Message correct order from bottom
	 */
	public static SM_SYSTEM_MESSAGE STR_MSG_NOTIFY_LOGIN_GUILD(String charName)
	{
		return new SM_SYSTEM_MESSAGE(1400133, charName);
	}

	/**
	 * You cannot fly in this area.
	 */
	public static SM_SYSTEM_MESSAGE	STR_FLYING_FORBIDDEN_HERE			= new SM_SYSTEM_MESSAGE(1300960);

	/**
	 * You cannot use teleport services when you flying
	 */
	public static SM_SYSTEM_MESSAGE	STR_CANNOT_USE_AIRPORT_WHEN_FLYING	= new SM_SYSTEM_MESSAGE(1300696);

	/**
	 * Binding Point Messages
	 */
	public static SM_SYSTEM_MESSAGE STR_CANNOT_REGISTER_RESURRECT_POINT_NOT_ENOUGH_FEE()
	{
		return new SM_SYSTEM_MESSAGE(1300686);
	}

	public static SM_SYSTEM_MESSAGE STR_ALREADY_REGISTER_THIS_RESURRECT_POINT()
	{
		return new SM_SYSTEM_MESSAGE(1300688);
	}

	public static SM_SYSTEM_MESSAGE STR_DEATH_REGISTER_RESURRECT_POINT()
	{
		return new SM_SYSTEM_MESSAGE(1300670);
	}

	public static SM_SYSTEM_MESSAGE STR_ATTACK_TOO_FAR_FROM_TARGET()
	{
		return new SM_SYSTEM_MESSAGE(1300032);
	}

	public static SM_SYSTEM_MESSAGE NO_POWER_SHARD_EQUIPPED()
	{
		return new SM_SYSTEM_MESSAGE(1300490);
	}

	public static SM_SYSTEM_MESSAGE ACTIVATE_THE_POWER_SHARD()
	{
		return new SM_SYSTEM_MESSAGE(1300491);
	}

	public static SM_SYSTEM_MESSAGE DEACTIVATE_THE_POWER_SHARD()
	{
		return new SM_SYSTEM_MESSAGE(1300492);
	}

	public static SM_SYSTEM_MESSAGE NO_POWER_SHARD_LEFT()
	{
		return new SM_SYSTEM_MESSAGE(1400075);
	}

	public static SM_SYSTEM_MESSAGE ADDITIONAL_PLACES_IN_WAREHOUSE()
	{
		return new SM_SYSTEM_MESSAGE(1300433);
	}

	public static SM_SYSTEM_MESSAGE EARNED_ABYSS_POINT(String count)
	{
		return new SM_SYSTEM_MESSAGE(1320000, count);
	}

	public static SM_SYSTEM_MESSAGE STR_SKILL_CANCELED()
	{
		return new SM_SYSTEM_MESSAGE(1300023);
	}

	public static SM_SYSTEM_MESSAGE INVALID_TARGET()
	{
		return new SM_SYSTEM_MESSAGE(1300013);
	}

	public static SM_SYSTEM_MESSAGE SEARCH_NOT_EXIST()
	{
		return new SM_SYSTEM_MESSAGE(1310019);
	}

	public static SM_SYSTEM_MESSAGE QUEST_ACQUIRE_ERROR_INVENTORY_ITEM(int count)
	{
		return new SM_SYSTEM_MESSAGE(1300594, count);
	}

	/**
	 * Trading (Private Store, etc.)
	 */
	public static SM_SYSTEM_MESSAGE NOT_ENOUGH_KINAH(int kinah)
	{
		return new SM_SYSTEM_MESSAGE(901285, kinah);
	}

	public static final SM_SYSTEM_MESSAGE	MSG_FULL_INVENTORY	= new SM_SYSTEM_MESSAGE(1300762);

	public static final AionServerPacket	CUBEEXPAND_NOT_ENOUGH_KINAH	= new SM_SYSTEM_MESSAGE(1300831);

	/**
	 * Manastone Messages
	 */
	public static SM_SYSTEM_MESSAGE STR_GIVE_ITEM_OPTION_SUCCEED(DescriptionId itemDescId)
	{
		return new SM_SYSTEM_MESSAGE(1300462, itemDescId);
	}

	/**
	 * cannot equip items if require level higher than character level
	 */
	public static SM_SYSTEM_MESSAGE STR_CANNOT_USE_ITEM_TOO_LOW_LEVEL_MUST_BE_THIS_LEVEL(int itemLevel,
		DescriptionId itemDescId)
	{
		return new SM_SYSTEM_MESSAGE(1300372, itemLevel, itemDescId);
	}

	/**
	 * Delete character messages
	 */
	public static SM_SYSTEM_MESSAGE STR_DELETE_CHARACTER_IN_LEGION()
	{
		return new SM_SYSTEM_MESSAGE(1300306);
	}

	/**
	 * Loot
	 */
	public static SM_SYSTEM_MESSAGE STR_LOOT_NO_RIGHT()
	{
		// You are not authorized to examine the corpse.
		return new SM_SYSTEM_MESSAGE(901338);
	}
	
	public static SM_SYSTEM_MESSAGE STR_LOOT_FAIL_ONLOOTING()
	{
		// Someone is already looting that.
		return new SM_SYSTEM_MESSAGE(1300829);
	}

	public static SM_SYSTEM_MESSAGE CRAFT_RECIPE_LEARN(DescriptionId nameId)
	{
		return new SM_SYSTEM_MESSAGE(1330061, nameId);
	}
	
	public static SM_SYSTEM_MESSAGE MSG_DONT_GET_PRODUCTION_EXP(DescriptionId nameId)
	{
		return new SM_SYSTEM_MESSAGE(1390221, nameId);
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
	public SM_SYSTEM_MESSAGE(int code, Object... params)
	{
		this.code = code;
		this.params = params;
	}

	public SM_SYSTEM_MESSAGE(SystemMessageId sm, Object... params)
	{
		this.code = sm.getId();
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
			if(param instanceof DescriptionId)
			{
				writeH(buf, 0x24);
				writeD(buf, ((DescriptionId) param).getValue());
				writeH(buf, 0x00); // unk
			}
			else
				writeS(buf, String.valueOf(param));
		}
		writeC(buf, 0x00);
	}
}
