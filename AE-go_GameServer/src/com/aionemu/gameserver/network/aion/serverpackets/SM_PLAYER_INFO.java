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
import java.util.List;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerAppearance;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.gameobjects.stats.StatEnum;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet is displaying visible players.
 * 
 * @author -Nemesiss-, Avol, srx47
 */
public class SM_PLAYER_INFO extends AionServerPacket
{

	/**
	 * Visible player
	 */
	private final Player	player;
	private boolean			enemy;

	/**
	 * Constructs new <tt>SM_PLAYER_INFO </tt> packet
	 * 
	 * @param player
	 *            actual player.
	 * @param enemy
	 */
	public SM_PLAYER_INFO(Player player, boolean enemy)
	{
		this.player = player;
		this.enemy = enemy;
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
		 * A3 female asmodian A2 male asmodian A1 female elyos A0 male elyos
		 */
		writeD(buf, pcd.getTemplateId());
		/**
		 * Transformed state - send transformed model id Regular state - send player model id (from common data)
		 */
		writeD(buf, player.getTransformedModelId() == 0 ? pcd.getTemplateId() : player.getTransformedModelId());

		if(enemy)
		{
			writeC(buf, 0x00);
		}
		else
		{
			writeC(buf, 0x26);
		}
		writeC(buf, raceId); // race
		writeC(buf, pcd.getPlayerClass().getClassId());
		writeC(buf, genderId); // sex
		writeH(buf, player.getState());

		byte[] unk = new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00 };
		writeB(buf, unk);

		writeC(buf, player.getHeading());

		writeS(buf, player.getName());

		writeD(buf, pcd.getTitleId());
		writeC(buf, 0x0);// if set 0x1 can't jump and fly..
		writeH(buf, player.getCastingSkillId());
		writeH(buf, player.isLegionMember() ? player
			.getLegion().getLegionId() : 0);
		writeH(buf, 0); // User Emblem related?
		writeH(buf, player.isLegionMember() ? player
			.getLegion().getLegionEmblem().getEmblemId() : 0);
		writeC(buf, 0xFF);
		writeC(buf, player.isLegionMember() ? player
			.getLegion().getLegionEmblem().getColor_r() : 0);
		writeC(buf, player.isLegionMember() ? player
			.getLegion().getLegionEmblem().getColor_g() : 0);
		writeC(buf, player.isLegionMember() ? player
			.getLegion().getLegionEmblem().getColor_b() : 0);
		writeS(buf, player.isLegionMember() ? player
			.getLegion().getLegionName() : "");

		int maxHp = player.getLifeStats().getMaxHp();
		int currHp = player.getLifeStats().getCurrentHp();
		writeC(buf, 100 * currHp / maxHp);// %hp
		writeH(buf, pcd.getDp());// current dp
		writeC(buf, 0x00);// unk (0x00)

		List<Item> items = player.getEquipment().getEquippedItems();
		short mask = 0;
		for(Item item : items)
		{
			mask |= item.getEquipmentSlot();
		}

		writeH(buf, mask);

		for(Item item : items)
		{
			if(item.getEquipmentSlot() < Short.MAX_VALUE * 2)
			{
				writeD(buf, item.getItemTemplate().getTemplateId());
				writeD(buf, 0); // GodStone ItemId
				writeD(buf, item.getItemColor());
			}
		}

		writeD(buf, playerAppearance.getSkinRGB());
		writeD(buf, playerAppearance.getHairRGB());
		writeD(buf, playerAppearance.getEyeRGB());
		writeD(buf, playerAppearance.getLipRGB());
		writeC(buf, playerAppearance.getFace());
		writeC(buf, playerAppearance.getHair());
		writeC(buf, playerAppearance.getDeco());
		writeC(buf, playerAppearance.getTattoo());

		writeC(buf, 5);// always 5 o0

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

		writeC(buf, 0x00); // always 0
		writeC(buf, playerAppearance.getArmLength());
		writeC(buf, playerAppearance.getLegLength());
		writeC(buf, playerAppearance.getShoulders());
		writeC(buf, 0x00); // always 0
		writeC(buf, 0x00); // 0x00

		writeC(buf, playerAppearance.getVoice());

		writeF(buf, playerAppearance.getHeight());
		writeF(buf, 0.25f); // scale
		writeF(buf, 2.0f); // gravity or slide surface o_O
		writeF(buf, player.getGameStats().getCurrentStat(StatEnum.SPEED) / 1000f); // move speed

		writeH(buf, player.getGameStats().getCurrentStat(StatEnum.ATTACK_SPEED));
		writeH(buf, player.getGameStats().getCurrentStat(StatEnum.ATTACK_SPEED));
		writeC(buf, 0);

		writeS(buf, player.hasStore() ? player.getStore().getStoreMessage() : "");// private store message

		/**
		 * Movement
		 */
		writeF(buf, 0);
		writeF(buf, 0);
		writeF(buf, 0);

		writeF(buf, player.getX());// x
		writeF(buf, player.getY());// y
		writeF(buf, player.getZ());// z
		writeC(buf, 0x00); // move type

		writeC(buf, player.getVisualState()); // visualState
		writeS(buf, player.getCommonData().getNote()); // note show in right down windows if your target on player

		writeH(buf, player.getLevel()); // [level]
		writeH(buf, player.getPlayerSettings().getDisplay()); // unk - 0x04
		writeH(buf, player.getPlayerSettings().getDeny()); // unk - 0x00
		writeH(buf, player.getAbyssRank().getRank().getId()); // abyss rank
		writeH(buf, 0x00); // unk - 0x01
		if(player.getTarget() == null)
		{
			writeD(buf, 0);
		}
		else
		{
			writeD(buf, player.getTarget().getObjectId());
		}
		writeC(buf, 0); // suspect id
	}
}
