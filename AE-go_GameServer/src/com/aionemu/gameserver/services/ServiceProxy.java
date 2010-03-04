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
package com.aionemu.gameserver.services;

import com.aionemu.gameserver.dataholders.BindPointData;
import com.aionemu.gameserver.dataholders.PlayerInitialData;
import com.aionemu.gameserver.dataholders.PlayerStatsData;
import com.aionemu.gameserver.dataholders.TeleporterData;
import com.aionemu.gameserver.dataholders.TradeListData;
import com.aionemu.gameserver.world.World;
import com.google.inject.Inject;

/**
 * This service is used in object controllers as injecting all services one by one is overhead.
 * 
 * @author ATracer
 * 
 */
public class ServiceProxy
{
	@Inject
	private World					world;
	@Inject
	private AbyssService			abyssService;
	@Inject
	private DropService				dropService;
	@Inject
	private ExchangeService			exchangeService;
	@Inject
	private ItemService				itemService;
	@Inject
	private LegionService			legionService;
	@Inject
	private PrivateStoreService		privateStoreService;
	@Inject
	private RespawnService			respawnService;
	@Inject
	private TeleportService			teleportService;
	@Inject
	private TradeService			tradeService;
	@Inject
	private CubeExpandService		cubeExpandService;
	@Inject
	private SkillLearnService		skillLearnService;
	@Inject
	private GroupService			groupService;
	@Inject
	private CraftSkillUpdateService	craftSkillUpdateService;
	@Inject
	private WarehouseExpandService	warehouseExpandService;
	@Inject
	private BindPointData			bindPointData;
	@Inject
	private TradeListData			tradeListData;
	@Inject
	private TeleporterData			teleporterData;
	@Inject
	private PlayerStatsData			playerStatsData;
	@Inject
	private PlayerInitialData		playerInitialData;

	/**
	 * @return the world
	 */
	public World getWorld()
	{
		return world;
	}

	/**
	 * @return the abyssService
	 */
	public AbyssService getAbyssService()
	{
		return abyssService;
	}

	/**
	 * @return the dropService
	 */
	public DropService getDropService()
	{
		return dropService;
	}

	/**
	 * @return the exchangeService
	 */
	public ExchangeService getExchangeService()
	{
		return exchangeService;
	}

	/**
	 * @return the itemService
	 */
	public ItemService getItemService()
	{
		return itemService;
	}

	/**
	 * @return the legionService
	 */
	public LegionService getLegionService()
	{
		return legionService;
	}

	/**
	 * @return the privateStoreService
	 */
	public PrivateStoreService getPrivateStoreService()
	{
		return privateStoreService;
	}

	/**
	 * @return the respawnService
	 */
	public RespawnService getRespawnService()
	{
		return respawnService;
	}

	/**
	 * @return the teleportService
	 */
	public TeleportService getTeleportService()
	{
		return teleportService;
	}

	/**
	 * @return the tradeService
	 */
	public TradeService getTradeService()
	{
		return tradeService;
	}

	/**
	 * @return the cubeExpandService
	 */
	public CubeExpandService getCubeExpandService()
	{
		return cubeExpandService;
	}

	/**
	 * @return the skillLearnService
	 */
	public SkillLearnService getSkillLearnService()
	{
		return skillLearnService;
	}

	/**
	 * @return groupService
	 */
	public GroupService getGroupService()
	{
		return groupService;
	}

	/**
	 * @return the craftSkillUpdateService
	 */
	public CraftSkillUpdateService getCraftSkillUpdateService()
	{
		return craftSkillUpdateService;
	}

	/**
	 * @return warehouseExpandService
	 */
	public WarehouseExpandService getWarehouseExpandService()
	{
		return warehouseExpandService;
	}

	/**
	 * @return bindPointData
	 */
	public BindPointData getBindPointData()
	{
		return bindPointData;
	}

	/**
	 * @return tradeListData
	 */
	public TradeListData getTradeListData()
	{
		return tradeListData;
	}

	/**
	 * @return teleporterData
	 */
	public TeleporterData getTeleporterData()
	{
		return teleporterData;
	}

	/**
	 * @return the playerStatsData
	 */
	public PlayerStatsData getPlayerStatsData()
	{
		return playerStatsData;
	}

	/**
	 * @return the playerInitialData
	 */
	public PlayerInitialData getPlayerInitialData()
	{
		return playerInitialData;
	}
}
