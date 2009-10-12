/**
 * This file is part of aion-unique <aion-unique.com>.
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
package com.aionemu.gameserver.network.aion.serverpackets;

import java.nio.ByteBuffer;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerAppearance;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.gameobjects.player.Inventory;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet is displaying visible players.
 * 
 * @author -Nemesiss-, Avol, srx47
 * 
 */
public class SM_PLAYER_INFO extends AionServerPacket
{
	/**
	 * Visible player
	 */
	private final Player	player;
	private final boolean	self;

	/**
	 * Constructs new <tt>SM_CI </tt> packet
	 * 
	 * @param player
	 *            actual player.
	 */
	public SM_PLAYER_INFO(Player player, boolean self)
	{
		this.player = player;
		this.self = self;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con, ByteBuffer buf)
	{
		PlayerCommonData pcd = player.getCommonData();
		
		final int raceId = pcd.getRace().getRaceId();
		final int genderId = pcd.getGender().getGenderId();
		final PlayerAppearance playerAppearance = player.getPlayerAppearance();

		writeF(buf, player.getX());// x
		writeF(buf, player.getY());// y
		writeF(buf, player.getZ());// z
		writeD(buf, player.getObjectId());
		/**
		 * A3 famale asamodian A2 male asamodian A1 famale elyos A0 male elyos
		 */
		int raceSex = 100000 + raceId * 2 + genderId; // wtf ?
		writeD(buf, raceSex);
		writeD(buf, raceSex);

		writeC(buf, 0x26); // unk
		writeC(buf, raceId); //race
		writeC(buf, pcd.getPlayerClass().getClassId());
		writeC(buf, genderId); //sex

		byte[] unk = new byte[] { (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 };
		writeB(buf, unk);

		writeC(buf, player.getHeading());

		writeS(buf, player.getName());

		unk = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 };
		writeB(buf, unk);

		writeC(buf, 100);// %hp
		writeC(buf, 0x00);// unk (0x00)
		writeC(buf, 0x00);// unk (0x00)
		writeC(buf, 0x00);// unk (0x00)

		Inventory equipedItems = new Inventory();
		equipedItems.getEquipedItemsFromDb(player.getObjectId());
		int totalEquipedItemsCount = equipedItems.getEquipedItemsCount();
		
		int itemsCount = 0;
		int row = 0;
		while (totalEquipedItemsCount > 0) {
			int slot = equipedItems.getEquipedItemSlotArray(row);
			if (slot==5) {
				slot = 1; // or 2 weapon
			}
			if (slot==6) {
				slot = 8192;//or 16384 power shard
			}
			if (slot==7) {
				slot = 256;// 512 rings
			}
			if (slot==9) {
				slot = 64;// 128 earrings
			}
			itemsCount = itemsCount + slot;
			totalEquipedItemsCount = totalEquipedItemsCount-1;
			row+=1;
		}
	
		writeH(buf, itemsCount);// // items count

		totalEquipedItemsCount = equipedItems.getEquipedItemsCount();

		row = 0;
		while (totalEquipedItemsCount > 0) {
			writeD(buf, equipedItems.getEquipedItemIdArray(row));// item id
			writeD(buf, 0x00);// unk
			writeD(buf, 0x00);// color code
			totalEquipedItemsCount = totalEquipedItemsCount-1;
			row+=1;
		}
			
		writeD(buf, playerAppearance.getSkinRGB());
		writeD(buf, playerAppearance.getHairRGB());
		writeD(buf, playerAppearance.getEyeRGB());
		writeD(buf, playerAppearance.getLipRGB());
		writeC(buf, playerAppearance.getFace());
		writeC(buf, playerAppearance.getHair());
		writeC(buf, playerAppearance.getDeco());
		writeC(buf, playerAppearance.getTattoo());

		writeC(buf, 4);// always 4 o0

		writeC(buf, playerAppearance.getFaceShape());
		writeC(buf, playerAppearance.getForehead());

		writeC(buf, playerAppearance.getEyeHeight());
		writeC(buf, playerAppearance.getEyeSpace());
		writeC(buf, playerAppearance.getEyeWidth());
		writeC(buf, playerAppearance.getEyeSize());
		writeC(buf, playerAppearance.getEyeShape());
		writeC(buf, playerAppearance.getEyeAngle());

		writeC(buf, playerAppearance.getBrowHeight());
		writeC(buf, playerAppearance.getBrowAngle());
		writeC(buf, playerAppearance.getBrowShape());

		writeC(buf, playerAppearance.getNose());
		writeC(buf, playerAppearance.getNoseBridge());
		writeC(buf, playerAppearance.getNoseWidth());
		writeC(buf, playerAppearance.getNoseTip());

		writeC(buf, playerAppearance.getCheek());
		writeC(buf, playerAppearance.getLipHeight());
		writeC(buf, playerAppearance.getMouthSize());
		writeC(buf, playerAppearance.getLipSize());
		writeC(buf, playerAppearance.getSmile());
		writeC(buf, playerAppearance.getLipShape());
		writeC(buf, playerAppearance.getJawHeigh());
		writeC(buf, playerAppearance.getChinJut());
		writeC(buf, playerAppearance.getEarShape());
		writeC(buf, playerAppearance.getHeadSize());
		// 1.5.x 0x00, shoulderSize, armLength, legLength (BYTE) after HeadSize

		writeC(buf, playerAppearance.getNeck());
		writeC(buf, playerAppearance.getNeckLength());
		writeC(buf, playerAppearance.getShoulderSize());
		

		writeC(buf, playerAppearance.getTorso());
		writeC(buf, playerAppearance.getChest()); // only woman
		writeC(buf, playerAppearance.getWaist());

		writeC(buf, playerAppearance.getHips());
		writeC(buf, playerAppearance.getArmThickness());
		writeC(buf, playerAppearance.getHandSize());
		writeC(buf, playerAppearance.getLegThicnkess());

		writeC(buf, playerAppearance.getFootSize());
		writeC(buf, playerAppearance.getFacialRate());
		writeC(buf, playerAppearance.getVoice()); //maybe wrong voice
		
		writeC(buf, 0x00); // always 0
		writeC(buf, playerAppearance.getArmLength());
		writeC(buf, playerAppearance.getLegLength());
		writeC(buf, playerAppearance.getShoulders());
		writeC(buf, 0x00); // always 0
		writeC(buf, 0x00); // 0x00

		writeF(buf, playerAppearance.getHeight());

		unk = new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x80, (byte) 0x3E, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x40};
		writeB(buf, unk);
			
		writeF(buf, 6); // move speed
		
		unk = new byte[] {(byte) 0xDC, (byte) 0x05, (byte) 0xDC,
			(byte) 0x05, (byte) 0x02 };
		writeB(buf, unk);

		writeS(buf, "");// private shop?
		unk = new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 };
		writeB(buf, unk);

		/**
		 * Movement
		 */
		writeF(buf, player.getX());// x
		writeF(buf, player.getY());// y
		writeF(buf, player.getZ());// z
		writeC(buf, 0x00); // move type

		writeC(buf, self ? 0x40 : 0x00); // unk - 0x40, 0x00
		writeC(buf, 0x00); // unk, Putting 0x01 here makes your chatacter run in red armor, dunno what it means
		writeC(buf, 0x00); // unk

		writeD(buf, player.getLevel()); // unk - 0x01, 0x02, 0x03 etc [lvl?]
		writeC(buf, 0x00); // unk - 0x00
		writeD(buf, 0x01); // unk - 0x00
	}
}
