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
package com.aionemu.gameserver.model.group;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.aionemu.commons.objects.filter.ObjectFilter;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_GROUP_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_GROUP_MEMBER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEAVE_GROUP_MEMBER;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.idfactory.IDFactory;

/**
 * @author ATracer, Lyahim
 *
 */
public class PlayerGroup
{
	private IDFactory aionObjectsIDFactory;
	
	private static final Logger	log	= Logger.getLogger(PlayerGroup.class);
	
	private int groupId;
	
	private LootGroupRules lootGroupRules = new LootGroupRules();
	
	private Player groupLeader;
	
	private Map<Integer, Player> groupMembers = Collections.synchronizedMap(new HashMap<Integer, Player>());

	/**
	 *  Instantiates new player group with unique groupId
	 *  
	 * @param groupId
	 */
	public PlayerGroup(IDFactory aionObjectsIDFactory, Player groupleader)
	{
		this.aionObjectsIDFactory = aionObjectsIDFactory;
		this.groupId = aionObjectsIDFactory.nextId();
		this.groupMembers.put(groupleader.getObjectId(), groupleader);
		this.setGroupLeader(groupleader);
		groupleader.setPlayerGroup(this);
		PacketSendUtility.sendPacket(groupleader, new SM_GROUP_INFO(this));
	}

	/**
	 * @return the groupId
	 */
	public int getGroupId()
	{
		return groupId;
	}

	/**
	 * @return the groupLeader
	 */
	public Player getGroupLeader()
	{
		return groupLeader;
	}

	/**
	 *  Used to set group leader
	 * 
	 * @param groupLeader the groupLeader to set
	 */
	public void setGroupLeader(Player groupLeader)
	{
		this.groupLeader = groupLeader;
		updateGroupUIToEvent(groupLeader, GroupEvent.CHANGELEADER);
	}
		
	/**
	 *  Adds player to group
	 *  
	 * @param newComer
	 */
	public void addPlayerToGroup(Player newComer)
	{
		groupMembers.put(newComer.getObjectId(), newComer);
		newComer.setPlayerGroup(this);
		PacketSendUtility.sendPacket(newComer, new SM_GROUP_INFO(this));
		updateGroupUIToEvent(newComer, GroupEvent.ENTER);
	}
	/**
	 * Update the Client user interface with the newer data
	 */
	private void updateGroupUIToEvent(Player subjective, GroupEvent groupEvent)
	{
		for(Player member : groupMembers.values())
		{
			switch(groupEvent)
			{
				case CHANGELEADER:
					PacketSendUtility.sendPacket(member, new SM_GROUP_INFO(this));
					PacketSendUtility.sendPacket(member, SM_SYSTEM_MESSAGE.CHANGE_GROUP_LEADER());
					broadcastMemberStatus(member, groupEvent);
					break;
				case LEAVE:
					broadcastMemberStatus(member, groupEvent);
					if(!member.equals(subjective))
						PacketSendUtility.sendPacket(member, SM_SYSTEM_MESSAGE.MEMBER_LEFT_GROUP(subjective.getName()));
				default:
					broadcastMemberStatus(member, groupEvent);
					break;
			}	
		}
	}
	
	/**
	 * 
	 * @param player
	 * @param event
	 */
	public void broadcastMemberStatus(Player player, GroupEvent event)
	{
		for(Player member : groupMembers.values())
		{
			if(member.getObjectId() != player.getObjectId())
			{
				PacketSendUtility.sendPacket(member, new SM_GROUP_MEMBER_INFO(this, player, event));
			}
		}
	}
	
	/**
	 *  Removes player from group
	 *  
	 * @param player
	 */
	public void removePlayerFromGroup(Player player)
	{
		if(player.equals(this.groupLeader))//if groupleader leave changeing the leader
		{
			if(size() == 1)
			{
				disbandGroup(player, false);
			}			
			else if(size() == 2) //last 2 member, if one leave, the group disband
			{
				this.updateGroupUIToEvent(player, GroupEvent.LEAVE);
				this.groupMembers.remove(player.getObjectId());
				player.setPlayerGroup(null);
				this.disbandGroup(this.groupLeader, true);
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.YOU_LEFT_GROUP());					
			}
			else
			{
				this.updateGroupUIToEvent(player, GroupEvent.LEAVE);
				this.setGroupLeader(this.groupMembers.values().iterator().next());
				this.groupMembers.remove(player.getObjectId());
				player.setPlayerGroup(null);			
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.YOU_LEFT_GROUP());					
			}
		}
		else
		{
			this.updateGroupUIToEvent(player, GroupEvent.LEAVE);
			this.groupMembers.remove(player.getObjectId());
			player.setPlayerGroup(null);
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.YOU_LEFT_GROUP());					
			if(size() == 1)
				this.disbandGroup(this.groupLeader, true);
		}
		
		PacketSendUtility.broadcastPacket(player, new SM_LEAVE_GROUP_MEMBER(),true,	new ObjectFilter<Player>(){		
			@Override
			public boolean acceptObject(Player object)
			{
				return object.getPlayerGroup() == null ? true : false;
			}
		});
	}
	
	private void disbandGroup(Player groupleader, boolean showmess)
	{
		this.aionObjectsIDFactory.releaseId(groupId);
		for(Player player : groupMembers.values())
		{
			player.setPlayerGroup(null);
			if(showmess)
				PacketSendUtility.sendPacket(groupleader, SM_SYSTEM_MESSAGE.DISBAND_GROUP());
		}
		
	}
	/**
	 *  Checks whether group is full
	 *  
	 * @return
	 */
	public boolean isFull()
	{
		return groupMembers.size() == 6;
	}
	
	/**
	 * @return count of group members
	 */
	public int size()
	{
		return groupMembers.size();
	}

	/**
	 * @return the lootGroupRules
	 */
	public LootGroupRules getLootGroupRules()
	{
		return lootGroupRules;
	}
	
	public void setLootGroupRules(LootGroupRules lgr)
	{
		this.lootGroupRules = lgr;
		for(Player member : groupMembers.values())
			PacketSendUtility.sendPacket(member, new SM_GROUP_INFO(this));	
	}
}
