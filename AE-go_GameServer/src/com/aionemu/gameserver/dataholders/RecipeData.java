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
package com.aionemu.gameserver.dataholders;

import gnu.trove.TIntObjectHashMap;

import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.templates.recipe.RecipeTemplate;

/**
 * @author ATracer
 *
 */
@XmlRootElement(name = "recipe_templates")
@XmlAccessorType(XmlAccessType.FIELD)
public class RecipeData
{

	@XmlElement(name = "recipe_template")
	protected List<RecipeTemplate> list;

	/** A map containing all goodslist templates */
	private TIntObjectHashMap<RecipeTemplate> recipeData;

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		recipeData = new TIntObjectHashMap<RecipeTemplate>();
		for(RecipeTemplate it : list)
		{
			recipeData.put(it.getId(), it);
		}
		list = null;
	}

	public RecipeTemplate getRecipeTemplateById(int id)
	{
		return recipeData.get(id);
	}

	/**
	 * @return
	 */
	public int size()
	{
		return recipeData.size();
	}
}
