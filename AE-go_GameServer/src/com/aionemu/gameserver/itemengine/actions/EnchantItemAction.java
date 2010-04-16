/*
 * This file is part of aion-unique <aion-unique.com>.
 *
 *     Aion-unique is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Aion-unique is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aionemu.gameserver.itemengine.actions;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.dataholders.loadingutils.XmlServiceProxy;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author Nemiroff Date: 16.12.2009
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EnchantItemAction")
public class EnchantItemAction extends AbstractItemAction
{
	/**
	 * 
	 * @param u
	 * @param parent
	 */
	void afterUnmarshal (Unmarshaller u, Object parent)
	{		
		xsp = u.getAdapter(XmlServiceProxy.class);
	}

	@Override
	public void act(final Player player, final Item parentItem, final Item targetItem)
	{

		PacketSendUtility.sendPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(),
			parentItem.getObjectId(), parentItem.getItemTemplate().getTemplateId(), 5000, 0, 0));
		
		ThreadPoolManager.getInstance().schedule(new Runnable(){
			@Override
			public void run()
			{
				int itemId = parentItem.getItemTemplate().getTemplateId();
				if(itemId > 166000000 && itemId < 167000000)
				{
					boolean result = xsp.getEnchantService().enchantItem(player, parentItem, targetItem);
					PacketSendUtility.sendPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem
						.getObjectId(), parentItem.getItemTemplate().getTemplateId(), 0, result ? 1 : 2, 0));
				}
				else
				{
					PacketSendUtility.sendPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem
						.getObjectId(), parentItem.getItemTemplate().getTemplateId(), 0, 1, 0));
					
					xsp.getEnchantService().socketManastone(player, parentItem, targetItem);;
				}
			}
		}, 5000);
	}
}
