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
package mysql5;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.dao.PlayerQuestListDAO;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.QuestStateList;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author MrPoke
 * 
 */
public class MySQL5PlayerQuestListDAO extends PlayerQuestListDAO
{
	public static final String	SELECT_QUERY	= "SELECT `quest_id`, `status`, `quest_vars`, `complite_count` FROM `player_quests` WHERE `player_id`=?";
	public static final String	UPDATE_QUERY	= "UPDATE `player_quests` SET `status`=?, `quest_vars`=?, `complite_count`=? where `player_id`=? AND `quest_id`=?";
	public static final String	DELETE_QUERY	= "DELETE FROM `player_quests` WHERE `player_id`=? AND `quest_id`=?";
	public static final String	INSERT_QUERY	= "INSERT INTO `player_quests` (`player_id`, `quest_id`, `status`, `quest_vars`, `complite_count`) VALUES (?,?,?,?,?)";

	/*
	 * (non-Javadoc)
	 * @see com.aionemu.gameserver.dao.PlayerQuestListDAO#load(com.aionemu.gameserver.model.gameobjects.player.Player)
	 */
	@Override
	public QuestStateList load(final Player player)
	{
		final QuestStateList questStateList = new QuestStateList();

		DB.select(SELECT_QUERY, new ParamReadStH(){
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, player.getObjectId());
			}

			@Override
			public void handleRead(ResultSet rset) throws SQLException
			{
				while(rset.next())
				{
					int questId = rset.getInt("quest_id");
					int questVars = rset.getInt("quest_vars");
					int compliteCount = rset.getInt("complite_count");
					QuestStatus status = QuestStatus.valueOf(rset.getString("status"));
					QuestState questState = new QuestState(questId, status, questVars, compliteCount);
					questState.setPersistentState(PersistentState.UPDATED);
					questStateList.addQuest(questId, questState);
				}
			}
		});
		return questStateList;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aionemu.gameserver.dao.PlayerQuestListDAO#store(com.aionemu.gameserver.model.gameobjects.player.Player)
	 */
	@Override
	public void store(final Player player)
	{
		for(QuestState qs : player.getQuestStateList().getAllQuestState())
		{
			switch(qs.getPersistentState())
			{
				case NEW:
					addQuest(player.getObjectId(), qs);
					break;
				case UPDATE_REQUIRED:
					updateQuest(player.getObjectId(), qs);
					break;
			}
			qs.setPersistentState(PersistentState.UPDATED);
		}
	}

	/**
	 * @param playerId
	 * @param QuestState
	 */
	private void addQuest(final int playerId, final QuestState qs)
	{
		DB.insertUpdate(INSERT_QUERY, new IUStH(){
			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, playerId);
				stmt.setInt(2, qs.getQuestId());
				stmt.setString(3, qs.getStatus().toString());
				stmt.setInt(4, qs.getQuestVars().getQuestVars());
				stmt.setInt(5, qs.getCompliteCount());
				stmt.execute();
			}
		});
	}

	/**
	 * @param playerId
	 * @param qs
	 */
	private void updateQuest(final int playerId, final QuestState qs)
	{
		DB.insertUpdate(UPDATE_QUERY, new IUStH(){
			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException
			{
				stmt.setString(1, qs.getStatus().toString());
				stmt.setInt(2, qs.getQuestVars().getQuestVars());
				stmt.setInt(3, qs.getCompliteCount());
				stmt.setInt(4, playerId);
				stmt.setInt(5, qs.getQuestId());
				stmt.execute();
			}
		});
	}

	/**
	 * @param playerId The playerObjectId of the player who's quest needs to be deleted
	 * @param questId The questId that needs to be deleted
	 */
	public void deleteQuest(final int playerId, final int questId)
	{
		DB.insertUpdate(DELETE_QUERY, new IUStH(){
			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, playerId);
				stmt.setInt(2, questId);
				stmt.execute();
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean supports(String s, int i, int i1)
	{
		return MySQL5DAOUtils.supports(s, i, i1);
	}

}