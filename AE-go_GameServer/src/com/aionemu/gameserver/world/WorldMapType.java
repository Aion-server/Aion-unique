/*
 * This file is part of aion-unique <aionunique.smfnew.com>.
 *
 * aion-unique is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-unique is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.world;

public enum WorldMapType
{
	NONE(0, 0),

	//Asmodea
	Pandaemonlium(1, 120010000),
	Ishalgen(2, 220010000),
	Morhelm(3, 220020000),
	Altgard(4, 220030000),
	Brusthoni(5, 220040000),
	Beluslan(6, 220050000),

	//Elysia
	Sencrum(7, 110010000),
	Poeta(8, 210010000),
	Verteron(9, 210020000),
	Eltnen(10, 210030000),
	Theobomos(12, 210040000),
	Interdiktah(13, 210050000),

	Teminon(14, 400010000);

	private final int _worldId;
	private final int _shortID;

	WorldMapType(int shortID,int world )
	{
		_worldId = world;
		_shortID = shortID;
	}

	public int getId()
	{
		return _worldId;
	}

	public int getShortID()
	{
		return _shortID;
	}

	public static int getShortIdByWorldId(int worldId)
	{
		for (WorldMapType type : WorldMapType.values())
		{
			if (type.getId() == worldId)
			{
				return type.getShortID();
			}
		}

		return 0;
	}

	public static WorldMapType getWorld(int id)
	{
		for (WorldMapType type : WorldMapType.values())
		{
			if (type.getId() == id)
			{
				return type;
			}
		}
		return null;
	}
}
/**
 * 	<map id="110010000" name="LC1" />
 <map id="120010000" name="DC1" />
 <map id="210010000" max_user="200" twin_count="4" name="LF1" />
 <map id="210030000" max_user="200" twin_count="7" name="LF1A" />
 <map id="210020000" name="LF2" />
 <map id="210060000" name="LF2A" />
 <map id="210040000" name="LF3" />
 <map id="220010000" max_user="200" twin_count="4" name="DF1" />
 <map id="220030000" max_user="200" twin_count="7" name="DF1A" />
 <map id="220020000" name="DF2" />
 <map id="220050000" name="DF2A" />
 <map id="220040000" name="DF3" />
 <map id="300010000" name="IDAbPro" />
 <map id="310010000" twin_count="2" name="IDAbProL1" />
 <map id="310020000" name="IDAbProL2" />
 <map id="310030000" name="IDAbGateL1" />
 <map id="310040000" name="IDAbGateL2" />
 <map id="310050000" name="IDLF3Lp" />
 <map id="310060000" name="IDLF1B" />
 <map id="310070000" name="IDLF1B_Stigma" />
 <map id="310080000" name="IDLC1_arena" />
 <map id="310090000" name="IDLF3_Castle_indratoo" />
 <map id="310100000" name="IDLF3_Castle_Lehpar" />
 <map id="320010000" twin_count="2" name="IDAbProD1" />
 <map id="320020000" name="IDAbProD2" />
 <map id="320030000" name="IDAbGateD1" />
 <map id="320040000" name="IDAbGateD2" />
 <map id="320050000" name="IDDF2Flying" />
 <map id="320060000" name="IDDF1B" />
 <map id="320070000" name="IDSpace" />
 <map id="320080000" name="IDDF3_Dragon" />
 <map id="320090000" name="IDDC1_arena" />
 <map id="320100000" name="IDDF2_Dflame" />
 <map id="320110000" name="IDDF3_LP" />
 <map id="320120000" name="IDDC1_Arena_3F" />
 <map id="400010000" name="Ab1" />
 <map id="510010000" prison="true" name="LF_Prison" />
 <map id="520010000" prison="true" name="DF_Prison" />
 <map id="900020000" twin_count="10" name="Test_Basic" />
 <map id="900030000" twin_count="5" name="Test_Server" />
 <map id="900100000" name="Test_GiantMonster" />
 */
