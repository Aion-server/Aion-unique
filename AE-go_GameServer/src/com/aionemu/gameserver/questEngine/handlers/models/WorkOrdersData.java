/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 * aion-unique is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-unique is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aionemu.gameserver.questEngine.handlers.models;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.templates.quest.QuestItems;
import com.aionemu.gameserver.questEngine.handlers.QuestHandlers;
import com.aionemu.gameserver.questEngine.handlers.template.WorkOrders;

/**
 * @author Mr. Poke
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WorkOrdersData", propOrder = { "giveComponent" })
public class WorkOrdersData extends QuestScriptData
{

	@XmlElement(name = "give_component", required = true)
	protected List<QuestItems>	giveComponent;
	@XmlAttribute(name = "start_npc_id", required = true)
	protected int				startNpcId;
	@XmlAttribute(name = "recipe_id", required = true)
	protected int				recipeId;

	/**
	 * Gets the value of the giveComponent property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
	 * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
	 * the giveComponent property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getGiveComponent().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link QuestItems }
	 * 
	 * 
	 */
	public List<QuestItems> getGiveComponent()
	{
		if(giveComponent == null)
		{
			giveComponent = new ArrayList<QuestItems>();
		}
		return this.giveComponent;
	}

	/**
	 * Gets the value of the startNpcId property.
	 * 
	 */
	public int getStartNpcId()
	{
		return startNpcId;
	}

	/**
	 * Gets the value of the recipeId property.
	 * 
	 */
	public int getRecipeId()
	{
		return recipeId;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aionemu.gameserver.questEngine.handlers.models.QuestScriptData#register()
	 */
	@Override
	public void register()
	{
		QuestHandlers.addQuestHandler(new WorkOrders(this));
	}
}
