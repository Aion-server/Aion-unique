/*
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
package com.aionemu.gameserver.controllers;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.stats.NpcLifeStats;
import com.aionemu.gameserver.model.templates.stats.StatsTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LOOT_STATUS;
import com.aionemu.gameserver.services.DecayService;
import com.aionemu.gameserver.services.RespawnService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * This class is for controlling Npc's
 * 
 * @author -Nemesiss-, ATracer (2009-09-29)
 */
public class NpcController extends CreatureController<Npc>
{
	
	private static Logger log = Logger.getLogger(NpcController.class);

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.controllers.CreatureController#onDie()
	 */
	@Override
	public void onDie()
	{
		super.onDie();
		RespawnService.getInstance().scheduleRespawnTask(this.getOwner());
		DecayService.getInstance().scheduleDecayTask(this.getOwner());
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.controllers.CreatureController#onRespawn()
	 */
	@Override
	public void onRespawn()
	{
		super.onRespawn();
		StatsTemplate statsTemplate = getOwner().getTemplate().getStatsTemplate();
		this.getOwner().setLifeStats(new NpcLifeStats(statsTemplate.getMaxHp(),
			statsTemplate.getMaxMp(), statsTemplate.getMaxHp(), statsTemplate.getMaxMp()));
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.controllers.CreatureController#onAttack(com.aionemu.gameserver.model.gameobjects.Creature)
	 */
	@Override
	public boolean onAttack(Creature creature)
	{
		super.onAttack(creature);
		
		Npc npc = getOwner();
		NpcLifeStats lifeStats = npc.getLifeStats();
		
		//TODO resolve synchronization issue
		if(!lifeStats.isAlive())
		{
			//TODO send action failed packet
			return false;
		}

		int newHp = lifeStats.reduceHp(100);
		int hpPercentage = Math.round(100 *  newHp / lifeStats.getMaxHp());
		
		PacketSendUtility.broadcastPacket(npc, new SM_ATTACK_STATUS(npc.getObjectId(), hpPercentage));
		
		if(newHp == 0)
		{
			PacketSendUtility.broadcastPacket(npc, new SM_EMOTION(this.getOwner().getObjectId(), 13 , creature.getObjectId()));
			this.doDrop();
			this.doReward(creature);
			this.onDie();
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.controllers.CreatureController#doDrop()
	 */
	@Override
	public void doDrop()
	{
		super.doDrop();
		PacketSendUtility.broadcastPacket(this.getOwner(), new SM_LOOT_STATUS(this.getOwner().getObjectId(), 0));
	}

	/* (non-Javadoc)
	 * @see com.aionemu.gameserver.controllers.CreatureController#doReward()
	 */
	@Override
	public void doReward(Creature creature)
	{
		super.doReward(creature);
		
		if(creature instanceof Player)
		{
			Player player = (Player) creature;
			//TODO may be introduce increaseExpBy method in PlayerCommonData
			long currentExp = player.getCommonData().getExp();
			//TODO calculate exp reward
			player.getCommonData().setExp(currentExp + 100);
		}
	}
}
