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
package com.aionemu.gameserver.skillengine;

import java.util.Collections;

import org.apache.log4j.Logger;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerSkillListDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.SkillList;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_LIST;
import com.aionemu.gameserver.skillengine.model.learn.SkillLearnTemplate;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 *
 */
public class SkillLearnService
{
	private static final Logger log = Logger.getLogger(SkillLearnService.class);
	
	/**
	 * 
	 * @param player
	 * @param isNewCharacter
	 */
	public static void addNewSkills(Player player, boolean isNewCharacter)
	{
		int level = player.getCommonData().getLevel();
		PlayerClass playerClass = player.getCommonData().getPlayerClass();
		Race playerRace = player.getCommonData().getRace();

		SkillLearnTemplate[] skillTemplates =
			DataManager.SKILL_TREE_DATA.getTemplatesFor(playerClass, level, playerRace);
		
		if(isNewCharacter)
		{
			player.setSkillList(new SkillList());
		}
		
		for(SkillLearnTemplate template : skillTemplates)
		{
			player.getSkillList().addSkill(template.getSkillId(), template.getSkillLevel());
			
			if(!isNewCharacter)
			{
				//TODO message should be SM_SKILL_LIST.YOU_LEARNED
				PacketSendUtility.sendPacket(player,
					new SM_SKILL_LIST(Collections.singletonMap(template.getSkillId(), template.getSkillLevel()),
						0));
			}
			
			if(template.getSkillLevel() == 1)
			{
				DAOManager.getDAO(PlayerSkillListDAO.class).addSkill(player.getObjectId(), template.getSkillId(), template.getSkillLevel());
			}
			else
			{
				DAOManager.getDAO(PlayerSkillListDAO.class).updateSkill(player.getObjectId(), template.getSkillId(), template.getSkillLevel());
			}
		}
	}
}
