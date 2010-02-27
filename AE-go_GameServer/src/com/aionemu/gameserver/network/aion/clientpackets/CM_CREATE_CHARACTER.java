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
package com.aionemu.gameserver.network.aion.clientpackets;

import java.sql.Timestamp;

import com.aionemu.gameserver.model.Gender;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.account.Account;
import com.aionemu.gameserver.model.account.PlayerAccountData;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerAppearance;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CREATE_CHARACTER;
import com.aionemu.gameserver.services.PlayerService;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.aionemu.gameserver.utils.idfactory.IDFactoryAionObject;
import com.google.inject.Inject;

/**
 * In this packets aion client is requesting creation of character.
 * 
 * @author -Nemesiss-
 * 
 */
public class CM_CREATE_CHARACTER extends AionClientPacket
{
	/** Character appearance */
	private PlayerAppearance	playerAppearance;
	/** Player base data */
	private PlayerCommonData	playerCommonData;

	@Inject
	private PlayerService		playerService;

	@Inject
	@IDFactoryAionObject
	private IDFactory			aionObjectsIDFactory;

	/**
	 * Constructs new instance of <tt>CM_CREATE_CHARACTER </tt> packet
	 * 
	 * @param opcode
	 */
	public CM_CREATE_CHARACTER(int opcode)
	{
		super(opcode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl()
	{
		@SuppressWarnings("unused")
		int playOk2 = readD(); // ignored for now
		@SuppressWarnings("unused")
		String someShit = readS(); // something + accointId

		playerCommonData = new PlayerCommonData(aionObjectsIDFactory.nextId());
		String name = readS();

		playerCommonData.setName(name);
		// just for sure...
		//log.info((42 - name.length() * 2) + " == " + (getRemainingBytes() - 76));

		@SuppressWarnings("unused")
		byte[] shit = readB(42 - (name.length() * 2)); // some shit? 1.5.x

		/*
		 * int i = 1; for(byte b : shit) log.info("["+i+++"]="+b);
		 */

		// just for sure...
		//log.info("76 == " + (getRemainingBytes()));
		playerCommonData.setLevel(1);
		playerCommonData.setGender(readD() == 0 ? Gender.MALE : Gender.FEMALE);
		playerCommonData.setRace(readD() == 0 ? Race.ELYOS : Race.ASMODIANS);
		playerCommonData.setPlayerClass(PlayerClass.getPlayerClassById((byte) readD()));

		playerAppearance = new PlayerAppearance();

		playerAppearance.setVoice(readD());
		playerAppearance.setSkinRGB(readD());
		playerAppearance.setHairRGB(readD());
		playerAppearance.setEyeRGB(readD());
		//log.info("EyesColor: " + readD());
		playerAppearance.setLipRGB(readD());
		playerAppearance.setFace(readC());
		playerAppearance.setHair(readC());
		playerAppearance.setDeco(readC());
		playerAppearance.setTattoo(readC());

		readC(); // always 4 o0 // 5 in 1.5.x

		playerAppearance.setFaceShape(readC());
		playerAppearance.setForehead(readC());

		playerAppearance.setEyeHeight(readC());
		playerAppearance.setEyeSpace(readC());
		playerAppearance.setEyeWidth(readC());
		playerAppearance.setEyeSize(readC());
		playerAppearance.setEyeShape(readC());
		playerAppearance.setEyeAngle(readC());

		playerAppearance.setBrowHeight(readC());
		playerAppearance.setBrowAngle(readC());
		playerAppearance.setBrowShape(readC());

		playerAppearance.setNose(readC());
		playerAppearance.setNoseBridge(readC());
		playerAppearance.setNoseWidth(readC());
		playerAppearance.setNoseTip(readC());

		playerAppearance.setCheek(readC());
		playerAppearance.setLipHeight(readC());
		playerAppearance.setMouthSize(readC());
		playerAppearance.setLipSize(readC());
		playerAppearance.setSmile(readC());
		playerAppearance.setLipShape(readC());
		playerAppearance.setJawHeigh(readC());
		playerAppearance.setChinJut(readC());
		playerAppearance.setEarShape(readC());
		playerAppearance.setHeadSize(readC());

		playerAppearance.setNeck(readC());
		playerAppearance.setNeckLength(readC());

		playerAppearance.setShoulderSize(readC());

		playerAppearance.setTorso(readC());
		playerAppearance.setChest(readC()); // only woman
		playerAppearance.setWaist(readC());

		playerAppearance.setHips(readC());
		playerAppearance.setArmThickness(readC());

		playerAppearance.setHandSize(readC());
		playerAppearance.setLegThicnkess(readC());

		playerAppearance.setFootSize(readC());
		playerAppearance.setFacialRate(readC());

		@SuppressWarnings("unused")
		byte unk1 = (byte) readC(); // always 0
		playerAppearance.setArmLength(readC());
		playerAppearance.setLegLength(readC()); //wrong??
		playerAppearance.setShoulders(readC()); // 1.5.x May be ShoulderSize
		@SuppressWarnings("unused")
		byte unk2 = (byte) readC(); // always 0
		readC();
		playerAppearance.setHeight(readF());
	}

	/**
	 * Actually does the dirty job
	 */
	@Override
	protected void runImpl()
	{
		AionConnection client = getConnection();

		/* Some reasons why player can' be created */
		if(!playerService.isValidName(playerCommonData.getName()))
		{
			client.sendPacket(new SM_CREATE_CHARACTER(null, SM_CREATE_CHARACTER.RESPONSE_INVALID_NAME));
			aionObjectsIDFactory.releaseId(playerCommonData.getPlayerObjId());
			return;
		}
		if(!playerService.isFreeName(playerCommonData.getName()))
		{
			client.sendPacket(new SM_CREATE_CHARACTER(null, SM_CREATE_CHARACTER.RESPONSE_NAME_ALREADY_USED));
			aionObjectsIDFactory.releaseId(playerCommonData.getPlayerObjId());
			return;
		}
		if(!playerCommonData.getPlayerClass().isStartingClass())
		{
			client.sendPacket(new SM_CREATE_CHARACTER(null, SM_CREATE_CHARACTER.FAILED_TO_CREATE_THE_CHARACTER));
			aionObjectsIDFactory.releaseId(playerCommonData.getPlayerObjId());
			return;
		}

		Player player = playerService.newPlayer(playerCommonData, playerAppearance);

		Account account = client.getAccount();

		if(!playerService.storeNewPlayer(player, account.getName(), account.getId()))
		{
			client.sendPacket(new SM_CREATE_CHARACTER(null, SM_CREATE_CHARACTER.RESPONSE_DB_ERROR));
		}
		else
		{
			PlayerAccountData accPlData = new PlayerAccountData(playerCommonData, playerAppearance, player.getInventory(), player.getEquipment(), null);
			accPlData.setCreationDate(new Timestamp(System.currentTimeMillis()));

			playerService.storeCreationTime(player.getObjectId(), accPlData.getCreationDate());

			account.addPlayerAccountData(accPlData);
			client.sendPacket(new SM_CREATE_CHARACTER(accPlData, SM_CREATE_CHARACTER.RESPONSE_OK));
		}
	}
}
