/*
 * This file is part of aion-unique <www.aion-unique.com>.
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
package com.aionemu.gameserver.controllers.movement;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * @author ATracer
 *
 */
public class ActionObserver
{
	public enum ObserverType
	{
		MOVE,
		ATTACK,
		ATTACKED,
		EQUIP
	}
	
	private ObserverType observerType;
	
	public ActionObserver(ObserverType observerType)
	{
		this.observerType = observerType;
	}
	
	/**
	 * @return the observerType
	 */
	public ObserverType getObserverType()
	{
		return observerType;
	}

	public void moved(){};
	public void attacked(Creature creature){};
	public void attack(Creature creature){};
	public void equip(Item item, Player owner){};
	public void unequip(Item item, Player owner){};
}
