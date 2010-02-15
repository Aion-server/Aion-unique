/*
 * This file is part of aion-unique <aion-unique.org>.
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
package com.aionemu.gameserver.services;

import java.util.Map;
import java.util.Map.Entry;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.LegionConfig;
import com.aionemu.gameserver.dao.LegionDAO;
import com.aionemu.gameserver.dao.LegionMemberDAO;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.model.legion.Legion;
import com.aionemu.gameserver.model.legion.LegionMember;
import com.aionemu.gameserver.model.legion.OfflineLegionMember;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CHANGE_NICKNAME;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CHANGE_SELF_INTRODUCTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EDIT_LEGION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEAVE_LEGION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGIONMEMBER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_CREATED;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_MEMBER;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UPDATE_LEGION_TITLE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.collections.cachemap.CacheMap;
import com.aionemu.gameserver.utils.collections.cachemap.CacheMapFactory;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.aionemu.gameserver.utils.idfactory.IDFactoryAionObject;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * 
 * This class is designed to do all the work related with loading/storing legions and their members.<br>
 * 
 * @author Simple
 */
@SuppressWarnings("unused")
public class LegionService
{
	private CacheMap<Integer, Legion>		legionCache					= CacheMapFactory.createSoftCacheMap("Legion",
																			"legion");
	private CacheMap<Integer, LegionMember>	legionMembersCache			= CacheMapFactory.createSoftCacheMap(
																			"PlayerObjId", "legionMember");

	private IDFactory						aionObjectsIDFactory;
	private World							world;

	/**
	 * Legion Permission variables
	 */
	private static final int				MAX_LEGION_LEVEL			= 5;
	private static final int				BRIGADE_GENERAL_RANK		= 0x00;
	private static final int				CENTURION_RANK				= 0x01;
	private static final int				LEGIONAIRY_RANK				= 0x02;
	private static final int				PERMISSION1_MIN				= 0x60;
	private static final int				PERMISSION2_MIN				= 0x00;
	private static final int				LEGIONAR_PERMISSION2_MAX	= 0x08;
	private static final int				CENTURION_PERMISSION1_MAX	= 0x7C;
	private static final int				CENTURION_PERMISSION2_MAX	= 0x0E;

	@Inject
	public LegionService(@IDFactoryAionObject IDFactory aionObjectsIDFactory, World world)
	{
		this.aionObjectsIDFactory = aionObjectsIDFactory;
		this.world = world;
	}

	/**
	 * Checks if name is already taken or not
	 * 
	 * @param name
	 *            character name
	 * @return true if is free, false in other case
	 */
	public boolean isFreeName(String name)
	{
		return !DAOManager.getDAO(LegionDAO.class).isNameUsed(name);
	}

	/**
	 * Checks if a name is valid. It should contain only english letters
	 * 
	 * @param name
	 *            legion name
	 * @return true if name is valid, false overwise
	 */
	public boolean isValidName(String name)
	{
		return LegionConfig.LEGION_NAME_PATTERN.matcher(name).matches();
	}

	/**
	 * Checks if a self intro is valid. It should contain only english letters
	 * 
	 * @param name
	 *            character name
	 * @return true if name is valid, false overwise
	 */
	public boolean isValidSelfIntro(String name)
	{
		return LegionConfig.SELF_INTRO_PATTERN.matcher(name).matches();
	}

	/**
	 * Checks if a nickname is valid. It should contain only english letters
	 * 
	 * @param name
	 *            character name
	 * @return true if name is valid, false overwise
	 */
	public boolean isValidNickname(String name)
	{
		return LegionConfig.NICKNAME_PATTERN.matcher(name).matches();
	}

	/**
	 * Checks if a announcement is valid. It should contain only english letters
	 * 
	 * @param name
	 *            announcement
	 * @return true if name is valid, false overwise
	 */
	public boolean isValidAnnouncement(String name)
	{
		return LegionConfig.ANNOUNCEMENT_PATTERN.matcher(name).matches();
	}

	/**
	 * Stores newly created legion
	 * 
	 * @param legion
	 *            legion to store
	 * @return true if legion was successful saved.
	 */
	public boolean storeNewLegion(Legion legion)
	{
		legionCache.put(legion.getLegionId(), legion);
		return DAOManager.getDAO(LegionDAO.class).saveNewLegion(legion);
	}

	/**
	 * Stores newly created legion member
	 * 
	 * @param playerObjId
	 *            player object Id
	 * @param legionMember
	 *            legion member to store
	 * @return true if legion member was successful saved.
	 */
	public boolean storeNewLegionMember(int playerObjId, LegionMember legionMember)
	{
		legionMembersCache.put(playerObjId, legionMember);
		return DAOManager.getDAO(LegionMemberDAO.class).saveNewLegionMember(playerObjId, legionMember);
	}

	/**
	 * Stores newly created announcement
	 * 
	 * @param playerObjId
	 *            player object Id
	 * @param legionMember
	 *            legion member to store
	 * @return true if announcement was successful saved.
	 */
	public boolean storeNewAnnouncement(int legionId, int unixTime, String message)
	{
		return DAOManager.getDAO(LegionDAO.class).saveNewAnnouncement(legionId, unixTime, message);
	}

	/**
	 * Stores legion data into db
	 * 
	 * @param legion
	 */
	public void storeLegion(Legion legion)
	{
		DAOManager.getDAO(LegionDAO.class).storeLegion(legion);
	}

	/**
	 * Stores legion member data into db
	 * 
	 * @param legionMember
	 */
	public void storeLegionMember(int playerObjId, LegionMember legionMember)
	{
		DAOManager.getDAO(LegionMemberDAO.class).storeLegionMember(playerObjId, legionMember);
	}

	/**
	 * Returns the legion with given legionId (if such legion exists)
	 * 
	 * @param legionId
	 * @return
	 */
	public Legion getLegion(int legionId)
	{
		Legion legion = legionCache.get(legionId);
		if(legion != null)
			return legion;

		legion = DAOManager.getDAO(LegionDAO.class).loadLegion(legionId);

		Map<Integer, LegionMember> legionMembers = DAOManager.getDAO(LegionMemberDAO.class).loadLegionMembers(legionId);
		for(Integer key : legionMembers.keySet())
		{
			legion.addLegionMember(legionMembers.get(key).getPlayerObjId());
		}

		legion.setAnnouncementList(DAOManager.getDAO(LegionDAO.class).loadAnnouncementList(legion));

		legionCache.put(legionId, legion);

		return legion;
	}

	/**
	 * Returns the legion with given legionId (if such legion exists)
	 * 
	 * @param playerObjId
	 * @return
	 */
	public LegionMember getLegionMember(Player player)
	{
		LegionMember legionMember = legionMembersCache.get(player.getObjectId());
		if(legionMember != null)
			return legionMember;

		legionMember = DAOManager.getDAO(LegionMemberDAO.class).loadLegionMember(player, this);

		if(legionMember != null)
			legionMembersCache.put(player.getObjectId(), legionMember);

		return legionMember;
	}

	/**
	 * Returns the offline legion member with given playerId (if such member exists)
	 * 
	 * @param playerObjId
	 * @return
	 */
	public OfflineLegionMember getOfflineLegionMember(int playerObjId)
	{
		// OfflineLegionMember legionMember = legionMembersCache.get(player.getObjectId());
		// if(legionMember != null)
		// return legionMember;

		OfflineLegionMember offlineLegionMember = DAOManager.getDAO(LegionMemberDAO.class).loadOfflineLegionMember(
			playerObjId, this);

		// if(legionMember != null)
		// legionMembersCache.put(player.getObjectId(), legionMember);

		return offlineLegionMember;
	}

	/**
	 * Completely removes legion member from database
	 * 
	 * @param playerObjId
	 *            Player Object Id
	 */
	public void deleteLegionMember(int playerObjId)
	{
		legionMembersCache.remove(playerObjId);
		DAOManager.getDAO(LegionMemberDAO.class).deleteLegionMember(playerObjId);
	}

	/**
	 * Completely removes legion from database
	 * 
	 * @param legionId
	 *            id of legion to delete from db
	 */
	void deleteLegionFromDB(int legionId)
	{
		DAOManager.getDAO(LegionDAO.class).deleteLegion(legionId);
	}

	/**
	 * Gets a player ONLY if he is in the cache
	 * 
	 * @return Player or null if not cached
	 */
	public Legion getCachedLegion(int legionId)
	{
		return legionCache.get(legionId);
	}

	/**
	 * @param legion
	 */
	public void renewAnnouncementList(Legion legion)
	{
		legion.setAnnouncementList(DAOManager.getDAO(LegionDAO.class).loadAnnouncementList(legion));
	}

	/**
	 * This method will handle the creation of a legion
	 * 
	 * @param activePlayer
	 * @param legionName
	 */
	public void createLegion(Player activePlayer, String legionName)
	{

		Legion legion = new Legion(aionObjectsIDFactory.nextId(), legionName);

		/* Some reasons why legions can' be created */
		if(!isValidName(legion.getLegionName()))
		{
			aionObjectsIDFactory.releaseId(legion.getLegionId());
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CREATE_INVALID_NAME());
			return;
		}
		else if(!isFreeName(legion.getLegionName()))
		{
			aionObjectsIDFactory.releaseId(legion.getLegionId());
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CREATE_NAME_EXISTS());
			return;
		}
		// if(activePlayer.getDisbandLegionTime() < Config.LEGION_DISBAND_DIFFERENCE)
		// {
		// sendPacket(SM_SYSTEM_MESSAGE.LEGION_LEGION_CREATE_LAST_DAY_CHECK());
		// }
		else if(activePlayer.isLegionMember())
		{
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CREATE_ALREADY_MEMBER());
		}
		else if(activePlayer.getInventory().getKinahItem().getItemCount() < LegionConfig.LEGION_CREATE_REQUIRED_KINAH)
		{
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CREATE_NOT_ENOUGH_KINAH());
		}
		else if(storeNewLegion(legion))
		{
			activePlayer.getInventory().decreaseKinah(LegionConfig.LEGION_CREATE_REQUIRED_KINAH);
			/**
			 * Create a LegionMember and bind it to a Player
			 */
			LegionMember legionMember = new LegionMember(activePlayer.getObjectId(), legion, 0x00); // Leader Rank
			activePlayer.setLegionMember(legionMember);
			legion.addLegionMember(activePlayer.getObjectId());
			storeNewLegionMember(activePlayer.getObjectId(), legionMember);

			/**
			 * Send required packets
			 */
			PacketSendUtility.sendPacket(activePlayer, new SM_LEGION_INFO(legion));
			PacketSendUtility.sendPacket(activePlayer, new SM_LEGION_MEMBER(activePlayer));
			PacketSendUtility.sendPacket(activePlayer, new SM_LEGION_CREATED(legion.getLegionId()));
			PacketSendUtility.sendPacket(activePlayer, new SM_EDIT_LEGION(0x08));

			PacketSendUtility.broadcastPacket(activePlayer, new SM_UPDATE_LEGION_TITLE(activePlayer.getObjectId(),
				legion.getLegionId(), legion.getLegionName(), legionMember.getRank()), true);

			PacketSendUtility.sendPacket(activePlayer, new SM_LEGIONMEMBER_INFO(activePlayer));
			// 7A 55 39 00 00 34 00 00 00 03 02 00
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CREATED(legion.getLegionName()));
		}
		else
		{
			// Legion Creation Failed SYSTEM_MSG?
			// PacketSendUtility.sendPacket(activePlayer, new SM_CREATE_CHARACTER(null,
			// SM_CREATE_CHARACTER.RESPONSE_DB_ERROR));
		}
	}

	/**
	 * Method that will handle a invitation to a legion
	 * 
	 * @param activePlayer
	 * @param targetPlayer
	 */
	public void invitePlayerToLegion(final Player activePlayer, final Player targetPlayer,
		final PlayerService playerService)
	{
		final Legion legion = activePlayer.getLegionMember().getLegion();
		if(activePlayer.getLifeStats().isAlreadyDead())
		{
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CANT_INVITE_WHILE_DEAD());
		}
		else if(targetPlayer.getObjectId() == activePlayer.getObjectId())
		{
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CAN_NOT_INVITE_SELF());
		}
		else if(targetPlayer.isLegionMember())
		{
			if(targetPlayer.getLegionMember().getLegion() == legion)
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_HE_IS_MY_GUILD_MEMBER(targetPlayer
					.getName()));
			}
			else
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE
					.LEGION_HE_IS_OTHER_GUILD_MEMBER(targetPlayer.getName()));
			}
		}
		else
		{

			RequestResponseHandler responseHandler = new RequestResponseHandler(activePlayer){

				@Override
				public void acceptRequest(Creature requester, Player responder)
				{
					if(!targetPlayer.getCommonData().isOnline())
					{
						PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_INCORRECT_TARGET());
					}
					else
					{
						int playerObjId = responder.getObjectId();
						responder.setLegionMember(new LegionMember(playerObjId, legion));
						if(legion.addLegionMember(playerObjId))
						{
							storeNewLegionMember(playerObjId, responder.getLegionMember());
							PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.NEW_MEMBER_JOINED(responder
								.getName()));

							// Tell all legion members a player joined the legion

							// Send the new legion member the required legion packets
							PacketSendUtility.sendPacket(responder, new SM_LEGION_MEMBER(responder));
							PacketSendUtility.sendPacket(responder, new SM_LEGION_INFO(legion));

							refreshMembersInfoByPacket(legion, new SM_LEGIONMEMBER_INFO(responder));

							for(Integer memberObjId : legion.getLegionMembers())
							{
								Player legionMember = world.findPlayer(memberObjId);
								if(legionMember != null)
								{
									responder.getClientConnection().sendPacket(new SM_LEGIONMEMBER_INFO(legionMember));
								}
								else
								{
									OfflineLegionMember offlineLegionMember = getOfflineLegionMember(memberObjId);
									responder.getClientConnection().sendPacket(new SM_LEGIONMEMBER_INFO(legionMember));
								}
							}

							PacketSendUtility.broadcastPacket(responder, new SM_UPDATE_LEGION_TITLE(playerObjId, legion
								.getLegionId(), legion.getLegionName(), responder.getLegionMember().getRank()), true);

							/** (CURRENT ANNOUNCEMENT) Get 2 parameters from DB **/
							Entry<Integer, String> currentAnnouncement = legion.getCurrentAnnouncement();
							if(currentAnnouncement != null)
							{
								PacketSendUtility.sendPacket(responder, SM_SYSTEM_MESSAGE.LEGION_DISPLAY_ANNOUNCEMENT(
									currentAnnouncement.getValue(), currentAnnouncement.getKey(), 2));
							}
						}
						else
						{
							PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE
								.LEGION_CAN_NOT_ADD_MEMBER_ANY_MORE());
							responder.setLegionMember(null);
						}
					}

				}

				@Override
				public void denyRequest(Creature requester, Player responder)
				{
					PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.REJECTED_INVITE_REQUEST(responder
						.getName()));
				}
			};

			boolean requested = targetPlayer.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_LEGION_INVITE,
				responseHandler);
			// If the player is busy and could not be asked
			if(!requested)
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_TARGET_BUSY());
			}
			else
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE
					.SEND_INVITE_REQUEST(targetPlayer.getName()));

				// Send question packet to buddy
				PacketSendUtility.sendPacket(targetPlayer, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_LEGION_INVITE,
					activePlayer.getObjectId(), legion.getLegionName(), legion.getLegionLevel() + "", activePlayer
						.getName()));
			}
		}
	}

	/**
	 * This method will handle a player that leaves a legion
	 * 
	 * @param activePlayer
	 */
	public void leaveLegion(Player activePlayer)
	{
		final Legion legion = activePlayer.getLegionMember().getLegion();

		// TODO: Check if player is ONLY Brigade General in legion
		// Current state: Player can only leave when he/she is not brigade general
		if(activePlayer.getLegionMember().getRank() == BRIGADE_GENERAL_RANK)
		{
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CANT_LEAVE_BEFORE_CHANGE_MASTER());
		}
		else
		{
			PacketSendUtility.broadcastPacket(activePlayer, new SM_UPDATE_LEGION_TITLE(activePlayer.getObjectId(), 0,
				"", activePlayer.getLegionMember().getRank()), true);
			PacketSendUtility.sendPacket(activePlayer, new SM_LEAVE_LEGION(1300241, 0, legion.getLegionName()));
			activePlayer.setLegionMember(null);
			legion.deleteLegionMember(activePlayer.getObjectId());
			deleteLegionMember(activePlayer.getObjectId());

			refreshMembersInfoByPacket(legion, new SM_LEAVE_LEGION(900699, activePlayer.getObjectId(), activePlayer
				.getName()));
		}
	}

	/**
	 * This method will handle a player that leaves a legion
	 * 
	 * @param activePlayer
	 */
	public void kickPlayer(Player activePlayer, Player targetPlayer)
	{
		final Legion legion = activePlayer.getLegionMember().getLegion();

		if(targetPlayer.getObjectId() == activePlayer.getObjectId())
		{
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CANT_KICK_YOURSELF());
		}
		else if(targetPlayer.getLegionMember().getRank() == BRIGADE_GENERAL_RANK)
		{
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CANT_KICK_BRIGADE_GENERAL());
		}
		else
		{
			// TODO: Can not kick during a war!!
			PacketSendUtility.broadcastPacket(targetPlayer, new SM_UPDATE_LEGION_TITLE(targetPlayer.getObjectId(), 0,
				"", targetPlayer.getLegionMember().getRank()), true);
			targetPlayer.getClientConnection().sendPacket(new SM_LEAVE_LEGION(1300246, 0, legion.getLegionName()));
			targetPlayer.setLegionMember(null);
			legion.deleteLegionMember(targetPlayer.getObjectId());
			deleteLegionMember(targetPlayer.getObjectId());

			refreshMembersInfoByPacket(legion, new SM_LEAVE_LEGION(1300247, targetPlayer.getObjectId(), activePlayer
				.getName(), targetPlayer.getName()));
		}
	}

	/**
	 * This method will handle a new appointed legion leader
	 * 
	 * @param activePlayer
	 * @param targetPlayer
	 */
	public void appointBrigadeGeneral(Player activePlayer, Player targetPlayer)
	{
		final Legion legion = activePlayer.getLegionMember().getLegion();

		if(activePlayer.getLegionMember().getRank() != BRIGADE_GENERAL_RANK)
		{
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CHANGE_MEMBER_RANK_DONT_HAVE_RIGHT());
		}
		else if(activePlayer.getObjectId() == targetPlayer.getObjectId())
		{
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CHANGE_MASTER_ERROR_SELF());
		}
		else
		{
			LegionMember legionMember = targetPlayer.getLegionMember();
			if(legionMember.getRank() > BRIGADE_GENERAL_RANK)
			{
				activePlayer.getLegionMember().setRank(CENTURION_RANK);
				legionMember.setRank(BRIGADE_GENERAL_RANK);
				storeLegionMember(targetPlayer.getObjectId(), legionMember);

				// TODO: Need proper packet!!!!
				refreshMembersInfoByPacket(legion, new SM_LEGIONMEMBER_INFO(activePlayer));
				refreshMembersInfoByPacket(legion, new SM_LEGIONMEMBER_INFO(targetPlayer));
				refreshMembersInfoByPacket(legion, SM_SYSTEM_MESSAGE
					.LEGION_CHANGE_MEMBER_RANK_DONE_1_GUILD_MASTER(targetPlayer.getName()));
			}
		}
	}

	/**
	 * This method will handle the process when a member is demoted or promoted.
	 * 
	 * @param newCenturion
	 */
	public void appointRank(Player activePlayer, Player targetPlayer, int rank)
	{
		final Legion legion = activePlayer.getLegionMember().getLegion();

		if(activePlayer.getLegionMember().getRank() != BRIGADE_GENERAL_RANK)
		{
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CHANGE_MEMBER_RANK_DONT_HAVE_RIGHT());
		}
		else if(activePlayer.getObjectId() == targetPlayer.getObjectId())
		{
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CHANGE_MEMBER_RANK_ERROR_SELF());
		}
		else
		{
			if(rank == LEGIONAIRY_RANK)
			{
				LegionMember legionMember = targetPlayer.getLegionMember();
				if(legionMember.getRank() < LEGIONAIRY_RANK)
				{
					legionMember.setRank(LEGIONAIRY_RANK);
					storeLegionMember(targetPlayer.getObjectId(), legionMember);

					// TODO: Need proper packet!!!!
					refreshMembersInfoByPacket(legionMember.getLegion(), new SM_LEGIONMEMBER_INFO(targetPlayer));
					refreshMembersInfoByPacket(legionMember.getLegion(), SM_SYSTEM_MESSAGE
						.LEGION_CHANGE_MEMBER_RANK_DONE_3_GUILD_MEMBER(targetPlayer.getName()));
				}
			}
			else if(rank == CENTURION_RANK)
			{
				LegionMember legionMember = targetPlayer.getLegionMember();
				if(legionMember.getRank() > CENTURION_RANK)
				{
					legionMember.setRank(CENTURION_RANK);
					storeLegionMember(targetPlayer.getObjectId(), legionMember);

					// TODO: Need proper packet!!!!
					refreshMembersInfoByPacket(legionMember.getLegion(), new SM_LEGIONMEMBER_INFO(targetPlayer));
					refreshMembersInfoByPacket(legionMember.getLegion(), SM_SYSTEM_MESSAGE
						.LEGION_CHANGE_MEMBER_RANK_DONE_2_GUILD_OFFICER(targetPlayer.getName()));
				}
			}
		}
	}

	/**
	 * This will add a new announcement to the DB and change the current announcement
	 * 
	 * @param legion
	 * @param unixTime
	 * @param message
	 */
	public void changeAnnouncement(Player activePlayer, String announcement)
	{
		final Legion legion = activePlayer.getLegionMember().getLegion();

		if(isValidAnnouncement(announcement))
		{
			int unixTime = (int) (System.currentTimeMillis() / 1000);
			storeNewAnnouncement(legion.getLegionId(), unixTime, announcement);
			renewAnnouncementList(legion);
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_WRITE_NOTICE_DONE());
			refreshMembersInfoByPacket(legion, new SM_EDIT_LEGION(0x05, unixTime, announcement));
		}
	}

	/**
	 * This method will handle the changement of a self intro
	 * 
	 * @param activePlayer
	 */
	public void changeSelfIntro(Player activePlayer, String newSelfIntro)
	{
		final LegionMember legionMember = activePlayer.getLegionMember();

		if(isValidSelfIntro(newSelfIntro))
		{
			legionMember.setSelfIntro(newSelfIntro);
			storeLegionMember(activePlayer.getObjectId(), legionMember);
			refreshMembersInfoByPacket(legionMember.getLegion(), new SM_CHANGE_SELF_INTRODUCTION(activePlayer
				.getObjectId(), newSelfIntro));
		}
	}

	/**
	 * This method will handle the changement of permissions
	 * 
	 * @param legion
	 */
	public void changePermissions(Legion legion, int legionarPermission2, int centurionPermission1,
		int centurionPermission2)
	{
		if(checkPermissions(legionarPermission2, centurionPermission1, centurionPermission2))
		{
			legion.setLegionarPermissions(legionarPermission2, centurionPermission1, centurionPermission2);
			storeLegion(legion);
			refreshMembersInfoByPacket(legion, new SM_EDIT_LEGION(0x02, legion));
		}
	}

	/**
	 * This method will handle the leveling up of a legion
	 * 
	 * @param Legion
	 */
	public void changeLevel(Player activePlayer, int kinahAmount)
	{
		final Legion legion = activePlayer.getLegionMember().getLegion();
		int levelKinahPrice = legion.getKinahPrice();
		int levelContributionPrice = legion.getContributionPrice();

		if(legion.getLegionLevel() == MAX_LEGION_LEVEL)
		{
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CHANGE_LEVEL_CANT_LEVEL_UP());
		}
		else if(activePlayer.getInventory().getKinahItem().getItemCount() < levelKinahPrice)
		{
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CHANGE_LEVEL_NOT_ENOUGH_KINAH());
		}
		else if(!legion.hasRequiredMembers())
		{
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CHANGE_LEVEL_NOT_ENOUGH_MEMBER());
		}
		else if(legion.getLegionContribution() < levelContributionPrice)
		{
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CHANGE_LEVEL_NOT_ENOUGH_POINT());
		}
		else
		{
			activePlayer.getInventory().decreaseKinah(levelKinahPrice);
			legion.setLegionLevel(legion.getLegionLevel() + 1);
			storeLegion(legion);
			refreshMembersInfoByPacket(legion, new SM_EDIT_LEGION(0x00, legion));
			refreshMembersInfoByPacket(legion, SM_SYSTEM_MESSAGE.LEGION_LEVEL_UP(legion.getLegionLevel()));
		}
	}

	/**
	 * This method will handle the changement of a nickname
	 * 
	 * @param playerObjId
	 * @param legionMember
	 */
	public void changeNickname(Player activePlayer, Player targetPlayer, String newNickname)
	{
		if(isValidNickname(newNickname))
		{
			final LegionMember targetLegionMember = targetPlayer.getLegionMember();
			targetLegionMember.setNickname(newNickname);
			storeLegionMember(targetPlayer.getObjectId(), targetLegionMember);
			refreshMembersInfoByPacket(targetLegionMember.getLegion(), new SM_CHANGE_NICKNAME(targetPlayer
				.getObjectId(), newNickname));
			// / Message to targetPlayer? Nickname changed?
		}
		else
		{
			// Message to activePlayer? invalid nickname?
			// PacketSendUtility.sendPacket(activePlayer, );
			return;
		}
	}

	/**
	 * This method will send a packet to every legion member
	 * 
	 * @param legion
	 * @param packet
	 */
	private void refreshMembersInfoByPacket(Legion legion, AionServerPacket packet)
	{
		for(Player onlineLegionMember : legion.getOnlineLegionMembers(world))
		{
			PacketSendUtility.sendPacket(onlineLegionMember, packet);
		}
	}

	/**
	 * Check if all permissions are correct
	 * 
	 * @return true or false
	 */
	private boolean checkPermissions(int legionarPermission2, int centurionPermission1, int centurionPermission2)
	{
		/*
		 * DEFAULT Centurion: 60 adds 0x04 (0x64) Invite to Legion => adds 0x08 (0x6c) Kick from Legion => adds 0x0F
		 * (0x7C) ?!?!? DEFAULT: 00 Use Gate Guardian Stone => adds 0x08 (0x08) Use Artifact => adds 0x04 (0x0C) Edit
		 * Announcement => adds 0x02 (0x0E)
		 */
		if(legionarPermission2 < PERMISSION2_MIN || legionarPermission2 > LEGIONAR_PERMISSION2_MAX)
		{
			return false;
		}
		if(centurionPermission1 < PERMISSION1_MIN || centurionPermission1 > CENTURION_PERMISSION1_MAX)
		{
			return false;
		}
		if(centurionPermission2 < PERMISSION2_MIN || centurionPermission2 > CENTURION_PERMISSION2_MAX)
		{
			return false;
		}
		return true;
	}
}
