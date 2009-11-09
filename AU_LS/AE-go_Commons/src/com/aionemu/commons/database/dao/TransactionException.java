package com.aionemu.commons.database.dao;

import com.aionemu.commons.database.DatabaseException;

/**
 * Author: SoulKeeper
 */
public class TransactionException extends DatabaseException{

    /**
	 * {@inheritDoc}
	 */
	public TransactionException()
	{
	}

	/**
	 * {@inheritDoc}
	 */
	public TransactionException(String message)
	{
		super(message);
	}

	/**
	 * {@inheritDoc}
	 */
	public TransactionException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * {@inheritDoc}
	 */
	public TransactionException(Throwable cause)
	{
		super(cause);
	}
}
