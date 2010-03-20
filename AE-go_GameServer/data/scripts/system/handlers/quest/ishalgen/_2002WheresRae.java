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
package quest.ishalgen;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author Mr. Poke
 *
 */
public class _2002WheresRae extends QuestHandler
{
	private final static int	questId	= 2002;
	/**
	 * @param questId
	 */
	protected _2002WheresRae()
	{
		super(questId);
	}
	@Override
	public void register()
	{
		qe.addQuestLvlUp(questId);
		qe.setNpcQuestData(203519).addOnTalkEvent(questId);
		qe.setNpcQuestData(203534).addOnTalkEvent(questId);
		qe.setNpcQuestData(203553).addOnTalkEvent(questId);
		qe.setNpcQuestData(700045).addOnTalkEvent(questId);
		qe.setNpcQuestData(203516).addOnTalkEvent(questId);
		qe.setNpcQuestData(203537).addOnTalkEvent(questId);
		qe.setNpcQuestData(210377).addOnKillEvent(questId);
		qe.setNpcQuestData(210378).addOnKillEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null)
			return false;

		int var = qs.getQuestVarById(0);
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		if(qs.getStatus() == QuestStatus.START)
		{
			switch (targetId)
			{
				case 203519:
				{
					switch(env.getDialogId())
					{
						case 25:
							if(var == 0)
								return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1011);
						case 10000:
							if (var == 0)
							{
								qs.setQuestVarById(0, var + 1);
								updateQuestStatus(player, qs);
								PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
								return true;
							}
					}
				}
				case 203534:
				{
					switch(env.getDialogId())
					{
						case 25:
							if(var == 1)
								return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1352);
						case 10001:
							if (var == 1)
							{
								qs.setQuestVarById(0, var + 1);
								updateQuestStatus(player, qs);
								PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
								return true;
							}
					}
				}
				case 790002:
				{
					switch(env.getDialogId())
					{
						case 25:
							if(var == 2)
								return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1693);
							else if (var == 10)
								return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 2034);
							else if (var == 11)
								return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 2375);
							else if (var == 13)
								return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 2716);
						case 10002:
						case 10003:
						case 10005:
							if (var == 2 || var == 10)
							{
								qs.setQuestVarById(0, var + 1);
								updateQuestStatus(player, qs);
								PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
								return true;
							}
							else if (var == 13)
							{
								qs.setQuestVarById(0, 14);
								updateQuestStatus(player, qs);
								PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
								return true;
							}
							break;
						case 33:
							if(var == 11)
							{
								if(collectItemCheck(env))
								{
									SkillEngine.getInstance().getSkill(player, 8343, 1, player).useSkill();
									qs.setQuestVarById(0, 99);
									updateQuestStatus(player, qs);
									PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
									ThreadPoolManager.getInstance().schedule(new Runnable(){
										@Override
										public void run()
										{
											qs.setQuestVarById(0, 13);
											updateQuestStatus(player, qs);
										}
									}, 10000);
									return true;
								}
								else
									return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 2376);
							}
					}
				}
				break;
				case 700045:
					if(var == 11 && env.getDialogId() == -1)
						return true;
					break;
				case 203537:
					if(var == 14 && env.getDialogId() == -1)
					{
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(player, qs);
						Npc npc = (Npc)env.getVisibleObject();
						questService.addNewSpawn(player.getWorldId(), player.getInstanceId(), 203553, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), true);
						npc.getController().onDie();
						return true;
					}
					break;
				case 203553:
					switch(env.getDialogId())
					{
						case 25:
							if(var == 15)
								return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 3398);
						case 10007:
							if(var == 15)
							{
								env.getVisibleObject().getController().delete();
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(player, qs);
								PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
								return true;
							}
					}
			}
		}
		else if(qs.getStatus() == QuestStatus.REWARD)
		{
			if(targetId == 203516)
			{
				return defaultQuestEndDialog(env);
			}
		}
		return false;
	}
	
	@Override
	public boolean onKillEvent(QuestEnv env)
	{
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
			case 210377:
			case 210378:
				if(var >= 3 && var < 10)
				{
					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
					updateQuestStatus(player, qs);
					return true;
				}
		}
		return false;
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null || qs.getStatus() != QuestStatus.LOCKED)
			return false;

		qs.setStatus(QuestStatus.START);
		updateQuestStatus(player, qs);
		return true;
	}
}
