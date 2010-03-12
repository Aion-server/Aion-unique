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
package com.aionemu.gameserver.model.gameobjects.player;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javolution.util.FastMap;

import com.aionemu.gameserver.model.gameobjects.Letter;
/**
 * @author kosyachok
 *
 */
public class Mailbox
{
	private SortedMap<Timestamp,Letter> letters = new TreeMap<Timestamp, Letter>(Collections.reverseOrder());
	private Player owner;
	private int freeSlots;
	
	public Mailbox(Player owner)
	{
		this.owner = owner;
		this.freeSlots = 65536;
	}
	
	public Player getOwner()
	{
		return owner;
	}
	
	public void putLetterToMailbox(Timestamp timestamp ,Letter letter)
	{
		letters.put(timestamp, letter);
		
		freeSlots--;
	}
	
	public SortedMap<Timestamp,Letter> getLettersWithTimestamp()
	{
		return letters;
	}
	
	public List<Letter> getLettersOnly()
	{
		List<Letter> lettersList = new ArrayList<Letter>();
		for(Letter letter : letters.values())
		{
			lettersList.add(letter);
		}
		
		return lettersList;
	}
	
	public FastMap<Timestamp,Letter> getLetterFromMailboxWithTimestamp(int letterObjId)
	{
		FastMap<Timestamp,Letter> fmLetter = new FastMap<Timestamp, Letter>();
		Iterator<Timestamp> iterator = letters.keySet().iterator();
		Letter letter;
		Timestamp time;
		
		while(iterator.hasNext())
		{
			time = iterator.next();
			letter = letters.get(time);
			
			if(letter.getObjectId() == letterObjId)
			{
				fmLetter.put(time, letter);
				return fmLetter;
			}
		}
		
		return null;
	}
	
	public Letter getLetterFromMailbox(int letterObjId)
	{
		for(Letter letter : letters.values())
			if(letter.getObjectId() == letterObjId)
				return letter;
		
		return null;
	}
	
	
	public boolean haveUnread()
	{
		for(Letter letter : letters.values())
		{
			if(letter.isUnread())
				return true;
		}
		
		return false;
	}
	
	public int getFreeSlots()
	{
		return freeSlots;
	}
	
	public boolean haveFreeSlots()
	{
		return 65536 - freeSlots < 100;
	}
	
	public void deleteLetter(int letterId)
	{
		Iterator<Timestamp> iterator = letters.keySet().iterator();
		Letter letter;
		Timestamp time;
		
		while(iterator.hasNext())
		{
			time = iterator.next();
			letter = letters.get(time);
			
			if(letter.getObjectId() == letterId)
			{
				letters.remove(time);
				freeSlots++;
			}
		}
	}
	
	public int getLettersCount()
	{
		return letters.size();
	}
}
