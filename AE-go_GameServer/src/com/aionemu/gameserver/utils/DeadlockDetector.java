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
package com.aionemu.gameserver.utils;

import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

import org.apache.log4j.Logger;

/**
 * @author ATracer
 */
public class DeadlockDetector implements Runnable
{
	private static final Logger log = Logger.getLogger(DeadlockDetector.class);

	private int checkInterval = 0;
	private static String INDENT = "    ";
	private StringBuilder sb = null;

	public DeadlockDetector(int checkInterval)
	{
		this.checkInterval = checkInterval * 1000; 
	}

	@Override
	public void run() 
	{
		boolean noDeadLocks = true;

		while(noDeadLocks)
		{
			try
			{
				ThreadMXBean bean = ManagementFactory.getThreadMXBean();
				long[] threadIds = bean.findDeadlockedThreads();

				if (threadIds != null) {
					log.error("Deadlock detected!");
					sb = new StringBuilder();
					noDeadLocks = false;

					ThreadInfo[] infos = bean.getThreadInfo(threadIds);
					sb.append("\nTHREAD LOCK INFO: \n");
					for (ThreadInfo threadInfo : infos) 
					{
						printThreadInfo(threadInfo);
						LockInfo[] lockInfos = threadInfo.getLockedSynchronizers();
						MonitorInfo[] monitorInfos = threadInfo.getLockedMonitors();

						printLockInfo(lockInfos);
						printMonitorInfo(threadInfo, monitorInfos);
					}
					
					sb.append("\nTHREAD DUMPS: \n");
					for (ThreadInfo ti : bean.dumpAllThreads(true, true)) 
					{  
						printThreadInfo(ti); 
					}
					log.error(sb.toString());
				}
				Thread.sleep(checkInterval);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}			
		}
	}

	private void printThreadInfo(ThreadInfo threadInfo) 
	{
		printThread(threadInfo);
		sb.append(INDENT + threadInfo.toString() + "\n");
		StackTraceElement[] stacktrace = threadInfo.getStackTrace();
		MonitorInfo[] monitors = threadInfo.getLockedMonitors();

		for (int i = 0; i < stacktrace.length; i++) 
		{
			StackTraceElement ste = stacktrace[i];
			sb.append(INDENT + "at " + ste.toString() + "\n");
			for (MonitorInfo mi : monitors) 
			{
				if (mi.getLockedStackDepth() == i) {
					sb.append(INDENT + "  - locked " + mi + "\n");
				}
			}
		}
	}

	private void printThread(ThreadInfo ti) 
	{
		sb.append("\nPrintThread\n");
		sb.append("\"" + ti.getThreadName() + "\"" + " Id="
				+ ti.getThreadId() + " in " + ti.getThreadState() + "\n");
		if (ti.getLockName() != null) 
		{
			sb.append(" on lock=" + ti.getLockName() + "\n");
		}
		if (ti.isSuspended())
		{
			sb.append(" (suspended)" + "\n");
		}
		if (ti.isInNative()) 
		{
			sb.append(" (running in native)" + "\n");
		}
		if (ti.getLockOwnerName() != null) 
		{
			sb.append(INDENT + " owned by " + ti.getLockOwnerName() + " Id="
					+ ti.getLockOwnerId() + "\n");
		}
	}

	private void printMonitorInfo(ThreadInfo threadInfo, MonitorInfo[] monitorInfos) 
	{
		sb.append(INDENT + "Locked monitors: count = " + monitorInfos.length + "\n");
		for (MonitorInfo monitorInfo : monitorInfos)
		{
			sb.append(INDENT + "  - " + monitorInfo + " locked at " + "\n");
			sb.append(INDENT + "      " + monitorInfo.getLockedStackDepth() + " "
					+ monitorInfo.getLockedStackFrame() + "\n");
		}
	}

	private void printLockInfo(LockInfo[] lockInfos) 
	{
		sb.append(INDENT + "Locked synchronizers: count = " + lockInfos.length + "\n");
		for (LockInfo lockInfo : lockInfos)
		{
			sb.append(INDENT + "  - " + lockInfo + "\n");
		}
	}
}
