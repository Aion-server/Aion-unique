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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.main.LegionConfig;
import com.aionemu.gameserver.dao.LegionDAO;
import com.aionemu.gameserver.dao.LegionMemberDAO;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.model.gameobjects.player.StorageType;
import com.aionemu.gameserver.model.legion.Legion;
import com.aionemu.gameserver.model.legion.LegionEmblem;
import com.aionemu.gameserver.model.legion.LegionMember;
import com.aionemu.gameserver.model.legion.LegionMemberEx;
import com.aionemu.gameserver.model.legion.LegionRank;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_ADD_MEMBER;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_EDIT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_LEAVE_MEMBER;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_MEMBERLIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_UPDATE_EMBLEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_UPDATE_MEMBER;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_UPDATE_NICKNAME;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_UPDATE_SELF_INTRO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_UPDATE_TITLE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_WAREHOUSE_INFO;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.collections.cachemap.CacheMap;
import com.aionemu.gameserver.utils.collections.cachemap.CacheMapFactory;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.aionemu.gameserver.utils.idfactory.IDFactoryAionObject;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.container.LegionContainer;
import com.google.inject.Inject;

/**
 * 
 * This class is designed to do all the work related with loading/storing legions and their members.<br>
 * 
 * @author Simple
 */
public class LegionService
{
	private final LegionContainer					allCachedLegions	= new LegionContainer();

	private final CacheMap<Integer, LegionMember>	legionMemberCache	= CacheMapFactory.createSoftCacheMap(
																			"LegionMember", "legionMember");
	private final CacheMap<Integer, LegionMemberEx>	legionMemberExCache	= CacheMapFactory.createSoftCacheMap(
																			"LegionMemberEx", "legionMemberEx");

	private IDFactory								aionObjectsIDFactory;
	private World									world;

	/**
	 * Legion Permission variables
	 */
	private static final int						MAX_LEGION_LEVEL	= 5;
	private static final int						INVITE				= 1;
	private static final int						KICK				= 2;
	private static final int						WAREHOUSE			= 3;
	private static final int						ANNOUNCEMENT		= 4;
	@SuppressWarnings("unused")
	private static final int						ARTIFACT			= 5;
	@SuppressWarnings("unused")
	private static final int						GATEGUARDIAN		= 6;

	/** Static Emblem information **/
	private static final int						MIN_EMBLEM_ID		= 0;
	private static final int						MAX_EMBLEM_ID		= 10;

	/**
	 * Legion ranking system
	 */
	private HashMap<Integer, Integer>				legionRanking;

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
		addCachedLegion(legion);
		return DAOManager.getDAO(LegionDAO.class).saveNewLegion(legion);
	}

	/**
	 * Stores newly created legion member
	 * 
	 * @param legionMember
	 *            legion member to store
	 * @return true if legion member was successful saved.
	 */
	public boolean storeNewLegionMember(LegionMember legionMember)
	{
		addCachedLegionMember(legionMember);
		return DAOManager.getDAO(LegionMemberDAO.class).saveNewLegionMember(legionMember);
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
	public boolean storeNewAnnouncement(int legionId, String message)
	{
		return DAOManager.getDAO(LegionDAO.class).saveNewAnnouncement(legionId, message);
	}

	/**
	 * Stores legion data into db
	 * 
	 * @param legion
	 */
	public void storeLegion(Legion legion)
	{
		DAOManager.getDAO(LegionDAO.class).storeLegion(legion);
		if(legion.getLegionEmblem().isChanged())
			storeLegionEmblem(legion.getLegionId(), legion.getLegionEmblem());
	}

	/**
	 * Stores legion member data into db
	 * 
	 * @param legionMember
	 */
	public void storeLegionMember(LegionMember legionMember)
	{
		DAOManager.getDAO(LegionMemberDAO.class).storeLegionMember(legionMember.getObjectId(), legionMember);
	}

	/**
	 * Stores legion member data into db
	 * 
	 * @param legionMemberEx
	 */
	public void storeLegionMemberExInCache(Player player)
	{
		if(legionMemberExCache.contains(player.getObjectId()))
		{
			LegionMemberEx legionMemberEx = legionMemberExCache.get(player.getObjectId());
			legionMemberEx.setNickname(player.getLegionMember().getNickname());
			legionMemberEx.setSelfIntro(player.getLegionMember().getSelfIntro());
			legionMemberEx.setPlayerClass(player.getPlayerClass());
			legionMemberEx.setExp(player.getCommonData().getExp());
			legionMemberEx.setLastOnline(player.getCommonData().getLastOnline());
			legionMemberEx.setWorldId(player.getPosition().getMapId());
			legionMemberEx.setOnline(false);
		}
		else
		{
			LegionMemberEx legionMemberEx = new LegionMemberEx(player, player.getLegionMember(), false);
			addCachedLegionMemberEx(legionMemberEx);
		}
	}

	public void storeLegionEmblem(int legionId, LegionEmblem legionEmblem)
	{
		if(legionEmblem.isDefaultEmblem())
			DAOManager.getDAO(LegionDAO.class).saveNewLegionEmblem(legionId, legionEmblem);

		else
			DAOManager.getDAO(LegionDAO.class).storeLegionEmblem(legionId, legionEmblem);
	}

	/**
	 * Gets a legion ONLY if he is in the cache
	 * 
	 * @return Legion or null if not cached
	 */
	public Legion getCachedLegion(int legionId)
	{
		return allCachedLegions.get(legionId);
	}

	/**
	 * Gets a legion ONLY if he is in the cache
	 * 
	 * @return Legion or null if not cached
	 */
	public Legion getCachedLegion(String legionName)
	{
		return allCachedLegions.get(legionName);
	}

	/**
	 * @return the legionMembersCache
	 */
	public LegionMember getCachedLegionMember(int playerObjId)
	{
		return legionMemberCache.get(playerObjId);
	}

	/**
	 * @return the legionMembersExCache
	 */
	public LegionMemberEx getCachedLegionMemberEx(int playerObjId)
	{
		return legionMemberExCache.get(playerObjId);
	}

	/**
	 * This method will add a new legion to the cache
	 * 
	 * @param playerObjId
	 * @param legionMember
	 */
	public void addCachedLegion(Legion legion)
	{
		allCachedLegions.add(legion);
	}

	/**
	 * This method will add a new legion member to the cache
	 * 
	 * @param playerObjId
	 * @param legionMember
	 */
	public void addCachedLegionMember(LegionMember legionMember)
	{
		legionMemberCache.put(legionMember.getObjectId(), legionMember);
	}

	/**
	 * This method will add a new legion member to the cache
	 * 
	 * @param playerObjId
	 * @param legionMemberEx
	 */
	public void addCachedLegionMemberEx(LegionMemberEx legionMemberEx)
	{
		legionMemberExCache.put(legionMemberEx.getObjectId(), legionMemberEx);
	}

	/**
	 * Completely removes legion member from database
	 * 
	 * @param playerObjId
	 *            Player Object Id
	 */
	public void deleteLegionMember(int playerObjId)
	{
		if(legionMemberCache.contains(playerObjId))
			this.legionMemberCache.remove(playerObjId);
		DAOManager.getDAO(LegionMemberDAO.class).deleteLegionMember(playerObjId);
	}

	/**
	 * Completely removes legion from database
	 * 
	 * @param legionId
	 *            id of legion to delete from db
	 */
	public void deleteLegionFromDB(Legion legion)
	{
		if(allCachedLegions.contains(legion.getLegionId()))
			this.allCachedLegions.remove(legion);
		DAOManager.getDAO(LegionDAO.class).deleteLegion(legion.getLegionId());
	}

	/**
	 * Returns the legion with given legionId (if such legion exists)
	 * 
	 * @param legionId
	 * @return Legion
	 */
	public Legion getLegion(String legionName)
	{
		/**
		 * First check if our legion already exists in our Cache
		 */
		if(allCachedLegions.contains(legionName))
		{
			Legion legion = getCachedLegion(legionName);
			return legion;
		}

		/**
		 * Else load the legion information from the database
		 */
		Legion legion = DAOManager.getDAO(LegionDAO.class).loadLegion(legionName);

		/**
		 * This will handle the rest of the information that needs to be loaded
		 */
		getLegionInfo(legion);

		/**
		 * Return the legion
		 */
		return legion;
	}

	/**
	 * Returns the legion with given legionId (if such legion exists)
	 * 
	 * @param legionId
	 * @return Legion
	 */
	public Legion getLegion(int legionId)
	{
		/**
		 * First check if our legion already exists in our Cache
		 */
		if(allCachedLegions.contains(legionId))
		{
			Legion legion = getCachedLegion(legionId);
			return legion;
		}

		/**
		 * Else load the legion information from the database
		 */
		Legion legion = DAOManager.getDAO(LegionDAO.class).loadLegion(legionId);

		/**
		 * This will handle the rest of the information that needs to be loaded
		 */
		getLegionInfo(legion);

		/**
		 * Return the legion
		 */
		return legion;
	}

	/**
	 * This method will load the legion information
	 * 
	 * @param legion
	 */
	private void getLegionInfo(Legion legion)
	{
		/**
		 * Check if legion is not null
		 */
		if(legion == null)
			return;

		/**
		 * Load and add the legion members to legion
		 */
		legion.setLegionMembers(DAOManager.getDAO(LegionMemberDAO.class).loadLegionMembers(legion.getLegionId()));

		/**
		 * Load and set the announcement list
		 */
		legion.setAnnouncementList(DAOManager.getDAO(LegionDAO.class).loadAnnouncementList(legion.getLegionId()));

		/**
		 * Set legion emblem
		 */
		legion.setLegionEmblem(DAOManager.getDAO(LegionDAO.class).loadLegionEmblem(legion.getLegionId()));

		/**
		 * Load Legion Warehouse
		 */
		legion.setLegionWarehouse(DAOManager.getDAO(LegionDAO.class).loadLegionStorage(legion));

		/**
		 * Load legion ranking system if not already set
		 */
		loadLegionRanking();
		if(legionRanking.size() > 0)
			legion.setLegionRank(legionRanking.get(legion.getLegionId()));

		/**
		 * Add our legion to the Cache
		 */
		addCachedLegion(legion);
	}

	/**
	 * Returns the legion with given legionId (if such legion exists)
	 * 
	 * @param playerObjId
	 * @return LegionMember
	 */
	public LegionMember getLegionMember(int playerObjId)
	{
		LegionMember legionMember;
		/** First check if our legion member already exists in our Cache **/
		if(legionMemberCache.contains(playerObjId))
			legionMember = getCachedLegionMember(playerObjId);
		else
			legionMember = DAOManager.getDAO(LegionMemberDAO.class).loadLegionMember(playerObjId, this);

		if(legionMember != null)
		{
			if(checkDisband(legionMember.getLegion()))
				return null;

			if(!legionMemberCache.contains(legionMember.getObjectId()))
				addCachedLegionMember(legionMember);
		}

		return legionMember;
	}

	/**
	 * Method that checks if a legion is disbanding
	 * 
	 * @param legion
	 * @return true if it's time to be deleted
	 */
	private boolean checkDisband(final Legion legion)
	{
		if(legion.isDisbanding())
		{
			if((new Date().getTime() / 1000) > legion.getDisbandTime())
			{
				disbandLegion(legion);
				return true;
			}
		}
		return false;
	}

	/**
	 * This method will disband a legion and update all members
	 */
	public void disbandLegion(Legion legion)
	{
		for(Integer memberObjId : legion.getLegionMembers())
		{
			legionMemberCache.remove(memberObjId);
		}
		updateAfterDisbandLegion(legion);
		deleteLegionFromDB(legion);
	}

	/**
	 * Returns the offline legion member with given playerId (if such member exists)
	 * 
	 * @param playerObjId
	 * @return LegionMemberEx
	 */
	public LegionMemberEx getLegionMemberEx(int playerObjId)
	{
		return DAOManager.getDAO(LegionMemberDAO.class).loadLegionMemberEx(playerObjId, this);
	}

	/**
	 * Returns the offline legion member with given playerId (if such member exists)
	 * 
	 * @param playerObjId
	 * @return LegionMemberEx
	 */
	public LegionMemberEx getLegionMemberEx(String playerName)
	{
		return DAOManager.getDAO(LegionMemberDAO.class).loadLegionMemberEx(playerName, this);
	}

	/**
	 * @param legion
	 */
	public void renewAnnouncementList(Legion legion)
	{
		legion.setAnnouncementList(DAOManager.getDAO(LegionDAO.class).loadAnnouncementList(legion.getLegionId()));
	}

	/**
	 * This method will handle when disband request is called
	 * 
	 * @param npc
	 * @param activePlayer
	 */
	public void requestDisbandLegion(final Creature npc, final Player activePlayer)
	{
		final LegionMember legionMember = activePlayer.getLegionMember();
		final Legion legion = legionMember.getLegion();
		if(!legionMember.isBrigadeGeneral())
		{
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_DISPERSE_ONLY_MASTER_CAN_DISPERSE());
		}
		else if(legion.isDisbanding())
		{
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_DISPERSE_ALREADY_REQUESTED());
		}
		else if(legion.getLegionWarehouse().getStorageItems().size() > 0)
		{
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE
				.LEGION_DISPERSE_CANT_DISPERSE_GUILD_STORE_ITEM_IN_WAREHOUSE());
		}
		else
		{
			RequestResponseHandler disbandResponseHandler = new RequestResponseHandler(npc){

				@Override
				public void acceptRequest(Creature requester, Player responder)
				{
					// TODO: Can't disband during a war!!
					// TODO: Can't disband during using legion warehouse!!
					// TODO: Can't disband legion with fortress or hideout!!
					int unixTime = (int) ((System.currentTimeMillis() / 1000) + LegionConfig.LEGION_DISBAND_TIME);
					legion.setDisbandTime(unixTime);
					updateMembersOfDisbandLegion(legion, unixTime);
				}

				@Override
				public void denyRequest(Creature requester, Player responder)
				{
					// no message
				}
			};

			boolean disbandResult = activePlayer.getResponseRequester().putRequest(
				SM_QUESTION_WINDOW.STR_LEGION_DISBAND, disbandResponseHandler);
			if(disbandResult)
			{
				PacketSendUtility.sendPacket(activePlayer, new SM_QUESTION_WINDOW(
					SM_QUESTION_WINDOW.STR_LEGION_DISBAND, 0));
			}
		}
	}

	/**
	 * This method will handle the creation of a legion
	 * 
	 * @param activePlayer
	 * @param legionName
	 */
	public void createLegion(Player activePlayer, String legionName)
	{
		/* Some reasons why legions can' be created */
		if(!isValidName(legionName))
		{
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CREATE_INVALID_NAME());
			return;
		}
		else if(!isFreeName(legionName))
		{
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CREATE_NAME_EXISTS());
			return;
		}
		else if(activePlayer.isLegionMember())
		{
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CREATE_ALREADY_MEMBER());
		}
		else if(activePlayer.getInventory().getKinahItem().getItemCount() < LegionConfig.LEGION_CREATE_REQUIRED_KINAH)
		{
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CREATE_NOT_ENOUGH_KINAH());
		}
		else
		{
			Legion legion = new Legion(aionObjectsIDFactory.nextId(), legionName, activePlayer.getObjectId());

			LegionEmblem legionEmblem = legion.getLegionEmblem();
			activePlayer.getInventory().decreaseKinah(LegionConfig.LEGION_CREATE_REQUIRED_KINAH);
			/**
			 * Create a LegionMember, add it to the legion and bind it to a Player
			 */
			activePlayer.setLegionMember(new LegionMember(activePlayer.getObjectId(), legion,
				LegionRank.BRIGADE_GENERAL));
			storeNewLegion(legion);
			storeNewLegionMember(activePlayer.getLegionMember());

			/**
			 * Send required packets
			 */
			PacketSendUtility.sendPacket(activePlayer, new SM_LEGION_INFO(legion));
			PacketSendUtility.sendPacket(activePlayer, new SM_LEGION_ADD_MEMBER(activePlayer, false, 0, ""));
			PacketSendUtility.sendPacket(activePlayer, new SM_LEGION_UPDATE_EMBLEM(legion.getLegionId(), legionEmblem
				.getEmblemId(), legionEmblem.getColor_r(), legionEmblem.getColor_g(), legionEmblem.getColor_b()));
			PacketSendUtility.sendPacket(activePlayer, new SM_LEGION_EDIT(0x08));

			PacketSendUtility.broadcastPacket(activePlayer, new SM_LEGION_UPDATE_TITLE(activePlayer.getObjectId(),
				legion.getLegionId(), legion.getLegionName(), activePlayer.getLegionMember().getRank().getRankId()),
				true);

			PacketSendUtility.sendPacket(activePlayer, new SM_LEGION_MEMBERLIST(loadLegionMemberExList(legion)));
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CREATED(legion.getLegionName()));
		}
	}

	/**
	 * Method that will handle a invitation to a legion
	 * 
	 * @param activePlayer
	 * @param targetPlayer
	 */
	public void invitePlayerToLegion(final Player activePlayer, final Player targetPlayer)
	{
		final Legion legion = activePlayer.getLegion();
		if(activePlayer.getLifeStats().isAlreadyDead())
		{
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CANT_INVITE_WHILE_DEAD());
		}
		else if(targetPlayer.sameObjectId(activePlayer.getObjectId()))
		{
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CAN_NOT_INVITE_SELF());
		}
		else if(targetPlayer.isLegionMember())
		{
			if(legion.isMember(targetPlayer.getObjectId()))
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
		else if(!activePlayer.getLegionMember().hasRights(INVITE))
		{
			// No rights to invite
		}
		else if(activePlayer.getCommonData().getRace() != targetPlayer.getCommonData().getRace()
			&& !LegionConfig.LEGION_INVITEOTHERFACTION)
		{
			// Not Same Race
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
						int playerObjId = targetPlayer.getObjectId();
						if(legion.addLegionMember(playerObjId))
						{
							// Bind LegionMember to Player
							targetPlayer.setLegionMember(new LegionMember(playerObjId, legion, LegionRank.LEGIONARY));

							// Tell all legion members a player joined the legion
							PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.NEW_MEMBER_JOINED(targetPlayer
								.getName()));

							// Send the new legion member the required legion packets
							PacketSendUtility.sendPacket(targetPlayer, new SM_LEGION_INFO(legion));

							// Send legion member info to the members
							PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_ADD_MEMBER(targetPlayer,
								false, 0, ""), world);

							// Send the member list to the new legion member
							PacketSendUtility.sendPacket(targetPlayer, new SM_LEGION_MEMBERLIST(
								loadLegionMemberExList(legion)));

							PacketSendUtility.broadcastPacket(targetPlayer, new SM_LEGION_UPDATE_TITLE(playerObjId,
								legion.getLegionId(), legion.getLegionName(), targetPlayer.getLegionMember().getRank()
									.getRankId()), true);

							/** (CURRENT ANNOUNCEMENT) Get 2 parameters from DB **/
							Entry<Timestamp, String> currentAnnouncement = legion.getCurrentAnnouncement();
							if(currentAnnouncement != null)
							{
								PacketSendUtility.sendPacket(targetPlayer, SM_SYSTEM_MESSAGE
									.LEGION_DISPLAY_ANNOUNCEMENT(currentAnnouncement.getValue(),
										(int) (currentAnnouncement.getKey().getTime() / 1000), 2));
							}

							// Save the new legion member
							storeNewLegionMember(targetPlayer.getLegionMember());
						}
						else
						{
							PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE
								.LEGION_CAN_NOT_ADD_MEMBER_ANY_MORE());
							targetPlayer.resetLegionMember();
						}
					}

				}

				@Override
				public void denyRequest(Creature requester, Player responder)
				{
					PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.REJECTED_INVITE_REQUEST(targetPlayer
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
	public void leaveLegion(final Player activePlayer)
	{
		final Legion legion = activePlayer.getLegion();
		final LegionMember legionMember = activePlayer.getLegionMember();

		if(legionMember.isBrigadeGeneral())
		{
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CANT_LEAVE_BEFORE_CHANGE_MASTER());
		}
		else
		{
			PacketSendUtility.broadcastPacket(activePlayer, new SM_LEGION_UPDATE_TITLE(activePlayer.getObjectId(), 0,
				"", legionMember.getRank().getRankId()), true);
			PacketSendUtility.sendPacket(activePlayer, new SM_LEGION_LEAVE_MEMBER(1300241, 0, legion.getLegionName()));
			PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_LEAVE_MEMBER(900699, activePlayer
				.getObjectId(), activePlayer.getName()), world);

			activePlayer.resetLegionMember();
			legion.deleteLegionMember(activePlayer.getObjectId());
			deleteLegionMember(activePlayer.getObjectId());
		}
	}

	/**
	 * This method will handle a player that is kicked from a legion
	 * 
	 * @param activePlayer
	 * @param legionMember
	 */
	public void kickPlayer(final Player activePlayer, final LegionMemberEx legionMember)
	{
		final Legion legion = activePlayer.getLegion();

		if(legionMember.sameObjectId(activePlayer.getObjectId()))
		{
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CANT_KICK_YOURSELF());
		}
		else if(legionMember.isBrigadeGeneral())
		{
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CANT_KICK_BRIGADE_GENERAL());
		}
		else if(legionMember.getRank() == activePlayer.getLegionMember().getRank())
		{
			// Same rank so can't kick
			// TODO: Message
		}
		else if(!legion.isMember(legionMember.getObjectId()))
		{
			// Not in same legion
		}
		else if(!activePlayer.getLegionMember().hasRights(KICK))
		{
			// No rights to kick
		}
		else
		{
			legion.deleteLegionMember(legionMember.getObjectId());
			deleteLegionMember(legionMember.getObjectId());

			PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_LEAVE_MEMBER(1300247, legionMember
				.getObjectId(), activePlayer.getName(), legionMember.getName()), world);
		}
	}

	/**
	 * This method will handle a new appointed legion leader
	 * 
	 * @param activePlayer
	 * @param targetPlayer
	 */
	public void appointBrigadeGeneral(final Player activePlayer, final Player targetPlayer)
	{
		final Legion legion = activePlayer.getLegion();

		if(!activePlayer.getLegionMember().isBrigadeGeneral())
		{
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CHANGE_MEMBER_RANK_DONT_HAVE_RIGHT());
		}
		else if(targetPlayer.sameObjectId(activePlayer.getObjectId()))
		{
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CHANGE_MASTER_ERROR_SELF());
		}
		else if(!legion.isMember(targetPlayer.getObjectId()))
		{
			// not in same legion
		}
		else
		{
			RequestResponseHandler responseHandler = new RequestResponseHandler(activePlayer){

				@Override
				public void acceptRequest(Creature requester, Player responder)
				{
					if(!targetPlayer.getCommonData().isOnline())
					{
						PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE
							.LEGION_CHANGE_MASTER_NO_SUCH_USER());
					}
					else
					{
						LegionMember legionMember = targetPlayer.getLegionMember();
						if(legionMember.getRank().getRankId() > LegionRank.BRIGADE_GENERAL.getRankId())
						{
							// Demote Brigade General to Centurion
							activePlayer.getLegionMember().setRank(LegionRank.CENTURION);
							PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_UPDATE_MEMBER(activePlayer,
								0, ""), world);

							// Promote member to Brigade General
							legionMember.setRank(LegionRank.BRIGADE_GENERAL);
							PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_UPDATE_MEMBER(targetPlayer,
								1300273, targetPlayer.getName()), world);
						}
					}
				}

				@Override
				public void denyRequest(Creature requester, Player responder)
				{
					PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE
						.LEGION_CHANGE_MASTER_HE_DECLINE_YOUR_OFFER(targetPlayer.getName()));
				}
			};

			boolean requested = targetPlayer.getResponseRequester().putRequest(
				SM_QUESTION_WINDOW.STR_LEGION_CHANGE_MASTER, responseHandler);
			// If the player is busy and could not be asked
			if(!requested)
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE
					.LEGION_CHANGE_MASTER_SENT_CANT_OFFER_WHEN_HE_IS_QUESTION_ASKED());
			}
			else
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE
					.LEGION_CHANGE_MASTER_SENT_OFFER_MSG_TO_HIM(targetPlayer.getName()));

				// Send question packet to buddy
				// TODO: Add char name parameter? Doesn't work?
				PacketSendUtility.sendPacket(targetPlayer, new SM_QUESTION_WINDOW(
					SM_QUESTION_WINDOW.STR_LEGION_CHANGE_MASTER, activePlayer.getObjectId()));
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
		final Legion legion = activePlayer.getLegion();

		if(!activePlayer.getLegionMember().isBrigadeGeneral())
		{
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CHANGE_MEMBER_RANK_DONT_HAVE_RIGHT());
		}
		else if(targetPlayer.sameObjectId(activePlayer.getObjectId()))
		{
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CHANGE_MEMBER_RANK_ERROR_SELF());
		}
		else if(!legion.isMember(targetPlayer.getObjectId()))
		{
			// not in same legion
		}
		else
		{
			int msgId;
			LegionMember legionMember = targetPlayer.getLegionMember();
			if(rank == LegionRank.CENTURION.getRankId() && legionMember.getRank() == LegionRank.LEGIONARY)
			{
				// Change rank and define needed msg id
				legionMember.setRank(LegionRank.CENTURION);
				msgId = 1300267;
			}
			else
			{
				// Change rank and define needed msg id
				legionMember.setRank(LegionRank.LEGIONARY);
				msgId = 1300268;
			}

			PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_UPDATE_MEMBER(targetPlayer, msgId,
				targetPlayer.getName()), world);
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
		final Legion legion = activePlayer.getLegion();

		if(isValidAnnouncement(announcement) && activePlayer.getLegionMember().hasRights(ANNOUNCEMENT))
		{
			storeNewAnnouncement(legion.getLegionId(), announcement);
			renewAnnouncementList(legion);
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_WRITE_NOTICE_DONE());
			PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_EDIT(0x05, (int) (System
				.currentTimeMillis() / 1000), announcement), world);
		}
	}

	/**
	 * This method will handle the changement of a self intro
	 * 
	 * @param activePlayer
	 * @param newSelfIntro
	 */
	public void changeSelfIntro(Player activePlayer, String newSelfIntro)
	{
		final LegionMember legionMember = activePlayer.getLegionMember();

		if(isValidSelfIntro(newSelfIntro))
		{
			legionMember.setSelfIntro(newSelfIntro);
			PacketSendUtility.broadcastPacketToLegion(legionMember.getLegion(), new SM_LEGION_UPDATE_SELF_INTRO(
				activePlayer.getObjectId(), newSelfIntro), world);
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_WRITE_INTRO_DONE());
		}
	}

	/**
	 * This method will handle the changement of permissions
	 * 
	 * @param legion
	 */
	public void changePermissions(Legion legion, int lP2, int cP1, int cP2)
	{
		if(legion.setLegionPermissions(lP2, cP1, cP2))
		{
			PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_EDIT(0x02, legion), world);
		}
	}

	/**
	 * This method will handle the leveling up of a legion
	 * 
	 * @param Legion
	 */
	public void requestChangeLevel(Player activePlayer, int kinahAmount)
	{
		final Legion legion = activePlayer.getLegion();
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
		else if(legion.getContributionPoints() < levelContributionPrice)
		{
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CHANGE_LEVEL_NOT_ENOUGH_POINT());
		}
		else
		{
			activePlayer.getInventory().decreaseKinah(levelKinahPrice);
			changeLevel(legion, legion.getLegionLevel() + 1, false);
		}
	}

	/**
	 * This method will change the legion level and send update to online members
	 * 
	 * @param legion
	 */
	public void changeLevel(Legion legion, int newLevel, boolean save)
	{
		legion.setLegionLevel(newLevel);
		PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_EDIT(0x00, legion), world);
		PacketSendUtility.broadcastPacketToLegion(legion, SM_SYSTEM_MESSAGE.LEGION_LEVEL_UP(newLevel), world);
		if(save)
			storeLegion(legion);
	}

	/**
	 * This method will handle the changement of a nickname
	 * 
	 * @param playerObjId
	 * @param legionMember
	 */
	public void changeNickname(Player activePlayer, Player targetPlayer, String newNickname)
	{
		final Legion legion = activePlayer.getLegion();
		if(!isValidNickname(newNickname))
		{
			// invalid nickname
		}
		else if(!legion.isMember(targetPlayer.getObjectId()))
		{
			// not in same legion
		}
		else
		{
			final LegionMember targetLegionMember = targetPlayer.getLegionMember();
			targetLegionMember.setNickname(newNickname);
			PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_UPDATE_NICKNAME(targetPlayer.getObjectId(),
				newNickname), world);
		}
	}

	/**
	 * This method will remove legion from all legion members online after a legion has been disbanded
	 * 
	 * @param legion
	 */
	public void updateAfterDisbandLegion(Legion legion)
	{
		for(Player onlineLegionMember : legion.getOnlineLegionMembers(world))
		{
			PacketSendUtility.broadcastPacket(onlineLegionMember, new SM_LEGION_UPDATE_TITLE(onlineLegionMember
				.getObjectId(), 0, "", onlineLegionMember.getLegionMember().getRank().getRankId()), true);
			PacketSendUtility.sendPacket(onlineLegionMember, new SM_LEGION_LEAVE_MEMBER(1300302, 0, legion
				.getLegionName()));
			onlineLegionMember.resetLegionMember();
		}
	}

	/**
	 * This method will send a packet to every legion member
	 * 
	 * @param legion
	 */
	public void updateMembersEmblem(Legion legion)
	{
		LegionEmblem legionEmblem = legion.getLegionEmblem();
		for(Player onlineLegionMember : legion.getOnlineLegionMembers(world))
		{
			PacketSendUtility.broadcastPacket(onlineLegionMember, new SM_LEGION_UPDATE_EMBLEM(legion.getLegionId(),
				legionEmblem.getEmblemId(), legionEmblem.getColor_r(), legionEmblem.getColor_g(), legionEmblem
					.getColor_b()), true);
		}
	}

	/**
	 * This method will send a packet to every legion member and update them about the disband
	 * 
	 * @param legion
	 * @param unixTime
	 */
	public void updateMembersOfDisbandLegion(Legion legion, int unixTime)
	{
		for(Player onlineLegionMember : legion.getOnlineLegionMembers(world))
		{
			PacketSendUtility.sendPacket(onlineLegionMember, new SM_LEGION_UPDATE_MEMBER(onlineLegionMember, 1300303,
				unixTime + ""));
			PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_EDIT(0x06, unixTime), world);
		}
	}

	/**
	 * This method will send a packet to every legion member and update them about the disband
	 * 
	 * @param legion
	 * @param unixTime
	 */
	public void updateMembersOfRecreateLegion(Legion legion)
	{
		for(Player onlineLegionMember : legion.getOnlineLegionMembers(world))
		{
			PacketSendUtility.sendPacket(onlineLegionMember, new SM_LEGION_UPDATE_MEMBER(onlineLegionMember, 1300307,
				""));
			PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_EDIT(0x07), world);
		}
	}

	/**
	 * Stores the new legion emblem
	 * 
	 * @param legion
	 * @param emblemId
	 * @param color
	 */
	public void storeLegionEmblem(Player activePlayer, int legionId, int emblemId, int color_r, int color_g, int color_b)
	{
		final Legion legion = activePlayer.getLegion();

		if(emblemId < MIN_EMBLEM_ID || emblemId > MAX_EMBLEM_ID)
		{
			// Not a valid emblemId
		}
		else if(legionId != legion.getLegionId())
		{
			// legion id not equal
		}
		else if(legion.getLegionLevel() < 2)
		{
			// legion level not high enough
		}
		else if(activePlayer.getInventory().getKinahItem().getItemCount() < LegionConfig.LEGION_EMBLEM_REQUIRED_KINAH)
		{
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE
				.NOT_ENOUGH_KINAH(LegionConfig.LEGION_EMBLEM_REQUIRED_KINAH));
		}
		else
		{
			activePlayer.getInventory().decreaseKinah(LegionConfig.LEGION_EMBLEM_REQUIRED_KINAH);
			legion.getLegionEmblem().setEmblem(emblemId, color_r, color_g, color_b);
			updateMembersEmblem(legion);
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CHANGED_EMBLEM());
		}
	}

	/**
	 * @param legion
	 */
	public ArrayList<LegionMemberEx> loadLegionMemberExList(final Legion legion)
	{
		ArrayList<LegionMemberEx> legionMembers = new ArrayList<LegionMemberEx>();
		for(Integer memberObjId : legion.getLegionMembers())
		{
			LegionMemberEx legionMemberEx;
			Player memberPlayer = world.findPlayer(memberObjId);
			if(memberPlayer != null)
			{
				legionMemberEx = new LegionMemberEx(memberPlayer, memberPlayer.getLegionMember(), true);
			}
			else
			{
				if(legionMemberExCache.contains(memberObjId))
					legionMemberEx = getCachedLegionMemberEx(memberObjId);
				else
				{
					legionMemberEx = getLegionMemberEx(memberObjId);
					addCachedLegionMemberEx(legionMemberEx);
				}
			}
			if(legionMemberEx.isValidLegionMemberEx())
				legionMembers.add(legionMemberEx);
		}
		return legionMembers;
	}

	/**
	 * @param activePlayer
	 */
	public void openLegionWarehouse(Player activePlayer)
	{
		if(activePlayer.isLegionMember())
		{
			final Legion legion = activePlayer.getLegion();
			if(!activePlayer.getLegionMember().hasRights(WAREHOUSE))
			{
				// No warehouse rights
			}
			else if(legion.isDisbanding())
			{
				PacketSendUtility
					.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_WAREHOUSE_CANT_USE_WHILE_DISPERSE());
			}
			else if(!LegionConfig.LEGION_WAREHOUSE)
			{
				// Legion Warehouse not enabled
			}
			else
			{
				// TODO: ADD WAREHOUSE EXPAND TO LEGION!!!
				PacketSendUtility.sendPacket(activePlayer, new SM_DIALOG_WINDOW(activePlayer.getObjectId(), 25));
				PacketSendUtility.sendPacket(activePlayer, new SM_WAREHOUSE_INFO(legion.getLegionWarehouse()
					.getStorageItems(), StorageType.LEGION_WAREHOUSE.getId(), 0));
				PacketSendUtility.sendPacket(activePlayer, new SM_WAREHOUSE_INFO(null, StorageType.LEGION_WAREHOUSE
					.getId(), 0)); // strange retail way of sending
				// warehouse packets
			}
		}
		else
		{
			// TODO: Message: Not in a legion
		}
	}

	/**
	 * @param player
	 */
	public void legionMemberOnLogin(Player activePlayer)
	{
		Legion legion = activePlayer.getLegion();

		// Send legion info packets
		PacketSendUtility.sendPacket(activePlayer, new SM_LEGION_INFO(legion));

		// Tell all legion members player has come online
		PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_UPDATE_MEMBER(activePlayer, 0, ""), world);

		// Notify legion members player has logged in
		PacketSendUtility.broadcastPacketToLegion(legion, SM_SYSTEM_MESSAGE.STR_MSG_NOTIFY_LOGIN_GUILD(activePlayer
			.getName()), world, activePlayer.getObjectId());

		// Send member add to player
		PacketSendUtility.sendPacket(activePlayer, new SM_LEGION_ADD_MEMBER(activePlayer, true, 0, ""));

		// Send member list to player
		PacketSendUtility.sendPacket(activePlayer, new SM_LEGION_MEMBERLIST(loadLegionMemberExList(legion)));

		// Send current announcement to player
		Entry<Timestamp, String> currentAnnouncement = legion.getCurrentAnnouncement();
		if(currentAnnouncement != null)
		{
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_DISPLAY_ANNOUNCEMENT(
				currentAnnouncement.getValue(), (int) (currentAnnouncement.getKey().getTime() / 1000), 2));
		}

		if(legion.isDisbanding())
			PacketSendUtility.sendPacket(activePlayer, new SM_LEGION_EDIT(0x06, legion.getDisbandTime()));
	}

	/**
	 * @param npc
	 * @param player
	 */
	public void recreateLegion(Npc npc, Player activePlayer)
	{
		final LegionMember legionMember = activePlayer.getLegionMember();
		final Legion legion = legionMember.getLegion();
		if(!legionMember.isBrigadeGeneral())
		{
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_DISPERSE_ONLY_MASTER_CAN_DISPERSE());
		}
		else if(!legion.isDisbanding())
		{
			// Legion is not disbanding
		}
		else
		{
			RequestResponseHandler disbandResponseHandler = new RequestResponseHandler(npc){

				@Override
				public void acceptRequest(Creature requester, Player responder)
				{
					legion.setDisbandTime(0);
					PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_EDIT(0x07), world);
					updateMembersOfRecreateLegion(legion);
				}

				@Override
				public void denyRequest(Creature requester, Player responder)
				{
					// no message
				}
			};

			boolean disbandResult = activePlayer.getResponseRequester().putRequest(
				SM_QUESTION_WINDOW.STR_LEGION_DISBAND_CANCEL, disbandResponseHandler);
			if(disbandResult)
			{
				PacketSendUtility.sendPacket(activePlayer, new SM_QUESTION_WINDOW(
					SM_QUESTION_WINDOW.STR_LEGION_DISBAND_CANCEL, 0));
			}
		}
	}

	/**
	 * @param legionRanking
	 *            the legionRanking to set
	 */
	private void setLegionRanking(HashMap<Integer, Integer> legionRanking)
	{
		this.legionRanking = legionRanking;
	}

	/**
	 * @return the legionRanking
	 */
	public HashMap<Integer, Integer> getLegionRanking()
	{
		return legionRanking;
	}

	/**
	 * This method will set the legion ranking if needed
	 * 
	 * @param legion
	 */
	private void loadLegionRanking()
	{
		if(legionRanking == null)
		{
			setLegionRanking(DAOManager.getDAO(LegionDAO.class).loadLegionRanking());
		}
	}

	/**
	 * This method will change the legion ranking if needed
	 * 
	 * @param legion
	 */
	private void reloadLegionRanking()
	{
		setLegionRanking(null);
		loadLegionRanking();

		for(Integer legionId : legionRanking.keySet())
		{
			Legion legion = getCachedLegion(legionId);
			if(legion != null)
			{
				legion.setLegionRank(legionRanking.get(legion.getLegionId()));
				PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_EDIT(0x01, legion), world);
			}
		}
	}

	/**
	 * This method will update all players about the level/class change
	 * 
	 * @param player
	 */
	public void updateMemberInfo(Player player)
	{
		PacketSendUtility
			.broadcastPacketToLegion(player.getLegion(), new SM_LEGION_UPDATE_MEMBER(player, 0, ""), world);
	}

	/**
	 * Method that will add gained points to contribution points of legion
	 * 
	 * @param legion
	 * @param pointsGained
	 */
	public void addContributionPoints(Legion legion, int pointsGained)
	{
		legion.addContributionPoints(pointsGained);
		PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_EDIT(0x03, legion), world);
		reloadLegionRanking();
	}

	/**
	 * This method will set the contribution points, specially for legion command
	 * 
	 * @param legion
	 * @param newPoints
	 */
	public void setContributionPoints(Legion legion, int newPoints, boolean save)
	{
		legion.setContributionPoints(newPoints);
		PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_EDIT(0x03, legion), world);
		reloadLegionRanking();
		if(save)
			storeLegion(legion);
	}

	/**
	 * @param totalSize
	 */
	public void uploadEmblemInfo(Player activePlayer, int totalSize)
	{
		// TODO: System Messages
		if(!activePlayer.getLegionMember().isBrigadeGeneral())
		{
			// Not legion leader
		}
		else if(activePlayer.getLegion().getLegionLevel() < 3)
		{
			// Legion level isn't high enough
		}
		else if(activePlayer.getLegion().getLegionEmblem().isUploading())
		{
			// Already uploading emblem, reset uploading
			activePlayer.getLegion().getLegionEmblem().setUploading(false);
		}
		else
		{
			final LegionEmblem legionEmblem = activePlayer.getLegion().getLegionEmblem();
			legionEmblem.setUploadSize(totalSize);
			legionEmblem.setUploading(true);
		}
	}

	/**
	 * @param size
	 * @param data
	 */
	public void uploadEmblemData(Player activePlayer, int size, byte[] data)
	{
		if(!activePlayer.getLegionMember().isBrigadeGeneral())
		{
			// Not legion leader
		}
		else if(activePlayer.getLegion().getLegionLevel() < 3)
		{
			// Legion level isn't high enough
		}
		else if(!activePlayer.getLegion().getLegionEmblem().isUploading())
		{
			// Not uploading emblem
		}
		else
		{
			final LegionEmblem legionEmblem = activePlayer.getLegion().getLegionEmblem();
			legionEmblem.addUploadedSize(size);
			legionEmblem.addUploadData(data);

			if(legionEmblem.getUploadSize() == legionEmblem.getUploadedSize())
			{
				// Finished
				legionEmblem.resetUploadSettings();
			}
		}
	}

	/**
	 * @param legion
	 * @param newLegionName
	 */
	public void setLegionName(Legion legion, String newLegionName, boolean save)
	{
		legion.setLegionName(newLegionName);
		PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_INFO(legion), world);

		for(Player legionMember : legion.getOnlineLegionMembers(world))
		{
			PacketSendUtility.broadcastPacket(legionMember, new SM_LEGION_UPDATE_TITLE(legionMember.getObjectId(),
				legion.getLegionId(), legion.getLegionName(), legionMember.getLegionMember().getRank().getRankId()),
				true);
		}
		if(save)
			storeLegion(legion);
	}
}
