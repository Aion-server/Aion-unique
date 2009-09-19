package com.aionemu.gameserver.model.gameobjects.player;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

/**
 * Player macrosses collection, contains all player macrosses.
 * <p/>
 * Created on: 13.07.2009 16:28:23
 * 
 * @author Aquanox
 */
public class MacroList
{
	/**
	 * Class logger
	 */
	private static final Logger			logger	= Logger.getLogger(MacroList.class);

	/**
	 * Container of macrosses, position to xml.
	 */
	private final Map<Integer, String>	macrosses;

	/**
	 * Creates an empty macro list
	 */
	public MacroList()
	{
		this.macrosses = new HashMap<Integer, String>();
	}

	/**
	 * Create new instance of <tt>MacroList</tt>.
	 * 
	 * @param arg
	 */
	public MacroList(Map<Integer, String> arg)
	{
		this.macrosses = arg;
	}

	/**
	 * Returns map with all macrosses
	 * 
	 * @return all macrosses
	 */
	public Map<Integer, String> getMacrosses()
	{
		return Collections.unmodifiableMap(macrosses);
	}

	/**
	 * Add macro to the collection.
	 * 
	 * @param macroPosition
	 *            Macro order.
	 * @param macroXML
	 *            Macro Xml contents.
	 * @return <tt>true</tt> if macro addition was successful, and it can be stored into database. Otherwise
	 *         <tt>false</tt>.
	 */
	public synchronized boolean addMacro(int macroPosition, String macroXML)
	{
		if(macrosses.containsKey(macroPosition))
		{
			logger.warn("Trying to add macro with already existing order.");
			return false;
		}

		macrosses.put(macroPosition, macroXML);
		return true;
	}

	/**
	 * Remove macro from the list.
	 * 
	 * @param macroPosition
	 * @return <tt>true</tt> if macro deletion was successful, and changes can be stored into database. Otherwise
	 *         <tt>false</tt>.
	 */
	public synchronized boolean removeMacro(int macroPosition)
	{
		String m = macrosses.remove(macroPosition);
		if(m == null)//
		{
			logger.warn("Trying to remove non existing macro.");
			return false;
		}
		return true;
	}

	/**
	 * Returns count of available macrosses.
	 * 
	 * @return count of available macrosses.
	 */
	public int getSize()
	{
		return macrosses.size();
	}

	/**
	 * Returns an entry set of macro id to macro contents.
	 */
	public Set<Entry<Integer, String>> entrySet()
	{
		return Collections.unmodifiableSet(getMacrosses().entrySet());
	}
}
