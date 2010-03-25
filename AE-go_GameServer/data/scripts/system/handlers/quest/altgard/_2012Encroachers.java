/** MODIF EVO **/
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
package quest.altgard;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Pyro refix by Nephis
 *
 * Aller tuer 4 brutes et retourner voir Meiyer
 * Status locked de toutes les missions de Altgard
 */
public class _2012Encroachers extends QuestHandler
{
	private final static int	questId	= 2012;
	private final static int[]	mob_ids	= { 210715 };	//Brute lvl 10
	
	public _2012Encroachers()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.setNpcQuestData(203559).addOnTalkEvent(questId);
		qe.addQuestLvlUp(questId);
		for(int mob_id : mob_ids)
			qe.setNpcQuestData(mob_id).addOnKillEvent(questId);
	}
	
	/** Disponible des le level 10 **/
	public boolean onLvlUpEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null || player.getCommonData().getLevel() < 10 || qs.getStatus() != QuestStatus.LOCKED)
			return false;
				
		qs.setStatus(QuestStatus.START);
		updateQuestStatus(player, qs);
		return true;
	}
	
	public boolean onDialogEvent(QuestEnv env)
	{
		/** Initialisation de l'event **/
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null)
			return false;
		
		int var = qs.getQuestVarById(0);
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		
		/**Si on start la quete **/
		if(qs.getStatus() == QuestStatus.START)
		{
			if(targetId == 203559)
			{

				switch(env.getDialogId())
				{
					case 25:			
						if(var == 0)		//Initialisation du dialogue
							return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1011);
						else if(var <= 5)	//Rendu de la quete
						{
							return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1352);
						}
						else if(var >= 5)
						{
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(player, qs);
						}
					case 10000:
					case 10001:
						if(var == 0 || var == 5)
						{
							qs.setQuestVarById(0, var + 1);
							updateQuestStatus(player, qs);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject()
								.getObjectId(), 10));
							return true;
						}
				}
				
			}
		}
		else if(qs.getStatus() == QuestStatus.REWARD)
		{
			if(targetId == 203559)
			{
				return defaultQuestEndDialog(env);
			}
		}
		return false;
	}
	
	public boolean onKillEvent(QuestEnv env)
	{
		/** Checks **/
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null)
			return false;

		int var = qs.getQuestVarById(0);
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if(qs.getStatus() != QuestStatus.START)
			return false;
		
		switch(targetId)
		{
			case 210715:				//Brute
				if(var > 0 && var < 4)	//En tuer 4
				{
					qs.setQuestVarById(0, var + 1);
					updateQuestStatus(player, qs);
					return true;
				}
				else if(var == 4)			//Au 4eme REWARD
				{
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(player, qs);
					return true;
				}
		}
		return false;		
	}

}

/** FIN MODIF EVO **/
