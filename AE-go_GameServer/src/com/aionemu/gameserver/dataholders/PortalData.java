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

import com.aionemu.gameserver.model.templates.portal.PortalTemplate;

/**
 * @author ATracer
 *
 */
@XmlRootElement(name = "portal_templates")
@XmlAccessorType(XmlAccessType.FIELD)
public class PortalData
{
	@XmlElement(name = "portal")
	private List<PortalTemplate> portals;
	
	/** A map containing all npc templates */
	private TIntObjectHashMap<PortalTemplate> portalData	= new TIntObjectHashMap<PortalTemplate>();
	private TIntObjectHashMap<PortalTemplate> worldIdPortalMap	= new TIntObjectHashMap<PortalTemplate>();
	
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(PortalTemplate portal : portals)
		{
			portalData.put(portal.getNpcId(), portal);
			worldIdPortalMap.put(portal.getExitPoint().getMapId(), portal);
		}
		portals = null;
	}
	
	public int size()
	{
		return portalData.size();
	}
	
	/**
	 * 
	 * @param npcId
	 * @return
	 */
	public PortalTemplate getPortalTemplate(int npcId)
	{
		return portalData.get(npcId);
	}
	
	/**
	 * 
	 * @param worldId
	 * @return
	 */
	public PortalTemplate getPortalTemplateByMap(int worldId)
	{
		return worldIdPortalMap.get(worldId);
	}
	
	
}
