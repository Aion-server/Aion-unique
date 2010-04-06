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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.main.LegionConfig;
import com.aionemu.gameserver.dao.LegionDAO;
import com.aionemu.gameserver.dao.LegionMemberDAO;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.DeniedStatus;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.model.gameobjects.player.StorageType;
import com.aionemu.gameserver.model.legion.Legion;
import com.aionemu.gameserver.model.legion.LegionEmblem;
import com.aionemu.gameserver.model.legion.LegionHistory;
import com.aionemu.gameserver.model.legion.LegionHistoryType;
import com.aionemu.gameserver.model.legion.LegionMember;
import com.aionemu.gameserver.model.legion.LegionMemberEx;
import com.aionemu.gameserver.model.legion.LegionRank;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_ADD_MEMBER;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_EDIT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_LEAVE_MEMBER;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_MEMBERLIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_TABS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_UPDATE_EMBLEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_UPDATE_MEMBER;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_UPDATE_NICKNAME;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_UPDATE_SELF_INTRO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_UPDATE_TITLE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_WAREHOUSE_INFO;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.aionemu.gameserver.utils.idfactory.IDFactoryAionObject;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.container.LegionContainer;
import com.aionemu.gameserver.world.container.LegionMemberContainer;
import com.google.inject.Inject;

/**
 * 
 * This class is designed to do all the work related with loading/storing legions and their members.<br>
 * 
 * @author Simple
 */
public class LegionService
{
	private static final Logger			log						= Logger.getLogger(LegionService.class);

	private final LegionContainer		allCachedLegions		= new LegionContainer();
	private final LegionMemberContainer	allCachedLegionMembers	= new LegionMemberContainer();

	private IDFactory					aionObjectsIDFactory;
	private World						world;

	/**
	 * Legion Permission variables
	 */
	private static final int			MAX_LEGION_LEVEL		= 5;
	private static final int			INVITE					= 1;
	private static final int			KICK					= 2;
	private static final int			WAREHOUSE				= 3;
	private static final int			ANNOUNCEMENT			= 4;
	@SuppressWarnings("unused")
	private static final int			ARTIFACT				= 5;
	@SuppressWarnings("unused")
	private static final int			GATEGUARDIAN			= 6;

	/**
	 * Legion ranking system
	 */
	private HashMap<Integer, Integer>	legionRanking;

	/**
	 * Legion Restrictions
	 */
	private LegionRestrictions			legionRestrictions		= new LegionRestrictions();

	@Inject
	public LegionService(@IDFactoryAionObject IDFactory aionObjectsIDFactory, World world)
	{
		this.aionObjectsIDFactory = aionObjectsIDFactory;
		this.world = world;
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
	 * Stores legion data into db
	 * 
	 * @param legion
	 * @param newLegion
	 */
	private void storeLegion(Legion legion, boolean newLegion)
	{
		if(newLegion)
		{
			addCachedLegion(legion);
			DAOManager.getDAO(LegionDAO.class).saveNewLegion(legion);
		}
		else
		{
			DAOManager.getDAO(LegionDAO.class).storeLegion(legion);
			if(legion.getLegionEmblem().isChanged())
				storeLegionEmblem(legion.getLegionId(), legion.getLegionEmblem());
		}
	}

	/**
	 * Stores newly created legion
	 * 
	 * @param legion
	 *            legion to store @
	 */
	private void storeLegion(Legion legion)
	{
		storeLegion(legion, false);
	}

	/**
	 * Stores legion member data into db or saves a new one
	 * 
	 * @param legionMember
	 * @param newMember
	 */
	private void storeLegionMember(LegionMember legionMember, boolean newMember)
	{
		if(newMember)
		{
			addCachedLegionMember(legionMember);
			DAOManager.getDAO(LegionMemberDAO.class).saveNewLegionMember(legionMember);
		}
		else
			DAOManager.getDAO(LegionMemberDAO.class).storeLegionMember(legionMember.getObjectId(), legionMember);
	}

	/**
	 * Stores a legion member
	 * 
	 * @param legionMember
	 *            legion member to store
	 */
	private void storeLegionMember(LegionMember legionMember)
	{
		storeLegionMember(legionMember, false);
	}

	/**
	 * Stores legion member data into database
	 * 
	 * @param legionMemberEx
	 */
	private void storeLegionMemberExInCache(Player player)
	{
		if(this.allCachedLegionMembers.containsEx(player.getObjectId()))
		{
			LegionMemberEx legionMemberEx = allCachedLegionMembers.getMemberEx(player.getObjectId());
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

	/**
	 * Stores legion emblem data into db
	 * 
	 * @param legionId
	 * @param legionEmblem
	 */
	private void storeLegionEmblem(int legionId, LegionEmblem legionEmblem)
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
	private Legion getCachedLegion(int legionId)
	{
		return this.allCachedLegions.get(legionId);
	}

	/**
	 * Gets a legion ONLY if he is in the cache
	 * 
	 * @return Legion or null if not cached
	 */
	private Legion getCachedLegion(String legionName)
	{
		return this.allCachedLegions.get(legionName);
	}

	/**
	 * Iterator for loaded legions
	 * 
	 * @return
	 */
	public Iterator<Legion> getCachedLegionIterator()
	{
		return allCachedLegions.iterator();
	}

	/**
	 * This method will add a new legion to the cache
	 * 
	 * @param playerObjId
	 * @param legionMember
	 */
	private void addCachedLegion(Legion legion)
	{
		this.allCachedLegions.add(legion);
	}

	/**
	 * This method will add a new legion member to the cache
	 * 
	 * @param playerObjId
	 * @param legionMember
	 */
	private void addCachedLegionMember(LegionMember legionMember)
	{
		this.allCachedLegionMembers.addMember(legionMember);
	}

	/**
	 * This method will add a new legion member to the cache
	 * 
	 * @param playerObjId
	 * @param legionMemberEx
	 */
	private void addCachedLegionMemberEx(LegionMemberEx legionMemberEx)
	{
		this.allCachedLegionMembers.addMemberEx(legionMemberEx);
	}

	/**
	 * Completely removes legion from database and cache
	 * 
	 * @param legionId
	 *            id of legion to delete from db
	 */
	private void deleteLegionFromDB(Legion legion)
	{
		this.allCachedLegions.remove(legion);
		DAOManager.getDAO(LegionDAO.class).deleteLegion(legion.getLegionId());
	}

	/**
	 * This method will remove the legion member from cache and the database
	 * 
	 * @param playerObjId
	 */
	private void deleteLegionMemberFromDB(LegionMemberEx legionMember)
	{
		this.allCachedLegionMembers.remove(legionMember);
		DAOManager.getDAO(LegionMemberDAO.class).deleteLegionMember(legionMember.getObjectId());
		Legion legion = legionMember.getLegion();
		legion.deleteLegionMember(legionMember.getObjectId());
		addHistory(legion, legionMember.getName(), LegionHistoryType.KICK);
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
		loadLegionInfo(legion);
		
		/**
		 * Add our legion to the Cache
		 */
		addCachedLegion(legion);

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
		loadLegionInfo(legion);
		
		/**
		 * Add our legion to the Cache
		 */
		addCachedLegion(legion);

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
	private void loadLegionInfo(Legion legion)
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
		if(legionRanking == null)
		{
			int DELAY_LEGIONRANKING = LegionConfig.LEGION_RANKING_PERIODICUPDATE * 1000;
			ThreadPoolManager.getInstance().scheduleAtFixedRate(new LegionRankingUpdateTask(), DELAY_LEGIONRANKING,
				DELAY_LEGIONRANKING);
			setLegionRanking(DAOManager.getDAO(LegionDAO.class).loadLegionRanking());
		}

		if(legionRanking.containsKey(legion.getLegionId()))
			legion.setLegionRank(legionRanking.get(legion.getLegionId()));

		/**
		 * Load Legion History
		 */
		DAOManager.getDAO(LegionDAO.class).loadLegionHistory(legion);
	}

	/**
	 * Returns the legion with given legionId (if such legion exists)
	 * 
	 * @param playerObjId
	 * @return LegionMember
	 */
	public LegionMember getLegionMember(int playerObjId)
	{
		LegionMember legionMember = null;
		if(this.allCachedLegionMembers.contains(playerObjId))
			legionMember = this.allCachedLegionMembers.getMember(playerObjId);
		else
		{
			legionMember = DAOManager.getDAO(LegionMemberDAO.class).loadLegionMember(playerObjId, this);
			if(legionMember != null)
				addCachedLegionMember(legionMember);
		}

		if(legionMember != null)
			if(checkDisband(legionMember.getLegion()))
				return null;

		return legionMember;
	}

	/**
	 * Method that checks if a legion is disbanding
	 * 
	 * @param legion
	 * @return true if it's time to be deleted
	 */
	private boolean checkDisband(Legion legion)
	{
		if(legion.isDisbanding())
		{
			if((System.currentTimeMillis() / 1000) > legion.getDisbandTime())
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
			this.allCachedLegionMembers.remove(getLegionMemberEx(memberObjId));
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
	private LegionMemberEx getLegionMemberEx(int playerObjId)
	{
		if(this.allCachedLegionMembers.containsEx(playerObjId))
			return this.allCachedLegionMembers.getMemberEx(playerObjId);
		else
		{
			LegionMemberEx legionMember = DAOManager.getDAO(LegionMemberDAO.class)
				.loadLegionMemberEx(playerObjId, this);
			addCachedLegionMemberEx(legionMember);
			return legionMember;
		}
	}

	/**
	 * Returns the offline legion member with given playerId (if such member exists)
	 * 
	 * @param playerObjId
	 * @return LegionMemberEx
	 */
	private LegionMemberEx getLegionMemberEx(String playerName)
	{
		if(this.allCachedLegionMembers.containsEx(playerName))
			return this.allCachedLegionMembers.getMemberEx(playerName);
		else
		{
			LegionMemberEx legionMember = DAOManager.getDAO(LegionMemberDAO.class).loadLegionMemberEx(playerName, this);
			addCachedLegionMemberEx(legionMember);
			return legionMember;
		}
	}

	/**
	 * This method will handle when disband request is called
	 * 
	 * @param npc
	 * @param activePlayer
	 */
	public void requestDisbandLegion(Creature npc, final Player activePlayer)
	{
		final Legion legion = activePlayer.getLegion();
		if(legionRestrictions.canDisbandLegion(activePlayer, legion))
		{
			RequestResponseHandler disbandResponseHandler = new RequestResponseHandler(npc){
				@Override
				public void acceptRequest(Creature requester, Player responder)
				{
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
		if(legionRestrictions.canCreateLegion(activePlayer, legionName))
		{
			/**
			 * Create new legion and put originator as first member
			 */
			Legion legion = new Legion(aionObjectsIDFactory.nextId(), legionName);
			legion.addLegionMember(activePlayer.getObjectId());
			
			activePlayer.getInventory().decreaseKinah(LegionConfig.LEGION_CREATE_REQUIRED_KINAH);

			/**
			 * Create a LegionMember, add it to the legion and bind it to a Player
			 */
			storeLegion(legion, true);
			addLegionMember(legion, activePlayer, LegionRank.BRIGADE_GENERAL);

			/**
			 * Add create and joined legion history and save it
			 */
			addHistory(legion, "", LegionHistoryType.CREATE);
			addHistory(legion, activePlayer.getName(), LegionHistoryType.JOIN);

			/**
			 * Send required packets
			 */
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CREATED(legion.getLegionName()));
		}
	}

	/**
	 * Method that will handle a invitation to a legion
	 * 
	 * @param activePlayer
	 * @param targetPlayer
	 */
	private void invitePlayerToLegion(final Player activePlayer, final Player targetPlayer)
	{
		if(legionRestrictions.canInvitePlayer(activePlayer, targetPlayer))
		{
			final Legion legion = activePlayer.getLegion();

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
							addLegionMember(legion, targetPlayer);

							// Tell all legion members a player joined the legion
							PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.NEW_MEMBER_JOINED(targetPlayer
								.getName()));

							// Display current announcement
							displayLegionMessage(targetPlayer, legion.getCurrentAnnouncement());

							// Add to history of legion
							addHistory(legion, targetPlayer.getName(), LegionHistoryType.JOIN);
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
	 * Displays current legion announcement
	 * 
	 * @param targetPlayer
	 * @param currentAnnouncement
	 */
	private void displayLegionMessage(Player targetPlayer, Entry<Timestamp, String> currentAnnouncement)
	{
		if(currentAnnouncement != null)
		{
			PacketSendUtility.sendPacket(targetPlayer, SM_SYSTEM_MESSAGE.LEGION_DISPLAY_ANNOUNCEMENT(
				currentAnnouncement.getValue(), (int) (currentAnnouncement.getKey().getTime() / 1000), 2));
		}
	}

	/**
	 * This method will handle a new appointed legion leader
	 * 
	 * @param activePlayer
	 * @param targetPlayer
	 */
	private void appointBrigadeGeneral(final Player activePlayer, final Player targetPlayer)
	{
		if(legionRestrictions.canAppointBrigadeGeneral(activePlayer, targetPlayer))
		{
			final Legion legion = activePlayer.getLegion();
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

							addHistory(legion, targetPlayer.getName(), LegionHistoryType.APPOINTED);
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
	private void appointRank(Player activePlayer, Player targetPlayer, int rank)
	{
		if(legionRestrictions.canAppointRank(activePlayer, targetPlayer))
		{
			Legion legion = activePlayer.getLegion();
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
	 * This method will handle the changement of a self intro
	 * 
	 * @param activePlayer
	 * @param newSelfIntro
	 */
	private void changeSelfIntro(Player activePlayer, String newSelfIntro)
	{
		if(legionRestrictions.canChangeSelfIntro(activePlayer, newSelfIntro))
		{
			LegionMember legionMember = activePlayer.getLegionMember();
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
	private void requestChangeLevel(Player activePlayer, int kinahAmount)
	{
		if(legionRestrictions.canChangeLevel(activePlayer, kinahAmount))
		{
			Legion legion = activePlayer.getLegion();
			activePlayer.getInventory().decreaseKinah(legion.getKinahPrice());
			changeLevel(legion, legion.getLegionLevel() + 1, false);
			addHistory(legion, legion.getLegionLevel() + "", LegionHistoryType.LEVEL_UP);
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
	private void changeNickname(Player activePlayer, Player targetPlayer, String newNickname)
	{
		Legion legion = activePlayer.getLegion();
		if(legionRestrictions.canChangeNickname(legion, targetPlayer.getObjectId(), newNickname))
		{
			LegionMember targetLegionMember = targetPlayer.getLegionMember();
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
	private void updateAfterDisbandLegion(Legion legion)
	{
		for(Player onlineLegionMember : legion.getOnlineLegionMembers(world))
		{
			PacketSendUtility.broadcastPacket(onlineLegionMember, new SM_LEGION_UPDATE_TITLE(onlineLegionMember
				.getObjectId(), 0, "", 0), true);
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
	private void updateMembersEmblem(Legion legion)
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
	private void updateMembersOfDisbandLegion(Legion legion, int unixTime)
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
	private void updateMembersOfRecreateLegion(Legion legion)
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
		if(legionRestrictions.canStoreLegionEmblem(activePlayer, legionId, emblemId))
		{
			Legion legion = activePlayer.getLegion();
			if(legion.getLegionEmblem().isDefaultEmblem())
				addHistory(legion, "", LegionHistoryType.EMBLEM_REGISTER);
			else
				addHistory(legion, "", LegionHistoryType.EMBLEM_MODIFIED);

			activePlayer.getInventory().decreaseKinah(LegionConfig.LEGION_EMBLEM_REQUIRED_KINAH);
			legion.getLegionEmblem().setEmblem(emblemId, color_r, color_g, color_b);
			updateMembersEmblem(legion);
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CHANGED_EMBLEM());
		}
	}

	/**
	 * @param legion
	 */
	private ArrayList<LegionMemberEx> loadLegionMemberExList(Legion legion)
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
				legionMemberEx = getLegionMemberEx(memberObjId);
			}
			legionMembers.add(legionMemberEx);
		}
		return legionMembers;
	}

	/**
	 * @param activePlayer
	 */
	public void openLegionWarehouse(Player activePlayer)
	{
		if(legionRestrictions.canOpenWarehouse(activePlayer))
		{
			// TODO: ADD WAREHOUSE EXPAND TO LEGION!!!
			// TODO send splitted wh packets ?
			PacketSendUtility.sendPacket(activePlayer, new SM_DIALOG_WINDOW(activePlayer.getObjectId(), 25));
			PacketSendUtility.sendPacket(activePlayer, new SM_WAREHOUSE_INFO(activePlayer.getLegion()
				.getLegionWarehouse().getStorageItems(), StorageType.LEGION_WAREHOUSE.getId(), 0, true));
			PacketSendUtility.sendPacket(activePlayer, new SM_WAREHOUSE_INFO(null,
				StorageType.LEGION_WAREHOUSE.getId(), 0, false));
		}
	}

	/**
	 * @param npc
	 * @param player
	 */
	public void recreateLegion(Npc npc, Player activePlayer)
	{
		final Legion legion = activePlayer.getLegion();
		if(legionRestrictions.canRecreateLegion(activePlayer, legion))
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
	 * This method will set the legion ranking if needed
	 * 
	 * @param legion
	 */
	private class LegionRankingUpdateTask implements Runnable
	{
		@Override
		public void run()
		{
			log.info("Legion ranking update task started");
			long startTime = System.currentTimeMillis();
			Iterator<Legion> legionsIterator = allCachedLegions.iterator();
			int legionsUpdated = 0;

			setLegionRanking(DAOManager.getDAO(LegionDAO.class).loadLegionRanking());

			while(legionsIterator.hasNext())
			{
				Legion legion = legionsIterator.next();
				try
				{
					if(legionRanking.containsKey(legion.getLegionId()))
					{
						legion.setLegionRank(legionRanking.get(legion.getLegionId()));
						PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_EDIT(0x01, legion), world);
					}
				}
				catch(Exception ex)
				{
					log.error("Exception during periodic update of legion ranking " + ex.getMessage());
				}

				legionsUpdated++;
			}
			long workTime = System.currentTimeMillis() - startTime;
			log.info("Legion ranking update: " + workTime + " ms, legions: " + legionsUpdated);
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
		if(save)
			storeLegion(legion);
	}

	/**
	 * @param totalSize
	 */
	public void uploadEmblemInfo(Player activePlayer, int totalSize)
	{
		if(legionRestrictions.canUploadEmblemInfo(activePlayer))
		{
			LegionEmblem legionEmblem = activePlayer.getLegion().getLegionEmblem();
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
		if(legionRestrictions.canUploadEmblem(activePlayer))
		{
			// LegionEmblem legionEmblem = activePlayer.getLegion().getLegionEmblem();
			// legionEmblem.addUploadedSize(size);
			// legionEmblem.addUploadData(data);

			// if(legionEmblem.getUploadSize() == legionEmblem.getUploadedSize())
			// {
			// Finished
			// legionEmblem.resetUploadSettings();
			// }
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

	/**
	 * This will add a new announcement to the DB and change the current announcement
	 * 
	 * @param legion
	 * @param unixTime
	 * @param message
	 */
	private void changeAnnouncement(Player activePlayer, String announcement)
	{
		if(legionRestrictions.canChangeAnnouncement(activePlayer.getLegionMember(), announcement))
		{
			Legion legion = activePlayer.getLegion();

			Timestamp currentTime = new Timestamp(System.currentTimeMillis());
			storeNewAnnouncement(legion.getLegionId(), currentTime, announcement);
			legion.addAnnouncementToList(currentTime, announcement);
			PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_WRITE_NOTICE_DONE());
			PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_EDIT(0x05, (int) (System
				.currentTimeMillis() / 1000), announcement), world);
		}
	}

	/**
	 * This method stores all legion announcements
	 * 
	 * @param legion
	 */
	private void storeLegionAnnouncements(Legion legion)
	{
		for(int i = 0; i < (legion.getAnnouncementList().size() - 7); i++)
		{
			removeAnnouncement(legion.getLegionId(), legion.getAnnouncementList().firstEntry().getKey());
			legion.removeFirstEntry();
		}
	}

	/**
	 * Stores newly created announcement
	 * 
	 * @param legionId
	 * 
	 * @param currentTime
	 * 
	 * @param message
	 * @return true if announcement was successful saved.
	 */
	private boolean storeNewAnnouncement(int legionId, Timestamp currentTime, String message)
	{
		return DAOManager.getDAO(LegionDAO.class).saveNewAnnouncement(legionId, currentTime, message);
	}

	/**
	 * @param legionId
	 * @param key
	 * @return true if succeeded
	 */
	private void removeAnnouncement(int legionId, Timestamp key)
	{
		DAOManager.getDAO(LegionDAO.class).removeAnnouncement(legionId, key);
	}

	/**
	 * This method will add a new history for a legion
	 * 
	 * @param legion
	 * @param legionHistory
	 */
	private void addHistory(Legion legion, String text, LegionHistoryType legionHistoryType)
	{
		LegionHistory legionHistory = new LegionHistory(legionHistoryType, text, new Timestamp(System.currentTimeMillis()));

		legion.addHistory(legionHistory);
		DAOManager.getDAO(LegionDAO.class).saveNewLegionHistory(legion.getLegionId(), legionHistory);

		PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_TABS(legion.getLegionHistory()), world);
	}

	/**
	 * This method will add a new legion member to a legion with LEGIONARY rank
	 * 
	 * @param legion
	 * @param player
	 */
	private void addLegionMember(Legion legion, Player player)
	{
		addLegionMember(legion, player, LegionRank.LEGIONARY);
	}

	/**
	 * This method will add a new legion member to a legion with input rank
	 * 
	 * @param legion
	 * @param player
	 * @param rank
	 */
	private void addLegionMember(Legion legion, Player player, LegionRank rank)
	{
		// Set legion member of player and save in the database
		player.setLegionMember(new LegionMember(player.getObjectId(), legion, rank));
		storeLegionMember(player.getLegionMember(), true);

		// Send the new legion member the required legion packets
		PacketSendUtility.sendPacket(player, new SM_LEGION_INFO(legion));

		// Send legion member info to the members
		PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_ADD_MEMBER(player, false, 0, ""), world);

		// Send the member list to the new legion member
		PacketSendUtility.sendPacket(player, new SM_LEGION_MEMBERLIST(loadLegionMemberExList(legion)));

		// Update legion member's appearance in game
		PacketSendUtility.broadcastPacket(player, new SM_LEGION_UPDATE_TITLE(player.getObjectId(),
			legion.getLegionId(), legion.getLegionName(), player.getLegionMember().getRank().getRankId()), true);

		// Send legion emblem information
		LegionEmblem legionEmblem = legion.getLegionEmblem();
		PacketSendUtility.sendPacket(player, new SM_LEGION_UPDATE_EMBLEM(legion.getLegionId(), legionEmblem
			.getEmblemId(), legionEmblem.getColor_r(), legionEmblem.getColor_g(), legionEmblem.getColor_b()));

		// Send legion edit
		PacketSendUtility.sendPacket(player, new SM_LEGION_EDIT(0x08));
	}

	/**
	 * This method will remove a legion member
	 * 
	 * @param charName
	 * @return true if successful
	 */
	private boolean removeLegionMember(String charName, boolean kick, String playerName)
	{
		/**
		 * Get LegionMemberEx from cache or database if offline
		 */
		LegionMemberEx legionMember = getLegionMemberEx(charName);
		if(legionMember == null)
		{
			log.error("Char name does not exist in legion member table: " + charName);
			return false;
		}

		/**
		 * Delete legion member from database and cache
		 */
		deleteLegionMemberFromDB(legionMember);

		/**
		 * If player is online send packet and reset legion member
		 */
		Player player = world.findPlayer(charName);
		if(player != null)
		{
			PacketSendUtility.broadcastPacket(player, new SM_LEGION_UPDATE_TITLE(player.getObjectId(), 0, "", 2), true);
		}

		/**
		 * Send packets to legion members
		 */
		if(kick)
		{
			PacketSendUtility.broadcastPacketToLegion(legionMember.getLegion(), new SM_LEGION_LEAVE_MEMBER(1300247,
				legionMember.getObjectId(), playerName, legionMember.getName()), world);
		}
		else
		{
			PacketSendUtility.broadcastPacketToLegion(legionMember.getLegion(), new SM_LEGION_LEAVE_MEMBER(900699,
				legionMember.getObjectId(), charName), world);			
		}

		return true;
	}

	/**
	 * This method will handle legion stuff
	 * 
	 * @param exOpcode
	 * @param activePlayer
	 * @param charName
	 * @param rank
	 */
	public void handleCharNameRequest(int exOpcode, Player activePlayer, String charName, String newNickname, int rank)
	{
		Legion legion = activePlayer.getLegion();

		charName = Util.convertName(charName);
		Player targetPlayer = world.findPlayer(charName);

		switch(exOpcode)
		{
			/** Invite to legion **/
			case 0x01:
				if(targetPlayer != null)
				{
					if(targetPlayer.getPlayerSettings().isInDeniedStatus(DeniedStatus.GUILD))
					{
						PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE
							.STR_MSG_REJECTED_INVITE_GUILD(charName));
						return;
					}
					invitePlayerToLegion(activePlayer, targetPlayer);
				}
				else
				{
					PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_NO_USER_TO_INVITE());
				}
				break;
			/** Kick member from legion **/
			case 0x04:
				/**
				 * Check if player can be kicked
				 */
				if(legionRestrictions.canKickPlayer(activePlayer, charName))
				{
					if(removeLegionMember(charName, true, activePlayer.getName()))
					{
						// send packet to members?
						if(targetPlayer != null)
						{
							PacketSendUtility.sendPacket(targetPlayer, new SM_LEGION_LEAVE_MEMBER(1300246, 0, legion
								.getLegionName()));
							targetPlayer.resetLegionMember();
						}
					}
				}
				break;
			/** Appoint a new Brigade General **/
			case 0x05:
				if(targetPlayer != null)
				{
					appointBrigadeGeneral(activePlayer, targetPlayer);
				}
				else
				{
					PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CHANGE_MASTER_NO_SUCH_USER());
				}
				break;
			/** Appoint Centurion/Legionairy **/
			case 0x06:
				if(targetPlayer != null)
				{
					appointRank(activePlayer, targetPlayer, rank);
				}
				else
				{
					PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CHANGE_MEMBER_RANK_NO_USER());
				}
				break;
			/** Set nickname **/
			case 0x0F:
				if(targetPlayer == null || targetPlayer.getLegion() != legion)
					// Player off line or NOT in same legion as player
					return;
				changeNickname(activePlayer, targetPlayer, newNickname);
				break;
		}
	}

	/**
	 * This method will handle announcement and self intro changement
	 * 
	 * @param exOpcode
	 * @param activePlayer
	 * @param text
	 */
	public void handleLegionRequest(int exOpcode, Player activePlayer, String text)
	{
		switch(exOpcode)
		{
			/** Edit announcements **/
			case 0x09:
				changeAnnouncement(activePlayer, text);
				break;
			/** Change self introduction **/
			case 0x0A:
				changeSelfIntro(activePlayer, text);
				break;
		}
	}

	/**
	 * @param exOpcode
	 * @param activePlayer
	 */
	public void handleLegionRequest(int exOpcode, Player activePlayer)
	{
		switch(exOpcode)
		{
			/** Leave legion **/
			case 0x02:
				if(legionRestrictions.canLeave(activePlayer))
				{
					if(removeLegionMember(activePlayer.getName(), false, ""))
					{
						Legion legion = activePlayer.getLegion();
						PacketSendUtility.sendPacket(activePlayer, new SM_LEGION_LEAVE_MEMBER(1300241, 0, legion
							.getLegionName()));
						activePlayer.resetLegionMember();
					}
				}
				break;
			/** Level legion up **/
			case 0x0E:
				requestChangeLevel(activePlayer, activePlayer.getInventory().getKinahItem().getItemCount());
				break;
		}
	}

	/**
	 * @param player
	 */
	public void onLogin(Player activePlayer)
	{
		Legion legion = activePlayer.getLegion();

		// Send Legion add member packet
		PacketSendUtility.sendPacket(activePlayer, new SM_LEGION_ADD_MEMBER(activePlayer, false, 0, ""));
		
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
		displayLegionMessage(activePlayer, legion.getCurrentAnnouncement());

		if(legion.isDisbanding())
			PacketSendUtility.sendPacket(activePlayer, new SM_LEGION_EDIT(0x06, legion.getDisbandTime()));
	}

	/**
	 * @param player
	 */
	public void onLogout(Player player)
	{
		Legion legion = player.getLegion();
		PacketSendUtility.broadcastPacketToLegion(legion, new SM_LEGION_UPDATE_MEMBER(player, 0, ""), world);
		storeLegion(legion);
		storeLegionMember(player.getLegionMember());
		storeLegionMemberExInCache(player);
		storeLegionAnnouncements(legion);
	}

	/**
	 * This class contains all restrictions for legion features
	 * 
	 * @author Simple
	 */
	private class LegionRestrictions
	{
		/** Static Emblem information **/
		private static final int	MIN_EMBLEM_ID	= 0;
		private static final int	MAX_EMBLEM_ID	= 10;

		/**
		 * This method checks all restrictions for legion creation
		 * 
		 * @param activePlayer
		 * @param legionName
		 * @return true if allow to create a legion
		 */
		private boolean canCreateLegion(Player activePlayer, String legionName)
		{
			/* Some reasons why legions can' be created */
			if(!isValidName(legionName))
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CREATE_INVALID_NAME());
				return false;
			}
			else if(!isFreeName(legionName))
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CREATE_NAME_EXISTS());
				return false;
			}
			else if(activePlayer.isLegionMember())
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CREATE_ALREADY_MEMBER());
				return false;
			}
			else if(activePlayer.getInventory().getKinahItem().getItemCount() < LegionConfig.LEGION_CREATE_REQUIRED_KINAH)
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CREATE_NOT_ENOUGH_KINAH());
				return false;
			}
			return true;
		}

		/**
		 * This method checks all restrictions for invite player to legion
		 * 
		 * @param activePlayer
		 * @param targetPlayer
		 * @return true if can invite player
		 */
		private boolean canInvitePlayer(Player activePlayer, Player targetPlayer)
		{
			Legion legion = activePlayer.getLegion();
			if(activePlayer.getLifeStats().isAlreadyDead())
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CANT_INVITE_WHILE_DEAD());
				return false;
			}
			if(isSelf(activePlayer, targetPlayer.getObjectId()))
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CAN_NOT_INVITE_SELF());
				return false;
			}
			else if(targetPlayer.isLegionMember())
			{
				if(legion.isMember(targetPlayer.getObjectId()))
				{
					PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE
						.LEGION_HE_IS_MY_GUILD_MEMBER(targetPlayer.getName()));
				}
				else
				{
					PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE
						.LEGION_HE_IS_OTHER_GUILD_MEMBER(targetPlayer.getName()));
				}
				return false;
			}
			else if(!activePlayer.getLegionMember().hasRights(INVITE))
			{
				// No rights to invite
				return false;
			}
			else if(activePlayer.getCommonData().getRace() != targetPlayer.getCommonData().getRace()
				&& !LegionConfig.LEGION_INVITEOTHERFACTION)
			{
				// Not Same Race
				return false;
			}
			return true;
		}

		/**
		 * This method checks all restrictions for kicking a player from a legion
		 * 
		 * @param activePlayer
		 * @param charName
		 * @return true if can kick player
		 */
		private boolean canKickPlayer(Player activePlayer, String charName)
		{
			/**
			 * Get LegionMemberEx from cache or database if offline
			 */
			LegionMemberEx legionMember = getLegionMemberEx(charName);
			if(legionMember == null)
			{
				log.error("Char name does not exist in legion member table: " + charName);
				return false;
			}

			// TODO: Can not kick during a war!!
			Legion legion = activePlayer.getLegion();

			if(isSelf(activePlayer, legionMember.getObjectId()))
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CANT_KICK_YOURSELF());
				return false;
			}
			else if(legionMember.isBrigadeGeneral())
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CANT_KICK_BRIGADE_GENERAL());
				return false;
			}
			else if(legionMember.getRank() == activePlayer.getLegionMember().getRank())
			{
				// Same rank so can't kick
				// TODO: Message
				return false;
			}
			else if(!legion.isMember(legionMember.getObjectId()))
			{
				// Not in same legion
				return false;
			}
			else if(!activePlayer.getLegionMember().hasRights(KICK))
			{
				// No rights to kick
				return false;
			}
			return true;
		}

		/**
		 * This method checks all restrictions for appointing brigade general
		 * 
		 * @param activePlayer
		 * @param targetPlayer
		 * @return true if can appoint brigade general
		 */
		private boolean canAppointBrigadeGeneral(Player activePlayer, Player targetPlayer)
		{
			Legion legion = activePlayer.getLegion();
			if(!isBrigadeGeneral(activePlayer))
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE
					.LEGION_CHANGE_MEMBER_RANK_DONT_HAVE_RIGHT());
				return false;
			}
			if(isSelf(activePlayer, targetPlayer.getObjectId()))
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CHANGE_MASTER_ERROR_SELF());
				return false;
			}
			else if(!legion.isMember(targetPlayer.getObjectId()))
				// not in same legion
				return false;
			return true;
		}

		/**
		 * This method checks all restrictions for appointing rank
		 * 
		 * @param activePlayer
		 * @param targetPlayer
		 * @return true if can appoint rank
		 */
		private boolean canAppointRank(Player activePlayer, Player targetPlayer)
		{
			Legion legion = activePlayer.getLegion();
			if(!isBrigadeGeneral(activePlayer))
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE
					.LEGION_CHANGE_MEMBER_RANK_DONT_HAVE_RIGHT());
				return false;
			}
			if(isSelf(activePlayer, targetPlayer.getObjectId()))
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CHANGE_MEMBER_RANK_ERROR_SELF());
				return false;
			}
			else if(!legion.isMember(targetPlayer.getObjectId()))
			{
				// not in same legion
				return false;
			}
			return true;
		}

		/**
		 * This method checks all restrictions for changing self intro
		 * 
		 * @param activePlayer
		 * @param newSelfIntro
		 * @return true if allowed to change self intro
		 */
		private boolean canChangeSelfIntro(Player activePlayer, String newSelfIntro)
		{
			if(!isValidSelfIntro(newSelfIntro))
				return false;
			return true;
		}

		/**
		 * This method checks all restrictions for changing legion level
		 * 
		 * @param activePlayer
		 * @param kinahAmount
		 * @return true if allowed to change legion level
		 */
		private boolean canChangeLevel(Player activePlayer, int kinahAmount)
		{
			Legion legion = activePlayer.getLegion();
			int levelContributionPrice = legion.getContributionPrice();

			if(legion.getLegionLevel() == MAX_LEGION_LEVEL)
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CHANGE_LEVEL_CANT_LEVEL_UP());
				return false;
			}
			else if(activePlayer.getInventory().getKinahItem().getItemCount() < legion.getKinahPrice())
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CHANGE_LEVEL_NOT_ENOUGH_KINAH());
				return false;
			}
			else if(!legion.hasRequiredMembers())
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CHANGE_LEVEL_NOT_ENOUGH_MEMBER());
				return false;
			}
			else if(legion.getContributionPoints() < levelContributionPrice)
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CHANGE_LEVEL_NOT_ENOUGH_POINT());
				return false;
			}
			return true;
		}

		/**
		 * This method will check all restrictions for changing nickname
		 * 
		 * @param activePlayer
		 * @return true if allowed to change nickname of target player
		 */
		private boolean canChangeNickname(Legion legion, int targetObjectId, String newNickname)
		{
			if(!isValidNickname(newNickname))
			{
				// invalid nickname
				return false;
			}
			else if(!legion.isMember(targetObjectId))
			{
				// not in same legion
				return false;
			}
			return true;
		}

		/**
		 * This method checks all restrictions for changing announcements
		 * 
		 * @param legionMember
		 * @param announcement
		 * @return true if can change announcement
		 */
		private boolean canChangeAnnouncement(LegionMember legionMember, String announcement)
		{
			if(!isValidAnnouncement(announcement) && legionMember.hasRights(ANNOUNCEMENT))
				return false;
			return true;
		}

		/**
		 * This method checks all restrictions for disband legion
		 * 
		 * @param activePlayer
		 * @param legion
		 * @return true if can disband legion
		 */
		private boolean canDisbandLegion(Player activePlayer, Legion legion)
		{
			// TODO: Can't disband during a war!!
			// TODO: Can't disband legion with fortress or hideout!!
			if(!isBrigadeGeneral(activePlayer))
			{
				PacketSendUtility
					.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_DISPERSE_ONLY_MASTER_CAN_DISPERSE());
				return false;
			}
			else if(legion.getLegionWarehouse().size() > 0)
			{
				// TODO: Can't disband during using legion warehouse!!
				return false;
			}
			else if(legion.isDisbanding())
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_DISPERSE_ALREADY_REQUESTED());
				return false;
			}
			else if(legion.getLegionWarehouse().getStorageItems().size() > 0)
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE
					.LEGION_DISPERSE_CANT_DISPERSE_GUILD_STORE_ITEM_IN_WAREHOUSE());
				return false;
			}
			return true;
		}

		/**
		 * This method checks all restrictions for leaving
		 * 
		 * @param activePlayer
		 * @return true if allowed to leave
		 */
		private boolean canLeave(Player activePlayer)
		{
			if(isBrigadeGeneral(activePlayer))
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_CANT_LEAVE_BEFORE_CHANGE_MASTER());
				return false;
			}
			return true;
		}

		/**
		 * This method checks all restrictions for recreate legion
		 * 
		 * @param activePlayer
		 * @param legion
		 * @return true if allowed to recreate legion
		 */
		private boolean canRecreateLegion(Player activePlayer, Legion legion)
		{
			if(!isBrigadeGeneral(activePlayer))
			{
				PacketSendUtility
					.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.LEGION_DISPERSE_ONLY_MASTER_CAN_DISPERSE());
				return false;
			}
			else if(!legion.isDisbanding())
			{
				// Legion is not disbanding
				return false;
			}
			return true;
		}

		/**
		 * This method checks all restrictions for upload emblem info
		 * 
		 * @param activePlayer
		 * @return true if allowed to upload emblem info
		 */
		private boolean canUploadEmblemInfo(Player activePlayer)
		{
			// TODO: System Messages
			if(!isBrigadeGeneral(activePlayer))
				// Not legion leader
				return false;
			else if(activePlayer.getLegion().getLegionLevel() < 3)
			{
				// Legion level isn't high enough
				return false;
			}
			else if(activePlayer.getLegion().getLegionEmblem().isUploading())
			{
				// Already uploading emblem, reset uploading
				activePlayer.getLegion().getLegionEmblem().setUploading(false);
				return false;
			}
			return true;
		}

		/**
		 * This method checks all restrictions for uploading emblem
		 * 
		 * @param activePlayer
		 * @return true if allowed to upload emblem
		 */
		private boolean canUploadEmblem(Player activePlayer)
		{
			if(!isBrigadeGeneral(activePlayer))
			{
				// Not legion leader
				return false;
			}
			else if(activePlayer.getLegion().getLegionLevel() < 3)
			{
				// Legion level isn't high enough
				return false;
			}
			else if(!activePlayer.getLegion().getLegionEmblem().isUploading())
			{
				// Not uploading emblem
				return false;
			}
			return true;
		}

		/**
		 * @param activePlayer
		 * @return
		 */
		public boolean canOpenWarehouse(Player activePlayer)
		{
			if(!activePlayer.isLegionMember())
			{
				// TODO: Message: Not in a legion
				return false;
			}
			else
			{
				Legion legion = activePlayer.getLegion();
				if(!activePlayer.getLegionMember().hasRights(WAREHOUSE))
				{
					// No warehouse rights
					return false;
				}
				else if(legion.isDisbanding())
				{
					PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE
						.LEGION_WAREHOUSE_CANT_USE_WHILE_DISPERSE());
					return false;
				}
				else if(!LegionConfig.LEGION_WAREHOUSE)
				{
					// Legion Warehouse not enabled
					return false;
				}
				else
				{
					// TODO: ADD WAREHOUSE EXPAND TO LEGION!!!
					// TODO send splitted wh packets ? remove duplication
					PacketSendUtility.sendPacket(activePlayer, new SM_DIALOG_WINDOW(activePlayer.getObjectId(), 25));
					PacketSendUtility.sendPacket(activePlayer, new SM_WAREHOUSE_INFO(legion.getLegionWarehouse()
						.getStorageItems(), StorageType.LEGION_WAREHOUSE.getId(), 0, true));
					PacketSendUtility.sendPacket(activePlayer, new SM_WAREHOUSE_INFO(null, StorageType.LEGION_WAREHOUSE
						.getId(), 0, false));
					return true;
				}
			}
		}

		/**
		 * @param activePlayer
		 * @param emblemId
		 * @return
		 */
		public boolean canStoreLegionEmblem(Player activePlayer, int legionId, int emblemId)
		{
			Legion legion = activePlayer.getLegion();
			if(emblemId < MIN_EMBLEM_ID || emblemId > MAX_EMBLEM_ID)
			{
				// Not a valid emblemId
				return false;
			}
			else if(legionId != legion.getLegionId())
			{
				// legion id not equal
				return false;
			}
			else if(legion.getLegionLevel() < 2)
			{
				// legion level not high enough
				return false;
			}
			else if(activePlayer.getInventory().getKinahItem().getItemCount() < LegionConfig.LEGION_EMBLEM_REQUIRED_KINAH)
			{
				PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE
					.NOT_ENOUGH_KINAH(LegionConfig.LEGION_EMBLEM_REQUIRED_KINAH));
				return false;
			}
			return true;
		}

		/**
		 * Checks if player is brigade general and returns message if not
		 * 
		 * @param player
		 * @param message
		 * @return
		 */
		private boolean isBrigadeGeneral(Player player)
		{
			return player.getLegionMember().isBrigadeGeneral();
		}

		/**
		 * Checks if target is same as current player
		 * 
		 * @param player
		 * @param targetObjId
		 * @param message
		 * @return
		 */
		private boolean isSelf(Player player, int targetObjId)
		{
			return player.sameObjectId(targetObjId);
		}

		/**
		 * Checks if name is already taken or not
		 * 
		 * @param name
		 *            character name
		 * @return true if is free, false in other case
		 */
		private boolean isFreeName(String name)
		{
			return !DAOManager.getDAO(LegionDAO.class).isNameUsed(name);
		}

		/**
		 * Checks if a self intro is valid. It should contain only english letters
		 * 
		 * @param name
		 *            character name
		 * @return true if name is valid, false overwise
		 */
		private boolean isValidSelfIntro(String name)
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
		private boolean isValidNickname(String name)
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
		private boolean isValidAnnouncement(String name)
		{
			return LegionConfig.ANNOUNCEMENT_PATTERN.matcher(name).matches();
		}
	}
}
