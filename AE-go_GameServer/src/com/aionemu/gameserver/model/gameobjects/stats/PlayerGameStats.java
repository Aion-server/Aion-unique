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
import com.aionemu.gameserver.configs.main.CustomConfig;
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
	 *  Adds listener that will check SPEED, FLY_SPEED or ATTACK_SPEED changes
	 *  
	 * @param owner
	 */
	private void addRecomputeListener(final Player owner)
	{
		((EnhancedObject)this).addCallback(new StatChangeListener(this){	
			
			private int currentRunSpeed = 0;
			private int currentFlySpeed = 0;
			private int currentAttackSpeed = 0;
			
			@Override
			protected void onRecompute()
			{
				int newRunSpeed = gameStats.getCurrentStat(StatEnum.SPEED);
				int newFlySpeed = gameStats.getCurrentStat(StatEnum.FLY_SPEED);
				int newAttackSpeed = gameStats.getCurrentStat(StatEnum.ATTACK_SPEED);

				if(newRunSpeed != currentRunSpeed || currentFlySpeed != newFlySpeed || newAttackSpeed != currentAttackSpeed)
				{
					PacketSendUtility.broadcastPacket(owner, new SM_EMOTION(owner, 30, 0, 0), true);
				}	
				
				PacketSendUtility.sendPacket(owner, new SM_STATS_INFO(owner));
				
				this.currentRunSpeed = newRunSpeed;
				this.currentFlySpeed = newFlySpeed;
				this.currentAttackSpeed = newAttackSpeed;
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
		lock.writeLock().lock();
		try
		{
			this.initStats(pst.getMaxHp(), pst.getMaxMp(), pst.getPower(), pst.getHealth(), pst.getAgility(), pst
				.getAccuracy(), pst.getKnowledge(), pst.getWill(), pst.getMainHandAttack(), pst.getMainHandCritRate(), Math
				.round(pst.getAttackSpeed() * 1000), 1500, Math.round(pst.getRunSpeed() * 1000), Math.round(pst.getFlySpeed() * 1000));
			setAttackCounter(1);
			initStat(StatEnum.PARRY, pst.getParry());
			initStat(StatEnum.BLOCK, pst.getBlock());
			initStat(StatEnum.EVASION, pst.getEvasion());
			initStat(StatEnum.MAGICAL_ACCURACY, pst.getMagicAccuracy());
			initStat(StatEnum.MAIN_HAND_ACCURACY, pst.getMainHandAccuracy());
			initStat(StatEnum.FLY_TIME, CustomConfig.BASE_FLYTIME);
			initStat(StatEnum.REGEN_HP, level + 3);
			initStat(StatEnum.REGEN_MP, level + 8);
			initStat(StatEnum.MAXDP, 4000);
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	/**
	 * 
	 * @param maxHp
	 * @param maxMp
	 * @param power
	 * @param health
	 * @param agility
	 * @param accuracy
	 * @param knowledge
	 * @param will
	 * @param mainHandAttack
	 * @param mainHandCritRate
	 * @param attackSpeed
	 * @param attackRange
	 * @param runSpeed
	 * @param flySpeed
	 */
	protected void initStats(int maxHp, int maxMp, int power, int health, int agility, int accuracy, int knowledge, int will, int mainHandAttack, int mainHandCritRate, int attackSpeed, int attackRange, int runSpeed, int flySpeed)
	{
		stats.clear();
		initStat(StatEnum.MAXHP, maxHp);
		initStat(StatEnum.MAXMP, maxMp);
		initStat(StatEnum.POWER, power);
		initStat(StatEnum.ACCURACY, accuracy);
		initStat(StatEnum.HEALTH, health);
		initStat(StatEnum.AGILITY, agility);
		initStat(StatEnum.KNOWLEDGE, knowledge);
		initStat(StatEnum.WILL, will);
		initStat(StatEnum.MAIN_HAND_POWER, Math.round(18 * (power * 0.01f)));
		initStat(StatEnum.MAIN_HAND_CRITICAL, mainHandCritRate);
		initStat(StatEnum.OFF_HAND_POWER, 0);
		initStat(StatEnum.OFF_HAND_CRITICAL, 0);
		initStat(StatEnum.ATTACK_SPEED, attackSpeed);
		initStat(StatEnum.MAIN_HAND_ATTACK_SPEED, attackSpeed);
		initStat(StatEnum.OFF_HAND_ATTACK_SPEED, 0);
		initStat(StatEnum.ATTACK_RANGE, attackRange);
		initStat(StatEnum.PHYSICAL_DEFENSE, 0);
		initStat(StatEnum.PARRY, Math.round(agility * 3.1f - 248.5f + 12.4f * (int)owner.getLevel()));
		initStat(StatEnum.EVASION, Math.round(agility * 3.1f - 248.5f + 12.4f * (int)owner.getLevel()));
		initStat(StatEnum.BLOCK, Math.round(agility * 3.1f - 248.5f + 12.4f * (int)owner.getLevel()));
		initStat(StatEnum.DAMAGE_REDUCE, 0);
		initStat(StatEnum.MAIN_HAND_ACCURACY, Math.round((accuracy * 2 - 10) + 8 * (int)owner.getLevel()));
		initStat(StatEnum.OFF_HAND_ACCURACY, Math.round((accuracy * 2 - 10) + 8 * (int)owner.getLevel()));
		initStat(StatEnum.MAGICAL_RESIST, 0);
		initStat(StatEnum.WIND_RESISTANCE, 0);
		initStat(StatEnum.FIRE_RESISTANCE, 0);
		initStat(StatEnum.WATER_RESISTANCE, 0);
		initStat(StatEnum.EARTH_RESISTANCE, 0);
		initStat(StatEnum.MAGICAL_ACCURACY, Math.round(14.26f * (int)owner.getLevel()));
		initStat(StatEnum.BOOST_MAGICAL_SKILL, 0);
		initStat(StatEnum.SPEED, runSpeed);
		initStat(StatEnum.FLY_SPEED, flySpeed);
	}

	/**
	 * 
	 * @param playerStatsData
	 * @param level
	 */
	public void doLevelUpgrade()
	{
		initStats(owner.getPlayerStatsTemplate(), owner.getLevel());
		recomputeStats();
	}
}
