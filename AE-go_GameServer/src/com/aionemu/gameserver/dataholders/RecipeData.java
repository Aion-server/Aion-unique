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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.templates.recipe.RecipeTemplate;
import com.aionemu.gameserver.skillengine.model.learn.SkillRace;

/**
 * @author ATracer, MrPoke
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
	private final TIntObjectHashMap<ArrayList<RecipeTemplate>> learnTemplates = new TIntObjectHashMap<ArrayList<RecipeTemplate>>();

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		recipeData = new TIntObjectHashMap<RecipeTemplate>();
		for(RecipeTemplate it : list)
		{
			recipeData.put(it.getId(), it);
			if (it.getAutolearn() == 0)
				continue;
			addTemplate(it);
		}
		list = null;
	}

	public RecipeTemplate getRecipeTemplateById(int id)
	{
		return recipeData.get(id);
	}

	private void addTemplate(RecipeTemplate template)
	{	
		SkillRace race = template.getRace();
		if(race == null)
			race = SkillRace.ALL;

		int hash = makeHash(race.ordinal(), template.getSkillid(), template.getSkillpoint());
		ArrayList<RecipeTemplate> value = learnTemplates.get(hash);
		if(value == null)
		{
			value = new ArrayList<RecipeTemplate>();
			learnTemplates.put(hash, value);
		}
			
		value.add(template);
	}

	public RecipeTemplate[] getRecipeIdFor(Race race, int skillId, int skillPoint)
	{
		List<RecipeTemplate> newRecipes = new ArrayList<RecipeTemplate>();
		List<RecipeTemplate> raceSpecificTemplates = 
			learnTemplates.get(makeHash(race.ordinal(), skillId, skillPoint));
		List<RecipeTemplate> allRaceSpecificTemplates = 
			learnTemplates.get(makeHash(SkillRace.ALL.ordinal(), skillId, skillPoint));
		
		if (raceSpecificTemplates != null)
			newRecipes.addAll(raceSpecificTemplates);
		if (allRaceSpecificTemplates != null)
			newRecipes.addAll(allRaceSpecificTemplates);

		return newRecipes.toArray(new RecipeTemplate[newRecipes.size()]);
	}

	/**
	 * @return recipeData.size()
	 */
	public int size()
	{
		return recipeData.size();
	}
	
	private static int makeHash(int race, int skillId, int skillLevel)
	{
		int result = race << 8;
        result = (result | skillId) << 8;
        return result | skillLevel;
	}
}
