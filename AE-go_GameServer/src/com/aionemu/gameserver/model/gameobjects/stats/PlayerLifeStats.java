/*
 * This file is part of aion-unique <aion-unique.com>.
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
package com.aionemu.gameserver.model.gameobjects.stats;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.group.GroupEvent;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATUPDATE_HP;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATUPDATE_MP;
import com.aionemu.gameserver.taskmanager.tasks.PacketBroadcaster.BroadcastMode;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 *
 */
public class PlayerLifeStats extends CreatureLifeStats<Player>
{

	public PlayerLifeStats(Player owner, int currentHp, int currentMp)
	{
		super(owner,currentHp,currentMp);
	}

	@Override
	protected void onReduceHp()
	{
		sendHpPacketUpdate();
		triggerRestoreTask();
		sendGroupPacketUpdate();	
	}

	@Override
	protected void onReduceMp()
	{
		sendMpPacketUpdate();		
		triggerRestoreTask();
		sendGroupPacketUpdate();
	}
	
	@Override
	protected void onIncreaseMp()
	{
		sendMpPacketUpdate();
		sendGroupPacketUpdate();
	}
	
	
	@Override
	protected void onIncreaseHp()
	{
		sendHpPacketUpdate();
		sendAttackStatusPacketUpdate();
		sendGroupPacketUpdate();
	}
	
	private void sendGroupPacketUpdate()
	{
		Player owner = getOwner();
		if(owner.getPlayerGroup() != null)
		{
			owner.getPlayerGroup().updateGroupUIToEvent(owner, GroupEvent.UNK);
		}
	}

	@Override
	public Player getOwner()
	{
		return (Player) super.getOwner();
	}

	@Override
	public void restoreHp()
	{
		int currentRegenHp = getOwner().getGameStats().getCurrentStat(StatEnum.REGEN_HP);
		if(getOwner().isInState(CreatureState.RESTING))
			currentRegenHp *= 8;
		increaseHp(currentRegenHp);
	}

	@Override
	public void restoreMp()
	{
		int currentRegenMp = getOwner().getGameStats().getCurrentStat(StatEnum.REGEN_MP);
		if(getOwner().isInState(CreatureState.RESTING))
			currentRegenMp *= 8;
		increaseMp(currentRegenMp);
	}
	
	public void sendHpPacketUpdate()
	{
		owner.addPacketBroadcastMask(BroadcastMode.UPDATE_PLAYER_HP_STAT);
	}
	
	public void sendHpPacketUpdateImpl()
	{
		if(owner == null)
			return;
		
		PacketSendUtility.sendPacket((Player) owner, new SM_STATUPDATE_HP(currentHp, getMaxHp()));
	}
	
	public void sendMpPacketUpdate()
	{
		owner.addPacketBroadcastMask(BroadcastMode.UPDATE_PLAYER_MP_STAT);
	}
	
	public void sendMpPacketUpdateImpl()
	{
		if(owner == null)
			return;

		PacketSendUtility.sendPacket((Player) owner, new SM_STATUPDATE_MP(currentMp, getMaxMp()));
	}
}
