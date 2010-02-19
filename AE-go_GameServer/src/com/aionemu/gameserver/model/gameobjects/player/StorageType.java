/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aionemu.gameserver.model.gameobjects.player;

/**
 *
 * @author kosyachok
 */
public enum StorageType
{
	CUBE(0),
	REGULAR_WAREHOUSE(1),
	ACCOUNT_WAREHOUSE(2),
	LEGION_WAREHOUSE(3);

	private int id;

	private StorageType(int id)
	{
		this.id = id;
	}

	public int getId()
	{
		return id;
	}
}
