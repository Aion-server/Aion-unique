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
package com.aionemu.gameserver.dataholders;

import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.aionemu.gameserver.model.templates.NpcTemplate;
import com.aionemu.gameserver.model.templates.SpawnTemplate;
import com.aionemu.gameserver.utils.collections.IteratorIterator;

/**
 * This is a container holding informations about all spawn definitions, represented by {@link SpawnTemplate}, loaded
 * from spawn data files.
 * 
 * @author Luno
 * 
 */
public class SpawnData extends DataLoader implements Iterable<SpawnTemplate>
{
	/** Map of all spawn templates, divided by mapId (which is a key in this map) */
	private Map<Integer, Set<SpawnTemplate>>	spawns	= new HashMap<Integer, Set<SpawnTemplate>>();

	/** Reference to singleton {@link NpcData}, used to get NpcTemplate */
	private NpcData								npcData;

	/** Counter counting number of spawns */
	private int									i		= 0;

	/**
	 * SpawnData constructor, should be called only from {@link DataManager} class.
	 * 
	 * @param npcData
	 */
	SpawnData(NpcData npcData)
	{
		super("spawns");
		this.npcData = npcData;

		loadData();
		log.info("Loaded " + i + " spawns");
	}

	/**
	 * Reloads whole spawn data.
	 */
	public void reloadData()
	{
		clearSpawns();
		loadData();
	}

	/**
	 * Clean whole spawn data. <br>
	 * <font color="red"><b>NOTICE:</b></font> It doesn't despawn NPCs from the world.
	 */
	public void clearSpawns()
	{
		spawns.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<SpawnTemplate> iterator()
	{
		return new IteratorIterator<SpawnTemplate>(spawns.values());
	}

	/**
	 * Creates {@link SpawnTemplate} from given parameters and forward it to {@link #addSpawn(SpawnTemplate)} method.
	 * 
	 * @param worldId
	 * @param npcId
	 * @param x
	 * @param y
	 * @param z
	 * @param heading
	 * @return SpawnTemplate object representing new spawn or null when there is no template for npcId
	 */
	public SpawnTemplate addNewSpawn(int worldId, int npcId, float x, float y, float z, byte heading)
	{
		NpcTemplate npcTemplate = npcData.getNpcTemplate(npcId);
		if(npcTemplate == null)
			return null;

		SpawnTemplate spawnTemplate = new SpawnTemplate(npcTemplate, worldId, x, y, z, heading);
		addSpawn(spawnTemplate);

		return spawnTemplate;
	}

	/**
	 * Removes given instance of {@link SpawnTemplate} from the collection holding all spawn templates.
	 * 
	 * @param spawn
	 *            SpawnTemplate to be removed
	 */
	public void removeSpawn(SpawnTemplate spawn)
	{
		Set<SpawnTemplate> worldSpawns = spawns.get(spawn.getWorldId());
		if(worldSpawns != null)
		{
			worldSpawns.remove(spawn);
		}
	}

	/**
	 * Parses the string containing info about spawn entry.<br>
	 * After parsing a row {@link #addNewSpawn(int, int, float, float, float, byte)} method is being called.
	 */
	@Override
	protected void parse(String params)
	{
		String[] splt = params.split(" ");
		if(splt.length < 5)
			return;

		int worldId = Integer.parseInt(splt[0]);
		int npcId = Integer.parseInt(splt[1]);

		float x = Float.parseFloat(splt[2]);
		float y = Float.parseFloat(splt[3]);
		float z = Float.parseFloat(splt[4]);

		byte heading = 0;
		if(splt.length >= 6)
			heading = Byte.parseByte(splt[5]);

		if(addNewSpawn(worldId, npcId, x, y, z, heading) == null)
			log.warn("No npc template for id " + npcId);
	}

	/**
	 * Fills the given <tt>FileWriter</tt> with spawn data informations.
	 */
	@Override
	protected void saveEntries(FileWriter fr) throws Exception
	{

		for(SpawnTemplate temp : this)
		{
			fr.write(temp.getWorldId() + " " + temp.getNpc().getNpcId() + " " + temp.getX() + " " + temp.getY() + " "
				+ temp.getZ() + " " + temp.getHeading() + "\n");
			fr.flush();
		}
	}

	/**
	 * Adds given instance of <tt>SpawnTemplate</tt>.
	 * 
	 * @param spawn
	 */
	private void addSpawn(SpawnTemplate spawn)
	{
		Set<SpawnTemplate> worldSpawns = spawns.get(spawn.getWorldId());
		if(worldSpawns == null)
		{
			worldSpawns = new HashSet<SpawnTemplate>();
			spawns.put(spawn.getWorldId(), worldSpawns);
		}
		worldSpawns.add(spawn);
		i++;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getSaveFile()
	{
		return "spawns/new/new_spawndata.txt";
	}
}
