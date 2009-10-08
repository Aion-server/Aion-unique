package com.aionemu.gameserver.GeoData;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.aionemu.gameserver.world.WorldMapType;
import com.aionemu.gameserver.model.locations.Loc;

/**
 * Author: VISTALL
 * Thanks: Blakkky
 * Company: J Develop Station
 * Date: 10.09.2009
 * Time: 22:48:27
 */
public class GeoEngine
{
	private static GeoEngine _instance;

	private int[][][] _content = new int[WorldMapType.values().length][1536][1536];
	private Logger _log = Logger.getLogger(GeoEngine.class);

	public static GeoEngine getInstance()
	{
		if (_instance == null) _instance = new GeoEngine();
		return _instance;
	}

	GeoEngine()
	{
		load();
	}

	private void load()
	{
		File dir = new File("./data/geo");
		if (!dir.exists())
		{
			_log.info("[GeoEngine] dir with geo files " + dir.getAbsolutePath() + " not exists");
			return;
		}
		File[] files = dir.listFiles();

		for (File f : files)
		{
			if (f.getName().endsWith(".aion.geo"))
			{
				_log.info("[GeoEngine] parsing geomap for world: " + WorldMapType.getWorld(Integer.parseInt(f.getName().replace(".aion.geo", ""))));
				parseFile(f);
			}
		}
	}

	private void parseFile(File f)
	{
		int mapId = WorldMapType.getShortIdByWorldId(Integer.parseInt(f.getName().replace(".aion.geo", "")));

		FileInputStream in = null;

		try
		{
			in = new FileInputStream(f);
			byte[] data = new byte[in.available()];
			in.read(data);
			int byteRate = 24 / 8;
			for (int i = 0; i < 1536; i++)
			{
				for (int j = 0; j < 1536; j++)
				{
					byte pointData[] = new byte[byteRate];
					System.arraycopy(data, (i * 1536 + j) * byteRate, pointData, 0, byteRate);
					_content[mapId][j][i] = makeValue(pointData);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(in != null)
					in.close();
			}
			catch (IOException e)
			{
				//e.printStackTrace();
			}
		}
	}

	private int makeValue(byte... a)
	{
		return a[2] & 0xff;
	}

	public float getZ(Loc loc)
	{
		if(_content[loc.getWorldType().getShortID()] == null)
			return loc.getZ();

		return _content[loc.getWorldType().getShortID()][(int)loc.getX()][(int)loc.getY()];
	}
}
