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

import com.aionemu.gameserver.model.templates.itemset.ItemSetTemplate;

/**
 * @author ATracer
 *
 */
@XmlRootElement(name = "item_sets")
@XmlAccessorType(XmlAccessType.FIELD)
public class ItemSetData
{
	@XmlElement(name="itemset")
	protected List<ItemSetTemplate> itemsetList;
	
	private TIntObjectHashMap<ItemSetTemplate> sets;
	
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		sets = new TIntObjectHashMap<ItemSetTemplate>();
		for(ItemSetTemplate set: itemsetList)
		{
			sets.put(set.getId(), set);
		}
		itemsetList = null;
	}
	
	/**
	 * 
	 * @param itemId
	 * @return
	 */
	public ItemSetTemplate getItemSetTemplate(int itemId)
	{
		return sets.get(itemId);
	}
	
	/**
	 * @return items.size()
	 */
	public int size()
	{
		return sets.size();
	}
}
