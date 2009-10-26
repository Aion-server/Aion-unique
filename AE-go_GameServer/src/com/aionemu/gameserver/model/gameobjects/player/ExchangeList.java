/*
 * This file is part of aion-unique <aion-unique.com>.
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
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.gameobjects.player;

import org.apache.log4j.Logger;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * @author  Avol
 *
 */
public class ExchangeList
{

	private static final Logger log = Logger.getLogger(ExchangeList.class);

	private int exchangePartner;
	private int[] exchangeItemList;
	private int[] exchangeItemCountList;
	private int exchangeKinah;

	private int exchangeItemListLenght = 0;
	private boolean allow;
	private boolean isTradeConfirmed;

	/*
	* Reset item list on trade start.
	*/

	public void setExchangeItemList()
	{
		exchangeItemList = new int[100];
		exchangeItemCountList = new int[100];
		this.exchangeItemListLenght = 0;
	}

	public void setExchangePartner(int exchangePartner)
	{
		this.exchangePartner = exchangePartner;
	}

	public void setExchangeKinah(int kinah)
	{
		this.exchangeKinah = kinah;
	}

	public int getExchangePartner()
	{
		return exchangePartner;
	}

	public void exchangeItemListAdd(int itemObjId, int itemCount)
	{
		int check = exchangeItemListLenght;
		allow = true;
		while (check >= 0)
		{
			if (exchangeItemList[check]==itemObjId) 
			{
				allow = false;
			}
			check--;
		}
		if (allow ==true) 
		{
			this.exchangeItemList[exchangeItemListLenght] = itemObjId;
			this.exchangeItemCountList[exchangeItemListLenght] = itemCount;
			this.exchangeItemListLenght++;
		}
	}

	public int getExchangeItemList(int row)
	{
		return exchangeItemList[row];
	}

	public int getExchangeItemCountList(int row)
	{
		return exchangeItemCountList[row];
	}

	public int getExchangeItemListLenght() 
	{
		return exchangeItemListLenght;
	}

	public int getExchangeKinah() 
	{
		return exchangeKinah;
	}

	public void setConfirm(boolean confirm) 
	{
		this.isTradeConfirmed = confirm;
	}

	public boolean getConfirm() 
	{
		return isTradeConfirmed;
	}

}
