/*
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
package com.aionemu.gameserver.dataholders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.templates.TitleTemplate;

/**
 * @author xavier
 *
 */
@XmlRootElement(name = "player_titles")
@XmlAccessorType(XmlAccessType.FIELD)
public class TitleData
{
	@XmlElement(name="title")
	private List<TitleTemplate> tts;
	
	private Map<Integer, TitleTemplate> titles;
	
	void afterUnmarshal(Unmarshaller u, Object parent)
	{
		titles = new HashMap<Integer, TitleTemplate>();
		for(TitleTemplate tt: tts)
		{
			titles.put(tt.getTitleId(), tt);
		}
		tts = null;
	}
	
	public TitleTemplate getTitleTemplate(int titleId)
	{
		return titles.get(titleId);
	}

	/**
	 * @return
	 */
	public int size()
	{
		return titles.size();
	}
}
