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
package com.aionemu.gameserver.questEngine.model;

import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * @author MrPoke
 *
 */
public class QuestEnv
{
	private VisibleObject visibleObject;

	private Player player;
	
	private Integer questId;
	
	private Integer dialogId;

	/**
	 * @param creature
	 * @param player
	 * @param questId
	 */
	public QuestEnv(VisibleObject visibleObject, Player player, Integer questId, Integer dialogId)
	{
		super();
		this.visibleObject = visibleObject;
		this.player = player;
		this.questId = questId;
		this.dialogId = dialogId;
	}

	/**
	 * @return the visibleObject
	 */
	public VisibleObject getVisibleObject()
	{
		return visibleObject;
	}

	/**
	 * @param visibleObject the visibleObject to set
	 */
	public void setVisibleObject(VisibleObject visibleObject)
	{
		this.visibleObject = visibleObject;
	}

	/**
	 * @return the player
	 */
	public Player getPlayer()
	{
		return player;
	}

	/**
	 * @param player the player to set
	 */
	public void setPlayer(Player player)
	{
		this.player = player;
	}

	/**
	 * @return the questId
	 */
	public Integer getQuestId()
	{
		return questId;
	}

	/**
	 * @param questId the questId to set
	 */
	public void setQuestId(Integer questId)
	{
		this.questId = questId;
	}

	/**
	 * @return the dialogId
	 */
	public Integer getDialogId()
	{
		return dialogId;
	}

	/**
	 * @param dialogId the dialogId to set
	 */
	public void setDialogId(Integer dialogId)
	{
		this.dialogId = dialogId;
	}

}
