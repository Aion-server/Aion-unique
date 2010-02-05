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
package com.aionemu.gameserver.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

import com.aionemu.gameserver.utils.collections.IteratorIterator;

/**
 * @author Luno
 * 
 */
public class IteratorIteratorTest
{
	@SuppressWarnings("unchecked")
	@Test
	public void testIt()
	{
		Map<Integer, Set<Integer>> map1 = new HashMap<Integer, Set<Integer>>();

		Set set1 = new HashSet<Integer>();
		Collections.addAll(set1, 50, 60, 70, 80);

		Set set2 = new HashSet<Integer>();
		Collections.addAll(set2, 1);

		Set set3 = new HashSet<Integer>();
		Collections.addAll(set3);

		Set set4 = new HashSet<Integer>();
		Collections.addAll(set4, 99, 100);

		map1.put(1, set1);
		map1.put(2, set2);
		map1.put(3, set3);
		map1.put(4, set4);

		List<Integer> res = new ArrayList<Integer>();

		IteratorIterator<Integer> it = new IteratorIterator<Integer>(map1.values());

		int valNum = 0;
		while (it.hasNext())
		{
			valNum++;
			Integer v = it.next();
			res.add(v);
		}

		List<Integer> goodRes = new ArrayList<Integer>();
		Collections.addAll(goodRes, 50, 60, 70, 80, 1, 99, 100);

		Assert.assertEquals(7, valNum);
		Assert.assertTrue(res.containsAll(goodRes) && goodRes.containsAll(res));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testIt1()
	{
		List<List<String>> li = new ArrayList<List<String>>();

		List<String> li1 = new ArrayList<String>();
		Collections.addAll(li1, "a", "b", "c", "d");

		List<String> li2 = new ArrayList<String>();
		Collections.addAll(li2);

		List<String> li3 = new ArrayList<String>();
		Collections.addAll(li3, "x");
		
		Collections.addAll(li, li1,li2,li3);
		
		
		List<String> goodRes = new ArrayList<String>();
		Collections.addAll(goodRes, "a","b","c","d","x");
		
		List<String> res = new ArrayList<String>();
		int count = 0;
		IteratorIterator<String> it = new IteratorIterator<String>(li);
		
		while(it.hasNext())
		{
			count++;
			res.add(it.next());
		}
		
		Assert.assertTrue(goodRes.containsAll(res) && res.containsAll(goodRes));
		Assert.assertEquals(count, 5);
	}
	@Test
	public void nullTest()
	{
		List<List<String>> li = new ArrayList<List<String>>();
		li.add(null);
		
		IteratorIterator<String> it = new IteratorIterator<String>(li);
		
		try
		{
			Assert.assertEquals(it.hasNext(), false);
		}catch(Exception e)
		{
			Assert.fail(e.getMessage());
		}
	}
}
