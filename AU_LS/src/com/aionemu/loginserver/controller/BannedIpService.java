/**
 * This file is part of aion-emu <aion-emu.com>.
 *
 * aion-emu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-emu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.loginserver.controller;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.utils.NetworkUtils;
import com.aionemu.loginserver.dao.BannedIpDAO;
import com.aionemu.loginserver.model.BannedIP;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Class that controlls all ip banning activity
 * 
 * @author SoulKeeper
 */
@Service
public class BannedIpService
{
	/**
	 * Logger for this class.
	 */
	private static final Logger		log	= Logger.getLogger(BannedIpService.class);

	/**
	 * List of banned ip adresses
	 */
	private Set<BannedIP>	banList;



	private final DAOManager daoManager;

	@Autowired
	public BannedIpService(DAOManager daoManager){
		this.daoManager = daoManager;
	}

	/**
	 * Loads list of banned ips
	 */
	@PostConstruct
	public void load()
	{
		reload();
	}

	/**
	 * Loads list of banned ips
	 */
	public void reload()
	{
		// we are not going to make ip ban every minute, so it's ok to simplify a concurrent code a bit
		banList = new CopyOnWriteArraySet<BannedIP>(getDAO().getAllBans());
		log.info("BannedIpService loaded " + banList.size() + " IP bans.");
	}

	/**
	 * Checks if ip (or mask) is banned
	 * 
	 * @param ip
	 *            ip address to check for ban
	 * @return is it banned or not
	 */
	public boolean isBanned(String ip)
	{
		for (BannedIP ipBan : banList)
		{
			if (ipBan.isActive() && NetworkUtils.checkIPMatching(ipBan.getMask(), ip))
				return true;
		}
		return false;
	}

	/**
	 * Bans ip or mask for infinite period of time.<br>
	 * Updates ban if exists
	 * 
	 * @param ip
	 *            ip to ban
	 */
	public void banIp(String ip)
	{
		banIp(ip, null);
	}

	/**
	 * Bans ip (or mask).<br>
	 * Updates ban if exists
	 * 
	 * @param ip
	 *            ip to ban
	 * @param expireTime
	 *            ban expiration time, null = never expires
	 */
	public void banIp(String ip, Timestamp expireTime)
	{
		BannedIP ipBan = new BannedIP();
		ipBan.setMask(ip);
		ipBan.setTimeEnd(expireTime);
		getDAO().save(ipBan);
		banList.add(ipBan);
	}

	/**
	 * Removes ip ban.
	 * 
	 * @param mask
	 *            ip to unban
	 */
	public void unbanIp(String mask)
	{
		Iterator<BannedIP> it = banList.iterator();
		while (it.hasNext())
		{
			BannedIP ipBan = it.next();
			if (ipBan.getMask().equals(mask))
			{
				getDAO().remove(ipBan.getId());
				it.remove();
				break;
			}
		}
	}

	/**
	 * Returns {@link com.aionemu.loginserver.dao.BannedIpDAO} , just a shortcut
	 * 
	 * @return {@link com.aionemu.loginserver.dao.BannedIpDAO}
	 */
	private BannedIpDAO getDAO()
	{
		return daoManager.getDAO(BannedIpDAO.class);
	}
}
