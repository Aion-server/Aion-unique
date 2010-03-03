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

import java.util.HashMap;
import java.util.Map;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.model.templates.recipe.RecipeTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author MrPoke
 *
 */

public class CraftSkillUpdateService
{
	private static final Map<Integer, LearnTemplate> npcBySkill = new HashMap <Integer, LearnTemplate>();
	private static final Map<Integer, Integer> cost = new HashMap <Integer, Integer>();

	public CraftSkillUpdateService()
	{
		npcBySkill.put(204096, new LearnTemplate(30002, false,"Extract Vitality"));
		npcBySkill.put(204257, new LearnTemplate(30003, false,"Extract Aether"));

		npcBySkill.put(204100, new LearnTemplate(40001, true,"Cooking"));
		npcBySkill.put(204104,  new LearnTemplate(40002, true,"Weaponsmithing"));
		npcBySkill.put(204106,  new LearnTemplate(40003, true,"Armorsmithing"));
		npcBySkill.put(204110,  new LearnTemplate(40004, true,"Tailoring"));
		//npcBySkill.put(key, 40005); //LEATHERWORK
		//npcBySkill.put(key, 40006); //CARPENTRY
		npcBySkill.put(204102,  new LearnTemplate(40007,true,"Alchemy"));
		npcBySkill.put(204108,  new LearnTemplate(40008,true,"Handicrafting"));

		npcBySkill.put(203780, new LearnTemplate(30002, false,"Extract Vitality"));
		npcBySkill.put(203782, new LearnTemplate(30003, false,"Extract Aether"));
		
		npcBySkill.put(203784,  new LearnTemplate(40001,true,"Cooking"));
		npcBySkill.put(203788,  new LearnTemplate(40002,true,"Weaponsmithing"));
		npcBySkill.put(203790,  new LearnTemplate(40003,true,"Armorsmithing"));
		npcBySkill.put(203793,  new LearnTemplate(40004,true,"Tailoring"));
		//npcBySkill.put(key, 40005); //LEATHERWORK
		//npcBySkill.put(key, 40006); //CARPENTRY
		npcBySkill.put(203786,  new LearnTemplate(40007,true,"Alchemy"));
		npcBySkill.put(203792,  new LearnTemplate(40008,true,"Handicrafting"));
		
		cost.put(0, 3500);
		cost.put(99, 17000);
		cost.put(199, 115000);
		cost.put(299, 460000);
	}
	class LearnTemplate
	{
		private int skillId;
		private boolean isCraftSkill;
		/**
		 * @return the isCraftSkill
		 */
		public boolean isCraftSkill()
		{
			return isCraftSkill;
		}
		LearnTemplate(int skillId, boolean isCraftSkill,String skillName)
		{
			this.skillId=skillId;
			this.isCraftSkill = isCraftSkill;
		}
		/**
		 * @return the skillId
		 */
		public int getSkillId()
		{
			return skillId;
		}
	}

	public void learnSkill(Player player, Npc npc)
	{
		if (player.getLevel() < 10)
			return;
		final LearnTemplate template = npcBySkill.get(npc.getNpcId());
		if (template == null)
			return;
		final int skillId = template.getSkillId();
		if (skillId == 0)
			return;

		int skillLvl = 0;
		if (player.getSkillList().isSkillPresent(skillId))
			skillLvl = player.getSkillList().getSkillLevel(skillId);

		if (!cost.containsKey(skillLvl))
			return;
		final int price = cost.get(skillLvl);
		final Item kinahItem = player.getInventory().getKinahItem();
		if(price > kinahItem.getItemCount())
		{
			PacketSendUtility.sendPacket(player, new SM_MESSAGE(0, null, "You don't have enough Kinah.", ChatType.ANNOUNCEMENTS));
			return;
		}
		final int skillLevel = skillLvl;
		RequestResponseHandler responseHandler = new RequestResponseHandler(npc) 
		{				
			@Override
			public void acceptRequest(Creature requester, Player responder)
			{
				if (responder.getInventory().decreaseKinah(price))
				{
					responder.getSkillList().addSkill(responder, skillId, skillLevel+1, true);
					int skillLevel = responder.getSkillList().getSkillLevel(skillId);
					RecipeTemplate[] newRecipes = DataManager.RECIPE_DATA.getRecipeIdFor(responder.getCommonData().getRace(), skillId, skillLevel);
					for (RecipeTemplate recipe : newRecipes)
					{
						responder.getRecipeList().addRecipe(responder, recipe);
					}
				}
			}

			@Override
			public void denyRequest(Creature requester, Player responder)
			{
				//nothing to do
			}
		};

		boolean result = player.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_CRAFT_ADDSKILL_CONFIRM,responseHandler);
		if(result)
		{
			PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_CRAFT_ADDSKILL_CONFIRM, 0, new DescriptionId(DataManager.SKILL_DATA.getSkillTemplate(skillId).getNameId()), String.valueOf(price)));
		}
	}
}
