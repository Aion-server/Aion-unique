/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 * aion-unique is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-unique is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.spawnengine;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.controllers.RiftController;
import com.aionemu.gameserver.controllers.effect.EffectController;
import com.aionemu.gameserver.controllers.factory.RiftControllerFactory;
import com.aionemu.gameserver.dataholders.NpcData;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.templates.NpcTemplate;
import com.aionemu.gameserver.model.templates.spawn.SpawnGroup;
import com.aionemu.gameserver.model.templates.spawn.SpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.aionemu.gameserver.utils.idfactory.IDFactoryAionObject;
import com.aionemu.gameserver.world.KnownList;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * @author ATracer
 *
 */
public class RiftSpawnManager
{
	private static final Logger log = Logger.getLogger(RiftSpawnManager.class);
	
	@Inject
	private NpcData npcData;
	@IDFactoryAionObject
	@Inject
	private IDFactory aionObjectsIDFactory;
	@Inject
	private World	world;
	
	@Inject
	private RiftControllerFactory objectControllerFactory;
	
	private static final int RIFT_RESPAWN_DELAY = 100 * 60 * 1000;
	private static final int RIFT_LIFETIME = 26 * 60 * 1000;
	
	private Map<String, SpawnGroup> spawnGroups = new HashMap<String, SpawnGroup>();
	
	public void addRiftSpawnGroup(SpawnGroup spawnGroup)
	{
		spawnGroups.put(spawnGroup.getAnchor(), spawnGroup);
	}

	/**
	 * 
	 */
	public void startRiftPool()
	{
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable(){
			
			@Override
			public void run()
			{
				RiftEnum rift1 = RiftEnum.values()[Rnd.get(0, 6)];
				RiftEnum rift2 = RiftEnum.values()[Rnd.get(7, 13)];
				RiftEnum rift3 = RiftEnum.values()[Rnd.get(14, 20)];
				RiftEnum rift4 = RiftEnum.values()[Rnd.get(21, 27)];
				
				spawnRift(rift1);
				spawnRift(rift2);
				spawnRift(rift3);
				spawnRift(rift4);
			}
		}, 10000, RIFT_RESPAWN_DELAY);
	}
	
	/**
	 * @param rift1
	 */
	private void spawnRift(RiftEnum rift)
	{
		log.info("Spawning rift : " + rift.name());
		SpawnGroup masterGroup = spawnGroups.get(rift.getMaster());
		SpawnGroup slaveGroup = spawnGroups.get(rift.getSlave());
		
		if(masterGroup == null || slaveGroup == null)
			return;
		
		int instanceCount = world.getWorldMap(masterGroup.getMapid()).getInstanceCount();
		
		SpawnTemplate masterTemplate = masterGroup.getNextRandomTemplate();
		SpawnTemplate slaveTemplate = slaveGroup.getNextRandomTemplate();
		
		for(int i = 1; i <= instanceCount; i++)
		{
			Npc slave = spawnInstance(i, masterGroup, slaveTemplate, new RiftController());
			spawnInstance(i, masterGroup, masterTemplate, objectControllerFactory.create(slave, rift
				.getEntries(), rift.getMaxLevel()));
		}		
	}

	private Npc spawnInstance(int instanceIndex, SpawnGroup spawnGroup, SpawnTemplate spawnTemplate, RiftController riftController)
	{
		NpcTemplate masterObjectTemplate = npcData.getNpcTemplate(spawnGroup.getNpcid());
		Npc npc = new Npc(aionObjectsIDFactory.nextId(),riftController,
			spawnTemplate, masterObjectTemplate);
		
		npc.setKnownlist(new KnownList(npc));
		npc.setEffectController(new EffectController(npc));
		npc.getController().onRespawn();
		
		world.storeObject(npc);
		world.setPosition(npc, spawnTemplate.getWorldId(), instanceIndex, 
			spawnTemplate.getX(), spawnTemplate.getY(), spawnTemplate.getZ(), spawnTemplate.getHeading());
		world.spawn(npc);
		
		scheduleDespawn(npc);
		return npc;
	}

	/**
	 * @param npc
	 */
	private void scheduleDespawn(final Npc npc)
	{
		ThreadPoolManager.getInstance().schedule(new Runnable()
		{
			@Override
			public void run()
			{
				if(npc != null && !npc.isSpawned())
				{
					PacketSendUtility.broadcastPacket(npc, new SM_DELETE(npc));
					world.despawn(npc);	
				}	
			}
		}, RIFT_LIFETIME);
	}

	private enum RiftEnum
	{
		ELTNEN_AM("ELTNEN_AM", "MORHEIM_AS", 12, 28),
		ELTNEN_BM("ELTNEN_BM", "MORHEIM_BS", 20, 32),
		ELTNEN_CM("ELTNEN_CM", "MORHEIM_CS", 35, 36),
		ELTNEN_DM("ELTNEN_DM", "MORHEIM_DS", 35, 37),
		ELTNEN_EM("ELTNEN_EM", "MORHEIM_ES", 45, 40),
		ELTNEN_FM("ELTNEN_FM", "MORHEIM_FS", 50, 40),
		ELTNEN_GM("ELTNEN_GM", "MORHEIM_GS", 50, 45),
		
		HEIRON_AM("HEIRON_AM", "BELUSLAN_AS", 24, 35),
		HEIRON_BM("HEIRON_BM", "BELUSLAN_BS", 36, 35),
		HEIRON_CM("HEIRON_CM", "BELUSLAN_CS", 48, 46),
		HEIRON_DM("HEIRON_DM", "BELUSLAN_DS", 48, 40),
		HEIRON_EM("HEIRON_EM", "BELUSLAN_ES", 60, 50),
		HEIRON_FM("HEIRON_FM", "BELUSLAN_FS", 60, 50),
		HEIRON_GM("HEIRON_GM", "BELUSLAN_GS", 72, 50),
		
		MORHEIM_AM("MORHEIM_AM", "ELTNEN_AS", 12, 28),
		MORHEIM_BM("MORHEIM_BM", "ELTNEN_BS", 20, 32),
		MORHEIM_CM("MORHEIM_CM", "ELTNEN_CS", 35, 36),
		MORHEIM_DM("MORHEIM_DM", "ELTNEN_DS", 35, 37),
		MORHEIM_EM("MORHEIM_EM", "ELTNEN_ES", 45, 40),
		MORHEIM_FM("MORHEIM_FM", "ELTNEN_FS", 50, 40),
		MORHEIM_GM("MORHEIM_GM", "ELTNEN_GS", 50, 45),
		
		BELUSLAN_AM("BELUSLAN_AM", "HEIRON_AS", 24, 35),
		BELUSLAN_BM("BELUSLAN_BM", "HEIRON_BS", 36, 35),
		BELUSLAN_CM("BELUSLAN_CM", "HEIRON_CS", 48, 46),
		BELUSLAN_DM("BELUSLAN_DM", "HEIRON_DS", 48, 40),
		BELUSLAN_EM("BELUSLAN_EM", "HEIRON_ES", 60, 50),
		BELUSLAN_FM("BELUSLAN_FM", "HEIRON_FS", 60, 50),
		BELUSLAN_GM("BELUSLAN_GM", "HEIRON_GS", 72, 50);
		
		private String master;
		private String slave;
		private int entries;
		private int maxLevel;
		
		private RiftEnum(String master, String slave, int entries, int maxLevel)
		{
			this.master = master;
			this.slave = slave;
			this.entries = entries;
			this.maxLevel = maxLevel;
		}

		/**
		 * @return the master
		 */
		public String getMaster()
		{
			return master;
		}

		/**
		 * @return the slave
		 */
		public String getSlave()
		{
			return slave;
		}

		/**
		 * @return the entries
		 */
		public int getEntries()
		{
			return entries;
		}

		/**
		 * @return the maxLevel
		 */
		public int getMaxLevel()
		{
			return maxLevel;
		}
	}
}
