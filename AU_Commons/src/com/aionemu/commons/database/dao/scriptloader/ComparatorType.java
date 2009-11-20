/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 * aion-emu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-emu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aionemu.commons.database.dao.scriptloader;

/**
 * Type of comparator that should be used to compare DB versions
 * 
 * @author SoulKeeper
 */
public enum ComparatorType
{

	/**
	 * <
	 */
	LESS,

	/**
	 * <=
	 */
	LESS_OR_EQUAL,

	/**
	 * ==
	 */
	EQUAL,

	/**
	 * >=
	 */
	GREATER_OR_EQUAL,

	/**
	 * >
	 */
	GREATER;

	public static boolean compare(ComparatorType[] cts, Pair[] nums){

		if(cts.length != nums.length){
			throw new RuntimeException("Number of comparators and arguments should be equal");
		}

		for(int i = 0; i < cts.length; i++){
			ComparatorType ct = cts[i];
			Pair numbers = nums[i];

			switch(ct){
				case LESS:
					if(numbers.getN1() >= numbers.getN2()){
						return false;
					}
					break;
				case LESS_OR_EQUAL:
					if(numbers.getN1() > numbers.getN2()){
						return false;
					}
					break;
				case EQUAL:
					if(numbers.getN1() != numbers.getN2()){
						return false;
					}
					break;
				case GREATER_OR_EQUAL:
					if(numbers.getN1() < numbers.getN2()){
						return false;
					}
					break;
				case GREATER:
					if(numbers.getN1() <= numbers.getN2()){
						return false;
					}
					break;
			}
		}

		return true;
	}

	public static class Pair{

		private final int n1;

		private final int n2;

		public Pair(int n1, int n2) {
			this.n1 = n1;
			this.n2 = n2;
		}

		public int getN1() {
			return n1;
		}

		public int getN2() {
			return n2;
		}
	}
}
