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
package com.aionemu.loginserver.network.netty.packet.l2c.client;

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;

import com.aionemu.loginserver.controller.AccountController;
import com.aionemu.loginserver.network.aion.AionAuthResponse;
import com.aionemu.loginserver.network.aion.SessionKey;
import com.aionemu.loginserver.network.netty.handler.ClientChannelHandler;
import com.aionemu.loginserver.network.netty.handler.ClientChannelHandler.State;
import com.aionemu.loginserver.network.netty.packet.l2c.AbstractClientPacket;
import com.aionemu.loginserver.network.netty.packet.l2c.server.SM_LOGIN_FAIL;
import com.aionemu.loginserver.network.netty.packet.l2c.server.SM_LOGIN_OK;

/**
 * @author -Nemesiss-, KID
 */
public class CM_LOGIN extends AbstractClientPacket
{
	/**
	 * Logger for this class.
	 */
	private static final Logger	log	= Logger.getLogger(CM_LOGIN.class);

	/**
	 * byte array contains encrypted login and password.
	 */
	private byte[]				data;

	/**
	 * Constructs new instance of <tt>CM_LOGIN </tt> packet.
	 * 
	 * @param buf
	 * @param channelHandler
	 */
	public CM_LOGIN(ChannelBuffer buf, ClientChannelHandler channelHandler)
	{
		super(buf, channelHandler, 0x0B);
	}

	/**
	 *  {@inheritDoc}
	 */
	@Override
	protected void readImpl()
	{
		readD();
		if (getRemainingBytes() >= 128)
		{
			data = readB(128);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void runImpl()
	{
		if (data == null)
			return;

		byte[] decrypted;
		try
		{
			Cipher rsaCipher = Cipher.getInstance("RSA/ECB/nopadding");
			rsaCipher.init(Cipher.DECRYPT_MODE, getHandler().getRSAPrivateKey());
			decrypted = rsaCipher.doFinal(data, 0, 128);
		}
		catch (GeneralSecurityException e)
		{
			log.warn("Error while decripting data on user auth." + e, e);
			getHandler().sendPacket(new SM_LOGIN_FAIL(AionAuthResponse.SYSTEM_ERROR));
			return;
		}
		String user = new String(decrypted, 64, 32).trim().toLowerCase();
		String password = new String(decrypted, 96, 32).trim();

		int ncotp = decrypted[0x7c];
		ncotp |= decrypted[0x7d] << 8;
		ncotp |= decrypted[0x7e] << 16;
		ncotp |= decrypted[0x7f] << 24;

		log.debug("AuthLogin: " + user + " pass: " + password + " ncotp: " + ncotp);

		ClientChannelHandler channelHandler = getHandler();
		AionAuthResponse response = AccountController.login(user, password, channelHandler);
		switch (response)
		{
			case AUTHED:
				channelHandler.setState(State.AUTHED_LOGIN);
				channelHandler.setSessionKey(new SessionKey(channelHandler.getAccount()));
				channelHandler.sendPacket(new SM_LOGIN_OK(channelHandler.getSessionKey()));
				break;

			default:
				channelHandler.close(new SM_LOGIN_FAIL(response), true);
				break;
		}
	}
}
