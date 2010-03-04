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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.templates.tribe.AggroRelations;
import com.aionemu.gameserver.model.templates.tribe.Tribe;

/**
 * @author ATracer
 *
 */
@XmlRootElement(name = "tribe_relations")
@XmlAccessorType(XmlAccessType.FIELD)
public class TribeRelationsData
{
	@XmlElement(name = "tribe", required = true)
	protected List<Tribe> tribeList;

	protected Map<String, Tribe> tribeNameMap = new HashMap<String, Tribe>();

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(Tribe tribe : tribeList)
		{
			tribeNameMap.put(tribe.getName(), tribe);
		}
		tribeList = null;
	}

	/**
	 * @return tribeNameMap.size()
	 */
	public int size()
	{
		return tribeNameMap.size();
	}
	
	public boolean isAggressiveRelation(String tribeName1, String tribeName2)
	{
		Tribe tribe1 = tribeNameMap.get(tribeName1);
		if(tribe1 == null)
			return false;
		AggroRelations aggroRelations = tribe1.getAggroRelations();
		if(aggroRelations == null)
			return false;
		return aggroRelations.getTo().contains(tribeName2);
	}
}
