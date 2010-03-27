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
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.Race;
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
	private HashMap<Integer, ArrayList<PortalTemplate>> instancesMap = new HashMap<Integer, ArrayList<PortalTemplate>>();
	private HashMap<String, PortalTemplate> namedPortals = new HashMap<String, PortalTemplate>();

	/**
	 *  - Inititialize all maps for subsequent use
	 *  - Don't nullify initial portal list as it will be used during reload
	 * @param u
	 * @param parent
	 */
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		portalData.clear();
		instancesMap.clear();
		namedPortals.clear();
		
		for(PortalTemplate portal : portals)
		{
			portalData.put(portal.getNpcId(), portal);
			if(portal.isInstance())
			{
				int mapId = portal.getExitPoint().getMapId();
				ArrayList<PortalTemplate> templates = instancesMap.get(mapId);
				if(templates == null)
				{
					templates = new ArrayList<PortalTemplate>();
					instancesMap.put(mapId, templates);
				}
				templates.add(portal);
			}
			if(portal.getName() != null && !portal.getName().isEmpty())
				namedPortals.put(portal.getName(), portal);
		}
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
	 * @param race
	 * @return
	 */
	public PortalTemplate getInstancePortalTemplate(int worldId, Race race)
	{		
		for(PortalTemplate portal : instancesMap.get(worldId))
		{
			if(portal.getRace() == null || portal.getRace().equals(race))
				return portal;
		}
		throw new IllegalArgumentException("There is no portal template for: " + worldId + " " + race);	
	}
	
	/**
	 * 
	 * @param worldId
	 * @param name
	 * @return
	 */
	public PortalTemplate getTemplateByNameAndWorld(int worldId, String name)
	{
		PortalTemplate portal = namedPortals.get(name);
		
		if(portal != null && portal.getExitPoint().getMapId() != worldId)
			throw new IllegalArgumentException("Invalid combination of world and name: " + worldId + " " + name);	
			
		return portal;
	}

	/**
	 * @return the portals
	 */
	public List<PortalTemplate> getPortals()
	{
		return portals;
	}

	/**
	 * @param portals the portals to set
	 */
	public void setPortals(List<PortalTemplate> portals)
	{
		this.portals = portals;
		afterUnmarshal(null, null);
	}
	
	
}
