/*
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

import java.util.HashSet;
import java.util.Set;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.PlayerRecipesDAO;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.templates.recipe.RecipeTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEARN_RECIPE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author MrPoke
 *
 */
public class RecipeList
{
	private Set<Integer>		recipeList = new HashSet<Integer>();
	
	public RecipeList (HashSet<Integer> recipeList)
	{
		this.recipeList = recipeList;
	}
	
	public Set<Integer> getRecipeList()
	{
		return recipeList;
	}

	public void addRecipe(Player player, RecipeTemplate recipeTemplate)
	{
		int recipeId = recipeTemplate.getId();
		if (!player.getRecipeList().isRecipePresent(recipeId))
		{
			recipeList.add(recipeId);
			DAOManager.getDAO(PlayerRecipesDAO.class).addRecipe(player.getObjectId(), recipeId);
			PacketSendUtility.sendPacket(player, new SM_LEARN_RECIPE(recipeId));
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.CRAFT_RECIPE_LEARN(new DescriptionId(recipeTemplate.getNameid())));
		}
	}

	public boolean isRecipePresent(int recipeId)
	{
		return recipeList.contains(recipeId);
	}
}
