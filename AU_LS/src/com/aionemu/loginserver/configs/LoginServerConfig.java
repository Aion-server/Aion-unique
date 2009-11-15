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
package com.aionemu.loginserver.configs;

import com.aionemu.commons.configuration.AbstractConfig;
import com.aionemu.commons.configuration.Property;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Properties;

/**
 * @author -Nemesiss-
 * @author SoulKeeper
 */
@Component
public class LoginServerConfig extends AbstractConfig
{
	/**
	 * @see com.aionemu.commons.configuration.AbstractConfig#AbstractConfig()
	 * @throws java.io.IOException if failed to load default properties
	 * @throws RuntimeException  if failed to parse properties
	 */
	public LoginServerConfig() throws IOException, RuntimeException {
		super();
	}

	/**
	 * @param properties properties object to use or null to use default
	 * @see com.aionemu.commons.configuration.AbstractConfig#AbstractConfig()
	 * @throws java.io.IOException if failed to load default properties
	 * @throws RuntimeException  if failed to parse properties
	 */
	public LoginServerConfig(Properties properties) throws IOException, RuntimeException {
		super(properties);
	}

	/**
     * Address that will be used to listen for clients
     */
    @Property(key = "loginserver.network.client.listen.address", defaultValue = "*:2106")
    private InetSocketAddress clientListenAddress;

    /**
     * Address that will be used to listen for gameservers
     */
    @Property(key = "loginserver.network.gameserver.listen.address", defaultValue = "*:9014")
    private InetSocketAddress gameServerListenAddress;

	/**
	 * Number of Threads that will handle io read (>= 0)
	 */
	@Property(key = "loginserver.network.nio.threads.read", defaultValue = "0")
	public int				nioReadThreads;

	/**
	 * Number of Threads that will handle io write (>= 0)
	 */
	@Property(key = "loginserver.network.nio.threads.write", defaultValue = "0")
	public int				nioWriteThreads;

	/**
	 * Should server automaticly create accounts for users or not?
	 */
	@Property(key = "loginserver.accounts.autocreate", defaultValue = "true")
	public boolean			autocreateAccounts;

	@Override
	protected String getSystemPropertiesKey() {
		return "com.aionemu.loginserver.properties";
	}

	@Override
	protected String getDefaultPropertiesLocation() {
		return "./config/loginserver.properties";
	}

    /**
     * Returns address that will be used to listen for clients
     * @return address that will be used to listen for clients
     */
    public InetSocketAddress getClientListenAddress() {
        return clientListenAddress;
    }

    /**
     * Sets address that will be used to listen for clients
     * @param clientListenAddress address that will be used to listen for clients
     */
    public void setClientListenAddress(InetSocketAddress clientListenAddress) {
        this.clientListenAddress = clientListenAddress;
    }

    /**
     * Returns address that will be used to listen for gameservers
     * @return address that will be used to listen for gameservers
     */
    public InetSocketAddress getGameServerListenAddress() {
        return gameServerListenAddress;
    }

    /**
     * Sets address that will be used to listen for gameservers
     * @param gameServerListenAddress address that will be used to listen for gameservers
     */
    public void setGameServerListenAddress(InetSocketAddress gameServerListenAddress) {
        this.gameServerListenAddress = gameServerListenAddress;
    }

    /**
     * Returns amount of threads that nio will use for reading
     * @return amount of threads that nio will use for reading
     */
    public int getNioReadThreads() {
        return nioReadThreads;
    }

    /**
     * Sets amount of threads that nio will use for reading
     * @param nioReadThreads amount of threads
     */
    public void setNioReadThreads(int nioReadThreads) {
        this.nioReadThreads = nioReadThreads;
    }

    /**
     * Returns amount of threads that nio will use for writing
     * @return amount of threads that nio will use for writing
     */
    public int getNioWriteThreads() {
        return nioWriteThreads;
    }

    /**
     * Sets amount of threads that nio will use for writing
     * @param nioWriteThreads amount of threads that nio will use for writing
     */
    public void setNioWriteThreads(int nioWriteThreads) {
        this.nioWriteThreads = nioWriteThreads;
    }

    /**
     * Returns true if account autocration is enabled. False in outher case
     * @return if account autocration is enabled
     */
    public boolean isAutocreateAccounts() {
        return autocreateAccounts;
    }

    /**
     * Sets account autocreation enabled or disabled
     * @param autocreateAccounts account autocreation enabled or disabled.
     */
    public void setAutocreateAccounts(boolean autocreateAccounts) {
        this.autocreateAccounts = autocreateAccounts;
    }
}
