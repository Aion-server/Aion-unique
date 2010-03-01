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
	public void doReward(Player victim, Player winner)
	{
		int pointsLost = Math.round(victim.getAbyssRank().getRank().getPointsLost() * victim.getRates().getApRate());
		int pointsGained = Math.round(victim.getAbyssRank().getRank().getPointsGained() * winner.getRates().getApRate());
		
		victim.getAbyssRank().addAp(-pointsLost);
		PacketSendUtility.broadcastPacket(victim, new SM_ABYSS_RANK_UPDATE(victim));
		winner.getAbyssRank().addAp(pointsGained);
		if(winner.isLegionMember())
			legionService.addContributionPoints(winner.getLegion(), pointsGained);
		PacketSendUtility.broadcastPacket(winner, new SM_ABYSS_RANK_UPDATE(winner));
		winner.getAbyssRank().setAllKill();
		PacketSendUtility.sendPacket(victim, new SM_ABYSS_RANK(victim.getAbyssRank()));
		PacketSendUtility.sendPacket(winner, new SM_ABYSS_RANK(winner.getAbyssRank()));
		PacketSendUtility.sendPacket(winner, SM_SYSTEM_MESSAGE.EARNED_ABYSS_POINT(String.valueOf(pointsGained)));
		PacketSendUtility.sendPacket(victim, new SM_SYSTEM_MESSAGE(1340002, winner.getName()));
	}
	
	/**
	 * 
	 * @param victim
	 * @param winner
	 */
	public void doReward(Creature victim, Player winner)
	{
		int apReward = StatFunctions.calculateSoloAPReward(winner, victim);
		
		winner.getAbyssRank().addAp(apReward);
		if(winner.isLegionMember())
			legionService.addContributionPoints(winner.getLegion(), apReward);
		PacketSendUtility.broadcastPacket(winner, new SM_ABYSS_RANK_UPDATE(winner));
		winner.getAbyssRank().setAllKill();
		PacketSendUtility.sendPacket(winner, new SM_ABYSS_RANK(winner.getAbyssRank()));
		PacketSendUtility.sendPacket(winner, SM_SYSTEM_MESSAGE.EARNED_ABYSS_POINT(String.valueOf(apReward)));
	}
}
