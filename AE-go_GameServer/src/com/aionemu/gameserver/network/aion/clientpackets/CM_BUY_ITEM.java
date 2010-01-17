/**
 * This file is part of aion-unique <aion-unique.smfnew.com>.
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
package com.aionemu.gameserver.network.aion.clientpackets;

import org.apache.log4j.Logger;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.trade.TradeList;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.services.TradeService;
import com.google.inject.Inject;

/**
 * 
 * @author orz
 * modified by ATracer
 */
public class CM_BUY_ITEM extends AionClientPacket
{
	private int npcObjId;
	private int unk1; 
	private int amount;
	private int itemId;
	private int count;

	public int unk2;
	
	public CM_BUY_ITEM(int opcode)
	{
		super(opcode);
	}

	/**
	 * Logger
	 */
	private static final Logger	log	= Logger.getLogger(CM_BUY_ITEM.class);
	
	@Inject
	private TradeService tradeService;
	
	private TradeList	tradeList;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl()
	{
		npcObjId = readD();
		unk1	 = readH();
		amount = readH(); //total no of items

		tradeList = new TradeList();
		tradeList.setNpcObjId(npcObjId);
		
		for (int i = 0; i < amount; i++) 
		{
			itemId = readD();
			count  = readD();
			unk2   = readD();
			
			if(unk1 == 12)
			{
				tradeList.addBuyItem(itemId, count);
			}
			else if(unk1 == 1)
			{
				tradeList.addSellItem(itemId, count);
			}		
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl()
	{
		Player player = getConnection().getActivePlayer();
		
		if (unk1 == 12) //buy
		{
			tradeService.performBuyFromShop(player, tradeList);
		}
		else if (unk1 == 1) //sell
		{
			tradeService.performSellToShop(player, tradeList);
		}
		else
		{
			log.info(String.format("Unhandle shop action unk1: %d", unk1));
		}
	}

}
