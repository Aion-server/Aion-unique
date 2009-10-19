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
package com.aionemu.gameserver.model.drop;


/**
 * @author ATracer
 *
 */
public class DropTemplate
{
	private int mobId;
	private int itemId;
	private int min;
	private int max;
	private float chance;
	private int quest;
	
	/**
	 * @param mobId
	 * @param itemId
	 * @param min
	 * @param max
	 * @param chance
	 * @param quest
	 */
	public DropTemplate(int mobId, int itemId, int min, int max, float chance, int quest) 
	{
		this.mobId = mobId;
		this.itemId = itemId;
		this.min = min;
		this.max = max;
		this.chance = chance;
		this.quest = quest;
	}

	/**
	 * @return the mobId
	 */
	public int getMobId()
	{
		return mobId;
	}

	/**
	 * @return the itemId
	 */
	public int getItemId()
	{
		return itemId;
	}

	/**
	 * @return the min
	 */
	public int getMin()
	{
		return min;
	}

	/**
	 * @return the max
	 */
	public int getMax()
	{
		return max;
	}

	/**
	 * @return the chance
	 */
	public float getChance()
	{
		return chance;
	}

	/**
	 * @return the quest
	 */
	public int getQuest()
	{
		return quest;
	}

}
