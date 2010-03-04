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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import javolution.util.FastMap;

import com.aionemu.commons.objects.filter.ObjectFilter;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_GROUP_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_GROUP_MEMBER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEAVE_GROUP_MEMBER;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer, Lyahim, Simple
 * 
 */
public class PlayerGroup
{
	private int							groupId;

	private LootGroupRules				lootGroupRules		= new LootGroupRules();

	private Player						groupLeader;

	private FastMap<Integer, Player>	groupMembers		= new FastMap<Integer, Player>().setShared(true);

	private ArrayList<Integer>			pickedLootMembers	= new ArrayList<Integer>();

	/**
	 * Instantiates new player group with unique groupId
	 * 
	 * @param groupId
	 */
	public PlayerGroup(int groupId, Player groupleader)
	{
		this.groupId = groupId;
		this.groupMembers.put(groupleader.getObjectId(), groupleader);
		this.setGroupLeader(groupleader);
		groupleader.setPlayerGroup(this);
		PacketSendUtility.sendPacket(groupLeader, new SM_GROUP_INFO(this));
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
	 * Used to set group leader
	 * 
	 * @param groupLeader
	 *            the groupLeader to set
	 */
	public void setGroupLeader(Player groupLeader)
	{
		this.groupLeader = groupLeader;
	}

	/**
	 * Adds player to group
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
	 * This method will return random group member
	 * 
	 * @return memberObjId
	 */
	public int getRandomMember()
	{
		if(pickedLootMembers.size() >= size())
			pickedLootMembers.clear();
		
		//TODO need rework logic here
		Random random = new Random();
		int pickedMemberObjId = 0;
		while(pickedMemberObjId == 0)
		{
			int index = random.nextInt(size());
			if(!pickedLootMembers.contains(index))
			{
				int i = 0;
				for(int memberObjId : getMemberObjIds())
				{
					if(i == index)
					{
						pickedLootMembers.add(index);
						pickedMemberObjId = memberObjId;
					}
					i++;
				}
			}
		}
		return pickedMemberObjId;
	}

	/**
	 * Removes player from group
	 * 
	 * @param player
	 */
	public void removePlayerFromGroup(Player player)
	{
		this.groupMembers.remove(player.getObjectId());
		player.setPlayerGroup(null);
		updateGroupUIToEvent(player, GroupEvent.LEAVE);

		/**
		 * Inform all group members player has left the group
		 */
		PacketSendUtility.broadcastPacket(player, new SM_LEAVE_GROUP_MEMBER(), true, new ObjectFilter<Player>(){
			@Override
			public boolean acceptObject(Player object)
			{
				return object.getPlayerGroup() == null ? true : false;
			}
		});
	}

	public void onGroupMemberLogIn(Player player)
	{
		groupMembers.remove(player.getObjectId());
		groupMembers.put(player.getObjectId(), player);
	}

	/**
	 * Checks whether group is full
	 * 
	 * @return true or false
	 */
	public boolean isFull()
	{
		return groupMembers.size() == 6;
	}

	public Collection<Player> getMembers()
	{
		return groupMembers.values();
	}

	public Collection<Integer> getMemberObjIds()
	{
		return groupMembers.keySet();
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

	public Player lootDistributionService(Player costumer)
	{
		switch(this.lootGroupRules.getLootRule())
		{
			case FREEFORALL:
				return costumer;
			case ROUNDROBIN:
				return this.groupMembers.get(Rnd.nextInt() % this.size());
			case LEADER:
				return this.groupLeader;
		}
		return null;
	}

	/**
	 * Update the Client user interface with the newer data
	 */
	// TODO: Move to GroupService
	public void updateGroupUIToEvent(Player subjective, GroupEvent groupEvent)
	{
		switch(groupEvent)
		{
			case CHANGELEADER:
			{
				for(Player member : this.getMembers())
				{
					PacketSendUtility.sendPacket(member, new SM_GROUP_INFO(this));
					if(subjective.equals(member))
						PacketSendUtility.sendPacket(member, SM_SYSTEM_MESSAGE.CHANGE_GROUP_LEADER());
					PacketSendUtility.sendPacket(member, new SM_GROUP_MEMBER_INFO(this, subjective, groupEvent));
				}
			}
				break;
			case LEAVE:
			{
				boolean changeleader = false;
				if(subjective == this.getGroupLeader())// change group leader
				{
					this.setGroupLeader(this.getMembers().iterator().next());
					changeleader = true;
				}
				for(Player member : this.getMembers())
				{
					if(changeleader)
					{
						PacketSendUtility.sendPacket(member, new SM_GROUP_INFO(this));
						PacketSendUtility.sendPacket(member, SM_SYSTEM_MESSAGE.CHANGE_GROUP_LEADER());
					}
					if(!subjective.equals(member))
						PacketSendUtility.sendPacket(member, new SM_GROUP_MEMBER_INFO(this, subjective, groupEvent));
					if(this.size() > 1)
						PacketSendUtility.sendPacket(member, SM_SYSTEM_MESSAGE.MEMBER_LEFT_GROUP(subjective.getName()));
				}
				eventToSubjective(subjective, GroupEvent.LEAVE);
			}
				break;
			case ENTER:
			{
				eventToSubjective(subjective, GroupEvent.ENTER);
				for(Player member : this.getMembers())
				{
					if(!subjective.equals(member))
						PacketSendUtility.sendPacket(member, new SM_GROUP_MEMBER_INFO(this, subjective, groupEvent));
				}
			}
				break;
			default:
			{
				for(Player member : this.getMembers())
				{
					if(!subjective.equals(member))
						PacketSendUtility.sendPacket(member, new SM_GROUP_MEMBER_INFO(this, subjective, groupEvent));
				}
			}
				break;
		}
	}

	// TODO: Move to GroupService
	private void eventToSubjective(Player subjective, GroupEvent groupEvent)
	{
		for(Player member : getMembers())
		{
			if(!subjective.equals(member))
				PacketSendUtility.sendPacket(subjective, new SM_GROUP_MEMBER_INFO(this, member, groupEvent));
		}
		if(groupEvent == GroupEvent.LEAVE)
			PacketSendUtility.sendPacket(subjective, SM_SYSTEM_MESSAGE.YOU_LEFT_GROUP());
	}
}
