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
package com.aionemu.gameserver.model.gameobjects.player;

import java.util.Collection;

import javolution.util.FastMap;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.templates.TitleTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TITLE_LIST;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author xavier
 * 
 */
public class TitleList
{
	private FastMap<Integer, Title>	titles;
	private Player owner;

	public TitleList()
	{
		this.titles = new FastMap<Integer, Title>();
		this.owner = null;
	}

	public void setOwner(Player owner)
	{
		this.owner = owner;
	}
	
	public Player getOwner()
	{
		return owner;
	}
	
	public boolean addTitle(int titleId)
	{
		TitleTemplate tt = DataManager.TITLE_DATA.getTitleTemplate(titleId);
		if (tt==null)
		{
			throw new IllegalArgumentException("Invalid title id "+titleId);
		}
		if (owner!=null)
		{
			if (owner.getCommonData().getRace().getRaceId()!=tt.getRace())
			{
				return false;
			}
		}
		if(!titles.containsKey(titleId))
		{
			titles.put(titleId, new Title (tt));
		}
		else
		{
			return false;
		}

		if (owner!=null)
		{
			PacketSendUtility.sendPacket(owner, new SM_TITLE_LIST(owner));
		}
		return true;
	}
	
	public int size()
	{
		return titles.size();
	}

	public Collection<Title> getTitles()
	{
		return titles.values();
	}
}
