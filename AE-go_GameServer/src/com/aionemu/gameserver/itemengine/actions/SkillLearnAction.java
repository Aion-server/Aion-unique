/*
 * This file is part of aion-unique <www.aion-unique.com>.
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
package com.aionemu.gameserver.itemengine.actions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerSkillListDAO;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.SkillListEntry;
import com.aionemu.gameserver.model.templates.ItemTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_LIST;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.skillengine.model.learn.SkillClass;
import com.aionemu.gameserver.skillengine.model.learn.SkillRace;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SkillLearnAction")
public class SkillLearnAction extends AbstractItemAction
{
	@XmlAttribute
	protected int skillid;
	@XmlAttribute
	protected int level;
	@XmlAttribute(name = "class")
	protected SkillClass playerClass;
	@XmlAttribute
	protected SkillRace race;

	@Override
	public void act(Player player, Item parentItem)
	{
		//1. check player level
		if(player.getCommonData().getLevel() < level)
			return;
		//2. check player class and SkillClass.ALL
		if(player.getCommonData().getPlayerClass().ordinal() != playerClass.ordinal()
			&& playerClass != SkillClass.ALL)
			return;
		//3. check player race and SkillRace.ALL
		if(player.getCommonData().getRace().ordinal() != race.ordinal() 
			&& race != SkillRace.ALL)
			return;
		//4. check whether this skill is already learned
		if(player.getSkillList().isSkillPresent(skillid))
			return;
		
		//item animation and message
		ItemTemplate itemTemplate = parentItem.getItemTemplate();
		//PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.USE_ITEM(itemTemplate.getDescription()));
		PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(),
			parentItem.getObjectId(), itemTemplate.getItemId()), true);	
		//add skill
		player.getSkillList().addSkill(skillid, 1);
		PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(new SkillListEntry(skillid, 1, null)));
		DAOManager.getDAO(PlayerSkillListDAO.class).storeSkills(player);
		
		//remove book from inventory (assuming its not stackable)
		Item item = player.getInventory().getItemByObjId(parentItem.getObjectId());
		player.getInventory().removeFromBag(item);
		PacketSendUtility.sendPacket(player, new SM_DELETE_ITEM(parentItem.getObjectId()));	
	}
}
