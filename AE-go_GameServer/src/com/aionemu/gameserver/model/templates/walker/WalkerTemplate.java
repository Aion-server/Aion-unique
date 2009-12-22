/*
 * This file is part of aion-unique <aion-unique.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
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
package com.aionemu.gameserver.model.templates.walker;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author KKnD
 * 
 */
@XmlRootElement(name="walker_template")
@XmlAccessorType(XmlAccessType.NONE)
public class WalkerTemplate
{
	@XmlAttribute(name = "route_id", required = true)
	private int routeId;

	@XmlElement(name = "routes")
	private RouteData routeData;

	public int getRouteId()
	{
		return routeId;
	}

	public RouteData getRouteData()
	{
		return routeData;
	}
}
