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
package com.aionemu.loginserver;

import org.apache.log4j.Logger;

/**
 * @author -Nemesiss-
 */
public class LoginServer
{
    /**
     * Logger for this class.
     */
    private static final Logger	log = Logger.getLogger(LoginServer.class);

/*
//TODO: move to LoginServer2 needed data, remove rest
    public static void main(String[] args)
    {
        LoggingService.init();

		Config.load();

		DatabaseFactory.init();
		DAOManager.init();

        // Start deadlock detector that will restart server if deadlock happened
        new DeadLockDetector(60, DeadLockDetector.RESTART).start();
        ThreadPoolManager.getInstance();



        // Initialize Key Generator
        try
        {
            KeyGen.init();
        }
        catch (Exception e)
        {
            log.fatal("Failed initializing Key Generator. Reason: " + e.getMessage(), e);
            System.exit(ExitCode.CODE_ERROR);
        }

        GameServerService.load();
        BannedIpService.load();

        // TODO! flood protector
        // TODO! brute force protector
        


        // Default NIO server
        // IOServer.getInstance().connect();
        LoginNettyServer.getInstance().initialize();
        
        Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());

        MemoryUsage	hm  = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        MemoryUsage	nhm = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();

        log.info("Heap Memory Usage: " + (hm.getUsed() / 1048576) + "/" + (hm.getMax() / 1048576) + " MB");
        log.info("NonHeap Memory Usage: " + (nhm.getUsed() / 1048576) + "/" + (nhm.getMax() / 1048576) + " MB");
    }
*/
}
