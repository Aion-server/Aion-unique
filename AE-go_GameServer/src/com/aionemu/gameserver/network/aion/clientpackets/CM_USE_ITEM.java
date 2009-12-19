/**
 * This file is part of aion-unique <www.aion-unique.com>.
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
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import org.apache.log4j.Logger;

import com.aionemu.gameserver.itemengine.actions.AbstractItemAction;
import com.aionemu.gameserver.itemengine.actions.ItemActions;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;

/**
 * @author Avol
 */
public class CM_USE_ITEM extends AionClientPacket {

    public int uniqueItemId;
    public int type, targetItemId;


    private static final Logger log = Logger.getLogger(CM_USE_ITEM.class);

    public CM_USE_ITEM(int opcode) {
        super(opcode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void readImpl() {
        uniqueItemId = readD();
        type = readC();
        if (type == 2)
        {
            targetItemId = readD();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void runImpl() {


            Player player = getConnection().getActivePlayer();
            Item item = player.getInventory().getItemByObjId(uniqueItemId);
            Item targetItem = player.getInventory().findItemByObjId(targetItemId);
            ItemActions itemActions = item.getItemTemplate().getActions();
        
            if (itemActions != null)
            {
                for (AbstractItemAction itemAction : itemActions.getItemActions())
                {
                    itemAction.act(player, item, targetItem);
                }
            }
    }
}
