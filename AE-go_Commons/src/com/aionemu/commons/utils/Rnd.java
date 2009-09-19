/**
 * This file is part of aion-emu.
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
package com.aionemu.commons.utils;

/**
 * $
 * 
 * @author Balancer
 */
public class Rnd
{
	private static final MTRandom	rnd	= new MTRandom();

	public static float get() // get random number from 0 to 1
	{
		return rnd.nextFloat();
	}

	/**
	 * Gets a random number from 0(inclusive) to n(exclusive)
	 * 
	 * @param n
	 *            The superior limit (exclusive)
	 * @return A number from 0 to n-1
	 */
	public static int get(int n)
	{
		return (int) Math.floor(rnd.nextDouble() * n);
	}

	public static int get(int min, int max) // get random number from
	// min to max (not max-1 !)
	{
		return min + (int) Math.floor(rnd.nextDouble() * (max - min + 1));
	}

	public static int nextInt(int n)
	{
		return (int) Math.floor(rnd.nextDouble() * n);
	}

	public static int nextInt()
	{
		return rnd.nextInt();
	}

	public static double nextDouble()
	{
		return rnd.nextDouble();
	}

	public static double nextGaussian()
	{
		return rnd.nextGaussian();
	}

	public static boolean nextBoolean()
	{
		return rnd.nextBoolean();
	}
}
