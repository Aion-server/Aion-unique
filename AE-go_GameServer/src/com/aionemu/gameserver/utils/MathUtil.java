/**
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

package com.aionemu.gameserver.utils;

import java.awt.Point;

import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.geometry.Point3D;

/**
 * Class with basic math.<br>
 * Thanks to: <li>
 * <ul>
 * http://geom-java.sourceforge.net/
 * </ul>
 * <ul>
 * http://local.wasp.uwa.edu.au/~pbourke/geometry/pointline/DistancePoint.java
 * </ul>
 * </li> <br>
 * <br>
 * Few words about speed:
 * 
 * <pre>
 * Math.hypot(dx, dy); // Extremely slow
 * Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2)); // 20 times faster than hypot
 * Math.sqrt(dx * dx + dy * dy); // 10 times faster then previous line
 * </pre>
 * 
 * We don't need squared distances for calculations, {@linkplain Math#sqrt(double)} is very fast.<br>
 * In fact the difference is very small, so it can be ignored.<br>
 * Feel free to run the following test (or to find a mistake in it ^^).<br>
 * 
 * <pre>
 * import java.util.Random;
 * 
 * public class MathSpeedTest
 * {
 * 
 * 	private static long	time;
 * 
 * 	private static long	n	= 100000000L;
 * 
 * 	public static void main(String[] args)
 * 	{
 * 
 * 		Random r = new Random();
 * 
 * 		long x;
 * 		long y;
 * 
 * 		double res = 0;
 * 		setTime();
 * 		for(int i = 0; i &lt; n; i++)
 * 		{
 * 			x = r.nextInt();
 * 			y = r.nextInt();
 * 			res = Math.sqrt(x * x + y * y);
 * 		}
 * 		printTime();
 * 		System.out.println(res);
 * 
 * 		setTime();
 * 		for(int i = 0; i &lt; n; i++)
 * 		{
 * 			x = r.nextInt();
 * 			y = r.nextInt();
 * 			res = x * x + y * y;
 * 		}
 * 		printTime();
 * 		System.out.println(Math.sqrt(res));
 * 	}
 * 
 * 	public static void setTime()
 * 	{
 * 		time = System.currentTimeMillis();
 * 	}
 * 
 * 	public static void printTime()
 * 	{
 * 		System.out.println(System.currentTimeMillis() - time);
 * 	}
 * }
 * </pre>
 * 
 * @author Disturbing
 * @author SoulKeeper
 */
public class MathUtil
{
	/**
	 * Returns distance between two 2D points
	 * 
	 * @param point1
	 *            first point
	 * @param point2
	 *            second point
	 * @return distance between points
	 */
	public static double getDistance(Point point1, Point point2)
	{
		return getDistance(point1.x, point1.y, point2.x, point2.y);
	}

	/**
	 * Returns distance between two sets of coords
	 * 
	 * @param x1
	 *            first x coord
	 * @param y1
	 *            first y coord
	 * @param x2
	 *            second x coord
	 * @param y2
	 *            second y coord
	 * @return distance between sets of coords
	 */
	public static double getDistance(int x1, int y1, int x2, int y2)
	{
		// using long to avoid possible overflows when multiplying
		long dx = x2 - x1;
		long dy = y2 - y1;

		// return Math.hypot(x2 - x1, y2 - y1); // Extremely slow
		// return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)); // 20 times faster than hypot
		return Math.sqrt(dx * dx + dy * dy); // 10 times faster then previous line
	}

	/**
	 * Returns distance between two 3D points
	 * 
	 * @param point1
	 *            first point
	 * @param point2
	 *            second point
	 * @return distance between points
	 */
	public static double getDistance(Point3D point1, Point3D point2)
	{
		return getDistance(point1.getX(), point1.getY(), point1.getZ(), point2.getX(), point2.getY(), point2.getZ());
	}

	/**
	 * Returns distance between 3D set of coords
	 * 
	 * @param x1
	 *            first x coord
	 * @param y1
	 *            first y coord
	 * @param z1
	 *            first z coord
	 * @param x2
	 *            second x coord
	 * @param y2
	 *            second y coord
	 * @param z2
	 *            second z coord
	 * @return distance between coords
	 */
	public static double getDistance(float x1, float y1, float z1, float x2, float y2, float z2)
	{
		float dx = x1 - x2;
		float dy = y1 - y2;
		float dz = z1 - z2;

		// We should avoid Math.pow or Math.hypot due to perfomance reasons
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	/**
	 * Returns closest point on segment to point
	 * 
	 * @param ss
	 *            segment start point
	 * @param se
	 *            segment end point
	 * @param p
	 *            point to found closest point on segment
	 * @return closest point on segment to p
	 */
	public static Point getClosestPointOnSegment(Point ss, Point se, Point p)
	{
		return getClosestPointOnSegment(ss.x, ss.y, se.x, se.y, p.x, p.y);
	}

	/**
	 * Returns closest point on segment to point
	 * 
	 * @param sx1
	 *            segment x coord 1
	 * @param sy1
	 *            segment y coord 1
	 * @param sx2
	 *            segment x coord 2
	 * @param sy2
	 *            segment y coord 2
	 * @param px
	 *            point x coord
	 * @param py
	 *            point y coord
	 * @return closets point on segment to point
	 */
	public static Point getClosestPointOnSegment(int sx1, int sy1, int sx2, int sy2, int px, int py)
	{
		double xDelta = sx2 - sx1;
		double yDelta = sy2 - sy1;

		if((xDelta == 0) && (yDelta == 0))
		{
			throw new IllegalArgumentException("Segment start equals segment end");
		}

		double u = ((px - sx1) * xDelta + (py - sy1) * yDelta) / (xDelta * xDelta + yDelta * yDelta);

		final Point closestPoint;
		if(u < 0)
		{
			closestPoint = new Point(sx1, sy1);
		}
		else if(u > 1)
		{
			closestPoint = new Point(sx2, sy2);
		}
		else
		{
			closestPoint = new Point((int) Math.round(sx1 + u * xDelta), (int) Math.round(sy1 + u * yDelta));
		}

		return closestPoint;
	}

	/**
	 * Returns distance to segment
	 * 
	 * @param ss
	 *            segment start point
	 * @param se
	 *            segment end point
	 * @param p
	 *            point to found closest point on segment
	 * @return distance to segment
	 */
	public static double getDistanceToSegment(Point ss, Point se, Point p)
	{
		return getDistanceToSegment(ss.x, ss.y, se.x, se.y, p.x, p.y);
	}

	/**
	 * Returns distance to segment
	 * 
	 * @param sx1
	 *            segment x coord 1
	 * @param sy1
	 *            segment y coord 1
	 * @param sx2
	 *            segment x coord 2
	 * @param sy2
	 *            segment y coord 2
	 * @param px
	 *            point x coord
	 * @param py
	 *            point y coord
	 * @return distance to segment
	 */
	public static double getDistanceToSegment(int sx1, int sy1, int sx2, int sy2, int px, int py)
	{
		Point closestPoint = getClosestPointOnSegment(sx1, sy1, sx2, sy2, px, py);
		return getDistance(closestPoint.x, closestPoint.y, px, py);
	}

	/**
	 * Checks whether two given instances of AionObject are within given range.
	 * 
	 * @param object1
	 * @param object2
	 * @param range
	 * @return true if objects are in range, false otherwise
	 */
	public static boolean isInRange(VisibleObject object1, VisibleObject object2, float range)
	{
		float dx = (object2.getX() - object1.getX());
		float dy = (object2.getY() - object1.getY());
		return dx * dx + dy * dy < range * range;
	}
	
	/**
	 * 
	 * @param obj1X
	 * @param obj1Y
	 * @param obj2X
	 * @param obj2Y
	 * @return float
	 */
	public final static float calculateAngleFrom(float obj1X, float obj1Y, float obj2X, float obj2Y)
	{
		float angleTarget = (float) Math.toDegrees(Math.atan2(obj2Y - obj1Y, obj2X - obj1X));
		if (angleTarget < 0)
			angleTarget = 360 + angleTarget;
		return angleTarget;
	}
	
	/**
	 * 
	 * @param obj1
	 * @param obj2
	 * @return float
	 */
	public static float calculateAngleFrom(VisibleObject obj1, VisibleObject obj2)
	{
		return calculateAngleFrom(obj1.getX(), obj1.getY(), obj2.getX(), obj2.getY());
	}
	
	/**
	 * 
	 * @param clientHeading
	 * @return float
	 */
	public final static float convertHeadingToDegree(byte clientHeading)
	{
		float degree = clientHeading * 3;
		return degree;
	}
}
