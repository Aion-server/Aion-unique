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
package com.aionemu.gameserver.dataholders.loadingutils;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.aionemu.gameserver.services.ItemService;
import com.aionemu.gameserver.services.ServiceProxy;
import com.aionemu.gameserver.services.TeleportService;
import com.aionemu.gameserver.spawnengine.SpawnEngine;

/**
 * @author ATracer
 *
 */
public class XmlServiceProxy extends XmlAdapter<Object, Object>
{
	private ServiceProxy serviceProxy;

	/**
	 * @param serviceProxy the serviceProxy to set
	 */
	public void setServiceProxy(ServiceProxy serviceProxy)
	{
		this.serviceProxy = serviceProxy;
	}
	
	/**
	 * 
	 * @return
	 */
	public ItemService getItemService()
	{
		return serviceProxy.getItemService();
	}

	/**
	 * 
	 * @return
	 */
	public TeleportService getTeleportService()
	{
		return serviceProxy.getTeleportService();
	}	
	
	public SpawnEngine getSpawnEngine()
	{
		return serviceProxy.getSpawnEngine();
	}
	
	@Override
	public Object marshal(Object v) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object unmarshal(Object v) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}
	
}
