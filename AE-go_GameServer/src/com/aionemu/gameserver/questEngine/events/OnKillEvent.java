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
package com.aionemu.gameserver.questEngine.events;

import java.util.StringTokenizer;

import org.w3c.dom.NamedNodeMap;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.questEngine.Quest;

/**
 * @author MrPoke
 */
public class OnKillEvent extends QuestEvent
{
    private static final String NAME = "on_kill";

    public OnKillEvent(Quest quest, NamedNodeMap attr)
    {
        super(quest);
		StringTokenizer st = new StringTokenizer(attr.getNamedItem("ids").getNodeValue(), ",");
		if (st == null)
			return;
		int tokenCount = st.countTokens();
		for (int i=0; i<tokenCount; i++)
		{
			Integer value = Integer.decode(st.nextToken().trim());
			DataManager.NPC_DATA.getNpcTemplate(value).getNpcQuestData().addOnKillEvent(this);
		}
    }

    @Override
    public String getName()
    {
        return NAME;
    }
}
