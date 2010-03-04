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
package com.aionemu.gameserver.model.templates;

/**
 * @author ATracer
 *
 */
public abstract class VisibleObjectTemplate
{
	/**
	 * For Npcs it will return npcid from templates xml
	 * 
	 * @return id of object template
	 */
	public abstract int getTemplateId();
	/**
	 * For Npcs it will return name from templates xml
	 * 
	 * @return name of object
	 */
	public abstract String getName();
	
	/**
	 *  Name id of object template
	 * @return int
	 */
	public abstract int getNameId();
}
