/**
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
package com.aionemu.gameserver.model.gameobjects.player;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_LIST;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Created on: 15.07.2009 19:33:07
 * Edited On:  13.09.2009 19:48:00
 *
 * @author IceReaper, orfeo087, Avol, AEJTester
 */
public class SkillList
{
	/**
	 * Class logger
	 */
	private static final Logger logger = Logger.getLogger(SkillList.class);

	public static final String[]	split	= null;

	/**
	 * Container of skilllist, position to xml.
	 */
	private final Map<Integer, SkillListEntry> skills;

	/**
	 * Creates an empty skill list
	 */
	public SkillList()
	{
		this.skills = new HashMap<Integer, SkillListEntry>();
	}

	/**
	 * Create new instance of <tt>SkillList</tt>.
	 * @param arg 
	 */
	public SkillList(Map<Integer, SkillListEntry> arg)
	{
		this.skills = arg;
	}

	/**
	 * Returns map with all skilllist 
	 * @return all skilllist
	 */
	public Map<Integer, SkillListEntry> getSkillMap()
	{
		return Collections.unmodifiableMap(skills);
	}
	
	/**
	 * Returns array with all skills
	 * @return
	 */
	public SkillListEntry[] getAllSkills()
	{
		return skills.values().toArray(new SkillListEntry[skills.size()]);
	}
	
	/**
	 * @param skillId
	 * @return
	 */
	public SkillListEntry getSkillEntry(int skillId)
	{
		return skills.get(skillId);
	}

	/**
	 * Add Skill to the collection.
	 * @return <tt>true</tt> if Skill addition was successful, and it can be stored into database.
	 *      Otherwise <tt>false</tt>.
	 */
	public synchronized boolean addSkill(Player player, int skillId, int skillLevel, boolean msg)
	{
		SkillListEntry existingSkill = skills.get(skillId);
		if (existingSkill != null)
		{
			if(existingSkill.getSkillLevel() > skillLevel)
			{
				logger.warn("Trying to add skill with lower skill level. ");
				return false;
			}
			existingSkill.setSkillLvl(skillLevel);
		}
		else
		{
			skills.put(skillId, new SkillListEntry(skillId, skillLevel, PersistentState.NEW));
		}
		if (msg)
			sendMessage(player, skillId);

		return true;
	}

	public void addSkillXp(Player player, int skillId, int xpReward)
	{
		SkillListEntry  skillEntry =  getSkillEntry(skillId);
		switch(skillEntry.getSkillId())
		{
			case 30001:
				if(skillEntry.getSkillLevel() == 49)
					return;
			case 30002:
			case 30003:
			case 40001:
			case 40002:
			case 40003:
			case 40004:
			case 40007:
			case 40008:
				switch (skillEntry.getSkillLevel())
				{
					case 99:
					case 199:
					case 299:
					case 399:
					case 450:
						//TODO system message
						return;
				}
		}
		boolean updateSkill = skillEntry.addSkillXp(xpReward);
		if (updateSkill)
			sendMessage(player, skillId);
	}
	/**
	 * Checks whether player have skill with specified skillId
	 * 
	 * @param skillId
	 * @return
	 */
	public boolean isSkillPresent(int skillId)
	{
		return skills.containsKey(skillId);
	}
	
	/**
	 * @param skillId
	 * @return level of the skill with specified skillId
	 * 
	 */
	public int getSkillLevel(int skillId)
	{
		return skills.get(skillId).getSkillLevel();
	}
	
	public synchronized boolean removeSkill(int skillId)
	{
		return skills.remove(skillId) == null ? false : true;
	}
	/**
	 * Returns count of available skillist.
	 * @return count of available skillist.
	 */
	public int getSize()
	{
		return skills.size();
	}
	
	private void sendMessage(Player player , int skillId)
	{
		switch (skillId)
		{
			case 30001:
			case 30002:
				PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player.getSkillList().getSkillEntry(skillId), 1330067));
				break;
			case 30003:
				PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player.getSkillList().getSkillEntry(skillId), 1330068));
				break;
			case 40001:
			case 40002:
			case 40003:
			case 40004:
			case 40005:
			case 40006:
			case 40007:
			case 40008:
			case 40009:
				PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player.getSkillList().getSkillEntry(skillId), 1330061));
				break;
			default:
				PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player.getSkillList().getSkillEntry(skillId), 1300050));
		}
	}
}