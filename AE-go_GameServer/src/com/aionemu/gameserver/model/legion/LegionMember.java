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
package com.aionemu.gameserver.model.legion;

/**
 * @author Simple
 * 
 */
public class LegionMember
{
	/** Static Legion Member information **/
	private static final int BRIGADE_GENERAL_RANK = 0x00;
	
	private int		objectId		= 0;
	protected Legion	legion			= null;
	/*
	 * Rank Information 0x00 = Brigade General => ALL RIGHTS 0x01 = Centurion => Only centurion rights 0x02 = Legionar
	 * => Only legionar rights
	 */
	protected int		rank			= 0x02;
	protected String	nickname		= "";
	protected String	selfIntro		= "";
	private boolean	stateChanged	= false;

	/**
	 * If player is defined later on this constructor is called
	 */
	public LegionMember(int objectId)
	{
		this.objectId = objectId;
	}

	/**
	 * If player is defined we can load the information from Player
	 */
	public LegionMember(int objectId, Legion legion)
	{
		this.setObjectId(objectId);
		this.setLegion(legion);
	}

	/**
	 * This constructor is called when a legion is created
	 */
	public LegionMember(int objectId, Legion legion, int rank)
	{
		this.setObjectId(objectId);
		this.setLegion(legion);
		this.setRank(rank);
	}

	/**
	 * This constructor is called when an offline legion member is called
	 */
	public LegionMember()
	{
	}

	/**
	 * @param legion
	 *            the legion to set
	 */
	public void setLegion(Legion legion)
	{
		this.legion = legion;
		this.setStateChanged(true);
	}

	/**
	 * @return the legion
	 */
	public Legion getLegion()
	{
		return legion;
	}

	/**
	 * @param rank
	 *            the rank to set
	 */
	public void setRank(int rank)
	{
		this.rank = rank;
		this.setStateChanged(true);
	}

	/**
	 * @return the rank
	 */
	public int getRank()
	{
		return rank;
	}
	
	public boolean isBrigadeGeneral()
	{
		return rank == BRIGADE_GENERAL_RANK;
	}

	/**
	 * @param nickname
	 *            the nickname to set
	 */
	public void setNickname(String nickname)
	{
		this.nickname = nickname;
		this.setStateChanged(true);
	}

	/**
	 * @return the nickname
	 */
	public String getNickname()
	{
		return nickname;
	}

	/**
	 * @param selfIntro
	 *            the selfIntro to set
	 */
	public void setSelfIntro(String selfIntro)
	{
		this.selfIntro = selfIntro;
		this.setStateChanged(true);
	}

	/**
	 * @return the selfIntro
	 */
	public String getSelfIntro()
	{
		return selfIntro;
	}

	/**
	 * @param objectId
	 *            the objectId to set
	 */
	public void setObjectId(int objectId)
	{
		this.objectId = objectId;
		this.setStateChanged(true);
	}

	/**
	 * @return the objectId
	 */
	public int getObjectId()
	{
		return objectId;
	}

	/**
	 * @param stateChanged
	 *            the stateChanged to set
	 */
	public void setStateChanged(boolean stateChanged)
	{
		this.stateChanged = stateChanged;
	}

	/**
	 * @return the stateChanged
	 */
	public boolean hasStateChanged()
	{
		return stateChanged;
	}
}
