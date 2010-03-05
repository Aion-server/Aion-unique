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
package com.aionemu.gameserver.utils.guice;

import com.aionemu.commons.services.ScriptService;
import com.aionemu.gameserver.dataholders.BindPointData;
import com.aionemu.gameserver.dataholders.CubeExpandData;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.dataholders.GatherableData;
import com.aionemu.gameserver.dataholders.GoodsListData;
import com.aionemu.gameserver.dataholders.ItemData;
import com.aionemu.gameserver.dataholders.NpcData;
import com.aionemu.gameserver.dataholders.PlayerExperienceTable;
import com.aionemu.gameserver.dataholders.PlayerInitialData;
import com.aionemu.gameserver.dataholders.PlayerStatsData;
import com.aionemu.gameserver.dataholders.SkillData;
import com.aionemu.gameserver.dataholders.SkillTreeData;
import com.aionemu.gameserver.dataholders.SpawnsData;
import com.aionemu.gameserver.dataholders.TeleLocationData;
import com.aionemu.gameserver.dataholders.TeleporterData;
import com.aionemu.gameserver.dataholders.TradeListData;
import com.aionemu.gameserver.dataholders.WalkerData;
import com.aionemu.gameserver.dataholders.WarehouseExpandData;
import com.aionemu.gameserver.dataholders.WorldMapsData;
import com.aionemu.gameserver.services.AbyssService;
import com.aionemu.gameserver.services.AccountService;
import com.aionemu.gameserver.services.CraftSkillUpdateService;
import com.aionemu.gameserver.services.CubeExpandService;
import com.aionemu.gameserver.services.DropService;
import com.aionemu.gameserver.services.ExchangeService;
import com.aionemu.gameserver.services.GroupService;
import com.aionemu.gameserver.services.ItemService;
import com.aionemu.gameserver.services.LegionService;
import com.aionemu.gameserver.services.PlayerService;
import com.aionemu.gameserver.services.PrivateStoreService;
import com.aionemu.gameserver.services.PunishmentService;
import com.aionemu.gameserver.services.RespawnService;
import com.aionemu.gameserver.services.ServiceProxy;
import com.aionemu.gameserver.services.SkillLearnService;
import com.aionemu.gameserver.services.SocialService;
import com.aionemu.gameserver.services.TeleportService;
import com.aionemu.gameserver.services.TradeService;
import com.aionemu.gameserver.services.WarehouseExpandService;
import com.aionemu.gameserver.services.WeatherService;
import com.aionemu.gameserver.spawnengine.RiftSpawnManager;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.chathandlers.ChatHandlers;
import com.aionemu.gameserver.utils.chathandlers.ChatHandlersFactory;
import com.aionemu.gameserver.world.World;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;

/**
 * This is a configuration module for <tt>Injector</tt> that is used in aion-emu.<br>
 * 
 * @author Luno
 * 
 */
public class DataInjectionModule extends AbstractModule
{
	private Injector	injector;

	public void setInjector(Injector injector)
	{
		this.injector = injector;
	}

	@Override
	protected void configure()
	{
		bind(DataManager.class).asEagerSingleton();
		bind(World.class).asEagerSingleton();
		bind(SpawnEngine.class).asEagerSingleton();
		bind(PlayerService.class).in(Scopes.SINGLETON);
		bind(AccountService.class).in(Scopes.SINGLETON);
		bind(SocialService.class).in(Scopes.SINGLETON);
		bind(ScriptService.class).in(Scopes.SINGLETON);
		bind(ItemService.class).in(Scopes.SINGLETON);
		bind(TradeService.class).in(Scopes.SINGLETON);
		bind(DropService.class).asEagerSingleton();
		bind(CubeExpandService.class).in(Scopes.SINGLETON);
		bind(RiftSpawnManager.class).in(Scopes.SINGLETON);
		bind(ExchangeService.class).in(Scopes.SINGLETON);
		bind(WeatherService.class).in(Scopes.SINGLETON);
		bind(LegionService.class).in(Scopes.SINGLETON);
		bind(PrivateStoreService.class).in(Scopes.SINGLETON);
		bind(AbyssService.class).in(Scopes.SINGLETON);
		bind(RespawnService.class).in(Scopes.SINGLETON);
		bind(TeleportService.class).in(Scopes.SINGLETON);
		bind(SkillLearnService.class).in(Scopes.SINGLETON);
		bind(ServiceProxy.class).in(Scopes.SINGLETON);
		bind(GroupService.class).in(Scopes.SINGLETON);
		bind(CraftSkillUpdateService.class).in(Scopes.SINGLETON);
		bind(WarehouseExpandService.class).in(Scopes.SINGLETON);
		bind(PunishmentService.class).in(Scopes.SINGLETON);
	}

	@Provides
	SpawnsData provideSpawnData(DataManager datamanager)
	{
		return datamanager.SPAWNS_DATA;
	}

	@Provides
	WorldMapsData provideWorldMapsData(DataManager datamanager)
	{
		return datamanager.WORLD_MAPS_DATA;
	}	

	@Provides
	NpcData provideNpcData(DataManager datamanager)
	{
		return datamanager.NPC_DATA;
	}
	
	@SuppressWarnings("static-access")
	@Provides
	ItemData provideItemData(DataManager datamanager)
	{
		return datamanager.ITEM_DATA;
	}

	@Provides
	CubeExpandData provideCubeExpand(DataManager datamanager)
	{
		return datamanager.CUBEEXPANDER_DATA;
	}

	@Provides
	WarehouseExpandData provideWarehouseExpand(DataManager datamanager)
	{
		return datamanager.WAREHOUSEEXPANDER_DATA;
	}
	
	@Provides
	GatherableData provideGatherableData(DataManager datamanager)
	{
		return datamanager.GATHERABLE_DATA;
	}
	
	@Provides
	BindPointData provideBindPointData(DataManager datamanager)
	{
		return datamanager.BIND_POINT_DATA;
	}
	
	@Provides
	@Singleton
	ChatHandlers provideChatHandlers()
	{
		return new ChatHandlersFactory(injector).createChatHandlers();
	}
	
	@Provides
	TradeListData provideTradeListData(DataManager datamanager)
	{
		return datamanager.TRADE_LIST_DATA;
	}
	
	@Provides
	TeleporterData provideTeleporterData(DataManager datamanager)
	{
		return datamanager.TELEPORTER_DATA;
	}
	
	@Provides
	TeleLocationData provideTeleLocationData(DataManager datamanager)
	{
		return datamanager.TELELOCATION_DATA;
	}
	
	@Provides
	GoodsListData provideGoodsListData(DataManager datamanager)
	{
		return datamanager.GOODSLIST_DATA;
	}
	
	@Provides
	SkillTreeData provideSkillTreeData(DataManager datamanager)
	{
		return datamanager.SKILL_TREE_DATA;
	}
	
	@SuppressWarnings("static-access")
	@Provides
	SkillData provideSkillData(DataManager datamanager)
	{
		return datamanager.SKILL_DATA;
	}
	
	@SuppressWarnings("static-access")
	@Provides
	WalkerData provideWalkerData(DataManager datamanager)
	{
		return datamanager.WALKER_DATA;
	}

	@SuppressWarnings("static-access")
	@Provides
	PlayerExperienceTable providePlayerExpTable(DataManager datamanager)
	{
		return datamanager.PLAYER_EXPERIENCE_TABLE;
	}

	@Provides
	PlayerStatsData providePlayerStatsData(DataManager datamanager)
	{
		return datamanager.PLAYER_STATS_DATA;
	}

	@Provides
	PlayerInitialData providePlayerInitialData(DataManager datamanager)
	{
		return datamanager.PLAYER_INITIAL_DATA;
	}
}
