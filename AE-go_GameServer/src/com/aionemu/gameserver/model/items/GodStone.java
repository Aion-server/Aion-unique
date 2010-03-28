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

import org.apache.log4j.Logger;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.controllers.movement.ActionObserver;
import com.aionemu.gameserver.controllers.movement.ActionObserver.ObserverType;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.GodstoneInfo;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.model.Skill;

/**
 * @author ATracer
 * 
 */
public class GodStone extends ItemStone
{
	private static final Logger	log	= Logger.getLogger(GodStone.class);

	private final GodstoneInfo	godstoneInfo;
	private ActionObserver		actionListener;
	private final int			probability;
	private final int			probabilityLeft;

	public GodStone(int itemObjId, int itemId, PersistentState persistentState)
	{
		super(itemObjId, itemId, 0, ItemStoneType.GODSTONE, persistentState);
		ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(itemId);
		godstoneInfo = itemTemplate.getGodstoneInfo();
		
		if(godstoneInfo != null)
		{
			probability = godstoneInfo.getProbability();
			probabilityLeft = godstoneInfo.getProbabilityleft();
		}
		else
		{
			probability = 0;
			probabilityLeft = 0;
			log.warn("CHECKPOINT: Godstone info missing for item : " + itemId);
		}
		
	}

	/**
	 * 
	 * @param player
	 */
	public void onEquip(final Player player)
	{
		if(godstoneInfo == null)
			return;

		actionListener = new ActionObserver(ObserverType.ATTACK){
			@Override
			public void attack(Creature creature)
			{
				int rand = Rnd.get(probability - probabilityLeft, probability);
				if(rand > Rnd.get(0, 1000))
				{
					Skill skill = SkillEngine.getInstance().getSkill(player, godstoneInfo.getSkillid(),
						godstoneInfo.getSkilllvl(), player.getTarget());
					skill.useSkill();
				}
			}
		};

		player.getObserveController().addObserver(actionListener);
	}

	/**
	 * 
	 * @param player
	 */
	public void onUnEquip(Player player)
	{
		if(actionListener != null)
			player.getObserveController().removeObserver(actionListener);

	}
}
