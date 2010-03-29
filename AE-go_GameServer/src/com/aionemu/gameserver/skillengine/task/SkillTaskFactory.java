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
package com.aionemu.gameserver.skillengine.task;

import com.aionemu.gameserver.model.gameobjects.Gatherable;
import com.aionemu.gameserver.model.gameobjects.StaticObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.gather.Material;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.recipe.RecipeTemplate;
import com.google.inject.assistedinject.Assisted;

/**
 * @author ATracer
 * 
 */
public interface SkillTaskFactory
{
	/**
	 * 
	 * @param requestor
	 * @param gatherable
	 * @param material
	 * @param skillLvlDiff
	 * @return
	 */
	public GatheringTask gatheringTask(Player requestor, Gatherable gatherable, Material material, int skillLvlDiff);

	/**
	 * 
	 * @param requestor
	 * @param responder
	 * @param recipeTemplate
	 * @param itemTemplate
	 * @param criticalTemplate
	 * @param skillLvlDiff
	 * @return
	 */
	public CraftingTask craftingTask(Player requestor, StaticObject responder, RecipeTemplate recipeTemplate,
		@Assisted("itemTemplate") ItemTemplate itemTemplate,
		@Assisted("criticalTemplate") ItemTemplate criticalTemplate, int skillLvlDiff);
}
