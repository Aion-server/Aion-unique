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
package com.aionemu.gameserver.model.legion;

/**
 * @author Simple
 * 
 */
public class LegionEmblem
{
	private int		emblemId		= 0x00;
	private int		color_r			= 0x00;
	private int		color_g			= 0x00;
	private int		color_b			= 0x00;
	private boolean	defaultEmblem	= true;

	public LegionEmblem()
	{
	}

	/**
	 * Legion Emblem
	 * 
	 * @param color_r
	 * @param color_g
	 * @param color_b
	 */
	public LegionEmblem(int color_r, int color_g, int color_b)
	{
		this.color_r = color_r;
		this.color_g = color_g;
		this.color_b = color_b;
	}

	/**
	 * @param emblemId
	 *            the emblemId to set
	 * @param color_r
	 *            the color_r to set
	 * @param color_g
	 *            the color_g to set
	 * @param color_b
	 *            the color_b to set
	 */
	public void setEmblem(int emblemId, int color_r, int color_g, int color_b)
	{
		this.emblemId = emblemId;
		this.color_r = color_r;
		this.color_g = color_g;
		this.color_b = color_b;
	}

	/**
	 * @return the emblemId
	 */
	public int getEmblemId()
	{
		return emblemId;
	}

	/**
	 * @return the color_r
	 */
	public int getColor_r()
	{
		return color_r;
	}

	/**
	 * @return the color_g
	 */
	public int getColor_g()
	{
		return color_g;
	}

	/**
	 * @return the color_b
	 */
	public int getColor_b()
	{
		return color_b;
	}

	/**
	 * @param defaultEmblem
	 *            the defaultEmblem to set
	 */
	public void setDefaultEmblem(boolean defaultEmblem)
	{
		this.defaultEmblem = defaultEmblem;
	}

	/**
	 * @return the defaultEmblem
	 */
	public boolean isDefaultEmblem()
	{
		return defaultEmblem;
	}
}
