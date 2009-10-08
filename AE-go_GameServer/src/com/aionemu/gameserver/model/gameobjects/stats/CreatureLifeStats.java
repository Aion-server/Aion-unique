/*
 * This file is part of aion-unique <aion-unique.smfnew.com>.
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
package com.aionemu.gameserver.model.gameobjects.stats;

import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS;
import com.aionemu.gameserver.services.LifeStatsRestoreService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 *
 */
public class CreatureLifeStats<T extends Creature>
{
	private static final Logger log = Logger.getLogger(CreatureLifeStats.class);
	
	private int currentHp;
	
	private int currentMp;
	
	private int maxHp;
	
	private int maxMp;
	
	private boolean alreadyDead = false;
	
	private Creature owner;
	
	private Future<?> lifeRestoreTask;

	public CreatureLifeStats(int currentHp, int currentMp, int maxHp, int maxMp)
	{
		super();
		this.currentHp = currentHp;
		this.currentMp = currentMp;
		this.maxHp = maxHp;
		this.maxMp = maxMp;
	}
	
	/**
	 * @param owner
	 */
	public void setOwner(Creature owner)
	{
		this.owner = owner;
	}

	/**
	 * @return the owner
	 */
	public Creature getOwner()
	{
		return owner;
	}

	/**
	 * @return the currentHp
	 */
	public int getCurrentHp()
	{
		return currentHp;
	}

	/**
	 * @param currentHp the currentHp to set
	 */
	public void setCurrentHp(int currentHp)
	{
		this.currentHp = currentHp;
	}

	/**
	 * @return the currentMp
	 */
	public int getCurrentMp()
	{
		return currentMp;
	}

	/**
	 * @param currentMp the currentMp to set
	 */
	public void setCurrentMp(int currentMp)
	{
		this.currentMp = currentMp;
	}

	/**
	 * @return the maxHp
	 */
	public int getMaxHp()
	{
		return maxHp;
	}

	/**
	 * @param maxHp the maxHp to set
	 */
	public void setMaxHp(int maxHp)
	{
		this.maxHp = maxHp;
	}

	/**
	 * @return the maxMp
	 */
	public int getMaxMp()
	{
		return maxMp;
	}

	/**
	 * @param maxMp the maxMp to set
	 */
	public void setMaxMp(int maxMp)
	{
		this.maxMp = maxMp;
	}
	
	/**
	 * @return the alreadyDead
	 * There is no setter method cause life stats should be completely renewed on revive
	 */
	public boolean isAlreadyDead()
	{
		return alreadyDead;
	}

	/**
	 *  This method is called whenever caller wants to absorb creatures's HP
	 * @param value
	 * @return
	 */
	public int reduceHp(int value)
	{
		synchronized(this)
		{
			int newHp = this.currentHp - value;
			
			if(newHp < 0)
			{
				this.currentHp = 0;
				if(!alreadyDead)
				{
					alreadyDead = true;
				}			
			}
			this.currentHp = newHp;
			
			sendHpPacketUpdate();
			
			if(alreadyDead)
			{
				getOwner().getController().onDie();	
			}
		}	
		
		if(lifeRestoreTask == null && !alreadyDead)
		{
			this.lifeRestoreTask = LifeStatsRestoreService.getInstance().scheduleRestoreTask(this);
		}
		
		return currentHp;
	}
	/**
	 * Informs about HP change
	 */
	private void sendHpPacketUpdate()
	{
		if(owner == null)
		{
			return;
		}
		int hpPercentage = Math.round(100 *  currentHp / maxHp);
		PacketSendUtility.broadcastPacket(owner, new SM_ATTACK_STATUS(getOwner().getObjectId(), hpPercentage));
		if(owner instanceof Player)
		{
			PacketSendUtility.sendPacket((Player) owner, new SM_ATTACK_STATUS(getOwner().getObjectId(), hpPercentage));
		}
	}
	
	/**
	 *  This method is called whenever caller wants to restore creatures's HP
	 * @param value
	 * @return
	 */
	public int increaseHp(int value)
	{
		synchronized(this)
		{
			if(isAlreadyDead())
			{
				return 0;
			}
			int newHp = this.currentHp + value;
			if(newHp > maxHp)
			{
				newHp = maxHp;
			}
			this.currentHp = newHp;		
		}		
		
		sendHpPacketUpdate();
		
		return currentHp;
	}
	
	public void cancelRestoreTask()
	{
		if(lifeRestoreTask != null && !lifeRestoreTask.isCancelled())
		{
			lifeRestoreTask.cancel(false);
			this.lifeRestoreTask = null;
		}
	}
}
