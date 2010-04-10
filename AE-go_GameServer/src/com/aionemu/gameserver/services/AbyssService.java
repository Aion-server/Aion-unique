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

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ABYSS_RANK;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ABYSS_RANK_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.stats.StatFunctions;
import com.google.inject.Inject;

/**
 * @author ATracer
 *
 */
public class AbyssService
{
	@Inject
	private LegionService legionService;
	
	/**
	 *  Very basic method for now
	 *  
	 * @param victim
	 * @param winner
	 */
	public void doReward(Player defeated, Player winner)
	{
		int pointsGained = Math.round(winner.getAbyssRank().getRank().getPointsGained() * winner.getRates().getApPlayerRate());
		int pointsLost = Math.round(defeated.getAbyssRank().getRank().getPointsLost() * defeated.getRates().getApPlayerRate());
		
		// Level penalty calc
		int difference = winner.getLevel() - defeated.getLevel();
		
		switch(difference)
		{
			case 3:
				pointsGained = Math.round(pointsGained * 0.85f);
				pointsLost = Math.round(pointsLost * 0.85f);
				break;
			case 4:
				pointsGained = Math.round(pointsGained * 0.65f);
				pointsLost = Math.round(pointsLost * 0.65f);
				break;
			case -2:
				pointsGained =  Math.round(pointsGained * 1.1f);
				break;
			case -3:
				pointsGained =  Math.round(pointsGained * 1.2f);
				break;
		}
		
		if(difference > 4)
		{
			pointsGained = Math.round(pointsGained * 0.1f);
			pointsLost = Math.round(pointsLost * 0.1f);
		}
		
		if(difference < -3)
		{
			pointsGained = Math.round(pointsGained * 1.3f);
		}
		
		// Abyss rank penalty calc
		int winnerAbyssRank = winner.getAbyssRank().getRank().getId();
		int defeatedAbyssRank = defeated.getAbyssRank().getRank().getId();
		int abyssRankDifference = winnerAbyssRank - defeatedAbyssRank;
		
		if(winnerAbyssRank <= 7 && abyssRankDifference > 0)
		{
			float penaltyPercent = abyssRankDifference * 0.05f;			
			
			pointsGained -= Math.round(pointsGained * penaltyPercent);
		}
		
		// AP farm protection
		if(defeated.getAbyssRank().getAp() < pointsGained)
			pointsGained = StatFunctions.calculateSoloAPReward(winner, defeated);
		
		int oldWinnerAbyssRank = winner.getAbyssRank().getRank().getId();
		int oldDefeatedAbyssRank = defeated.getAbyssRank().getRank().getId();
		
		defeated.getAbyssRank().addAp(-pointsLost);
		if(defeated.getAbyssRank().getRank().getId() != oldDefeatedAbyssRank)
			PacketSendUtility.broadcastPacket(defeated, new SM_ABYSS_RANK_UPDATE(defeated));
		
		winner.getAbyssRank().addAp(pointsGained);
		if(winner.isLegionMember())
			legionService.addContributionPoints(winner.getLegion(), pointsGained);
		
		if(winner.getAbyssRank().getRank().getId() != oldWinnerAbyssRank)
			PacketSendUtility.broadcastPacket(winner, new SM_ABYSS_RANK_UPDATE(winner));
		
		winner.getAbyssRank().setAllKill();
		PacketSendUtility.sendPacket(defeated, new SM_ABYSS_RANK(defeated.getAbyssRank()));
		PacketSendUtility.sendPacket(winner, new SM_ABYSS_RANK(winner.getAbyssRank()));
		PacketSendUtility.sendPacket(winner, SM_SYSTEM_MESSAGE.EARNED_ABYSS_POINT(String.valueOf(pointsGained)));
		PacketSendUtility.sendPacket(defeated, new SM_SYSTEM_MESSAGE(1340002, winner.getName()));
	}
	
	/**
	 * 
	 * @param victim
	 * @param winner
	 */
	public void doReward(Creature victim, Player winner)
	{
		int apReward = StatFunctions.calculateSoloAPReward(winner, victim);
		
		int oldWinnerAbyssRank = winner.getAbyssRank().getRank().getId();
		
		winner.getAbyssRank().addAp(apReward);
		if(winner.isLegionMember())
			legionService.addContributionPoints(winner.getLegion(), apReward);
		
		if(winner.getAbyssRank().getRank().getId() != oldWinnerAbyssRank)
			PacketSendUtility.broadcastPacket(winner, new SM_ABYSS_RANK_UPDATE(winner));
		
		winner.getAbyssRank().setAllKill();
		PacketSendUtility.sendPacket(winner, new SM_ABYSS_RANK(winner.getAbyssRank()));
		PacketSendUtility.sendPacket(winner, SM_SYSTEM_MESSAGE.EARNED_ABYSS_POINT(String.valueOf(apReward)));
	}
	
	public void doReward(Player winner, int apReward)
	{
		winner.getAbyssRank().addAp(apReward);
		if(winner.isLegionMember())
			legionService.addContributionPoints(winner.getLegion(), apReward);
		PacketSendUtility.broadcastPacket(winner, new SM_ABYSS_RANK_UPDATE(winner));
		winner.getAbyssRank().setAllKill();
		PacketSendUtility.sendPacket(winner, new SM_ABYSS_RANK(winner.getAbyssRank()));
		PacketSendUtility.sendPacket(winner, SM_SYSTEM_MESSAGE.EARNED_ABYSS_POINT(String.valueOf(apReward)));
	}
}
