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
public enum LegionRank
{
	/** All Legion Ranks **/
	BRIGADE_GENERAL(0),
	CENTURION(1),
	LEGIONARY(2);

	/** Static Rights Information **/
	// Add to 0x60
	private static final int	LP_LEGION_WAREHOUSE		= 0x04;
	private static final int	LP_INVITE_TO_LEGION		= 0x08;
	private static final int	LP_KICK_FROM_LEGION		= 0x10;
	private static final int	LP_COMBINATION_60_12	= 0x0C;
	private static final int	LP_COMBINATION_60_13	= 0x14;
	private static final int	LP_COMBINATION_60_23	= 0x18;
	private static final int	LP_COMBINATION_60_123	= 0x1C;

	// Add to 0x00
	private static final int	LP_EDIT_ANNOUNCEMENT	= 0x02;
	private static final int	LP_ARTIFACT				= 0x04;
	private static final int	LP_GATE_GUARDIAN_STONE	= 0x08;
	private static final int	LP_COMBINATION_00_12	= 0x06;
	private static final int	LP_COMBINATION_00_13	= 0x0A;
	private static final int	LP_COMBINATION_00_23	= 0x0C;
	private static final int	LP_COMBINATION_00_123	= 0x0E;

	private byte				rank;

	private LegionRank(int rank)
	{
		this.rank = (byte) rank;
	}

	/**
	 * Returns client-side id for this
	 * 
	 * @return byte
	 */
	public byte getRankId()
	{
		return this.rank;
	}

	/**
	 * @return true if legion member has enough rights for Use Gate Guardian Stone
	 */
	public boolean canUseGateGuardianStone(final int centurionPermission2, final int legionarPermission2)
	{
		switch(this)
		{
			/** Legion Member is Centurion **/
			case CENTURION:
				if(centurionPermission2 == LP_GATE_GUARDIAN_STONE || centurionPermission2 == (LP_COMBINATION_00_13)
					|| centurionPermission2 == (LP_COMBINATION_00_23)
					|| centurionPermission2 == (LP_COMBINATION_00_123))
					return true;
				break;
			/** Legion Member is Legionary **/
			case LEGIONARY:
				if(legionarPermission2 == LP_GATE_GUARDIAN_STONE)
					return true;
				break;
		}
		return false;
	}

	/**
	 * @return true if legion member has enough rights for Use Artifact
	 */
	public boolean canUseArtifact(final int centurionPermission2)
	{
		switch(this)
		{
			/** Legion Member is Centurion **/
			case CENTURION:
			{
				if(centurionPermission2 == LP_ARTIFACT || centurionPermission2 == (LP_COMBINATION_00_12)
					|| centurionPermission2 == (LP_COMBINATION_00_23)
					|| centurionPermission2 == (LP_COMBINATION_00_123))
					return true;
				break;
			}
		}
		return false;
	}

	/**
	 * @return true if legion member has enough rights for Edit Announcement
	 */
	public boolean canEditAnnouncement(final int centurionPermission2)
	{
		switch(this)
		{
			/** Legion Member is Centurion **/
			case CENTURION:
			{
				if(centurionPermission2 == LP_EDIT_ANNOUNCEMENT || centurionPermission2 == (LP_COMBINATION_00_13)
					|| centurionPermission2 == (LP_COMBINATION_00_23)
					|| centurionPermission2 == (LP_COMBINATION_00_123))
					return true;
				break;
			}
		}
		return false;
	}

	/**
	 * @return true if legion member has enough rights for Use Legion Warehouse
	 */
	public boolean canUseLegionWarehouse(final int centurionPermission1)
	{
		switch(this)
		{
			/** Legion Member is Centurion **/
			case CENTURION:
			{
				if(centurionPermission1 == LP_LEGION_WAREHOUSE || centurionPermission1 == (LP_COMBINATION_60_13)
					|| centurionPermission1 == (LP_COMBINATION_60_13)
					|| centurionPermission1 == (LP_COMBINATION_60_123))
					return true;
				break;
			}
		}
		return false;
	}

	/**
	 * @return true if legion member has enough rights for Kick from Legion
	 */
	public boolean canKickFromLegion(final int centurionPermission1)
	{
		switch(this)
		{
			/** Legion Member is Centurion **/
			case CENTURION:
			{
				if(centurionPermission1 == LP_KICK_FROM_LEGION || centurionPermission1 == (LP_COMBINATION_60_12)
					|| centurionPermission1 == (LP_COMBINATION_60_23)
					|| centurionPermission1 == (LP_COMBINATION_60_123))
					return true;
				break;
			}
		}
		return false;
	}

	/**
	 * @return true if legion member has enough rights for Invite to Legion
	 */
	public boolean canInviteToLegion(int centurionPermission1)
	{
		switch(this)
		{
			/** Legion Member is Centurion **/
			case CENTURION:
			{
				if(centurionPermission1 == LP_INVITE_TO_LEGION || centurionPermission1 == (LP_COMBINATION_60_13)
					|| centurionPermission1 == (LP_COMBINATION_60_23)
					|| centurionPermission1 == (LP_COMBINATION_60_123))
					return true;
				break;
			}
		}
		return false;
	}
}
