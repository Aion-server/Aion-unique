/*
 * This file is part of aion-unique <aion-unique.com>.
 *
 *     Aion-unique is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Aion-unique is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.stats.listeners.TitleChangeListener;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TITLE_SET;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TITLE_UPDATE;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Nemiroff
 * Date: 01.12.2009
 */
public class CM_TITLE_SET extends AionClientPacket 
{
    /**
     * Title id
     */
    private int titleId;

    /**
     * Constructs new instance of <tt>CM_TITLE_SET </tt> packet
     *
     * @param opcode
     */
    public CM_TITLE_SET(int opcode) 
    {
        super(opcode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void readImpl() 
    {
        titleId = readD();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void runImpl() 
    {
        Player player = getConnection().getActivePlayer();
        sendPacket(new SM_TITLE_SET(titleId));
        PacketSendUtility.broadcastPacket(player, (new SM_TITLE_UPDATE(player, titleId)));
        if (player.getCommonData().getTitleId()>0)
        {
        	if (player.getGameStats()!=null)
        	{
        		TitleChangeListener.onTitleChange(player.getGameStats(), player.getCommonData().getTitleId(), false);
        	}
        }
        player.getCommonData().setTitleId(titleId);
        if (player.getGameStats()!=null)
        {
        	TitleChangeListener.onTitleChange(player.getGameStats(), titleId, true);
        }
	}
}
