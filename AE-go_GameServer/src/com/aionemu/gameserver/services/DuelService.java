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

import javolution.util.FastMap;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.DuelResult;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DUEL;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.skillengine.model.SkillTargetSlot;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * @author Simple
 * @author Sphinx :)
 */
public class DuelService
{
	@Inject
	World								world;

	private static Logger				log		= Logger.getLogger(DuelService.class);

	private FastMap<Integer, Integer>	duels	= new FastMap<Integer, Integer>();

	/**
	 * Send the duel request to the owner
	 * 
	 * @param requester
	 *            the player who requested the duel
	 * @param responder
	 *            the player who respond to duel request
	 */
	public void onDuelRequest(Player requester, Player responder)
	{
		/**
		 * Check if requester isn't already in a duel and responder is same race
		 */
		if(requester.getController().isEnemy(responder) || isDueling(requester.getObjectId()))
			return;

		RequestResponseHandler rrh = new RequestResponseHandler(requester){
			@Override
			public void denyRequest(Creature requester, Player responder)
			{
				rejectDuelRequest((Player) requester, responder);
			}

			@Override
			public void acceptRequest(Creature requester, Player responder)
			{
				startDuel((Player) requester, responder);
			}
		};
		responder.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_DUEL_DO_YOU_ACCEPT_DUEL, rrh);
		PacketSendUtility.sendPacket(responder, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_DUEL_DO_YOU_ACCEPT_DUEL,
			0, requester.getName()));
		PacketSendUtility.sendPacket(responder, SM_SYSTEM_MESSAGE.DUEL_ASKED_BY(requester.getName()));
	}

	/**
	 * Asks confirmation for the duel request
	 * 
	 * @param requester
	 *            the player whose the duel was requested
	 * @param responder
	 *            the player whose the duel was responded
	 */
	public void confirmDuelWith(Player requester, Player responder)
	{
		/**
		 * Check if requester isn't already in a duel and responder is same race
		 */
		if(requester.getController().isEnemy(responder))
			return;

		RequestResponseHandler rrh = new RequestResponseHandler(responder){
			@Override
			public void denyRequest(Creature requester, Player responder)
			{
				log.debug("[Duel] Player " + responder.getName() + " confirmed his duel with " + requester.getName());
			}

			@Override
			public void acceptRequest(Creature requester, Player responder)
			{
				cancelDuelRequest(responder, (Player) requester);
			}
		};
		requester.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_DUEL_DO_YOU_CONFIRM_DUEL, rrh);
		PacketSendUtility.sendPacket(requester, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_DUEL_DO_YOU_CONFIRM_DUEL,
			0, responder.getName()));
		PacketSendUtility.sendPacket(requester, SM_SYSTEM_MESSAGE.DUEL_ASKED_TO(responder.getName()));
	}

	/**
	 * Rejects the duel request
	 * 
	 * @param requester
	 *            the duel requester
	 * @param responder
	 *            the duel responder
	 */
	private void rejectDuelRequest(Player requester, Player responder)
	{
		log.debug("[Duel] Player " + responder.getName() + " rejected duel request from " + requester.getName());
		PacketSendUtility.sendPacket(requester, SM_SYSTEM_MESSAGE.DUEL_REJECTED_BY(responder.getName()));
		PacketSendUtility.sendPacket(responder, SM_SYSTEM_MESSAGE.DUEL_REJECT_DUEL_OF(requester.getName()));
	}

	/**
	 * Cancels the duel request
	 * 
	 * @param target
	 *            the duel target
	 * @param requester
	 */
	private void cancelDuelRequest(Player owner, Player target)
	{
		log.debug("[Duel] Player " + owner.getName() + " cancelled his duel request with " + target.getName());
		PacketSendUtility.sendPacket(target, SM_SYSTEM_MESSAGE.DUEL_CANCEL_DUEL_BY(owner.getName()));
		PacketSendUtility.sendPacket(owner, SM_SYSTEM_MESSAGE.DUEL_CANCEL_DUEL_WITH(target.getName()));
	}

	/**
	 * Starts the duel
	 * 
	 * @param requester
	 *            the player to start duel with
	 * @param responder
	 *            the other player
	 */
	private void startDuel(Player requester, Player responder)
	{
		PacketSendUtility.sendPacket(requester, SM_DUEL.SM_DUEL_STARTED(responder.getObjectId()));
		PacketSendUtility.sendPacket(responder, SM_DUEL.SM_DUEL_STARTED(requester.getObjectId()));
		createDuel(requester.getObjectId(), responder.getObjectId());
	}

	/**
	 * This method will make the selected player lose the duel
	 * 
	 * @param player
	 */
	public void loseDuel(Player player)
	{
		if(!isDueling(player.getObjectId()))
			return;

		/**
		 * all debuffs are removed from looser
		 */
		player.getEffectController().removeAbnormalEffectsByTargetSlot(SkillTargetSlot.DEBUFF);
		
		int opponnentId = duels.get(player.getObjectId());
		Player opponent = world.findPlayer(opponnentId);

		if(opponent != null)
		{
			/**
			 * all debuffs are removed from winner, but buffs will remain
			 */
			opponent.getEffectController().removeAbnormalEffectsByTargetSlot(SkillTargetSlot.DEBUFF);
			PacketSendUtility.sendPacket(opponent, SM_DUEL.SM_DUEL_RESULT(DuelResult.DUEL_WON, player.getName()));
			PacketSendUtility.sendPacket(player, SM_DUEL.SM_DUEL_RESULT(DuelResult.DUEL_LOST, opponent.getName()));
		}
		else
		{
			log.warn("CHECKPOINT : duel opponent is already out of world");
		}

		removeDuel(player.getObjectId(), opponnentId);
	}

	/**
	 * @param player
	 * @param lastAttacker
	 */
	public void onDie(Player player, Player lastAttacker)
	{
		loseDuel(player);
		player.getLifeStats().setCurrentHp(1);
		player.getLifeStats().triggerHpMpRestoreTask();
	}

	/**
	 * @param playerObjId
	 * @return true of player is dueling
	 */
	public boolean isDueling(int playerObjId)
	{
		return (duels.containsKey(playerObjId) && duels.containsValue(playerObjId));
	}

	/**
	 * @param playerObjId
	 * @param targetObjId
	 * @return true of player is dueling
	 */
	public boolean isDueling(int playerObjId, int targetObjId)
	{
		return duels.containsKey(playerObjId) && duels.get(playerObjId) == targetObjId;
	}

	/**
	 * @param requesterObjId
	 * @param responderObjId
	 */
	private void createDuel(int requesterObjId, int responderObjId)
	{
		duels.put(requesterObjId, responderObjId);
		duels.put(responderObjId, requesterObjId);
	}

	/**
	 * @param requesterObjId
	 * @param responderObjId
	 */
	private void removeDuel(int requesterObjId, int responderObjId)
	{
		duels.remove(requesterObjId);
		duels.remove(responderObjId);
	}
}
