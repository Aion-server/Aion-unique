/*
 * This file is part of aion-unique <aion-unique.com>.
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
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.gameobjects.stats;

import com.aionemu.commons.callbacks.EnhancedObject;
import com.aionemu.gameserver.dataholders.PlayerStatsData;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.stats.listeners.StatChangeListener;
import com.aionemu.gameserver.model.templates.stats.PlayerStatsTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author xavier
 * 
 */
public class PlayerGameStats extends CreatureGameStats<Player>
{
	/**
	 * 
	 * @param owner
	 */
	public PlayerGameStats(Player owner)
	{
		super(owner);
	}

	/**
	 * 
	 * @param playerStatsData
	 * @param owner
	 */
	public PlayerGameStats(PlayerStatsData playerStatsData, final Player owner)
	{
		super(owner);
		PlayerStatsTemplate pst = playerStatsData.getTemplate(owner.getPlayerClass(), owner.getLevel());
		initStats(pst, owner.getLevel());
		log.debug("loading base game stats for player " + owner.getName() + " (id " + owner.getObjectId() + "): "
			+ this);

		addRecomputeListener(owner);
	}

	/**
	 *  Adds listener that will check SPEED or FLY_SPEED changes
	 *  
	 * @param owner
	 */
	private void addRecomputeListener(final Player owner)
	{
		((EnhancedObject)this).addCallback(new StatChangeListener(this){	
			
			private int currentRunSpeed = 0;
			private int currentFlySpeed = 0;
			
			@Override
			protected void onRecompute()
			{
				int newRunSpeed = gameStats.getCurrentStat(StatEnum.SPEED);
				int newFlySpeed = gameStats.getCurrentStat(StatEnum.FLY_SPEED);
				
				if(newRunSpeed != currentRunSpeed || currentFlySpeed != newFlySpeed)
				{
					PacketSendUtility.sendPacket(owner, new SM_EMOTION(owner, 30, 0, 0));					
				}	
				
				PacketSendUtility.sendPacket(owner, new SM_STATS_INFO(owner));
				
				this.currentRunSpeed = newRunSpeed;
				this.currentFlySpeed = newFlySpeed;
			}
		});
	}

	/**
	 * 
	 * @param pst
	 * @param level
	 */
	private void initStats(PlayerStatsTemplate pst, int level)
	{
		super.initStats(pst.getMaxHp(), pst.getMaxMp(), pst.getPower(), pst.getHealth(), pst.getAgility(), pst
			.getAccuracy(), pst.getKnowledge(), pst.getWill(), pst.getMainHandAttack(), pst.getMainHandCritRate(), Math
			.round(pst.getAttackSpeed() * 1000), 1500, Math.round(pst.getRunSpeed() * 1000), Math.round(pst.getFlySpeed() * 1000));
		setAttackCounter(1);
		initStat(StatEnum.PARRY, pst.getParry());
		initStat(StatEnum.BLOCK, pst.getBlock());
		initStat(StatEnum.EVASION, pst.getEvasion());
		initStat(StatEnum.MAGICAL_ACCURACY, pst.getMagicAccuracy());
		initStat(StatEnum.MAIN_HAND_ACCURACY, pst.getMainHandAccuracy());
		// TODO find good values for fly time
		initStat(StatEnum.FLY_TIME, 60);
		initStat(StatEnum.REGEN_HP, level + 3);
		initStat(StatEnum.REGEN_MP, level + 8);
		initStat(StatEnum.MAXDP, 4000);

	}

	/**
	 * 
	 * @param playerStatsData
	 * @param level
	 */
	public void doLevelUpgrade (PlayerStatsData playerStatsData, int level) {
		PlayerStatsTemplate pst = playerStatsData.getTemplate(((Player)getOwner()).getPlayerClass(), level);
		initStats(pst, level);
		recomputeStats();
	}
}
