/*
 * This file is part of aion-emu <aion-emu.com>.
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
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.restrictions;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

import org.apache.commons.lang.ArrayUtils;

import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * @author lord_rex
 * 
 */
public class RestrictionsManager
{
	private RestrictionsManager()
	{

	}

	private static enum RestrictionMode implements Comparator<Restrictions>
	{
		canAttack,
		canUseSkill,
		canChat,
		// TODO
		;

		private final Method	METHOD;

		private RestrictionMode()
		{
			for(Method method : Restrictions.class.getMethods())
			{
				if(name().equals(method.getName()))
				{
					METHOD = method;
					return;
				}
			}

			throw new InternalError();
		}

		private boolean equalsMethod(Method method)
		{
			if(!METHOD.getName().equals(method.getName()))
				return false;

			if(!METHOD.getReturnType().equals(method.getReturnType()))
				return false;

			return Arrays.equals(METHOD.getParameterTypes(), method.getParameterTypes());
		}

		private static final RestrictionMode[]	VALUES	= RestrictionMode.values();

		private static RestrictionMode parse(Method method)
		{
			for(RestrictionMode mode : VALUES)
			{
				if(mode.equalsMethod(method))
					return mode;
			}

			return null;
		}

		@Override
		public int compare(Restrictions o1, Restrictions o2)
		{
			return Double.compare(getPriority(o2), getPriority(o1));
		}

		private double getPriority(Restrictions restriction)
		{
			RestrictionPriority a1 = getMatchingMethod(restriction.getClass()).getAnnotation(RestrictionPriority.class);
			if(a1 != null)
				return a1.value();

			RestrictionPriority a2 = restriction.getClass().getAnnotation(RestrictionPriority.class);
			if(a2 != null)
				return a2.value();

			return RestrictionPriority.DEFAULT_PRIORITY;
		}

		private Method getMatchingMethod(Class<? extends Restrictions> clazz)
		{
			for(Method method : clazz.getMethods())
			{
				if(equalsMethod(method))
					return method;
			}

			throw new InternalError();
		}

	}

	private static final Restrictions[][]	RESTRICTIONS	= new Restrictions[RestrictionMode.VALUES.length][0];

	public synchronized static void activate(Restrictions restriction)
	{
		for(Method method : restriction.getClass().getMethods())
		{
			RestrictionMode mode = RestrictionMode.parse(method);

			if(mode == null)
				continue;

			if(method.getAnnotation(DisabledRestriction.class) != null)
				continue;

			Restrictions[] restrictions = RESTRICTIONS[mode.ordinal()];

			if(!ArrayUtils.contains(restrictions, restriction))
				restrictions = (Restrictions[]) ArrayUtils.add(restrictions, restriction);

			Arrays.sort(restrictions, mode);

			RESTRICTIONS[mode.ordinal()] = restrictions;
		}
	}

	public synchronized static void deactivate(Restrictions restriction)
	{
		for(RestrictionMode mode : RestrictionMode.VALUES)
		{
			Restrictions[] restrictions = RESTRICTIONS[mode.ordinal()];

			for(int index; (index = ArrayUtils.indexOf(restrictions, restriction)) != -1;)
				restrictions = (Restrictions[]) ArrayUtils.remove(restrictions, index);

			RESTRICTIONS[mode.ordinal()] = restrictions;
		}
	}

	static
	{
		activate(new PlayerRestrictions());
	}
	
	public static boolean canAttack(Player player, VisibleObject target)
	{
		for(Restrictions restrictions : RESTRICTIONS[RestrictionMode.canAttack.ordinal()])
		{
			if(!restrictions.canAttack(player, target))
				return false;
		}

		return true;
	}

	public static boolean canUseSkill(Player player, VisibleObject target)
	{
		for(Restrictions restrictions : RESTRICTIONS[RestrictionMode.canUseSkill.ordinal()])
		{
			if(!restrictions.canUseSkill(player, target))
				return false;
		}

		return true;
	}
	
	public static boolean canChat(Player player)
	{
		for(Restrictions restrictions : RESTRICTIONS[RestrictionMode.canChat.ordinal()])
		{
			if(!restrictions.canChat(player))
				return false;
		}
		
		return true;
	}
}
