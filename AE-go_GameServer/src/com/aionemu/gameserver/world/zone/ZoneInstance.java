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
package com.aionemu.gameserver.world.zone;

import java.util.List;

import com.aionemu.gameserver.model.templates.zone.Point2D;
import com.aionemu.gameserver.model.templates.zone.ZoneTemplate;

/**
 * @author ATracer
 *
 */
public class ZoneInstance
{
	private int corners;
	private float xCoordinates[];
	private float yCoordinates[];
	
	private ZoneTemplate template;
	
	private List<ZoneInstance> neighbors;

	public ZoneInstance(ZoneTemplate template)
	{
		this.template = template;
		this.corners = template.getPoints().getPoint().size();
		xCoordinates = new float[corners];
		yCoordinates = new float[corners];
		for(int i = 0; i < corners; i++)
		{
			Point2D point = template.getPoints().getPoint().get(i);
			xCoordinates[i] = point.getX();
			yCoordinates[i] = point.getY();
		}
	}

	/**
	 * @return the corners
	 */
	public int getCorners()
	{
		return corners;
	}

	/**
	 * @return the xCoordinates
	 */
	public float[] getxCoordinates()
	{
		return xCoordinates;
	}

	/**
	 * @return the yCoordinates
	 */
	public float[] getyCoordinates()
	{
		return yCoordinates;
	}

	/**
	 * @return the neighbours
	 */
	public List<ZoneInstance> getNeighbors()
	{
		return neighbors;
	}

	/**
	 * @param neighbours the neighbours to set
	 */
	public void setNeighbors(List<ZoneInstance> neighbours)
	{
		this.neighbors = neighbours;
	}

	/**
	 * @return the template
	 */
	public ZoneTemplate getTemplate()
	{
		return template;
	}
}
