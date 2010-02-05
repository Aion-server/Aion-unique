/**
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

import gnu.trove.TIntObjectHashMap;

import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.templates.NpcTemplate;
import com.aionemu.gameserver.model.templates.TradeListTemplate;

/**
 * This is a container holding and serving all {@link NpcTemplate} instances.<br>
 * Briefly: Every {@link Npc} instance represents some class of NPCs among which each have the same id, name, items,
 * statistics. Data for such NPC class is defined in {@link NpcTemplate} and is uniquely identified by npc id.
 * 
 * @author Luno
 * 
 */
@XmlRootElement(name = "npc_trade_list")
@XmlAccessorType(XmlAccessType.FIELD)
public class TradeListData
{
	@XmlElement(name = "tradelist_template")
	private List<TradeListTemplate> tlist;
	
	/** A map containing all trade list templates */
	private TIntObjectHashMap<TradeListTemplate>	npctlistData	= new TIntObjectHashMap<TradeListTemplate>();

	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		for(TradeListTemplate npc: tlist)
		{
			npctlistData.put(npc.getNpcId(), npc);
		}
	}
	
	public int size()
	{
		return npctlistData.size();
	}


	/**
	 * Returns an {@link NpcTemplate} object with given id.
	 * 
	 * @param id
	 *            id of NPC
	 * @return NpcTemplate object containing data about NPC with that id.
	 */
	public TradeListTemplate getTradeListTemplate(int id)
	{
		return npctlistData.get(id);
	}

}
