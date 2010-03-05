/*
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
package com.aionemu.gameserver.utils.guice;

import com.aionemu.commons.network.ConnectionFactory;
import com.aionemu.commons.network.NioServer;
import com.aionemu.commons.network.ServerCfg;
import com.aionemu.gameserver.configs.network.NetworkConfig;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionPacketHandler;
import com.aionemu.gameserver.network.factories.AionPacketHandlerFactory;
import com.aionemu.gameserver.network.factories.LoginServerConnectionFactory;
import com.aionemu.gameserver.network.factories.LsPacketHandlerFactory;
import com.aionemu.gameserver.network.loginserver.LoginServer;
import com.aionemu.gameserver.network.loginserver.LoginServerConnection;
import com.aionemu.gameserver.network.loginserver.LsPacketHandler;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryProvider;

/**
 * @author Luno
 *
 */
public class NetworkInjectionModule extends AbstractModule
{

	private Injector	injector;

	public void setInjector(Injector injector)
	{
		this.injector = injector;
	}
	@Override
	protected void configure()
	{
		// binds LoginServer as singleton
		bind(LoginServer.class).in(Scopes.SINGLETON);
		
		bind(ConnectionFactory.class).toProvider(
			FactoryProvider.newFactory(ConnectionFactory.class, AionConnection.class)).in(Scopes.SINGLETON);

		bind(LoginServerConnectionFactory.class).toProvider(
			FactoryProvider.newFactory(LoginServerConnectionFactory.class, LoginServerConnection.class)).in(
			Scopes.SINGLETON);
	}

	@Provides
	@Singleton
	AionPacketHandler provideAionPacketHandler()
	{
		return new AionPacketHandlerFactory(injector).getPacketHandler();
	}

	@Provides
	@Singleton
	LsPacketHandler provideLsPacketHandler()
	{
		return new LsPacketHandlerFactory(injector).getPacketHandler();
	}
	@Provides
	@Singleton
	NioServer provideNioServer(ConnectionFactory connectionFactory)
	{
		ServerCfg aion = new ServerCfg(NetworkConfig.GAME_BIND_ADDRESS, NetworkConfig.GAME_PORT, "Aion Connections",
			connectionFactory);

		return new NioServer(1, ThreadPoolManager.getInstance(), aion);
	}

}
