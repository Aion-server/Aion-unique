/*
 * This file is part of aion-unique <aionunique.smfnew.com>.
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
package com.aionemu.gameserver.questEngine.operations;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.questEngine.Quest;
import com.aionemu.gameserver.questEngine.QuestEngineException;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Blackmouse
 */
public class NpcDialogOperation extends QuestOperation
{
    private static final String NAME = "npc_dialog";
	private final int dialogId;
	private final int questId;

    public NpcDialogOperation(NamedNodeMap attr, Quest quest)
    {
        super(attr, quest);
        dialogId = Integer.parseInt(attr.getNamedItem("id").getNodeValue());
		Node tmp = attr.getNamedItem("quest_id");
		if (tmp == null)
			questId = quest.getId();
		else
			questId = Integer.parseInt(attr.getNamedItem("quest_id").getNodeValue());
        
    }

    @Override
    protected void doOperate(Player player) throws QuestEngineException 
    {
    	PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(player.getTarget().getObjectId(), dialogId, questId));
    }

    @Override
    public String getName()
    {
        return NAME;
    }
}
