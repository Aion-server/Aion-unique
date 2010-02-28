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
package com.aionemu.gameserver.model.gameobjects.player;

import com.aionemu.gameserver.model.gameobjects.PersistentState;

/**
 * @author ATracer
 *
 */
public class AbyssRank
{
	private int ap;
	private AbyssRankTemplate rank;
	
	private PersistentState persistentState;
    private int allKill;
    private int maxRank;


    public AbyssRank(int ap, int rank, int allKill, int maxRank)
	{
		super();
		this.ap = ap;
		this.rank = AbyssRankTemplate.getTemplateById(rank);
        this.allKill = allKill;
        this.maxRank = maxRank;
	}
	
	public void addAp(int ap)
	{	
		this.setAp(this.ap + ap);
		AbyssRankTemplate newTemplate = AbyssRankTemplate.getTemplateForAp(this.ap);
		if(newTemplate != this.rank)
			setRank(newTemplate);
	}
	/**
	 * @return the ap
	 */
	public int getAp()
	{
		return ap;
	}
	
	/**
	 * @param ap the ap to set
	 */
	public void setAp(int ap)
	{
		if(ap < 0)
			ap = 0;
		this.ap = ap;
		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}
	/**
	 * @return the rank
	 */
	public AbyssRankTemplate getRank()
	{
		return rank;
	}

    /**
     * @return all Kill
     */
    public int getAllKill()
    {
        return allKill;
    }

    public void setAllKill()
    {
        this.allKill = allKill+1;
    }

    /**
     * @return max Rank
     */
    public int getMaxRank()
    {
        return maxRank;
    }

	/**
	 * @param rank the rank to set
	 */
	public void setRank(AbyssRankTemplate rank)
	{
        if(rank.getId() > this.rank.getId())
            this.maxRank = rank.getId();
		this.rank = rank;

		setPersistentState(PersistentState.UPDATE_REQUIRED);
	}
	/**
	 * @return the persistentState
	 */
	public PersistentState getPersistentState()
	{
		return persistentState;
	}
	/**
	 * @param persistentState the persistentState to set
	 */
	public void setPersistentState(PersistentState persistentState)
	{
		switch(persistentState)
		{
			case UPDATE_REQUIRED:
				if(this.persistentState == PersistentState.NEW)
					break;
			default:
				this.persistentState = persistentState;
		}
	}

	public enum AbyssRankTemplate
	{
		GRADE9_SOLDIER(1, 120, 24, 0),
		GRADE8_SOLDIER(2, 168, 37, 1200),
		GRADE7_SOLDIER(3, 235, 58, 4220),
		GRADE6_SOLDIER(4, 329, 91, 10990),
		GRADE5_SOLDIER(5, 461, 143, 23500),
		GRADE4_SOLDIER(6, 645, 225, 42780),
		GRADE3_SOLDIER(7, 903, 356, 69700),
		GRADE2_SOLDIER(8, 1264, 561, 105600),
		GRADE1_SOLDIER(9, 1770, 885, 150800),
		STAR1_OFFICER(10, 2124, 1195, 214100),
		STAR2_OFFICER(11, 2549, 1616, 278700),
		STAR3_OFFICER(12, 3059, 2184, 344500),
		STAR4_OFFICER(13, 3671, 2949, 411700),
		STAR5_OFFICER(14, 4405, 3981, 488200),
		GENERAL(15, 5286, 5374, 565400),
		GREAT_GENERAL(16, 6343, 7258, 643200),
		COMMANDER(17, 7612, 9799, 721600),
		SUPREME_COMMANDER(18, 9134, 13229, 800700);
		
		private int id;
		private int pointsGained;
		private int pointsLost;		
		private int required;
		
		private AbyssRankTemplate(int id, int pointsGained, int pointsLost, int required)
		{
			this.id = id;
			this.pointsGained = pointsGained;
			this.pointsLost = pointsLost;
			this.required = required;
		}

		/**
		 * @return the id
		 */
		public int getId()
		{
			return id;
		}

		/**
		 * @return the pointsLost
		 */
		public int getPointsLost()
		{
			return pointsLost;
		}

		/**
		 * @return the pointsGained
		 */
		public int getPointsGained()
		{
			return pointsGained;
		}	
		
		
		
		/**
		 * @return the required
		 */
		public int getRequired()
		{
			return required;
		}

		public static AbyssRankTemplate getTemplateById(int id)
		{
			for(AbyssRankTemplate template : values())
			{
				if(template.getId() == id)
					return template;
			}
			throw new IllegalArgumentException("Invalid abyss rank provided");
		}
		
		public static AbyssRankTemplate getTemplateForAp(int ap)
		{
			AbyssRankTemplate t = AbyssRankTemplate.GRADE9_SOLDIER;
			for(AbyssRankTemplate template : values())
			{
				if(template.getRequired() <= ap)
					t = template;
				else
					break;
			}
			return t;
		}
	}

	
}

