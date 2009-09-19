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
package com.aionemu.gameserver.model.templates;

/**
 * @author Luno
 * 
 */
public class SpawnTemplate
{
	private NpcTemplate	npc;
	private int			worldId;
	private float		x;
	private float		y;
	private float		z;
	private byte		heading;

	public SpawnTemplate(NpcTemplate npc, int worldId, float x, float y, float z, byte heading)
	{
		super();
		this.npc = npc;
		this.worldId = worldId;
		this.x = x;
		this.y = y;
		this.z = z;
		this.heading = heading;
	}

	public NpcTemplate getNpc()
	{
		return npc;
	}

	public int getWorldId()
	{
		return worldId;
	}

	public float getX()
	{
		return x;
	}

	public float getY()
	{
		return y;
	}

	public float getZ()
	{
		return z;
	}

	public byte getHeading()
	{
		return heading;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "SpawnTemplate " + npc.getName() + " " + worldId + " " + x + " " + y + " " + z;
	}
}
