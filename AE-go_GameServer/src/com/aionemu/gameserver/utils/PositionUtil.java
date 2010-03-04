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
package com.aionemu.gameserver.utils;

import com.aionemu.gameserver.model.gameobjects.VisibleObject;

/**
 * @author ATracer
 *
 */
public class PositionUtil
{
	private static final float MAX_ANGLE_DIFF = 90f;
	
	/**
	 * 
	 * @param object1
	 * @param object2
	 * @return true or false
	 */
	public static boolean isBehindTarget(VisibleObject object1, VisibleObject object2)
	{
		float angleObject1 = MathUtil.calculateAngleFrom(object1, object2);
		float angleObject2 = MathUtil.convertHeadingToDegree(object2.getHeading());
		float angleDiff = angleObject1 - angleObject2;
		
		if (angleDiff <= -360 + MAX_ANGLE_DIFF) angleDiff += 360;
        if (angleDiff >= 360 - MAX_ANGLE_DIFF) angleDiff -= 360;
        return Math.abs(angleDiff) <= MAX_ANGLE_DIFF;
	}
	
	/**
	 * 
	 * @param object1
	 * @param object2
	 * @return true or false
	 */
	public static boolean isInFrontOfTarget(VisibleObject object1, VisibleObject object2)
	{
		float angleObject2 = MathUtil.calculateAngleFrom(object2, object1);
		float angleObject1 = MathUtil.convertHeadingToDegree(object2.getHeading());
		float angleDiff = angleObject1 - angleObject2;
		
		if (angleDiff <= -360 + MAX_ANGLE_DIFF) angleDiff += 360;
		if (angleDiff >= 360 - MAX_ANGLE_DIFF) angleDiff -= 360;
		return Math.abs(angleDiff) <= MAX_ANGLE_DIFF;
	}
}
