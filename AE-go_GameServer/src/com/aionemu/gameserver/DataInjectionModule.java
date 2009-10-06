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
package com.aionemu.gameserver;

import com.aionemu.commons.services.ScriptService;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.dataholders.NpcData;
import com.aionemu.gameserver.dataholders.PlayerExperienceTable;
import com.aionemu.gameserver.dataholders.PlayerInitialData;
import com.aionemu.gameserver.dataholders.PlayerStatsData;
import com.aionemu.gameserver.dataholders.SkillData;
import com.aionemu.gameserver.dataholders.SpawnData;
import com.aionemu.gameserver.dataholders.TradeListData;
import com.aionemu.gameserver.dataholders.WorldMapsData;
import com.aionemu.gameserver.services.AccountService;
import com.aionemu.gameserver.services.PlayerService;
import com.aionemu.gameserver.services.SocialService;
import com.aionemu.gameserver.skillengine.SkillEngine;
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
		// binds DataManager and creates its singleton immediately
		bind(DataManager.class).asEagerSingleton();
		// binds World and creates its singleton immediately
		bind(World.class).asEagerSingleton();
		// binds Spawn engine and creates its singleton immediately
		bind(SpawnEngine.class).asEagerSingleton();
		// binds PlayerService as singleton
		bind(PlayerService.class).in(Scopes.SINGLETON);
		// binds AccountService as singleton
		bind(AccountService.class).in(Scopes.SINGLETON);
		// binds SocialService as singleton
		bind(SocialService.class).in(Scopes.SINGLETON);
		// binds ScriptService as singleton
		bind(ScriptService.class).in(Scopes.SINGLETON);
	}

	@Provides
	SpawnData provideSpawnData(DataManager datamanager)
	{
		return datamanager.SPAWN_DATA;
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

	@Provides
	PlayerExperienceTable providePlayerExpTable(DataManager datamanager)
	{
		return datamanager.PLAYER_EXPERIENCE_TABLE;
	}
	
	@Provides
	@Singleton
	ChatHandlers provideChatHandlers()
	{
		return new ChatHandlersFactory(injector).createChatHandlers();
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
	
	@Provides
	TradeListData provideTradeListData(DataManager datamanager)
	{
		return datamanager.TRADE_LIST_DATA;
	}
	
	@Provides
	SkillData provideSkillData(DataManager datamanager)
	{
		return datamanager.SKILL_DATA;
	}
}
