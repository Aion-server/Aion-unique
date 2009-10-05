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
public abstract class AbstractDirParser extends AbstractParser
{
	private String _root;
	private String _ignoringFile;

	protected AbstractDirParser(String root, String ignoringFile)
	{
		_root = root;
		_ignoringFile = ignoringFile;
	}

	@Override
	protected final void parse()
	{
		parse(_root);
		_holder.log();
	}

	protected final void parse(String root)
	{
		File file = null;
		try
		{
			File dir = new File(root);

			if (!dir.exists())
			{
				_log.info("[" + getClass().getSimpleName() + "] Dir " + dir.getAbsolutePath() + " not exists");
				return;
			}

			File[] files = dir.listFiles();
			for (File f : files)
			{
				if (f.isDirectory())
				{
					parse(f.getAbsolutePath());
				}
				else if (f.getName().endsWith(".xml") && !f.getName().equals(_ignoringFile))
				{
					file = f;
					parseDocument(f);
				}
			}
		}
		catch (Exception e)
		{
			_log.info("[" + getClass().getSimpleName() + "] parse(): error " + e + " in file " + file.getName());
			e.printStackTrace();
		}
	}
}
