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
package com.aionemu.gameserver.controllers;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.lang.ArrayUtils;

import com.aionemu.gameserver.controllers.attack.AttackResult;
import com.aionemu.gameserver.controllers.attack.AttackStatus;
import com.aionemu.gameserver.controllers.movement.ActionObserver;
import com.aionemu.gameserver.controllers.movement.AttackCalcObserver;

/**
 * @author ATracer
 *
 */
public class ObserveController
{
	// TODO revisit here later
	protected Queue<ActionObserver>	moveObservers		= new ConcurrentLinkedQueue<ActionObserver>();
	protected Queue<ActionObserver>	attackObservers		= new ConcurrentLinkedQueue<ActionObserver>();
	protected Queue<ActionObserver>	attackedObservers	= new ConcurrentLinkedQueue<ActionObserver>();
	
	protected ActionObserver[] observers = new ActionObserver[0];
	protected AttackCalcObserver[] attackCalcObservers = new AttackCalcObserver[0];
	
	/**
	 * 
	 * @param observer
	 */
	public void attach(ActionObserver observer)
	{
		switch(observer.getObserverType())
		{
			case ATTACK:
				attackObservers.add(observer);
				break;
			case ATTACKED:
				attackedObservers.add(observer);
				break;
			case MOVE:
				moveObservers.add(observer);
				break;
		}
	}

	/**
	 * notify that creature moved
	 */
	protected void notifyMoveObservers()
	{
		while(!moveObservers.isEmpty())
		{
			ActionObserver observer = moveObservers.poll();
			observer.moved();
		}
		synchronized(observers)
		{
			for(ActionObserver observer : observers)
			{
				observer.moved();
			}
		}
	}

	/**
	 * notify that creature attacking
	 */
	protected void notifyAttackObservers()
	{
		while(!attackObservers.isEmpty())
		{
			ActionObserver observer = attackObservers.poll();
			observer.attack();
		}
		synchronized(observers)
		{
			for(ActionObserver observer : observers)
			{
				observer.attack();
			}
		}
	}

	/**
	 * notify that creature attacked
	 */
	protected void notifyAttackedObservers()
	{
		while(!attackedObservers.isEmpty())
		{
			ActionObserver observer = attackedObservers.poll();
			observer.attacked();
		}
		synchronized(observers)
		{
			for(ActionObserver observer : observers)
			{
				observer.attacked();
			}
		}
	}
	
	/**
	 * 
	 * @param observer
	 */
	public void addObserver(ActionObserver observer)
	{
		synchronized(observers)
		{
			observers = (ActionObserver[]) ArrayUtils.add(observers, observer);
		}
	}
	
	/**
	 * 
	 * @param observer
	 */
	public void addAttackCalcObserver(AttackCalcObserver observer)
	{
		synchronized(attackCalcObservers)
		{
			attackCalcObservers = (AttackCalcObserver[]) ArrayUtils.add(attackCalcObservers, observer);
		}
	}
	
	/**
	 * 
	 * @param observer
	 */
	public void removeObserver(ActionObserver observer)
	{
		synchronized(observers)
		{
			observers = (ActionObserver[]) ArrayUtils.removeElement(observers, observer);
		}
	}
	
	/**
	 * 
	 * @param observer
	 */
	public void removeAttackCalcObserver(AttackCalcObserver observer)
	{
		synchronized(attackCalcObservers)
		{
			attackCalcObservers = (AttackCalcObserver[]) ArrayUtils.removeElement(attackCalcObservers, observer);
		}
	}
	
	/**
	 * 
	 * @param status
	 * @return true or false
	 */
	public boolean checkAttackStatus(AttackStatus status)
	{
		synchronized(attackCalcObservers)
		{
			for(AttackCalcObserver observer : attackCalcObservers)
			{
				if(observer.checkStatus(status))
					return true;
			}
		}
		return false;
	}

	/**
	 * @param attackList
	 */
	public void checShieldStatus(List<AttackResult> attackList)
	{
		synchronized(attackCalcObservers)
		{
			for(AttackCalcObserver observer : attackCalcObservers)
			{
				observer.checkShield(attackList);
			}
		}
	}
}
