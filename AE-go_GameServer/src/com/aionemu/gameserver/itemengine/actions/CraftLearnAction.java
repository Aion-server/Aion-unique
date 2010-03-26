/*
 * This file is part of aion-unique <aionu-unique.org>.
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

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.recipe.RecipeTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer, MrPoke
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CraftLearnAction")
public class CraftLearnAction extends AbstractItemAction
{
	@XmlAttribute
	protected int recipeid;

	@Override
	public void act(Player player, Item parentItem, Item targetItem)
	{
		RecipeTemplate template = DataManager.RECIPE_DATA.getRecipeTemplateById(recipeid);
		if (template == null)
			return;

		if (template.getRace().ordinal() != player.getCommonData().getRace().getRaceId())
			return;

		if (player.getRecipeList().isRecipePresent(recipeid))
		{
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1330060));
			return;
		}

		if (!player.getSkillList().isSkillPresent(template.getSkillid()))
		{
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1330062, DataManager.SKILL_DATA.getSkillTemplate(template.getSkillid()).getName()));
			return;
		}

		if (template.getSkillpoint() > player.getSkillList().getSkillLevel(template.getSkillid()))
		{
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1330063));
			return;
		}
		
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.USE_ITEM(new DescriptionId(parentItem.getItemTemplate().getNameId())));
		PacketSendUtility.sendPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemTemplate().getTemplateId()));
		
		if (player.getInventory().removeFromBagByObjectId(parentItem.getObjectId(), 1))
		{
			player.getRecipeList().addRecipe(player, template);
		}
	}

}
