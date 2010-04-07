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
import com.aionemu.gameserver.network.aion.serverpackets.SM_TRANSFORM;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.zone.ZoneName;

/**
 * @author Mr. Poke
 *
 */
public class _2021KnowYourEnemy extends QuestHandler
{

	private final static int	questId	= 2021;

	public _2021KnowYourEnemy()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.addQuestLvlUp(questId);
		qe.setNpcQuestData(203669).addOnTalkEvent(questId);
		qe.setQuestEnterZone(ZoneName.BLACK_CLAW_OUTPOST_220030000).add(questId);
		qe.setNpcQuestData(700099).addOnKillEvent(questId);
		qe.setNpcQuestData(203557).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null)
			return false;

		final int var = qs.getQuestVarById(0);
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if(qs.getStatus() == QuestStatus.START)
		{
			switch (targetId)
			{
				case 203669:
					switch(env.getDialogId())
					{
						case 25:
							if(var == 0)
								return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1011);
							else if(var == 2)
							{
								player.setTransformedModelId(0);
								PacketSendUtility.broadcastPacketAndReceive(player, new SM_TRANSFORM(player));
								return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1352);
							}
							else if(var == 6)
								return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 1693);
							break;
						case 10000:
							if (var == 0)
							{
								player.setTransformedModelId(202501);
								PacketSendUtility.broadcastPacketAndReceive(player, new SM_TRANSFORM(player));
								qs.setQuestVarById(0, var+1);
								updateQuestStatus(player, qs);
								PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
								ThreadPoolManager.getInstance().schedule(new Runnable(){
									@Override
									public void run()
									{
										if(player == null || player.getTransformedModelId() == 0)
											return;
										player.setTransformedModelId(0);
										PacketSendUtility.broadcastPacketAndReceive(player, new SM_TRANSFORM(player));
									}
								}, 300000);
								return true;
							}
							break;
						case 10001:
							if (var == 2 )
							{
								qs.setQuestVarById(0, var+1);
								updateQuestStatus(player, qs);
								PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
								return true;
							}
						case 10002:
							if (var == 6)
							{
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
			if(targetId == 203557)
			{
				if (env.getDialogId() == -1 )
					return sendQuestDialog(player, env.getVisibleObject().getObjectId(), 2034);
				else
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
		if(qs == null || qs.getStatus() != QuestStatus.START)
			return false;

		int var = qs.getQuestVarById(0);
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		
		if (targetId == 700099 && var >= 3 && var < 6 )
		{
			qs.setQuestVarById(0, var+1);
			updateQuestStatus(player, qs);
			return true;
		}
		return false;
	}

	@Override
	public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName)
	{
		if(zoneName != ZoneName.BLACK_CLAW_OUTPOST_220030000)
			return false;
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null)
			return false;
		if (qs.getQuestVarById(0) == 1)
		{
			qs.setQuestVarById(0, 2);
			updateQuestStatus(player, qs);
			return true;
		}
		return false;
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null || qs.getStatus() != QuestStatus.LOCKED || player.getLevel() < 12)
			return false;
		qs.setStatus(QuestStatus.START);
		updateQuestStatus(player, qs);
		return true;
	}

}
