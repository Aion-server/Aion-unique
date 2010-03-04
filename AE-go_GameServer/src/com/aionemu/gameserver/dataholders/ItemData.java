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
package com.aionemu.gameserver.dataholders;

import gnu.trove.TIntObjectHashMap;

import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.templates.item.ItemTemplate;

/**
 * @author Luno
 *
 */
@XmlRootElement(name = "item_templates")
@XmlAccessorType(XmlAccessType.FIELD)
public class ItemData
{
	@XmlElement(name="item_template")
	private List<ItemTemplate> its;
	
	private TIntObjectHashMap<ItemTemplate> items;
	
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		items = new TIntObjectHashMap<ItemTemplate>();
		for(ItemTemplate it: its)
		{
			items.put(it.getTemplateId(), it);
		}
		its = null;
	}
	
	public ItemTemplate getItemTemplate(int itemId)
	{
		return items.get(itemId);
	}

	/**
	 * @return items.size()
	 */
	public int size()
	{
		return items.size();
	}
}
