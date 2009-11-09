package com.aionemu.commons.database;

/**
 * Author: SoulKeeper
 */
public class DatabaseException extends RuntimeException{

    /**
	 * {@inheritDoc}
	 */
	public DatabaseException()
	{
	}

	/**
	 * {@inheritDoc}
	 */
	public DatabaseException(String message)
	{
		super(message);
	}

	/**
	 * {@inheritDoc}
	 */
	public DatabaseException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * {@inheritDoc}
	 */
	public DatabaseException(Throwable cause)
	{
		super(cause);
	}

}
