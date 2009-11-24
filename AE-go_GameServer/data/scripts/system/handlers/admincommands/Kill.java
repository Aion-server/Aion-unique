package admincommands;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/*
 * This file is part of aion-unique <aion-unique.com>.
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

/**
 * @author ATracer
 *
 */
public class Kill extends AdminCommand
{
	public Kill()
	{
		super("kill");
	}
	
	@Override
	public void executeCommand(Player admin, String[] params)
	{
		VisibleObject target = admin.getTarget();
		if(target == null)
		{
			PacketSendUtility.sendMessage(admin, "No target selected");
			return;
		}
		if(target instanceof Creature)
		{
			Creature creature = (Creature) target;
			creature.getController().onAttack(admin);
			creature.getLifeStats().reduceHp(1000000); //hope it is enough to kill every life :)	
		}
		
	}
}
