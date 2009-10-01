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
package com.aionemu.gameserver.model.templates;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author orz
 * 
 */
@XmlRootElement(name="tradelist_template")
@XmlAccessorType(XmlAccessType.NONE)
public class TradeListTemplate
{
	/**
	 * Npc Id.
	 */
	@XmlAttribute(name = "npc_id", required = true)
	private int		npcId;
	
	/**
	 * Npc name.
	 */
	@XmlAttribute(name = "name", required = true)
	private String	name	= "";

	/**
	 * Number of twin instances [players will be balanced so every one could exp easy]
	 */
	@XmlAttribute(name = "count", required = true)
	private int	 Count = 0;

	/**
	 * Trade ListID.
	 */
	@XmlAttribute(name = "listId0")
	private int	_listId0 = 0;
	@XmlAttribute(name = "listId1")
	private int	_listId1 = 0;
	@XmlAttribute(name = "listId2")
	private int	_listId2 = 0;
	@XmlAttribute(name = "listId3")
	private int	_listId3 = 0;
	@XmlAttribute(name = "listId4")
	private int	_listId4 = 0;
	@XmlAttribute(name = "listId5")
	private int	_listId5 = 0;
	@XmlAttribute(name = "listId6")
	private int	_listId6 = 0;
	
	public String getName()
	{
		return name;
	}

	public int getNpcId()
	{
		return npcId;
	}

	public int getCount()
	{
		return Count;
	}

	public int getlistId0()
	{
		return _listId0;
	}

	public int getlistId1()
	{
		return _listId1;
	}
	public int getlistId2()
	{
		return _listId2;
	}
	public int getlistId3()
	{
		return _listId3;
	}
	public int getlistId4()
	{
		return _listId4;
	}
	public int getlistId5()
	{
		return _listId5;
	}
	public int getlistId6()
	{
		return _listId6;
	}
}
