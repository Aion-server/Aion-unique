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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNKF5;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;
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

	/**
	 * {@inheritDoc}
	 */
	public void executeCommand(Player admin, String... params)
	{
		if(params == null || params.length < 1)
		{
			PacketSendUtility.sendMessage(admin, "syntax //goto NomDuLieu");
			return;
		}
    
    if(params[0].equals("poeta"))
    {
		world.despawn(admin);
		world.setPosition(admin, 210010000, 1215, 1042, 141, admin.getHeading());
		admin.setProtectionActive(true);
		PacketSendUtility.sendPacket(admin, new SM_UNKF5(admin));
		PacketSendUtility.sendMessage(admin, "Teleported to Poeta");
		}
		
		else if(params[0].equals("verteron"))
    {
		world.despawn(admin);
		world.setPosition(admin, 210030000, 1643, 1500, 119, admin.getHeading());
		admin.setProtectionActive(true);
		PacketSendUtility.sendPacket(admin, new SM_UNKF5(admin));
		PacketSendUtility.sendMessage(admin, "Teleported to Verteron");
		}
		
		else if(params[0].equals("eltnen"))
    {
		world.despawn(admin);
		world.setPosition(admin, 210020000, 343, 2724, 264, admin.getHeading());
		admin.setProtectionActive(true);
		PacketSendUtility.sendPacket(admin, new SM_UNKF5(admin));
		PacketSendUtility.sendMessage(admin, "Teleported to Eltnen");
		}
		
		else if(params[0].equals("theobomos"))
    {
		world.despawn(admin);
		world.setPosition(admin, 210060000, 1398, 1557, 31, admin.getHeading());
		admin.setProtectionActive(true);
		PacketSendUtility.sendPacket(admin, new SM_UNKF5(admin));
		PacketSendUtility.sendMessage(admin, "Teleported to Theobomos");
		}
		
		else if(params[0].equals("heiron"))
    {
		world.despawn(admin);
		world.setPosition(admin, 210040000, 2540, 343, 411, admin.getHeading());
		admin.setProtectionActive(true);
		PacketSendUtility.sendPacket(admin, new SM_UNKF5(admin));
		PacketSendUtility.sendMessage(admin, "Teleported to Heiron");
		}
		
		else if(params[0].equals("sanctum"))
    {
		world.despawn(admin);
		world.setPosition(admin, 110010000, 1329, 1506, 570, admin.getHeading());
		admin.setProtectionActive(true);
		PacketSendUtility.sendPacket(admin, new SM_UNKF5(admin));
		PacketSendUtility.sendMessage(admin, "Teleported to Sanctum");
		}
		
		else if(params[0].equals("ishalgen"))
    {
		world.despawn(admin);
		world.setPosition(admin, 220010000, 562, 2786, 299, admin.getHeading());
		admin.setProtectionActive(true);
		PacketSendUtility.sendPacket(admin, new SM_UNKF5(admin));
		PacketSendUtility.sendMessage(admin, "Teleported to Ishalgen");
		}
		
		else if(params[0].equals("altgard"))
    {
		world.despawn(admin);
		world.setPosition(admin, 220030000, 1748, 1807, 254, admin.getHeading());
		admin.setProtectionActive(true);
		PacketSendUtility.sendPacket(admin, new SM_UNKF5(admin));
		PacketSendUtility.sendMessage(admin, "Teleported to Altgard");
		}
		
		else if(params[0].equals("morheim"))
    {
		world.despawn(admin);
		world.setPosition(admin, 220020000, 308, 2274, 449, admin.getHeading());
		admin.setProtectionActive(true);
		PacketSendUtility.sendPacket(admin, new SM_UNKF5(admin));
		PacketSendUtility.sendMessage(admin, "Teleported to Morheim");
		}
		
		else if(params[0].equals("brusthonin"))
    {
		world.despawn(admin);
		world.setPosition(admin, 220050000, 2917, 2421, 15, admin.getHeading());
		admin.setProtectionActive(true);
		PacketSendUtility.sendPacket(admin, new SM_UNKF5(admin));
		PacketSendUtility.sendMessage(admin, "Teleported to Brusthonin");
		}
		
		else if(params[0].equals("beluslan"))
    {
		world.despawn(admin);
		world.setPosition(admin, 220040000, 325, 336, 229, admin.getHeading());
		admin.setProtectionActive(true);
		PacketSendUtility.sendPacket(admin, new SM_UNKF5(admin));
		PacketSendUtility.sendMessage(admin, "Teleported to Beluslan");
		}
		
		else if(params[0].equals("pandemonium"))
    {
		world.despawn(admin);
		world.setPosition(admin, 120010000, 1682, 1397, 195, admin.getHeading());
		admin.setProtectionActive(true);
		PacketSendUtility.sendPacket(admin, new SM_UNKF5(admin));
		PacketSendUtility.sendMessage(admin, "Teleported to Pandemonium");
		}
		
		else if(params[0].equals("abyss1"))
    {
		world.despawn(admin);
		world.setPosition(admin, 400010000, 2867, 1034, 1528, admin.getHeading());
		admin.setProtectionActive(true);
		PacketSendUtility.sendPacket(admin, new SM_UNKF5(admin));
		PacketSendUtility.sendMessage(admin, "Teleported to Latis Plazza bottom elyos");
		}
		
		else if(params[0].equals("abyss2"))
    {
		world.despawn(admin);
		world.setPosition(admin, 400010000, 1078, 2839, 1636, admin.getHeading());
		admin.setProtectionActive(true);
		PacketSendUtility.sendPacket(admin, new SM_UNKF5(admin));
		PacketSendUtility.sendMessage(admin, "Teleported to Russet Plazza bottom asmodians");
		}
		
		else if(params[0].equals("abyss3"))
    {
		world.despawn(admin);
		world.setPosition(admin, 400010000, 1596, 2952, 2943, admin.getHeading());
		admin.setProtectionActive(true);
		PacketSendUtility.sendPacket(admin, new SM_UNKF5(admin));
		PacketSendUtility.sendMessage(admin, "Teleported to Top asmodians");
		}
		
		else if(params[0].equals("abyss4"))
    {
		world.despawn(admin);
		world.setPosition(admin, 400010000, 2054, 660, 2843, admin.getHeading());
		admin.setProtectionActive(true);
		PacketSendUtility.sendPacket(admin, new SM_UNKF5(admin));
		PacketSendUtility.sendMessage(admin, "Teleported to Top elyos");
		}
		
		else if(params[0].equals("abyssfortress"))
    {
		world.despawn(admin);
		world.setPosition(admin, 400010000, 2130, 1925, 2325, admin.getHeading());
		admin.setProtectionActive(true);
		PacketSendUtility.sendPacket(admin, new SM_UNKF5(admin));
		PacketSendUtility.sendMessage(admin, "Teleported to Core Fortress");
		}
		
		else if(params[0].equals("senza"))
    {
		world.despawn(admin);
		world.setPosition(admin, 300010000, 270, 200, 206, admin.getHeading());
		admin.setProtectionActive(true);
		PacketSendUtility.sendPacket(admin, new SM_UNKF5(admin));
		PacketSendUtility.sendMessage(admin, "Teleported to Senza Nome");
		}
		
		else if(params[0].equals("karamatis1"))
    {
		world.despawn(admin);
		world.setPosition(admin, 310010000, 270, 200, 206, admin.getHeading());
		admin.setProtectionActive(true);
		PacketSendUtility.sendPacket(admin, new SM_UNKF5(admin));
		PacketSendUtility.sendMessage(admin, "Teleported to Karamatis 1");
		}
		
		else if(params[0].equals("karamatis2"))
    {
		world.despawn(admin);
		world.setPosition(admin, 310020000, 270, 200, 206, admin.getHeading());
		admin.setProtectionActive(true);
		PacketSendUtility.sendPacket(admin, new SM_UNKF5(admin));
		PacketSendUtility.sendMessage(admin, "Teleported to Karamatis 2");
		}
		
		else if(params[0].equals("aerdina"))
    {
		world.despawn(admin);
		world.setPosition(admin, 310030000, 270, 189, 206, admin.getHeading());
		admin.setProtectionActive(true);
		PacketSendUtility.sendPacket(admin, new SM_UNKF5(admin));
		PacketSendUtility.sendMessage(admin, "Teleported to Aerdina");
		}
		
		else if(params[0].equals("gerania"))
    {
		world.despawn(admin);
		world.setPosition(admin, 310040000, 270, 189, 206, admin.getHeading());
		admin.setProtectionActive(true);
		PacketSendUtility.sendPacket(admin, new SM_UNKF5(admin));
		PacketSendUtility.sendMessage(admin, "Teleported to Gerania");
		}
		
		else if(params[0].equals("lepharist"))
    {
		world.despawn(admin);
		world.setPosition(admin, 310050000, 225, 244, 132, admin.getHeading());
		admin.setProtectionActive(true);
		PacketSendUtility.sendPacket(admin, new SM_UNKF5(admin));
		PacketSendUtility.sendMessage(admin, "Teleported to Lerpharist Secret Labratory");
		}
		
		else if(params[0].equals("fragment"))
    {
		world.despawn(admin);
		world.setPosition(admin, 310070000, 247, 249, 1392, admin.getHeading());
		admin.setProtectionActive(true);
		PacketSendUtility.sendPacket(admin, new SM_UNKF5(admin));
		PacketSendUtility.sendMessage(admin, "Teleported to Fragment of Darkness");
		}
		
		else if(params[0].equals("sanctumarena"))
    {
		world.despawn(admin);
		world.setPosition(admin, 310080000, 275, 242, 158, admin.getHeading());
		admin.setProtectionActive(true);
		PacketSendUtility.sendPacket(admin, new SM_UNKF5(admin));
		PacketSendUtility.sendMessage(admin, "Teleported to Sanctum Underground Arena");
		}
		
		else if(params[0].equals("idratu"))
    {
		world.despawn(admin);
		world.setPosition(admin, 310090000, 562, 335, 1015, admin.getHeading());
		admin.setProtectionActive(true);
		PacketSendUtility.sendPacket(admin, new SM_UNKF5(admin));
		PacketSendUtility.sendMessage(admin, "Teleported to Idratu Fortress");
		}
		
		else if(params[0].equals("azoturan"))
    {
		world.despawn(admin);
		world.setPosition(admin, 310100000, 458, 428, 1539, admin.getHeading());
		admin.setProtectionActive(true);
		PacketSendUtility.sendPacket(admin, new SM_UNKF5(admin));
		PacketSendUtility.sendMessage(admin, "Teleported to Azoturan");
		}
		
		else if(params[0].equals("narsass1"))
    {
		world.despawn(admin);
		world.setPosition(admin, 320010000, 229, 237, 206, admin.getHeading());
		admin.setProtectionActive(true);
		PacketSendUtility.sendPacket(admin, new SM_UNKF5(admin));
		PacketSendUtility.sendMessage(admin, "Teleported to Narsass 1");
		}
		
		else if(params[0].equals("narsass2"))
    {
		world.despawn(admin);
		world.setPosition(admin, 320020000, 229, 237, 206, admin.getHeading());
		admin.setProtectionActive(true);
		PacketSendUtility.sendPacket(admin, new SM_UNKF5(admin));
		PacketSendUtility.sendMessage(admin, "Teleported to Narsass 2");
		}
		
		else if(params[0].equals("bregirun"))
    {
		world.despawn(admin);
		world.setPosition(admin, 320030000, 264, 214, 210, admin.getHeading());
		admin.setProtectionActive(true);
		PacketSendUtility.sendPacket(admin, new SM_UNKF5(admin));
		PacketSendUtility.sendMessage(admin, "Teleported to Bregirun");
		}
		
		else if(params[0].equals("nidalber"))
    {
		world.despawn(admin);
		world.setPosition(admin, 320040000, 264, 214, 210, admin.getHeading());
		admin.setProtectionActive(true);
		PacketSendUtility.sendPacket(admin, new SM_UNKF5(admin));
		PacketSendUtility.sendMessage(admin, "Teleported to Nidalber");
		}
		
		else if(params[0].equals("skytemple"))
    {
		world.despawn(admin);
		world.setPosition(admin, 320050000, 177, 229, 536, admin.getHeading());
		admin.setProtectionActive(true);
		PacketSendUtility.sendPacket(admin, new SM_UNKF5(admin));
		PacketSendUtility.sendMessage(admin, "Teleported to Inside of the Sky Temple of Arkanis");
		}
		
		else if(params[0].equals("space"))
    {
		world.despawn(admin);
		world.setPosition(admin, 320070000, 246, 246, 125, admin.getHeading());
		admin.setProtectionActive(true);
		PacketSendUtility.sendPacket(admin, new SM_UNKF5(admin));
		PacketSendUtility.sendMessage(admin, "Teleported to Space of Destiny");
		}
		
		else if(params[0].equals("trinielarena"))
    {
		world.despawn(admin);
		world.setPosition(admin, 320090000, 275, 239, 158, admin.getHeading());
		admin.setProtectionActive(true);
		PacketSendUtility.sendPacket(admin, new SM_UNKF5(admin));
		PacketSendUtility.sendMessage(admin, "Teleported to Triniel Underground Arena");
		}
		
		else if(params[0].equals("firetemple"))
    {
		world.despawn(admin);
		world.setPosition(admin, 320100000, 144, 312, 122, admin.getHeading());
		admin.setProtectionActive(true);
		PacketSendUtility.sendPacket(admin, new SM_UNKF5(admin));
		PacketSendUtility.sendMessage(admin, "Teleported to Fire Temple");
		}
		
		else if(params[0].equals("reshanta"))
    {
		world.despawn(admin);
		world.setPosition(admin, 400010000, 951, 953, 1667, admin.getHeading());
		admin.setProtectionActive(true);
		PacketSendUtility.sendPacket(admin, new SM_UNKF5(admin));
		PacketSendUtility.sendMessage(admin, "Teleported to Reshanta");
		}
		
		else if(params[0].equals("prison1"))
    {
		world.despawn(admin);
		world.setPosition(admin, 510010000, 256, 256, 49, admin.getHeading());
		admin.setProtectionActive(true);
		PacketSendUtility.sendPacket(admin, new SM_UNKF5(admin));
		PacketSendUtility.sendMessage(admin, "Teleported to LF Prison");
    	}
		
		else if(params[0].equals("prison2"))
    {
		world.despawn(admin);
		world.setPosition(admin, 520010000, 256, 256, 49, admin.getHeading());
		admin.setProtectionActive(true);
		PacketSendUtility.sendPacket(admin, new SM_UNKF5(admin));
		PacketSendUtility.sendMessage(admin, "Teleported to DF Prison");
		}
		
		else if(params[0].equals("test1"))
    {
		world.despawn(admin);
		world.setPosition(admin, 900020000, 144, 136, 20, admin.getHeading());
		admin.setProtectionActive(true);
		PacketSendUtility.sendPacket(admin, new SM_UNKF5(admin));
		PacketSendUtility.sendMessage(admin, "Teleported to Test Basic");
		}
		
		else if(params[0].equals("test2"))
    {
		world.despawn(admin);
		world.setPosition(admin, 900030000, 228, 171, 49, admin.getHeading());
		admin.setProtectionActive(true);
		PacketSendUtility.sendPacket(admin, new SM_UNKF5(admin));
		PacketSendUtility.sendMessage(admin, "Teleported to Test Server");
		}
		
		else if(params[0].equals("giantmonster"))
    {
		world.despawn(admin);
		world.setPosition(admin, 900100000, 196, 187, 20, admin.getHeading());
		admin.setProtectionActive(true);
		PacketSendUtility.sendPacket(admin, new SM_UNKF5(admin));
		PacketSendUtility.sendMessage(admin, "Teleported to Test Giant Monster");
		}
		
		else 
		PacketSendUtility.sendMessage(admin, "Target location was not found");
	}
}