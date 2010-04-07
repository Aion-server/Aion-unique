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

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.services.LoggingService;
import com.aionemu.commons.utils.AEInfos;
import com.aionemu.commons.utils.ExitCode;
import com.aionemu.loginserver.configs.Config;
import com.aionemu.loginserver.controller.BannedIpController;
import com.aionemu.loginserver.network.IOServer;
import com.aionemu.loginserver.network.ncrypt.KeyGen;
import com.aionemu.loginserver.utils.DeadLockDetector;
import com.aionemu.loginserver.utils.ThreadPoolManager;
import com.aionemu.loginserver.utils.Util;

/**
 * @author -Nemesiss-
 */
public class LoginServer
{
    /**
     * Logger for this class.
     */
    private static final Logger	log = Logger.getLogger(LoginServer.class);

    /**
     * @param args
     */
    public static void main(String[] args)
    {
    	long start = System.currentTimeMillis();
    	
        LoggingService.init();

		Config.load();

		Util.printSection("DataBase");
		DatabaseFactory.init();
		DAOManager.init();

        /** Start deadlock detector that will restart server if deadlock happened */
        new DeadLockDetector(60, DeadLockDetector.RESTART).start();
        ThreadPoolManager.getInstance();


        /**
         * Initialize Key Generator
         */
        try
        {
        	Util.printSection("KeyGen");
            KeyGen.init();
        }
        catch (Exception e)
        {
            log.fatal("Failed initializing Key Generator. Reason: " + e.getMessage(), e);
            System.exit(ExitCode.CODE_ERROR);
        }

        Util.printSection("GSTable");
        GameServerTable.load();
        Util.printSection("BannedIP");
        BannedIpController.load();

        // TODO! flood protector
        // TODO! brute force protector

        Util.printSection("IOServer");
        IOServer.getInstance().connect();
        Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());

        Util.printSection("System");
        AEInfos.printAllInfos();
        
        Util.printSection("LoginServerLog");
        log.info("AE Login Server started in " + (System.currentTimeMillis() - start) / 1000 + " seconds.");
    }
}
