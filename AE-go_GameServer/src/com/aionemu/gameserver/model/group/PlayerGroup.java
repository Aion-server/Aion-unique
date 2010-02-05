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

import java.util.Collection;
import java.util.Iterator;

import javolution.util.FastMap;

import org.apache.log4j.Logger;

import com.aionemu.commons.objects.filter.ObjectFilter;
import com.aionemu.commons.utils.Rnd;
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
	
	private FastMap<Integer, Player> groupMembers = new FastMap<Integer, Player>().setShared(true);

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
	public void updateGroupUIToEvent(Player subjective, GroupEvent groupEvent)
	{
			switch(groupEvent)
			{
				case CHANGELEADER:
				{
					for(Player member : groupMembers.values())
					{
						PacketSendUtility.sendPacket(member, new SM_GROUP_INFO(this));
						if(subjective.equals(member))
							PacketSendUtility.sendPacket(member, SM_SYSTEM_MESSAGE.CHANGE_GROUP_LEADER());
						PacketSendUtility.sendPacket(member, new SM_GROUP_MEMBER_INFO(this, subjective, groupEvent));
					}
				}break;
				case LEAVE:
				{
					boolean changeleader = false;
					if(subjective == this.groupLeader)//change group leader
					{
						this.groupLeader = this.groupMembers.values().iterator().next();
						changeleader = true;
					}
					for(Player member : groupMembers.values())
					{
						if(changeleader)
						{
							PacketSendUtility.sendPacket(member, new SM_GROUP_INFO(this));
							PacketSendUtility.sendPacket(member, SM_SYSTEM_MESSAGE.CHANGE_GROUP_LEADER());
						}
						if(!subjective.equals(member))
							PacketSendUtility.sendPacket(member, new SM_GROUP_MEMBER_INFO(this, subjective, groupEvent));						
						if(this.size() > 1) PacketSendUtility.sendPacket(member, SM_SYSTEM_MESSAGE.MEMBER_LEFT_GROUP(subjective.getName()));
					}
					eventToSubjective(subjective, GroupEvent.LEAVE);
					
					if(this.size() == 1) this.disbandGroup();

				}break;
				case ENTER:
				{
					eventToSubjective(subjective, GroupEvent.ENTER);
					for(Player member : groupMembers.values())
					{
						if(!subjective.equals(member))
							PacketSendUtility.sendPacket(member, new SM_GROUP_MEMBER_INFO(this, subjective, groupEvent));						
					}
				}break;
				default:
				{
					for(Player member : groupMembers.values())
					{
						if(!subjective.equals(member))					
							PacketSendUtility.sendPacket(member, new SM_GROUP_MEMBER_INFO(this, subjective, groupEvent));						
					}					
				}break;
			}
	}
	private void eventToSubjective(Player subjective, GroupEvent groupEvent)
	{
		for(Player member : groupMembers.values())
		{
				PacketSendUtility.sendPacket(subjective, new SM_GROUP_MEMBER_INFO(this, member, groupEvent));
		}
		if(groupEvent == GroupEvent.LEAVE)
			PacketSendUtility.sendPacket(subjective, SM_SYSTEM_MESSAGE.YOU_LEFT_GROUP());
		log.info(subjective.getName());
	}
	/**
	 *  Removes player from group
	 *  
	 * @param player
	 */
	public void removePlayerFromGroup(Player player)
	{
		this.groupMembers.remove(player.getObjectId());
		player.setPlayerGroup(null);
		updateGroupUIToEvent(player, GroupEvent.LEAVE);
		
		PacketSendUtility.broadcastPacket(player, new SM_LEAVE_GROUP_MEMBER(),true,	new ObjectFilter<Player>(){		
			@Override
			public boolean acceptObject(Player object)
			{
				return object.getPlayerGroup() == null ? true : false;
			}
		});
	}
	
	private void disbandGroup()
	{
		this.aionObjectsIDFactory.releaseId(groupId);
		this.groupLeader.setPlayerGroup(null);
		PacketSendUtility.sendPacket(this.groupLeader, SM_SYSTEM_MESSAGE.DISBAND_GROUP());
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
	
	public Collection<Player> getMembers()
	{
		return groupMembers.values();
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
	
	public Iterator<Player> getGroupMemberIterator()
	{
		return this.groupMembers.values().iterator();
	}
	
	public Player lootDistributionService(Player costumer)
	{
		switch(this.lootGroupRules.getLootRule())
		{
			case FREEFORALL:
				return costumer;
			case ROUNDROBIN:
				return this.groupMembers.get(Rnd.nextInt()%this.size());
			case LEADER:
				return this.groupLeader;
		}
		return null;
	}
}
