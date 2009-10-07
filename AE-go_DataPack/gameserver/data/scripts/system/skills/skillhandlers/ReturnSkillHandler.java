/*
 * This file is part of aion-unique <aion-unique.smfnew.com>.
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
package skillhandlers;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

import com.aionemu.gameserver.dataholders.PlayerInitialData;
import com.aionemu.gameserver.dataholders.PlayerInitialData.LocationData;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.SkillTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CASTSPELL;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CASTSPELL_END;
import com.aionemu.gameserver.network.aion.serverpackets.unk.SM_UNKF5;
import com.aionemu.gameserver.skillengine.handlers.MiscSkillHandler;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.stats.StatFunctions;
import com.aionemu.gameserver.world.World;

/**
 * @author ATracer
 *
 */
public class ReturnSkillHandler extends MiscSkillHandler
{
    private static final Logger log = Logger.getLogger(ReturnSkillHandler.class);
    
    @Inject
    private World   world;
    
    @Inject
    private PlayerInitialData playerInitialData;

    public ReturnSkillHandler() 
    {
        this.setSkillId(1801);
    }

    @Override
    public void useSkill(Creature creature)
    {
        super.useSkill(creature);
        log.info("RETURN skill handler was called");
        
        
    }
    
    /* (non-Javadoc)
     * @see com.aionemu.gameserver.skillengine.handlers.TemplateSkillHandler#startUsage()
     */
    @Override
    protected void startUsage(Creature creature) 
    {      
        //TODO decide whether move logic upper in hierarchy
        Player player = (Player) creature;
        SkillTemplate template = getSkillTemplate();
        
        final int unk = 0;
        
        PacketSendUtility.broadcastPacket(player, 
                new SM_CASTSPELL(player.getObjectId(), getSkillId(), getSkillTemplate().getLevel(),
                        unk, 0, getSkillTemplate().getDuration()), true);
        
        schedulePerformAction(creature, getSkillTemplate().getDuration());
    }
    
    @Override
    protected void performAction(Creature creature) 
    {  
        Player player = (Player) creature;
        world.despawn(player);
        LocationData locationData = playerInitialData.getSpawnLocation(player.getCommonData().getRace());
        
        world.setPosition(player, locationData.getMapId(),
                locationData.getX(), locationData.getY(), locationData.getZ(), locationData.getHeading());
        
        player.setProtectionActive(true);
        PacketSendUtility.sendPacket(player, new SM_UNKF5(player));
        
        //TODO investigate unk
        int unk = 0;
        
        PacketSendUtility.broadcastPacket(player,
                new SM_CASTSPELL_END(player.getObjectId(), getSkillId(), getSkillTemplate().getLevel(), unk, 0, 0), true);
    }

}
