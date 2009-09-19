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
package com.aionemu.gameserver.model.items;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.aionemu.gameserver.dataholders.loadingutils.adapters.NpcEquipmentList;
import com.aionemu.gameserver.dataholders.loadingutils.adapters.NpcEquippedGearAdapter;
import com.aionemu.gameserver.model.ItemSlot;
import com.aionemu.gameserver.model.templates.ItemTemplate;

/**
 * @author Luno
 * 
 */
@XmlJavaTypeAdapter(NpcEquippedGearAdapter.class)
public class NpcEquippedGear implements Iterable<ItemTemplate>
{
	private Map<ItemSlot, ItemTemplate>	items;
	private short						mask;
	
	private NpcEquipmentList	v;
	public NpcEquippedGear(NpcEquipmentList v)
	{
		this.v = v;
	}

	/**
	 * @return
	 */
	public short getItemsMask()
	{
		if(items == null) init();
		return mask;
	}
	
	@Override
	public Iterator<ItemTemplate> iterator()
	{
		if(items == null) init();
		return items.values().iterator();
	}

	/**
	 * 
	 */
	private void init()
	{
		synchronized(this)
		{
			if(items == null)
			{
				items = new TreeMap<ItemSlot, ItemTemplate>();
				for(ItemTemplate item : v.items)
				{
					items.put(item.getItemSlot(), item);
					mask |= item.getItemSlot().getSlotIdMask();
					v = null;
				}
			}
		}
	}

	
	
}
