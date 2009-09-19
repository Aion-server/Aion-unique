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
package com.aionemu.loginserver.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;

import com.aionemu.commons.utils.Base64;

/**
 * Class with usefull methods to use with accounts
 * 
 * @author SoulKeeper
 */
public class AccountUtils
{
	/**
	 * Logger :)
	 */
	private static final Logger	log	= Logger.getLogger(AccountUtils.class);

	/**
	 * Encodes password. SHA-1 is used to encode password bytes, Base64 wraps SHA1-hash to string.
	 * 
	 * @param password
	 *            password to encode
	 * @return retunrs encoded password.
	 */
	public static String encodePassword(String password)
	{
		try
		{
			MessageDigest messageDiegest = MessageDigest.getInstance("SHA-1");
			messageDiegest.update(password.getBytes("UTF-8"));
			return Base64.encodeToString(messageDiegest.digest(), false);
		}
		catch (NoSuchAlgorithmException e)
		{
			log.error("Exception while encoding password");
			throw new Error(e);
		}
		catch (UnsupportedEncodingException e)
		{
			log.error("Exception while encoding password");
			throw new Error(e);
		}
	}
}
