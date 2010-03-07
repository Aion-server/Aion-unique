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
import com.aionemu.gameserver.model.gameobjects.stats.PlayerLifeStats;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DUEL;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Simple
 * @author Sphinx :)
 */
public class DuelService
{
	private static Logger				log		= Logger.getLogger(DuelService.class);

	private FastMap<Integer, Integer>	duels	= new FastMap<Integer, Integer>();

	/**
	 * Send the duel request to the owner
	 * 
	 * @param requester
	 *            the player who requested the duel
	 */
	public void onDuelRequest(Player requester, Player responder)
	{
		log.debug("[Duel] Player " + responder.getName() + " has been requested for a duel by " + requester.getName());
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
			requester.getObjectId(), requester.getName()));
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
			responder.getObjectId(), responder.getName()));
		PacketSendUtility.sendPacket(requester, SM_SYSTEM_MESSAGE.DUEL_ASKED_TO(responder.getName()));
	}

	/**
	 * Rejects the duel request
	 * 
	 * @param requester
	 *            the duel requester
	 * @param responder
	 */
	public void rejectDuelRequest(Player requester, Player responder)
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
	public void cancelDuelRequest(Player owner, Player target)
	{
		log.debug("[Duel] Player " + owner.getName() + " cancelled his duel request with " + target.getName());
		target.getClientConnection().sendPacket(SM_SYSTEM_MESSAGE.DUEL_CANCEL_DUEL_BY(owner.getName()));
		owner.getClientConnection().sendPacket(SM_SYSTEM_MESSAGE.DUEL_CANCEL_DUEL_WITH(target.getName()));
	}

	/**
	 * Starts the duel
	 * 
	 * @param requester
	 *            the player to start duel with
	 * @param responder
	 *            the other player
	 */
	public void startDuel(Player requester, Player responder)
	{
		PacketSendUtility.sendPacket(requester, SM_DUEL.SM_DUEL_STARTED(responder.getObjectId()));
		PacketSendUtility.sendPacket(responder, SM_DUEL.SM_DUEL_STARTED(requester.getObjectId()));
		requester.getController().setLastAttacker(responder);
		responder.getController().setLastAttacker(requester);
		duels.put(responder.getObjectId(), requester.getObjectId());
		duels.put(requester.getObjectId(), responder.getObjectId());
	}

	/**
	 * Won the duel
	 * 
	 * @param winner
	 *            the other player
	 * @param loserObjId
	 *            the loser's object id
	 * @param loserName
	 *            the loser's name
	 */
	public void wonDuelWith(Player winner, int loserObjId, String loserName)
	{
		log.debug("[Duel] Player " + winner.getName() + " won duel against " + loserName);
		PacketSendUtility.sendPacket(winner, SM_DUEL.SM_DUEL_RESULT(DuelResult.DUEL_WON, loserName));
		duels.remove(winner.getObjectId());
		duels.remove(loserObjId);
	}

	/**
	 * Lost the duel
	 * 
	 * @param winnerName
	 *            the other player name
	 * @param loser
	 *            the losing player
	 */
	public void lostDuelWith(String winnerName, Player loser)
	{
		log.debug("[Duel] Player " + loser.getName() + " lost duel against " + winnerName);
		PacketSendUtility.sendPacket(loser, SM_DUEL.SM_DUEL_RESULT(DuelResult.DUEL_LOST, winnerName));
		PlayerLifeStats pls = loser.getLifeStats();
		loser.setLifeStats(new PlayerLifeStats(loser, 1, pls.getCurrentMp()));
		loser.getLifeStats().triggerRestoreTask();
	}

	/**
	 * @param player
	 * @param lastAttacker
	 */
	public void onDie(Player player, Player lastAttacker)
	{
		lostDuelWith(lastAttacker.getName(), player);
		wonDuelWith(lastAttacker, player.getObjectId(), player.getName());
	}

	/**
	 * @param playerObjId
	 * @return true of player is dueling
	 */
	public boolean isDueling(int playerObjId)
	{
		return duels.containsKey(playerObjId);
	}
}
