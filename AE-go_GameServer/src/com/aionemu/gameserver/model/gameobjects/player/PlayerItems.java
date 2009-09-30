/**
 * This file is part of aion-emu <aion-emu.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-emu is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.gameobjects.player;

/**
 * @author AEJTester
 */
public class PlayerItems
{
    private int warmer;
    private int shield;
    private int helmet;
    private int armor;
    private int gloves;
    private int boots;
    private int learrings;
    private int rearrings;
    private int lring;
    private int rring;
    private int necklace;
    private int pauldron;
    private int pants;
    private int lshard;
    private int rshard;
    private int wing;
    
	public PlayerItems()
	{
       warmer = 0;
       shield = 0;
       helmet = 0;
       armor = 0;
       gloves = 0;
       boots = 0;
       learrings = 0;
       rearrings = 0;
       lring = 0;
       rring = 0;
       necklace = 0;
       pauldron = 0;
       pants = 0;
       lshard = 0;
       rshard = 0;
       wing = 0;
	}

    public int getWarmer()
    {
        return warmer;
    }

    public void setWarmer(int warmer)
    {
        this.warmer = warmer;
    }

    public int getShield()
    {
        return shield;
    }

    public void setShield(int shield)
    {
        this.shield = shield;
    }

    public int getHelmet()
    {
        return helmet;
    }

    public void setHelmet(int helmet)
    {
        this.helmet = helmet;
    }

    public int getArmor()
    {
        return armor;
    }

    public void setArmor(int armor)
    {
        this.armor = armor;
    }

    public int getGloves()
    {
        return gloves;
    }

    public void setGloves(int gloves)
    {
        this.gloves = gloves;
    }

    public int getBoots()
    {
        return boots;
    }

    public void setBoots(int boots)
    {
        this.boots = boots;
    }

    public int getLearrings()
    {
        return learrings;
    }

    public void setLearrings(int learrings)
    {
        this.learrings = learrings;
    }

    public int getRearrings()
    {
        return rearrings;
    }

    public void setRearrings(int rearrings)
    {
        this.rearrings = rearrings;
    }

    public int getLring()
    {
        return lring;
    }

    public void setLring(int lring)
    {
        this.lring = lring;
    }

    public int getRring()
    {
        return rring;
    }

    public void setRring(int rring)
    {
        this.rring = rring;
    }

    public int getNecklace()
    {
        return necklace;
    }

    public void setNecklace(int necklace)
    {
        this.necklace = necklace;
    }

    public int getPauldron()
    {
        return pauldron;
    }

    public void setPauldron(int pauldron)
    {
        this.pauldron = pauldron;
    }

    public int getPants()
    {
        return pants;
    }

    public void setPants(int pants)
    {
        this.pants = pants;
    }

    public int getRshard()
    {
        return rshard;
    }

    public void setRshard(int rshard)
    {
        this.rshard = rshard;
    }

    public int getLshard()
    {
        return lshard;
    }

    public void setLshard(int lshard)
    {
        this.lshard = lshard;
    }

    public int getWing()
    {
        return wing;
    }

    public void setWing(int wing)
    {
        this.wing = wing;
    }
}
