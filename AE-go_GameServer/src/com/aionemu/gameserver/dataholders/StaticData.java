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

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * An instance of this class is the result of data loading.
 * 
 * @author Luno, orz
 * 
 */
@XmlRootElement(name = "ae_static_data")
@XmlAccessorType(XmlAccessType.NONE)
public class StaticData
{
	@XmlElement(name = "world_maps")
	public WorldMapsData			worldMapsData;

	@XmlElement(name = "npc_trade_list")
	public TradeListData			tradeListData;

	@XmlElement(name = "npc_teleporter")
	public TeleporterData 			teleporterData;

	@XmlElement(name = "teleport_location")
	public TeleLocationData 		teleLocationData;

	@XmlElement(name = "bind_points")
	public BindPointData 			bindPointData;

	@XmlElement(name = "quests")
	public QuestsData			questData;

	@XmlElement(name = "quest_scripts")
	public QuestScriptsData			questsScriptData;

	@XmlElement(name = "player_experience_table")
	public PlayerExperienceTable	playerExperienceTable;

	@XmlElement(name = "stats_templates")
	public PlayerStatsData			statsData;

	@XmlElement(name = "item_templates")
	public ItemData					itemData;

	@XmlElement(name = "npc_templates")
	public NpcData					npcData;

	@XmlElement(name = "player_initial_data")
	public PlayerInitialData        playerInitialData;

	@XmlElement(name = "skill_data")
	public SkillData				skillData;

	@XmlElement(name = "skill_tree")
	public SkillTreeData			skillTreeData;

	@XmlElement(name = "cube_expander")
	public CubeExpandData			cubeExpandData;

	@XmlElement(name = "warehouse_expander")
	public WarehouseExpandData			warehouseExpandData;

	@XmlElement(name = "player_titles")
	public TitleData				titleData;

	@XmlElement(name = "gatherable_templates")
	public GatherableData			gatherableData;

	@XmlElement(name = "npc_walker")
	public WalkerData				walkerData;

	@XmlElement(name = "zones")
	public ZoneData					zoneData;

	@XmlElement(name = "goodslists")
	public GoodsListData			goodsListData;

	@XmlElement(name = "spawns")
	public SpawnsData				spawnsData;

	@XmlElement(name = "tribe_relations")
	public TribeRelationsData		tribeRelationsData;
	
	@XmlElement(name = "recipe_templates")
	public RecipeData				recipeData;

	// JAXB callback
	@SuppressWarnings("unused")
	private void afterUnmarshal(Unmarshaller unmarshaller, Object parent)
	{
		DataManager.log.info("Loaded world maps data: " + worldMapsData.size() + " maps");
		DataManager.log.info("Loaded player exp table: " + playerExperienceTable.getMaxLevel() + " levels");
		DataManager.log.info("Loaded " + statsData.size() + " stat templates");
		DataManager.log.info("Loaded " + itemData.size() + " item templates");
		DataManager.log.info("Loaded " + npcData.size() + " npc templates");
		DataManager.log.info("Loaded " + playerInitialData.size() + " initial player templates");
		DataManager.log.info("Loaded " + tradeListData.size() + " trade lists");
		DataManager.log.info("Loaded " + teleporterData.size() + " npc teleporter templates");
		DataManager.log.info("Loaded " + teleLocationData.size() + " teleport locations");
		DataManager.log.info("Loaded " + skillData.size() + " skill templates");
		DataManager.log.info("Loaded " + skillTreeData.size() + " skill learn entries");
		DataManager.log.info("Loaded " + cubeExpandData.size() + " cube expand entries");
		DataManager.log.info("Loaded " + warehouseExpandData.size() + " warehouse expand entries");
		DataManager.log.info("Loaded " + bindPointData.size() + " bind point entries");
		DataManager.log.info("Loaded " + questData.size() + " quest data entries");	
		DataManager.log.info("Loaded " + gatherableData.size() + " gatherable entries");
		DataManager.log.info("Loaded " + titleData.size() + " title entries");
		DataManager.log.info("Loaded " + walkerData.size() + " walker routes");
		DataManager.log.info("Loaded " + zoneData.size() + " zone entries");
		DataManager.log.info("Loaded " + goodsListData.size() + " goodslist entries");
		DataManager.log.info("Loaded " + spawnsData.size() + " spawn entries");
		DataManager.log.info("Loaded " + tribeRelationsData.size() + " tribe relation entries");
		DataManager.log.info("Loaded " + recipeData.size() + " recipe entries");
	}
}
