package com.aionemu.commons.database;

import com.aionemu.commons.database.dao.TransactionException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Simple transaction manager that shares a transaction by thread.
 *
 * Author: SoulKeeper
 */
@Service
public class ThreadLocalTransactionService {

	/**
	 * Logger
	 */
	private static final Logger log = Logger.getLogger(ThreadLocalTransactionService.class);

    /**
     * Transaction holder
     */
    private final ThreadLocal<Connection> holder = new ThreadLocal<Connection>();

    /**
     * Database Factory to crate Transaction
     */
    private final DatabaseFactory databaseFactory;

    /**
     * Constructor
     * @param databaseFactory database factory that will be used to create transactions
     */
    @Autowired
    public ThreadLocalTransactionService(DatabaseFactory databaseFactory){
        this.databaseFactory = databaseFactory;
    }

    /**
     * Begins new transaction
     * @return new transaction
     * @throws TransactionException if transaction is already started
     */
    public Connection beginTransaction() throws TransactionException{
        Connection c = holder.get();

        if(c != null){
            throw new TransactionException("Transaction already started, please join it.");
        }

        try{
            c = databaseFactory.getConnection();
            c.setAutoCommit(false);
            holder.set(c);
        } catch(Exception e){
            throw new TransactionException("Can't start transaction", e);
        }

        return c;
    }

    /**
     * Returns opened transaction
     * @return transaction
     * @throws TransactionException if there is no opened transaction
     */
    public Connection joinTransaction() throws TransactionException{
        Connection c = holder.get();
        if(c == null){
            throw new TransactionException("Transaction not found, please start a new one.");
        }

        return c;
    }

    /**
     * Commits transaction and closes connection
     * @throws TransactionException if there is no transaction to commit or commit failed
     */
    public void commitAndClose() throws TransactionException{
        Connection c = holder.get();
        if(c == null){
            throw new TransactionException("Can't commit not existing transaction");
        }

        try{
			// TODO: implement Rollbacks to prevent data corruption
            c.commit();
        }catch(SQLException e){
            throw new TransactionException("Transaction Commit failed", e);
        } finally {
			holder.set(null);
			try {
				c.close();
			} catch (SQLException e) {
				log.error("Exception while closing connection", e);
			}
        }
    }
}
