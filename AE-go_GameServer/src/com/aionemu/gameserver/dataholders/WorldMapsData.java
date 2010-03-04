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

import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.templates.WorldMapTemplate;

/**
 * 
 * Object of this class is containing <tt>WorldMapTemplate</tt> objects for all world maps. World maps are defined in
 * data/static_data/world_maps.xml file.
 * 
 * @author Luno
 * 
 */
@XmlRootElement(name="world_maps")
@XmlAccessorType(XmlAccessType.NONE)
public class WorldMapsData implements Iterable<WorldMapTemplate>
{
	@XmlElement(name = "map")
	private List<WorldMapTemplate>	worldMaps;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<WorldMapTemplate> iterator()
	{
		return worldMaps.iterator();
	}

	/**
	 * Returns the count of maps.
	 * 
	 * @return worldMaps.size()
	 */
	public int size()
	{
		return worldMaps == null ? 0 : worldMaps.size();
	}
}
