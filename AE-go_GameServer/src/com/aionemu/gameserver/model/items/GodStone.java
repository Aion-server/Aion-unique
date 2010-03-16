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
package com.aionemu.gameserver.model.items;

import com.aionemu.gameserver.controllers.movement.ActionObserver;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.GodstoneInfo;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;

/**
 * @author ATracer
 * 
 */
public class GodStone extends ItemStone
{
	private final GodstoneInfo	godstoneInfo;
	@SuppressWarnings("unused")
	private ActionObserver		actionListener;
	@SuppressWarnings("unused")
	private final int			probability;
	@SuppressWarnings("unused")
	private final int			probabilityLeft;

	public GodStone(int itemObjId, int itemId, PersistentState persistentState)
	{
		super(itemObjId, itemId, 0, ItemStoneType.GODSTONE, persistentState);
		ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(itemId);
		godstoneInfo = itemTemplate.getGodstoneInfo();
		probability = godstoneInfo.getProbability();
		probabilityLeft = godstoneInfo.getProbabilityleft();
	}

	/**
	 * 
	 * @param player
	 */
	public void onEquip(final Player player)
	{
		//todo
	}

	/**
	 * 
	 * @param player
	 */
	public void onUnEquip(Player player)
	{
		//todo
	}
}
