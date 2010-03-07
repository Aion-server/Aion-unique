/**
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
package com.aionemu.gameserver.model.gameobjects.player;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.commons.callbacks.Enhancable;
import com.aionemu.gameserver.controllers.PlayerController;
import com.aionemu.gameserver.controllers.effect.PlayerEffectController;
import com.aionemu.gameserver.model.Gender;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.listeners.PlayerLoggedInListener;
import com.aionemu.gameserver.model.gameobjects.player.listeners.PlayerLoggedOutListener;
import com.aionemu.gameserver.model.gameobjects.state.CreatureVisualState;
import com.aionemu.gameserver.model.gameobjects.stats.PlayerGameStats;
import com.aionemu.gameserver.model.gameobjects.stats.PlayerLifeStats;
import com.aionemu.gameserver.model.group.PlayerGroup;
import com.aionemu.gameserver.model.legion.Legion;
import com.aionemu.gameserver.model.legion.LegionMember;
import com.aionemu.gameserver.model.templates.stats.PlayerStatsTemplate;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.PlayerService;
import com.aionemu.gameserver.utils.rates.Rates;
import com.aionemu.gameserver.world.zone.ZoneInstance;

/**
 * This class is representing Player object, it contains all needed data.
 * 
 * 
 * @author -Nemesiss-
 * @author SoulKeeper
 * @author alexa026
 * 
 */
public class Player extends Creature
{
	private PlayerAppearance	playerAppearance;
	private PlayerCommonData	playerCommonData;
	private LegionMember		legionMember;
	private MacroList			macroList;
	private SkillList			skillList;
	private FriendList			friendList;
	private BlockList			blockList;
	private ResponseRequester	requester;
	private boolean				lookingForGroup	= false;
	private Storage				inventory;
	private Storage				regularWarehouse;
	private Storage				accountWarehouse;
	private Equipment			equipment;
	private PrivateStore		store;
	private PlayerStatsTemplate	playerStatsTemplate;
	private TitleList			titleList;
	private PlayerSettings		playerSettings;
	private QuestStateList		questStateList;
	private List<Integer>		nearbyQuestList	= new ArrayList<Integer>();
	private ZoneInstance		zoneInstance;
	private PlayerGroup			playerGroup;
	private AbyssRank			abyssRank;
	private Rates				rates;
	private RecipeList			recipeList;
	private int					flyState		= 0;
	private boolean				isTrading;
	private long				prisonTimer		= 0;
	private boolean				invul;
	/**
	 * Static information for players
	 */
	private static final int		CUBE_SPACE				= 9;
	private static final int		WAREHOUSE_SPACE			= 8;

	/**
	 * Connection of this Player.
	 */
	private AionConnection			clientConnection;

	public Player(PlayerController controller, PlayerCommonData plCommonData, PlayerAppearance appereance)
	{
		super(plCommonData.getPlayerObjId(), controller, null, null, plCommonData.getPosition());
		// TODO may be pcd->visibleObjectTemplate ?
		this.playerCommonData = plCommonData;
		this.playerAppearance = appereance;

		this.requester = new ResponseRequester(this);
		this.questStateList = new QuestStateList();
		this.titleList = new TitleList();
		controller.setOwner(this);

	}

	public PlayerCommonData getCommonData()
	{
		return playerCommonData;
	}

	@Override
	public String getName()
	{
		return playerCommonData.getName();
	}

	public PlayerAppearance getPlayerAppearance()
	{
		return playerAppearance;
	}

	/**
	 * Set connection of this player.
	 * 
	 * @param clientConnection
	 */
	public void setClientConnection(AionConnection clientConnection)
	{
		this.clientConnection = clientConnection;
	}

	/**
	 * Get connection of this player.
	 * 
	 * @return AionConnection of this player.
	 * 
	 */
	public AionConnection getClientConnection()
	{
		return this.clientConnection;
	}

	public MacroList getMacroList()
	{
		return macroList;
	}

	public void setMacroList(MacroList macroList)
	{
		this.macroList = macroList;
	}

	public SkillList getSkillList()
	{
		return skillList;
	}

	public void setSkillList(SkillList skillList)
	{
		this.skillList = skillList;
	}

	/**
	 * Gets this players Friend List
	 * 
	 * @return FriendList
	 */
	public FriendList getFriendList()
	{
		return friendList;
	}

	/**
	 * Is this player looking for a group
	 * 
	 * @return true or false
	 */
	public boolean isLookingForGroup()
	{
		return lookingForGroup;
	}

	/**
	 * Sets whether or not this player is looking for a group
	 * 
	 * @param lookingForGroup
	 */
	public void setLookingForGroup(boolean lookingForGroup)
	{
		this.lookingForGroup = lookingForGroup;
	}

	/**
	 * Sets this players friend list. <br />
	 * Remember to send the player the <tt>SM_FRIEND_LIST</tt> packet.
	 * 
	 * @param list
	 */
	public void setFriendList(FriendList list)
	{
		this.friendList = list;
	}

	public BlockList getBlockList()
	{
		return blockList;
	}

	public void setBlockList(BlockList list)
	{
		this.blockList = list;
	}

	/**
	 * @return the playerLifeStats
	 */
	@Override
	public PlayerLifeStats getLifeStats()
	{
		return (PlayerLifeStats) super.getLifeStats();
	}

	/**
	 * @param lifeStats
	 *            the lifeStats to set
	 */
	public void setLifeStats(PlayerLifeStats lifeStats)
	{
		super.setLifeStats(lifeStats);
	}

	/**
	 * @return the gameStats
	 */
	@Override
	public PlayerGameStats getGameStats()
	{
		return (PlayerGameStats) super.getGameStats();
	}

	/**
	 * @param gameStats
	 *            the gameStats to set
	 */
	public void setGameStats(PlayerGameStats gameStats)
	{
		super.setGameStats(gameStats);
	}

	/**
	 * Gets the ResponseRequester for this player
	 * 
	 * @return ResponseRequester
	 */
	public ResponseRequester getResponseRequester()
	{
		return requester;
	}

	public boolean isOnline()
	{
		return getClientConnection() != null;
	}

	public int getCubeSize()
	{
		return this.playerCommonData.getCubeSize();
	}

	public PlayerClass getPlayerClass()
	{
		return playerCommonData.getPlayerClass();
	}

	public Gender getGender()
	{
		return playerCommonData.getGender();
	}

	/**
	 * Return PlayerController of this Player Object.
	 * 
	 * @return PlayerController.
	 */
	@Override
	public PlayerController getController()
	{
		return (PlayerController) super.getController();
	}

	@Override
	public byte getLevel()
	{
		return (byte) playerCommonData.getLevel();
	}

	/**
	 * @return the inventory
	 */

	public Equipment getEquipment()
	{
		return equipment;
	}

	public void setEquipment(Equipment equipment)
	{
		this.equipment = equipment;
	}

	/**
	 * @return the player private store
	 */
	public PrivateStore getStore()
	{
		return store;
	}

	/**
	 * @param store the store that needs to be set
	 */
	public void setStore(PrivateStore store)
	{
		this.store = store;
	}

	/**
	 * @return the questStatesList
	 */
	public QuestStateList getQuestStateList()
	{
		return questStateList;
	}

	/**
	 * @param questStateList
	 *            the QuestStateList to set
	 */
	public void setQuestStateList(QuestStateList questStateList)
	{
		this.questStateList = questStateList;
	}

	/**
	 * @return the playerStatsTemplate
	 */
	public PlayerStatsTemplate getPlayerStatsTemplate()
	{
		return playerStatsTemplate;
	}

	/**
	 * @param playerStatsTemplate
	 *            the playerStatsTemplate to set
	 */
	public void setPlayerStatsTemplate(PlayerStatsTemplate playerStatsTemplate)
	{
		this.playerStatsTemplate = playerStatsTemplate;
	}

	public void updateNearbyQuests()
	{
		nearbyQuestList.clear();
		for(VisibleObject obj : getKnownList())
		{
			if(obj instanceof Npc)
			{
				for(int questId : QuestEngine.getInstance().getNpcQuestData(((Npc) obj).getNpcId()).getOnQuestStart())
				{
					QuestEnv env = new QuestEnv(obj, this, questId, 0);
					if(QuestEngine.getInstance().getQuest(env).checkStartCondition())
					{
						if(!nearbyQuestList.contains(questId))
						{
							nearbyQuestList.add(questId);
						}
					}
				}
			}
		}
		getController().updateNearbyQuestList();
	}

	public List<Integer> getNearbyQuests()
	{
		return nearbyQuestList;
	}

	public RecipeList getRecipeList()
	{
		return recipeList;
	}

	public void setRecipeList(RecipeList recipeList)
	{
		this.recipeList = recipeList;
	}

	/**
	 * @param inventory
	 *            the inventory to set Inventory should be set right after player object is created
	 */
	public void setStorage(Storage storage, StorageType storageType)
	{
		if(storageType == StorageType.CUBE)
		{
			this.inventory = storage;
			inventory.setOwner(this);
		}

		if(storageType == StorageType.REGULAR_WAREHOUSE)
		{
			this.regularWarehouse = storage;
			regularWarehouse.setOwner(this);
		}

		if(storageType == StorageType.ACCOUNT_WAREHOUSE)
		{
			this.accountWarehouse = storage;
			accountWarehouse.setOwner(this);
		}

	}

	public Storage getStorage(int storageType)
	{
		if(storageType == StorageType.REGULAR_WAREHOUSE.getId())
			return regularWarehouse;

		if(storageType == StorageType.ACCOUNT_WAREHOUSE.getId())
			return accountWarehouse;

		if(storageType == StorageType.LEGION_WAREHOUSE.getId())
			return getLegion().getLegionWarehouse();

		if(storageType == StorageType.CUBE.getId())
			return inventory;
		else
			return null;
	}

	public Storage getInventory()
	{
		return inventory;
	}

	/**
	 * @param CubeUpgrade
	 *            int Sets the cubesize
	 */
	public void setCubesize(int cubesize)
	{
		this.playerCommonData.setCubesize(cubesize);
		getInventory().setLimit(getInventory().getLimit() + (cubesize * CUBE_SPACE));
	}

	/**
	 * @return the playerSettings
	 */
	public PlayerSettings getPlayerSettings()
	{
		return playerSettings;
	}

	/**
	 * @param playerSettings
	 *            the playerSettings to set
	 */
	public void setPlayerSettings(PlayerSettings playerSettings)
	{
		this.playerSettings = playerSettings;
	}

	/**
	 * @return the zoneInstance
	 */
	public ZoneInstance getZoneInstance()
	{
		return zoneInstance;
	}

	/**
	 * @param zoneInstance
	 *            the zoneInstance to set
	 */
	public void setZoneInstance(ZoneInstance zoneInstance)
	{
		this.zoneInstance = zoneInstance;
	}

	public TitleList getTitleList()
	{
		return titleList;
	}

	public void setTitleList(TitleList titleList)
	{
		this.titleList = titleList;
		titleList.setOwner(this);
	}

	/**
	 * @return the playerGroup
	 */
	public PlayerGroup getPlayerGroup()
	{
		return playerGroup;
	}

	/**
	 * @param playerGroup
	 *            the playerGroup to set
	 */
	public void setPlayerGroup(PlayerGroup playerGroup)
	{
		this.playerGroup = playerGroup;
	}

	/**
	 * @return the abyssRank
	 */
	public AbyssRank getAbyssRank()
	{
		return abyssRank;
	}

	/**
	 * @param abyssRank
	 *            the abyssRank to set
	 */
	public void setAbyssRank(AbyssRank abyssRank)
	{
		this.abyssRank = abyssRank;
	}

	@Override
	public PlayerEffectController getEffectController()
	{
		return (PlayerEffectController) super.getEffectController();
	}

	@Override
	public void initializeAi()
	{
		// TODO Auto-generated method stub
	}

	/**
	 * This method is called when player logs into the game. It's main responsibility is to call all registered
	 * listeners.<br>
	 * <br>
	 * 
	 * <b><font color='red'>NOTICE: </font>this method is supposed to be called only from
	 * {@link PlayerService#playerLoggedIn(Player)}</b>
	 */
	@Enhancable(callback = PlayerLoggedInListener.class)
	public void onLoggedIn()
	{

	}

	/**
	 * This method is called when player leaves the game. It's main responsibility is to call all registered listeners.<br>
	 * <br>
	 * 
	 * <b><font color='red'>NOTICE: </font>this method is supposed to be called only from
	 * {@link PlayerService#playerLoggedOut(Player)}</b>
	 */
	@Enhancable(callback = PlayerLoggedOutListener.class)
	public void onLoggedOut()
	{

	}

	/**
	 * Returns true if has valid LegionMember
	 */
	public boolean isLegionMember()
	{
		return legionMember != null;
	}

	/**
	 * @param legionMember
	 *            the legionMember to set
	 */
	public void setLegionMember(LegionMember legionMember)
	{
		this.legionMember = legionMember;
	}

	/**
	 * @return the legionMember
	 */
	public LegionMember getLegionMember()
	{
		return legionMember;
	}

	/**
	 * @return the legion
	 */
	public Legion getLegion()
	{
		return legionMember.getLegion();
	}

	/**
	 * Checks if object id's are the same
	 * 
	 * @return true if the object id is the same
	 */
	public boolean sameObjectId(int objectId)
	{
		return this.getObjectId() == objectId;
	}

	/**
	 * @return true if a player has a store opened
	 */
	public boolean hasStore()
	{
		if(getStore() != null)
			return true;
		return false;
	}

	/**
	 * Removes legion from player
	 */
	public void resetLegionMember()
	{
		setLegionMember(null);
	}

	/**
	 * This method will return true if player is in a group
	 * 
	 * @return true or false
	 */
	public boolean isInGroup()
	{
		return playerGroup != null;
	}

	/**
	 * Access level of this player
	 * 
	 * @return byte
	 */
	public byte getAccessLevel()
	{
		return getClientConnection().getAccount().getAccessLevel();
	}

	/**
	 * @return the rates
	 */
	public Rates getRates()
	{
		return rates;
	}

	/**
	 * @param rates
	 *            the rates to set
	 */
	public void setRates(Rates rates)
	{
		this.rates = rates;
	}

	/**
	 * @return warehouse size
	 */
	public int getWarehouseSize()
	{
		return this.playerCommonData.getWarehouseSize();
	}

	/**
	 * @param warehouseSize
	 */
	public void setWarehouseSize(int warehouseSize)
	{
		this.playerCommonData.setWarehouseSize(warehouseSize);
		getWarehouse().setLimit(getWarehouse().getLimit() + (warehouseSize * WAREHOUSE_SPACE));
	}

	/**
	 * @return regularWarehouse
	 */
	public Storage getWarehouse()
	{
		return regularWarehouse;
	}
	
	/**
	 * 0: regular, 1: fly, 2: glide
	 */
	public int getFlyState()
	{
		return this.flyState;
	}

	public void setFlyState(int flyState)
	{
		this.flyState = flyState;
	}

	/**
	 * @return the isTrading
	 */
	public boolean isTrading()
	{
		return isTrading;
	}

	/**
	 * @param isTrading the isTrading to set
	 */
	public void setTrading(boolean isTrading)
	{
		this.isTrading = isTrading;
	}

	/**
	 * @return the isInPrison
	 */
	public boolean isInPrison()
	{
		return prisonTimer != 0;
	}

	/**
	 * @param prisonTimer the prisonTimer to set
	 */
	public void setPrisonTimer(long prisonTimer)
	{
		this.prisonTimer = prisonTimer;
	}

	/**
	 * @return the prisonTimer
	 */
	public long getPrisonTimer()
	{
		return prisonTimer;
	}

	/**
	 * @return
	 */
	public boolean isProtectionActive()
	{
		return isInVisualState(CreatureVisualState.BLINKING);
	}
	
	/**
	 * Check is player is invul
	 * 
	 * @return boolean
	 **/
	public boolean isInvul()
	{
		return invul;
	}

	/**
	 * Sets invul on player
	 * 
	 * @param invul
	 *            - boolean
	 **/
	public void setInvul(boolean invul)
	{
		this.invul = invul;
	}

}
