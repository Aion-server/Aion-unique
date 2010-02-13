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
package com.aionemu.gameserver.model.trade;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;

/**
 * @author ATracer
 *
 */
public class TradeList
{
	private int npcObjId;
	
	private List<TradeItem> tradeItems = new ArrayList<TradeItem>();
	
	/**
	 * 
	 * @param itemId
	 * @param count
	 */
	public void addBuyItem(int itemId, int count)
	{
		
		ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(itemId);
		if(itemTemplate != null)
		{
			TradeItem tradeItem = new TradeItem(itemId, count);
			tradeItem.setItemTemplate(itemTemplate);
			tradeItems.add(tradeItem);
		}
	}
	
	/**
	 * 
	 * @param itemObjId
	 * @param count
	 */
	public void addSellItem(int itemObjId, int count)
	{
		TradeItem tradeItem = new TradeItem(itemObjId, count);
		tradeItems.add(tradeItem);
	}
	
	/**
	 * 
	 * @return price TradeList sum price
	 */
	public int calculateBuyListPrice()
	{
		int price = 0;
		for(TradeItem tradeItem : tradeItems)
		{
			price += tradeItem.getItemTemplate().getPrice() * tradeItem.getCount();
		}
		return price;
	}
	
	/**
	 * 
	 * @return
	 */
	public int calculateAbyssBuyListPrice()
	{
		int price = 0;
		for(TradeItem tradeItem : tradeItems)
		{
			price += tradeItem.getItemTemplate().getAbyssPoints() * tradeItem.getCount();
		}
		return price;
	}
	
	
	/**
	 * @return the tradeItems
	 */
	public List<TradeItem> getTradeItems()
	{
		return tradeItems;
	}

	public int size()
	{
		return tradeItems.size();
	}

	/**
	 * @return the npcId
	 */
	public int getNpcObjId()
	{
		return npcObjId;
	}

	/**
	 * @param npcObjId the npcId to set
	 */
	public void setNpcObjId(int npcObjId)
	{
		this.npcObjId = npcObjId;
	}
}
