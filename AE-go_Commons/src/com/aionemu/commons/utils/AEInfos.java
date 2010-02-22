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
package com.aionemu.commons.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * @author lord_rex
 * This class is for get/log system informations.
 * 
 */
public class AEInfos
{
	private static final Logger	log	= Logger.getLogger(AEInfos.class);

	public static String[] getMemoryInfo()
	{
		double max = Runtime.getRuntime().maxMemory() / 1024; // maxMemory is the upper limit the jvm can use
		double allocated = Runtime.getRuntime().totalMemory() / 1024; // totalMemory the size of the current allocation pool
		double nonAllocated = max - allocated; // non allocated memory till jvm limit
		double cached = Runtime.getRuntime().freeMemory() / 1024; // freeMemory the unused memory in the allocation pool
		double used = allocated - cached; // really used memory
		double useable = max - used; // allocated, but non-used and non-allocated memory
		DecimalFormat df = new DecimalFormat(" (0.0000'%')");
		DecimalFormat df2 = new DecimalFormat(" # 'KB'");
		return new String[] { //
		    "+----", //
			"| Global Memory Informations at " + getRealTime().toString() + ":", //
			"|    |", //
			"| Allowed Memory:" + df2.format(max), //
			"|    |= Allocated Memory:" + df2.format(allocated) + df.format(allocated / max * 100), //
			"|    |= Non-Allocated Memory:" + df2.format(nonAllocated) + df.format(nonAllocated / max * 100), //
			"| Allocated Memory:" + df2.format(allocated), //
			"|    |= Used Memory:" + df2.format(used) + df.format(used / max * 100), //
			"|    |= Unused (cached) Memory:" + df2.format(cached) + df.format(cached / max * 100), //
			"| Useable Memory:" + df2.format(useable) + df.format(useable / max * 100), //
			"+----" //
		};
	}

	public static String[] getCPUInfo()
	{
		return new String[] { //
		    "Avaible CPU(s): " + Runtime.getRuntime().availableProcessors(), //
			"Processor(s) Identifier: " + System.getenv("PROCESSOR_IDENTIFIER") //
		};
	}

	public static String[] getOSInfo()
	{
		return new String[] { //
		    "OS: " + System.getProperty("os.name") + " Build: " + System.getProperty("os.version"), //
			"OS Arch: " + System.getProperty("os.arch") //
		};
	}

	public static String getRealTime()
	{
		SimpleDateFormat String = new SimpleDateFormat("H:mm:ss");
		return String.format(new Date());
	}
	
	public static void printMemoryInfo()
	{
		for(String line : getMemoryInfo())
			log.info(line);
	}
	
	public static void printCPUInfo()
	{
		for(String line : getCPUInfo())
			log.info(line);
	}
	
	public static void printOSInfo()
	{
		for(String line : getOSInfo())
			log.info(line);
	}
	
	public static void printRealTime()
	{
		log.info(getRealTime().toString());
	}
}
