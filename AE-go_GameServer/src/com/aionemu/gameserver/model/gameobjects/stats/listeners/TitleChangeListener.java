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
package com.aionemu.gameserver.model.gameobjects.stats.listeners;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.ItemSlot;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.stats.PlayerGameStats;
import com.aionemu.gameserver.model.gameobjects.stats.StatEffect;
import com.aionemu.gameserver.model.templates.TitleTemplate;

/**
 * @author xavier
 * 
 */
public class TitleChangeListener
{
	public static void onTitleChange(Player player, int titleId, boolean isSet)
	{
		TitleTemplate tt = DataManager.TITLE_DATA.getTitleTemplate(titleId);
		if(tt == null)
		{
			return;
		}
		if(player.getGameStats() == null)
		{
			return;
		}
		if(tt.getStatEffect() == null)
		{
			return;
		}
		PlayerGameStats pgs = player.getGameStats();
		if(!isSet)
		{
			pgs.endEffect(tt.getStatEffect());
		}
		else
		{
			StatEffect slotEffect = tt.getStatEffect().getEffectForSlot(ItemSlot.MAIN_HAND);
			pgs.addEffect(slotEffect);
		}
	}
}
