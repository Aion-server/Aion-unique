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
package com.aionemu.gameserver.services;

import java.util.List;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.SkillListEntry;
import com.aionemu.gameserver.model.templates.item.Stigma;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_ACTIVATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 *
 */
public class StigmaService
{
	private static final Logger	log			= Logger.getLogger(StigmaService.class);
	/**
	 * @param resultItem
	 */
	public void notifyEquipAction(Player player, Item resultItem)
	{
		if(resultItem.getItemTemplate().isStigma())
		{
			Stigma stigmaInfo = resultItem.getItemTemplate().getStigma();
			
			if(stigmaInfo == null)
			{
				log.warn("Stigma info missing for item: " + resultItem.getItemTemplate().getTemplateId());
				return;
			}
			
			int skillId = stigmaInfo.getSkillid();
			SkillListEntry skill = new SkillListEntry(skillId, stigmaInfo.getSkilllvl(), PersistentState.NOACTION);
			player.getSkillList().addSkill(skill);
			PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(skill, 1300401));
		}
	}
	
	/**
	 * @param resultItem
	 */
	public void notifyUnequipAction(Player player, Item resultItem)
	{
		if(resultItem.getItemTemplate().isStigma())
		{
			Stigma stigmaInfo = resultItem.getItemTemplate().getStigma();
			int skillId = stigmaInfo.getSkillid();
			int nameId = DataManager.SKILL_DATA.getSkillTemplate(skillId).getNameId();
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300403, new DescriptionId(nameId)));
			PacketSendUtility.sendPacket(player, new SM_SKILL_ACTIVATION(skillId));
		}
	}
	
	/**
	 * 
	 * @param player
	 */
	public void onPlayerLogin(Player player)
	{
		List<Item> equippedItems = player.getEquipment().getEquippedItems();
		for(Item item : equippedItems)
		{
			notifyEquipAction(player, item);
		}
	}
}
