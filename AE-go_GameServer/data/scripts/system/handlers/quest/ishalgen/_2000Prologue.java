/*
 * This file is part of aion-unique <aion-unique.org>.
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
package quest.ishalgen;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAY_MOVIE;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author MrPoke
 * 
 */
public class _2000Prologue extends QuestHandler
{

	private final static int	questId	= 2000;

	public _2000Prologue()
	{
		super(questId);
		QuestEngine.getInstance().addOnEnterWorld(questId);
		QuestEngine.getInstance().setQuestMovieEndIds(2).add(questId);
	}

	@Override
	public boolean onEnterWorldEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		if(player.getCommonData().getRace() != Race.ASMODIANS)
			return false;
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null)
		{
			env.setQuestId(questId);
			QuestEngine.getInstance().getQuest(env).startQuest(QuestStatus.START);
		}
		qs = player.getQuestStateList().getQuestState(questId);
		if(qs.getStatus() == QuestStatus.START)
		{
			PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(1, 2));
			return true;
		}
		return false;
	}

	@Override
	public boolean onMovieEndEvent(QuestEnv env, int movieId)
	{
		if(movieId != 2)
			return false;
		Player player = env.getPlayer();
		if(player.getCommonData().getRace() != Race.ASMODIANS)
			return false;
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null || qs.getStatus() != QuestStatus.START)
			return false;
		qs.setStatus(QuestStatus.REWARD);
		QuestEngine.getInstance().getQuest(env).questFinish();
		return true;
	}
}
