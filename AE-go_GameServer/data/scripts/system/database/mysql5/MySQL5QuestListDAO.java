/*
 * This file is part of aion-unique <aionu-unique.com>.
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

import org.apache.log4j.Logger;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.dao.QuestListDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.QuestStateList;
import com.aionemu.gameserver.questEngine.Quest;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.QuestEngineException;
import com.aionemu.gameserver.questEngine.QuestState;
import com.aionemu.gameserver.questEngine.types.QuestStatus;

/**
 * @author MrPoke
 * 
 */
public class MySQL5QuestListDAO extends QuestListDAO
{
    private static final Logger log = Logger.getLogger(MySQL5QuestListDAO.class);

    public static final String SELECT_QUERY = "SELECT `player_id`, `quest_id`, `status`, `quest_vars` FROM player_quests WHERE `player_id`=?";

    @Override
    public QuestStateList load(final Player player)
    {
        final QuestStateList questStateList = new QuestStateList();

        DB.select(SELECT_QUERY, new ParamReadStH()
        {
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
					try
					{
						int questId = rset.getInt("quest_id");
						Quest quest = QuestEngine.getInstance().getQuest(questId);
	                    int questVars = rset.getInt("quest_vars");
	                    QuestStatus status = QuestStatus.valueOf(rset.getString("status"));
	                    QuestState questState = new QuestState(quest, status, questVars);
	                    questStateList.addQuest(questId, questState);
					}
					catch(QuestEngineException e)
					{
						e.printStackTrace();
					}
                }
            }
        });
        return questStateList;
    }

    @Override
    public void store(final int playerId, final QuestStateList questStateList)
    {
        for(QuestState qs : questStateList.getAllQuestState())
        {
            store(playerId, qs);      
        } 
    }

    @Override
    public boolean store(final int playerId, final QuestState questState)
    {   
        //TODO decide on performance
        return DB.insertUpdate("REPLACE INTO player_quests ("
                + "`player_id`, `quest_id`, `status`, `quest_vars`)" + " VALUES "
                + "(?, ?, ?, ?" + ")", new IUStH() {
                    @Override
                    public void handleInsertUpdate(PreparedStatement ps) throws SQLException
                    {
                        ps.setInt(1, playerId);
                        ps.setInt(2, questState.getQuestId());
                        ps.setString(3, questState.getStatus().toString());
                        ps.setInt(4, questState.getQuestVars().getQuestVars());
                        ps.execute();
                    }
                });
    }

    @Override
    public void delete(final int playerId, final int questId) 
    {
        PreparedStatement statement = DB.prepareStatement("DELETE FROM player_quests WHERE player_id = ? AND quest_id = ?");
        try
        {
            statement.setInt(1, playerId);
            statement.setInt(2, questId);
        }
        catch(SQLException e)
        {
            log.error("Can't set int parameter to PreparedStatement", e);
        }
        DB.executeUpdateAndClose(statement);
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
