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
	ACCOUNT_WAREHOUSE(2);
	// TODO Legion Warehouse

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
