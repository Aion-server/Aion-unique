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
	private static final int	LP_CENT_NONE	= 0x60;
	private int					objectId		= 0;
	protected Legion			legion			= null;
	protected String			nickname		= "";
	protected String			selfIntro		= "";

	protected LegionRank		rank			= LegionRank.LEGIONARY;

	/**
	 * If player is defined later on this constructor is called
	 */
	public LegionMember(int objectId)
	{
		this.objectId = objectId;
	}

	/**
	 * This constructor is called when a legion is created
	 */
	public LegionMember(int objectId, Legion legion, LegionRank rank)
	{
		this.setObjectId(objectId);
		this.setLegion(legion);
		this.setRank(rank);
	}

	/**
	 * This constructor is called when a LegionMemberEx is called
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
	public void setRank(LegionRank rank)
	{
		this.rank = rank;
	}

	/**
	 * @return the rank
	 */
	public LegionRank getRank()
	{
		return rank;
	}

	public boolean isBrigadeGeneral()
	{
		return rank == LegionRank.BRIGADE_GENERAL;
	}

	/**
	 * @param nickname
	 *            the nickname to set
	 */
	public void setNickname(String nickname)
	{
		this.nickname = nickname;
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
	}

	/**
	 * @return the objectId
	 */
	public int getObjectId()
	{
		return objectId;
	}

	public boolean hasRights(int type)
	{
		if(getRank() == LegionRank.BRIGADE_GENERAL)
			return true;

		int legionarPermission2 = getLegion().getLegionarPermission2();
		int centurionPermission1 = getLegion().getCenturionPermission1() - LP_CENT_NONE;
		int centurionPermission2 = getLegion().getCenturionPermission2();

		switch(type)
		{
			case 1:
				if(getRank().canInviteToLegion(centurionPermission1))
					return true;

			case 2:
				if(getRank().canKickFromLegion(centurionPermission1))
					return true;

			case 3:
				if(getRank().canUseLegionWarehouse(centurionPermission1))
					return true;

			case 4:
				if(getRank().canEditAnnouncement(centurionPermission2))
					return true;

			case 5:
				if(getRank().canUseArtifact(centurionPermission2))
					return true;

			case 6:
				if(getRank().canUseGateGuardianStone(centurionPermission2, legionarPermission2))
					return true;
		}
		return false;

	}
}
