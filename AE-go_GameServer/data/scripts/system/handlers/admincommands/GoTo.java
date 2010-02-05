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

package admincommands;

import com.aionemu.gameserver.configs.AdminConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_SPAWN;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldMapType;
import com.google.inject.Inject;

/**
 * Admin moveto command
 * 
 * @author Dwarfpicker
 */

public class GoTo extends AdminCommand
{
	@Inject
	private World	world;

	/**
	 * Constructor.
	 */
	public GoTo()
	{
		super("goto");
	}

	@Override
	public void executeCommand(Player admin, String[] params)
	{
		if(admin.getCommonData().getAdminRole() < AdminConfig.COMMAND_GOTO)
		{
			PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
			return;
		}
		
		if(params == null || params.length < 1)
		{
			PacketSendUtility.sendMessage(admin, "syntax //goto <location>");
			return;
		}

		if(params[0].equals("poeta"))
		{
			admin.getController().teleportTo(WorldMapType.POETA.getId(), 806, 1242, 119, 0);
			PacketSendUtility.sendMessage(admin, "Teleported to Poeta");
		}

		else if(params[0].equals("verteron"))
		{
			admin.getController().teleportTo(WorldMapType.VERTERON.getId(), 1643, 1500, 119, 0);
			PacketSendUtility.sendMessage(admin, "Teleported to Verteron");
		}

		else if(params[0].equals("eltnen"))
		{
			admin.getController().teleportTo(WorldMapType.ELTNEN.getId(), 343, 2724, 264, 0);
			PacketSendUtility.sendMessage(admin, "Teleported to Eltnen");
		}

		else if(params[0].equals("theobomos"))
		{
			admin.getController().teleportTo(WorldMapType.THEOMOBOS.getId(), 1398, 1557, 31, 0);
			PacketSendUtility.sendMessage(admin, "Teleported to Theobomos");
		}

		else if(params[0].equals("heiron"))
		{
			admin.getController().teleportTo(WorldMapType.HEIRON.getId(), 2540, 343, 411, 0);
			PacketSendUtility.sendMessage(admin, "Teleported to Heiron");
		}

		else if(params[0].equals("sanctum"))
		{
			admin.getController().teleportTo(WorldMapType.SANCTUM.getId(), 1322, 1511, 568, 0);
			PacketSendUtility.sendMessage(admin, "Teleported to Sanctum");
		}

		else if(params[0].equals("ishalgen"))
		{
			admin.getController().teleportTo(WorldMapType.ISHALGEN.getId(), 529, 2449, 281, 0);
			PacketSendUtility.sendMessage(admin, "Teleported to Ishalgen");
		}

		else if(params[0].equals("altgard"))
		{
			admin.getController().teleportTo(WorldMapType.ALTGARD.getId(), 1748, 1807, 254, 0);
			PacketSendUtility.sendMessage(admin, "Teleported to Altgard");
		}

		else if(params[0].equals("morheim"))
		{
			admin.getController().teleportTo(WorldMapType.MORHEIM.getId(), 308, 2274, 449, 0);
			PacketSendUtility.sendMessage(admin, "Teleported to Morheim");
		}

		else if(params[0].equals("brusthonin"))
		{
			admin.getController().teleportTo(WorldMapType.BRUSTHONIN.getId(), 2917, 2421, 15, 0);
			PacketSendUtility.sendMessage(admin, "Teleported to Brusthonin");
		}

		else if(params[0].equals("beluslan"))
		{
			admin.getController().teleportTo(WorldMapType.BELUSLAN.getId(), 325, 336, 229, 0);
			PacketSendUtility.sendMessage(admin, "Teleported to Beluslan");
		}

		else if(params[0].equals("pandaemonium"))
		{
			admin.getController().teleportTo(WorldMapType.PANDAEMONIUM.getId(), 1679, 1400, 195, 0);
			PacketSendUtility.sendMessage(admin, "Teleported to Pandaemonium");
		}

		else if(params[0].equals("abyss1"))
		{
			admin.getController().teleportTo(400010000, 2867, 1034, 1528, 0);
			PacketSendUtility.sendMessage(admin, "Teleported to Latis Plazza bottom Elyos");
		}

		else if(params[0].equals("abyss2"))
		{
			admin.getController().teleportTo(400010000, 1078, 2839, 1636, 0);
			PacketSendUtility.sendMessage(admin, "Teleported to Russet Plazza bottom Asmodians");
		}

		else if(params[0].equals("abyss3"))
		{
			admin.getController().teleportTo(400010000, 1596, 2952, 2943, 0);
			PacketSendUtility.sendMessage(admin, "Teleported to Top Asmodians");
		}

		else if(params[0].equals("abyss4"))
		{
			admin.getController().teleportTo(400010000, 2054, 660, 2843, 0);
			PacketSendUtility.sendMessage(admin, "Teleported to Top Elyos");
		}

		else if(params[0].equals("abyssfortress"))
		{
			admin.getController().teleportTo(400010000, 2130, 1925, 2322, 0);
			PacketSendUtility.sendMessage(admin, "Teleported to Core Fortress");
		}

		else if(params[0].equals("senza"))
		{
			admin.getController().teleportTo(300010000, 270, 200, 206, 0);
			PacketSendUtility.sendMessage(admin, "Teleported to Senza Nome");
		}

		else if(params[0].equals("karamatis1"))
		{
			admin.getController().teleportTo(300010000, 270, 200, 206, 0);
			PacketSendUtility.sendMessage(admin, "Teleported to Karamatis 1");
		}

		else if(params[0].equals("karamatis2"))
		{
			admin.getController().teleportTo(300010000, 270, 200, 206, 0);
			PacketSendUtility.sendMessage(admin, "Teleported to Karamatis 2");
		}

		else if(params[0].equals("aerdina"))
		{
			admin.getController().teleportTo(300010000, 270, 200, 206, 0);
			PacketSendUtility.sendMessage(admin, "Teleported to Aerdina");
		}

		else if(params[0].equals("gerania"))
		{
			admin.getController().teleportTo(300010000, 270, 200, 206, 0);
			PacketSendUtility.sendMessage(admin, "Teleported to Gerania");
		}

		else if(params[0].equals("lepharist"))
		{
			admin.getController().teleportTo(310050000, 225, 244, 133, 0);
			PacketSendUtility.sendMessage(admin, "Teleported to Lerpharist Secret Labratory");
		}

		else if(params[0].equals("fragment"))
		{
			world.despawn(admin);
			world.setPosition(admin, 310070000, 247, 249, 1392, admin.getHeading());
			admin.setProtectionActive(true);
			PacketSendUtility.sendPacket(admin, new SM_PLAYER_SPAWN(admin));
			PacketSendUtility.sendMessage(admin, "Teleported to Fragment of Darkness");
		}

		else if(params[0].equals("sanctumarena"))
		{
			world.despawn(admin);
			world.setPosition(admin, 310080000, 275, 242, 159, admin.getHeading());
			admin.setProtectionActive(true);
			PacketSendUtility.sendPacket(admin, new SM_PLAYER_SPAWN(admin));
			PacketSendUtility.sendMessage(admin, "Teleported to Sanctum Underground Arena");
		}

		else if(params[0].equals("idratu"))
		{
			world.despawn(admin);
			world.setPosition(admin, 310090000, 562, 335, 1015, admin.getHeading());
			admin.setProtectionActive(true);
			PacketSendUtility.sendPacket(admin, new SM_PLAYER_SPAWN(admin));
			PacketSendUtility.sendMessage(admin, "Teleported to Idratu Fortress");
		}

		else if(params[0].equals("azoturan"))
		{
			world.despawn(admin);
			world.setPosition(admin, 310100000, 458, 428, 1039, admin.getHeading());
			admin.setProtectionActive(true);
			PacketSendUtility.sendPacket(admin, new SM_PLAYER_SPAWN(admin));
			PacketSendUtility.sendMessage(admin, "Teleported to Azoturan");
		}

		else if(params[0].equals("ataxiar1"))
		{
			world.despawn(admin);
			world.setPosition(admin, 320010000, 229, 237, 206, admin.getHeading());
			admin.setProtectionActive(true);
			PacketSendUtility.sendPacket(admin, new SM_PLAYER_SPAWN(admin));
			PacketSendUtility.sendMessage(admin, "Teleported to Narsass 1");
		}

		else if(params[0].equals("ataxiar2"))
		{
			world.despawn(admin);
			world.setPosition(admin, 320020000, 229, 237, 206, admin.getHeading());
			admin.setProtectionActive(true);
			PacketSendUtility.sendPacket(admin, new SM_PLAYER_SPAWN(admin));
			PacketSendUtility.sendMessage(admin, "Teleported to Narsass 2");
		}

		else if(params[0].equals("bregirun"))
		{
			world.despawn(admin);
			world.setPosition(admin, 320030000, 264, 214, 210, admin.getHeading());
			admin.setProtectionActive(true);
			PacketSendUtility.sendPacket(admin, new SM_PLAYER_SPAWN(admin));
			PacketSendUtility.sendMessage(admin, "Teleported to Bregirun");
		}

		else if(params[0].equals("nidalber"))
		{
			world.despawn(admin);
			world.setPosition(admin, 320040000, 264, 214, 210, admin.getHeading());
			admin.setProtectionActive(true);
			PacketSendUtility.sendPacket(admin, new SM_PLAYER_SPAWN(admin));
			PacketSendUtility.sendMessage(admin, "Teleported to Nidalber");
		}

		else if(params[0].equals("skytemple"))
		{
			world.despawn(admin);
			world.setPosition(admin, 320050000, 177, 229, 536, admin.getHeading());
			admin.setProtectionActive(true);
			PacketSendUtility.sendPacket(admin, new SM_PLAYER_SPAWN(admin));
			PacketSendUtility.sendMessage(admin, "Teleported to Inside of the Sky Temple of Arkanis");
		}

		else if(params[0].equals("space"))
		{
			world.despawn(admin);
			world.setPosition(admin, 320070000, 246, 246, 125, admin.getHeading());
			admin.setProtectionActive(true);
			PacketSendUtility.sendPacket(admin, new SM_PLAYER_SPAWN(admin));
			PacketSendUtility.sendMessage(admin, "Teleported to Space of Destiny");
		}

		else if(params[0].equals("trinielarena"))
		{
			world.despawn(admin);
			world.setPosition(admin, 320090000, 275, 239, 159, admin.getHeading());
			admin.setProtectionActive(true);
			PacketSendUtility.sendPacket(admin, new SM_PLAYER_SPAWN(admin));
			PacketSendUtility.sendMessage(admin, "Teleported to Triniel Underground Arena");
		}

		else if(params[0].equals("firetemple"))
		{
			world.despawn(admin);
			world.setPosition(admin, 320100000, 144, 312, 123, admin.getHeading());
			admin.setProtectionActive(true);
			PacketSendUtility.sendPacket(admin, new SM_PLAYER_SPAWN(admin));
			PacketSendUtility.sendMessage(admin, "Teleported to Fire Temple");
		}

		else if(params[0].equals("reshanta"))
		{
			world.despawn(admin);
			world.setPosition(admin, 400010000, 951, 936, 1667, admin.getHeading());
			admin.setProtectionActive(true);
			PacketSendUtility.sendPacket(admin, new SM_PLAYER_SPAWN(admin));
			PacketSendUtility.sendMessage(admin, "Teleported to Reshanta");
		}

		else if(params[0].equals("prison1"))
		{
			world.despawn(admin);
			world.setPosition(admin, 510010000, 256, 256, 49, admin.getHeading());
			admin.setProtectionActive(true);
			PacketSendUtility.sendPacket(admin, new SM_PLAYER_SPAWN(admin));
			PacketSendUtility.sendMessage(admin, "Teleported to LF Prison");
		}

		else if(params[0].equals("prison2"))
		{
			world.despawn(admin);
			world.setPosition(admin, 520010000, 256, 256, 49, admin.getHeading());
			admin.setProtectionActive(true);
			PacketSendUtility.sendPacket(admin, new SM_PLAYER_SPAWN(admin));
			PacketSendUtility.sendMessage(admin, "Teleported to DF Prison");
		}

		else if(params[0].equals("test1"))
		{
			world.despawn(admin);
			world.setPosition(admin, 900100000, 196, 187, 20, admin.getHeading());
			admin.setProtectionActive(true);
			PacketSendUtility.sendPacket(admin, new SM_PLAYER_SPAWN(admin));
			PacketSendUtility.sendMessage(admin, "Teleported to Test Giant Monster");
		}

		else if(params[0].equals("test2"))
		{
			world.despawn(admin);
			world.setPosition(admin, 900020000, 144, 136, 20, admin.getHeading());
			admin.setProtectionActive(true);
			PacketSendUtility.sendPacket(admin, new SM_PLAYER_SPAWN(admin));
			PacketSendUtility.sendMessage(admin, "Teleported to Test Basic");
		}

		else if(params[0].equals("test3"))
		{
			world.despawn(admin);
			world.setPosition(admin, 900030000, 228, 171, 49, admin.getHeading());
			admin.setProtectionActive(true);
			PacketSendUtility.sendPacket(admin, new SM_PLAYER_SPAWN(admin));
			PacketSendUtility.sendMessage(admin, "Teleported to Test Server");
		}

		else 
		PacketSendUtility.sendMessage(admin, "Target location was not found");
	}
}
