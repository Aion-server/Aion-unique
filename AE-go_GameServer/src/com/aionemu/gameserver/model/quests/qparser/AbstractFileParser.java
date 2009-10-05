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
package com.aionemu.gameserver.model.quests.qparser;

import java.io.File;

/**
 * @autor: Blackmouse
 */
public abstract class AbstractFileParser extends AbstractParser
{
	private String _file;

	protected AbstractFileParser(String file)
	{
		_file = file;
	}

	@Override
	protected final void parse()
	{
		try
		{
			File file = new File(_file);

			if (!file.exists())
			{
				_log.info("[" + getClass().getSimpleName() + "] file " + file.getAbsolutePath() + " not exists");
				return;
			}
			parseDocument(file);
		}
		catch (Exception e)
		{
			_log.info("[" + getClass().getSimpleName() + "] parse(): error " + e);
			e.printStackTrace();
		}

		if (_holder != null)
		{
			_holder.log();
		}
	}
}
