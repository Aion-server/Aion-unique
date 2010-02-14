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
package com.aionemu.gameserver.network.aion.clientpackets;

import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.configs.LegionConfig;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.model.legion.Legion;
import com.aionemu.gameserver.model.legion.LegionMember;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CHANGE_NICKNAME;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CHANGE_SELF_INTRODUCTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EDIT_LEGION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEAVE_LEGION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGIONMEMBER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_CREATED;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_TITLE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_UPDATE_LEGION_TITLE;
import com.aionemu.gameserver.services.LegionService;
import com.aionemu.gameserver.services.PlayerService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.aionemu.gameserver.utils.idfactory.IDFactoryAionObject;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * 
 * @author Simple
 * 
 */
@SuppressWarnings("unused")
public class CM_LEGION extends AionClientPacket
{
	private static final Logger	log							= Logger.getLogger(CM_LEGION.class);

	/** Legion based information **/
	@Inject
	private LegionService		legionService;

	@Inject
	private PlayerService		playerService;

	@Inject
	private World				world;

	@Inject
	@IDFactoryAionObject
	private IDFactory			aionObjectsIDFactory;

	/**
	 * exOpcode and the rest
	 */
	private int					exOpcode;
	private int					unk1;
	private int					unk2;
	private int					legionarPermission1;
	private int					legionarPermission2;
	private int					centurionPermission1;
	private int					centurionPermission2;
	private int					rank;
	private String				legionName;
	private String				charName;
	private String				newNickname;
	private String				announcement;
	private String				newSelfIntro;

	/**
	 * Legion Permission variables
	 */
	private static final int	BRIGADE_GENERAL_RANK		= 0x00;
	private static final int	CENTURION_RANK				= 0x01;
	private static final int	LEGIONAIRY_RANK				= 0x02;
	private static final int	PERMISSION1_MIN				= 0x60;
	private static final int	PERMISSION2_MIN				= 0x00;
	private static final int	LEGIONAR_PERMISSION2_MAX	= 0x08;
	private static final int	CENTURION_PERMISSION1_MAX	= 0x7C;
	private static final int	CENTURION_PERMISSION2_MAX	= 0x0E;

	/**
	 * Constructs new instance of CM_LEGION packet
	 * 
	 * @param opcode
	 */
	public CM_LEGION(int opcode)
	{
		super(opcode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl()
	{
		exOpcode = readC();

		switch(exOpcode)
		{
			/** Create a legion **/
			case 0x00:
				unk2 = readD(); // time? 00 78 19 00 40
				legionName = readS();
				break;
			/** Invite to legion **/
			case 0x01:
				unk1 = readD(); // empty
				charName = readS();
				break;
			/** Leave legion **/
			case 0x02:
				unk1 = readD(); // empty
				unk2 = readH(); // empty
				break;
			/** Kick member from legion **/
			case 0x04:
				unk1 = readD(); // empty
				charName = readS();
				break;
			/** Appoint a new Brigade General **/
			case 0x05:
				unk2 = readD();
				charName = readS();
				break;
			/** Appoint Centurion **/
			case 0x06:
				rank = readD();
				charName = readS();
				break;
			/** Demote to Legionary **/
			case 0x07:
				unk2 = readD(); // char id? 00 78 19 00 40
				charName = readS();
				break;
			/** Refresh legion info **/
			case 0x08:
				break;
			/** Edit announcements **/
			case 0x09:
				unk1 = readD(); // empty or char id?
				announcement = readS();
				break;
			/** Change self introduction **/
			case 0x0A:
				unk1 = readD(); // empty char id?
				newSelfIntro = readS();
				break;
			/** Edit permissions **/
			case 0x0D:
				centurionPermission1 = readC(); // 0x60 - 0x7C
				centurionPermission2 = readC(); // 0x00 - 0x0E
				legionarPermission1 = readC(); // can't be set is static 0x40
				legionarPermission2 = readC(); // 0x00 - 0x08
				break;
			/** Level legion up **/
			case 0x0E:
				unk1 = readD(); // empty
				unk2 = readH(); // empty
				break;
			case 0x0F:
				charName = readS();
				newNickname = readS();
				break;
			default:
				log.info("Unknown Legion exOpcode? 0x" + Integer.toHexString(exOpcode).toUpperCase());
				break;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
		final Player activePlayer = getConnection().getActivePlayer();
		if(activePlayer.isLegionMember())
		{
			final Legion legion = activePlayer.getLegionMember().getLegion();

			switch(exOpcode)
			{
				/** Invite to legion **/
				case 0x01:
					if(activePlayer.getLifeStats().isAlreadyDead())
					{
						sendPacket(SM_SYSTEM_MESSAGE.LEGION_CANT_INVITE_WHILE_DEAD());
					}
					else
					{
						final Player targetPlayer = world.findPlayer(charName);

						if(charName.equalsIgnoreCase(activePlayer.getName()))
						{
							// Invalid chars
						}
						else if(targetPlayer == null)
						{
							sendPacket(SM_SYSTEM_MESSAGE.LEGION_NO_USER_TO_INVITE());
						}
						else if(targetPlayer.getObjectId() == activePlayer.getObjectId())
						{
							sendPacket(SM_SYSTEM_MESSAGE.LEGION_CAN_NOT_INVITE_SELF());
						}
						else if(targetPlayer.isLegionMember())
						{
							if(targetPlayer.getLegionMember().getLegion() == legion)
							{
								sendPacket(SM_SYSTEM_MESSAGE.LEGION_HE_IS_MY_GUILD_MEMBER(targetPlayer.getName()));
							}
							else
							{
								sendPacket(SM_SYSTEM_MESSAGE.LEGION_HE_IS_OTHER_GUILD_MEMBER(targetPlayer.getName()));
							}
						}
						else
						{
							invitePlayerToLegion(activePlayer, targetPlayer, legion);
						}
					}
					break;
				/** Leave legion **/
				case 0x02:
					leaveLegion(activePlayer, legion);
					break;
				/** Kick member from legion **/
				case 0x04:
					Player kickedPlayer = world.findPlayer(charName);
					if(kickedPlayer != null)
					{
						if(kickedPlayer.getObjectId() == activePlayer.getObjectId())
						{
							sendPacket(SM_SYSTEM_MESSAGE.LEGION_CANT_KICK_YOURSELF());
						}
						else if(kickedPlayer.getLegionMember().getRank() == BRIGADE_GENERAL_RANK)
						{
							sendPacket(SM_SYSTEM_MESSAGE.LEGION_CANT_KICK_BRIGADE_GENERAL());
						}
						else
						{
							// TODO: Can not kick during a war!!
							kickMemberFromLegion(activePlayer.getName(), kickedPlayer, legion);
						}
					}
					else
					{
						// Player off line / does not exist? send message to player?
					}
					break;
				/** Appoint a new Brigade General **/
				case 0x05:
					Player newBrigadeGeneral = world.findPlayer(charName);
					if(newBrigadeGeneral == null)
					{
						sendPacket(SM_SYSTEM_MESSAGE.LEGION_CHANGE_MEMBER_RANK_NO_USER());
					}
					else if(activePlayer.getLegionMember().getRank() != BRIGADE_GENERAL_RANK)
					{
						sendPacket(SM_SYSTEM_MESSAGE.LEGION_CHANGE_MEMBER_RANK_DONT_HAVE_RIGHT());
					}
					else if(activePlayer.getObjectId() == newBrigadeGeneral.getObjectId())
					{
						sendPacket(SM_SYSTEM_MESSAGE.LEGION_CHANGE_MASTER_ERROR_SELF());
					}
					else
					{
						appointNewBrigadeGeneral(newBrigadeGeneral);
					}
					break;
				/** Appoint Centurion/Legionairy **/
				case 0x06:
					Player targetPlayer = world.findPlayer(charName);
					if(targetPlayer == null)
					{
						sendPacket(SM_SYSTEM_MESSAGE.LEGION_CHANGE_MEMBER_RANK_NO_USER());
					}
					else if(activePlayer.getLegionMember().getRank() != BRIGADE_GENERAL_RANK)
					{
						sendPacket(SM_SYSTEM_MESSAGE.LEGION_CHANGE_MEMBER_RANK_DONT_HAVE_RIGHT());
					}
					else if(activePlayer.getObjectId() == targetPlayer.getObjectId())
					{
						sendPacket(SM_SYSTEM_MESSAGE.LEGION_CHANGE_MEMBER_RANK_ERROR_SELF());
					}
					else
					{
						if(rank == LEGIONAIRY_RANK)
						{
							demoteToLegionary(targetPlayer);
						}
						else if(rank == CENTURION_RANK)
						{
							appointCenturion(targetPlayer);
						}
					}
					break;
				/** Refresh legion info **/
				case 0x08:
					sendPacket(new SM_LEGION_INFO(legion));
					break;
				/** Edit announcements **/
				case 0x09:
					changeAnnouncement(legion);
					break;
				/** Change self introduction **/
				case 0x0A:
					changeSelfIntro(activePlayer.getObjectId(), activePlayer.getLegionMember());
					break;
				/** Edit permissions **/
				case 0x0D:
					if(checkPermissions())
						changePermissions(legion);
					break;
				/** Level legion up **/
				case 0x0E:
					legionLevelUp(activePlayer.getInventory().getKinahItem().getItemCount(), legion);
					break;
				/** Set nickname **/
				case 0x0F:
					Player selectedPlayer = world.findPlayer(charName);
					if(selectedPlayer == null || selectedPlayer.getLegionMember().getLegion() != legion)
						// Player offline or NOT in same legion as player
						return;

					changeNickname(selectedPlayer.getObjectId(), selectedPlayer.getLegionMember());
					break;
			}
		}
		else
		{
			switch(exOpcode)
			{
				/** Create a legion **/
				case 0x00:
					Legion legion = new Legion(aionObjectsIDFactory.nextId(), legionName);

					/* Some reasons why legions can' be created */
					if(!legionService.isValidName(legion.getLegionName()))
					{
						log.debug("[CM_LEGION] Player " + activePlayer.getObjectId()
							+ " tried to create legion with an invalid name.");
						aionObjectsIDFactory.releaseId(legion.getLegionId());
						sendPacket(SM_SYSTEM_MESSAGE.LEGION_CREATE_INVALID_NAME());
						return;
					}
					else if(!legionService.isFreeName(legion.getLegionName()))
					{
						log.debug("[CM_LEGION] Player " + activePlayer.getObjectId()
							+ " tried to create legion with existing name.");
						aionObjectsIDFactory.releaseId(legion.getLegionId());
						sendPacket(SM_SYSTEM_MESSAGE.LEGION_CREATE_NAME_EXISTS());
						return;
					}
					// if(activePlayer.getDisbandLegionTime() < Config.LEGION_DISBAND_DIFFERENCE)
					// {
					// sendPacket(SM_SYSTEM_MESSAGE.LEGION_LEGION_CREATE_LAST_DAY_CHECK());
					// }
					else if(activePlayer.isLegionMember())
					{
						sendPacket(SM_SYSTEM_MESSAGE.LEGION_CREATE_ALREADY_MEMBER());
					}
					else if(activePlayer.getInventory().getKinahItem().getItemCount() < LegionConfig.LEGION_CREATE_REQUIRED_KINAH)
					{
						sendPacket(SM_SYSTEM_MESSAGE.LEGION_CREATE_NOT_ENOUGH_KINAH());
					}
					else if(legionService.storeNewLegion(legion))
					{
						log.debug("[CM_LEGION] Legion has been created!.");
						activePlayer.getInventory().decreaseKinah(LegionConfig.LEGION_CREATE_REQUIRED_KINAH);
						/** Send the packets that contain legion info to legion leader **/
						sendLegionCreationInfo(activePlayer, legion);
					}
					else
					{
						// Legion Creation Failed SYSTEM_MSG?
						// player.sendPacket(new SM_CREATE_CHARACTER(null, SM_CREATE_CHARACTER.RESPONSE_DB_ERROR));
					}
					break;
			}
		}
	}

	/**
	 * Method that will handle a invitation to a legion
	 * 
	 * @param activePlayer
	 * @param targetPlayer
	 * @param legion
	 */
	private void invitePlayerToLegion(final Player activePlayer, final Player targetPlayer, final Legion legion)
	{
		RequestResponseHandler responseHandler = new RequestResponseHandler(activePlayer){

			@Override
			public void acceptRequest(Creature requester, Player responder)
			{
				if(!targetPlayer.getCommonData().isOnline())
				{
					sendPacket(SM_SYSTEM_MESSAGE.LEGION_INCORRECT_TARGET());
				}
				else
				{
					int playerObjId = responder.getObjectId();
					responder.setLegionMember(new LegionMember(playerObjId, legion));
					if(legion.addLegionMember(playerObjId))
					{
						legionService.storeNewLegionMember(playerObjId, responder.getLegionMember());
						sendPacket(SM_SYSTEM_MESSAGE.NEW_MEMBER_JOINED(responder.getName()));

						// Tell all legion members a player joined the legion

						// Send the new legion member the required legion packets
						responder.getClientConnection().sendPacket(new SM_LEGION_TITLE(responder));
						responder.getClientConnection().sendPacket(new SM_LEGION_INFO(legion));

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
								legionMember = playerService.getPlayer(memberObjId);
								responder.getClientConnection().sendPacket(new SM_LEGIONMEMBER_INFO(legionMember));
							}
						}

						PacketSendUtility.broadcastPacket(responder, new SM_UPDATE_LEGION_TITLE(playerObjId, legion
							.getLegionId(), legion.getLegionName(), responder.getLegionMember().getRank()), true);

						/** (CURRENT ANNOUNCEMENT) Get 2 parameters from DB **/
						Entry<Integer, String> currentAnnouncement = legion.getCurrentAnnouncement();
						if(currentAnnouncement != null)
						{
							responder.getClientConnection().sendPacket(
								SM_SYSTEM_MESSAGE.LEGION_DISPLAY_ANNOUNCEMENT(currentAnnouncement.getValue(),
									currentAnnouncement.getKey(), 2));
						}
					}
					else
					{
						sendPacket(SM_SYSTEM_MESSAGE.LEGION_CAN_NOT_ADD_MEMBER_ANY_MORE());
						responder.setLegionMember(null);
					}
				}

			}

			@Override
			public void denyRequest(Creature requester, Player responder)
			{
				sendPacket(SM_SYSTEM_MESSAGE.REJECTED_INVITE_REQUEST(responder.getName()));
			}
		};

		boolean requested = targetPlayer.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_LEGION_INVITE,
			responseHandler);
		// If the player is busy and could not be asked
		if(!requested)
		{
			sendPacket(SM_SYSTEM_MESSAGE.LEGION_TARGET_BUSY());
		}
		else
		{
			sendPacket(SM_SYSTEM_MESSAGE.SEND_INVITE_REQUEST(targetPlayer.getName()));

			// Send question packet to buddy
			targetPlayer.getClientConnection().sendPacket(
				new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_LEGION_INVITE, activePlayer.getObjectId(), legion
					.getLegionName(), legion.getLegionLevel() + "", activePlayer.getName()));
		}
	}

	/**
	 * This method will handle a player that leaves a legion
	 * 
	 * @param activePlayer
	 */
	private void leaveLegion(Player activePlayer, Legion legion)
	{
		// TODO: Check if player is ONLY Brigade General in legion
		// Current state: Player can only leave when he/she is not brigade general
		if(activePlayer.getLegionMember().getRank() == BRIGADE_GENERAL_RANK)
		{
			sendPacket(SM_SYSTEM_MESSAGE.LEGION_CANT_LEAVE_BEFORE_CHANGE_MASTER());
		}
		else
		{
			PacketSendUtility.broadcastPacket(activePlayer, new SM_UPDATE_LEGION_TITLE(activePlayer.getObjectId(), 0,
				"", activePlayer.getLegionMember().getRank()), true);
			sendPacket(new SM_LEAVE_LEGION(1300241, 0, legion.getLegionName()));
			activePlayer.setLegionMember(null);
			legion.deleteLegionMember(activePlayer.getObjectId());
			legionService.deleteLegionMember(activePlayer.getObjectId());
			
			refreshMembersInfoByPacket(legion, new SM_LEAVE_LEGION(900699, activePlayer.getObjectId(), activePlayer.getName()));
		}
	}

	/**
	 * This method will handle a player that leaves a legion
	 * 
	 * @param activePlayer
	 */
	private void kickMemberFromLegion(String playerName, Player kickedPlayer, Legion legion)
	{
		PacketSendUtility.broadcastPacket(kickedPlayer, new SM_UPDATE_LEGION_TITLE(kickedPlayer.getObjectId(), 0, "",
			kickedPlayer.getLegionMember().getRank()), true);
		kickedPlayer.getClientConnection().sendPacket(new SM_LEAVE_LEGION(1300246, 0, legion.getLegionName()));
		kickedPlayer.setLegionMember(null);
		legion.deleteLegionMember(kickedPlayer.getObjectId());
		legionService.deleteLegionMember(kickedPlayer.getObjectId());

		refreshMembersInfoByPacket(legion, new SM_LEAVE_LEGION(1300247, kickedPlayer.getObjectId(), playerName, kickedPlayer.getName()));
	}

	/**
	 * This method will handle a new appointed legion leader
	 * 
	 * @param newBrigadeGeneral
	 */
	private void appointNewBrigadeGeneral(Player newBrigadeGeneral)
	{
		LegionMember legionMember = newBrigadeGeneral.getLegionMember();
		if(legionMember.getRank() > BRIGADE_GENERAL_RANK)
		{
			newBrigadeGeneral.getLegionMember().setRank(BRIGADE_GENERAL_RANK);
			legionService.storeLegionMember(newBrigadeGeneral.getObjectId(), legionMember);

			// TODO: Need proper packet!!!!
			refreshMembersInfoByPacket(legionMember.getLegion(), new SM_LEGIONMEMBER_INFO(newBrigadeGeneral));
			refreshMembersInfoByPacket(legionMember.getLegion(), SM_SYSTEM_MESSAGE
				.LEGION_CHANGE_MEMBER_RANK_DONE_1_GUILD_MASTER(newBrigadeGeneral.getName()));
		}
	}

	/**
	 * This method will handle the process when a legionary is promoted to centurion
	 * 
	 * @param newCenturion
	 */
	private void appointCenturion(Player newCenturion)
	{
		LegionMember legionMember = newCenturion.getLegionMember();
		if(legionMember.getRank() > CENTURION_RANK)
		{
			legionMember.setRank(CENTURION_RANK);
			legionService.storeLegionMember(newCenturion.getObjectId(), legionMember);

			// TODO: Need proper packet!!!!
			refreshMembersInfoByPacket(legionMember.getLegion(), new SM_LEGIONMEMBER_INFO(newCenturion));
			refreshMembersInfoByPacket(legionMember.getLegion(), SM_SYSTEM_MESSAGE
				.LEGION_CHANGE_MEMBER_RANK_DONE_2_GUILD_OFFICER(newCenturion.getName()));
		}
	}

	/**
	 * This method will handle the process when a member is demoted to Legionary
	 * 
	 * @param newCenturion
	 */
	private void demoteToLegionary(Player newLegionairy)
	{
		LegionMember legionMember = newLegionairy.getLegionMember();
		if(legionMember.getRank() < LEGIONAIRY_RANK)
		{
			legionMember.setRank(LEGIONAIRY_RANK);
			legionService.storeLegionMember(newLegionairy.getObjectId(), legionMember);

			// TODO: Need proper packet!!!!
			refreshMembersInfoByPacket(legionMember.getLegion(), new SM_LEGIONMEMBER_INFO(newLegionairy));
			refreshMembersInfoByPacket(legionMember.getLegion(), SM_SYSTEM_MESSAGE
				.LEGION_CHANGE_MEMBER_RANK_DONE_3_GUILD_MEMBER(newLegionairy.getName()));
		}
	}

	/**
	 * This will add a new announcement to the DB and change the current announcement
	 * 
	 * @param legion
	 * @param unixTime
	 * @param message
	 */
	private void changeAnnouncement(Legion legion)
	{
		if(legionService.isValidAnnouncement(announcement))
		{
			int unixTime = (int) (System.currentTimeMillis() / 1000);
			legionService.storeNewAnnouncement(legion.getLegionId(), unixTime, announcement);
			legionService.renewAnnouncementList(legion);
			sendPacket(SM_SYSTEM_MESSAGE.LEGION_WRITE_NOTICE_DONE());
			refreshMembersInfoByPacket(legion, new SM_EDIT_LEGION(0x05, unixTime, announcement));
		}
	}

	/**
	 * This method will handle the changement of a self intro
	 * 
	 * @param playerObjId
	 * @param legionMember
	 */
	private void changeSelfIntro(int playerObjId, LegionMember legionMember)
	{
		if(legionService.isValidSelfIntro(newSelfIntro))
		{
			legionMember.setSelfIntro(newSelfIntro);
			legionService.storeLegionMember(playerObjId, legionMember);
			refreshMembersInfoByPacket(legionMember.getLegion(), new SM_CHANGE_SELF_INTRODUCTION(playerObjId,
				newSelfIntro));
		}
	}

	/**
	 * This method will handle the changement of permissions
	 * 
	 * @param legion
	 */
	private void changePermissions(Legion legion)
	{
		legion.setLegionarPermissions(legionarPermission2, centurionPermission1, centurionPermission2);
		legionService.storeLegion(legion);
		refreshMembersInfoByPacket(legion, new SM_EDIT_LEGION(0x02, legion));
	}

	/**
	 * This method will handle the changement of a nickname
	 * 
	 * @param playerObjId
	 * @param legionMember
	 */
	private void changeNickname(int playerObjId, LegionMember legionMember)
	{
		if(playerService.isValidName(newNickname))
		{
			legionMember.setNickname(newNickname);
			legionService.storeLegionMember(playerObjId, legionMember);
			refreshMembersInfoByPacket(legionMember.getLegion(), new SM_CHANGE_NICKNAME(playerObjId, newNickname));
		}
	}

	/**
	 * This method will handle the leveling up of a legion
	 * 
	 * @param Legion
	 */
	private void legionLevelUp(int kinahAmount, Legion legion)
	{
		if(!legion.hasEnoughKinah(kinahAmount))
		{
			sendPacket(SM_SYSTEM_MESSAGE.LEGION_LEVEL_UP(legion.getLegionLevel()));
		}
		else if(!legion.hasRequiredMembers())
		{
			sendPacket(SM_SYSTEM_MESSAGE.LEGION_LEVEL_UP(legion.getLegionLevel()));
		}
		// else if(!legion.hasEnoughAbyssPoints(1000000))
		// {
		// sendPacket(SM_SYSTEM_MESSAGE.LEGION_LEVEL_UP(legion.getLegionLevel()));
		// }
		else
		{
			legion.setLegionLevel(legion.getLegionLevel() + 1);
			legionService.storeLegion(legion);
			refreshMembersInfoByPacket(legion, new SM_EDIT_LEGION(0x00, legion));
			refreshMembersInfoByPacket(legion, SM_SYSTEM_MESSAGE.LEGION_LEVEL_UP(legion.getLegionLevel()));
		}
	}

	/**
	 * This method will handle the creation of a legion
	 * 
	 * @param player
	 * @param legion
	 */
	private void sendLegionCreationInfo(Player activePlayer, Legion legion)
	{
		/**
		 * Create a LegionMember and bind it to a Player
		 */
		LegionMember legionMember = new LegionMember(activePlayer.getObjectId(), legion, 0x00); // Leader Rank
		activePlayer.setLegionMember(legionMember);
		legion.addLegionMember(activePlayer.getObjectId());
		legionService.storeNewLegionMember(activePlayer.getObjectId(), legionMember);

		/**
		 * Send required packets
		 */
		sendPacket(new SM_LEGION_INFO(legion));
		sendPacket(new SM_LEGION_TITLE(activePlayer));
		sendPacket(new SM_LEGION_CREATED(legion.getLegionId()));
		sendPacket(new SM_EDIT_LEGION(0x08));

		PacketSendUtility.broadcastPacket(activePlayer, new SM_UPDATE_LEGION_TITLE(activePlayer.getObjectId(), legion
			.getLegionId(), legion.getLegionName(), legionMember.getRank()), true);

		sendPacket(new SM_LEGIONMEMBER_INFO(activePlayer));
		// 7A 55 39 00 00 34 00 00 00 03 02 00
		sendPacket(SM_SYSTEM_MESSAGE.LEGION_CREATED(legion.getLegionName()));
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
	private boolean checkPermissions()
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
