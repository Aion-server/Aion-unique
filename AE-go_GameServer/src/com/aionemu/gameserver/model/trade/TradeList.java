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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;

/**
 * @author ATracer
 *
 */
public class TradeList
{
	private int sellerObjId;
	
	private List<TradeItem> tradeItems = new ArrayList<TradeItem>();
	
	private int requiredKinah;
	
	private int requiredAp;
	
	private Map<Integer, Integer> requiredItems  = new HashMap<Integer, Integer>();

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
	 * @param itemId
	 * @param count
	 */
	public void addPSItem(int itemId, int count)
	{
		TradeItem tradeItem = new TradeItem(itemId, count);
		tradeItems.add(tradeItem);
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
	public boolean calculateBuyListPrice(Player player, int priceRate)
	{
		int availableKinah = player.getInventory().getKinahItem().getItemCount();
		requiredKinah = 0;
		for(TradeItem tradeItem : tradeItems)
		{
			requiredKinah += tradeItem.getItemTemplate().getPrice() * tradeItem.getCount() * priceRate;
		}
		
		return availableKinah >= requiredKinah;
	}
	
	/**
	 * @return true or false
	 */
	public boolean calculateAbyssBuyListPrice(Player player)
	{
		int ap = player.getAbyssRank().getAp();
		
		this.requiredAp = 0;
		this.requiredItems.clear();
		
		for(TradeItem tradeItem : tradeItems)
		{
			requiredAp += tradeItem.getItemTemplate().getAbyssPoints() * tradeItem.getCount();
			int itemId = tradeItem.getItemTemplate().getAbyssItem();
			
			Integer alreadyAddedCount = requiredItems.get(itemId);
			if(alreadyAddedCount == null)
				requiredItems.put(itemId, tradeItem.getItemTemplate().getAbyssItemCount());
			else
				requiredItems.put(itemId, alreadyAddedCount + tradeItem.getItemTemplate().getAbyssItemCount());
		}		
		
		if(ap < requiredAp)
			return false;
		
		for(Integer itemId : requiredItems.keySet())
		{
			int count = player.getInventory().getItemCountByItemId(itemId);
			if(count < requiredItems.get(itemId))
				return false;
		}
		
		return true;
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
	public int getSellerObjId()
	{
		return sellerObjId;
	}

	/**
	 * @param sellerObjId the npcId to set
	 */
	public void setSellerObjId(int npcObjId)
	{
		this.sellerObjId = npcObjId;
	}

	/**
	 * @return the requiredAp
	 */
	public int getRequiredAp()
	{
		return requiredAp;
	}

	/**
	 * @return the requiredKinah
	 */
	public int getRequiredKinah()
	{
		return requiredKinah;
	}

	/**
	 * @return the requiredItems
	 */
	public Map<Integer, Integer> getRequiredItems()
	{
		return requiredItems;
	}
}
