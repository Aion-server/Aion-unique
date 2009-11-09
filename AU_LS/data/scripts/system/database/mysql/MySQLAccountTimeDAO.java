package system.database.mysql;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.commons.database.ThreadLocalTransactionService;
import com.aionemu.commons.database.dao.scriptloader.DAOInfo;
import com.aionemu.commons.database.dao.scriptloader.SupportedDB;
import com.aionemu.loginserver.dao.AccountTimeDAO;
import com.aionemu.loginserver.model.AccountTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import system.database.mysql.utils.MySQLInsertUpdateHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Author: SoulKeeper
 */
@DAOInfo(baseClass = AccountTimeDAO.class,
        supportedDBs = {
                @SupportedDB(name = "MySQL", majorVersion = 4, minorVersion = 1)
        }
)
@Component
public class MySQLAccountTimeDAO implements AccountTimeDAO {

    private DB db;

    private ThreadLocalTransactionService transactionService;

    @Autowired
    public MySQLAccountTimeDAO(DB db, ThreadLocalTransactionService transactionService) {
        this.db = db;
        this.transactionService = transactionService;
    }

    @Override
    public void save(AccountTime object) {
        try{
            Connection c = transactionService.joinTransaction();

            MySQLInsertUpdateHelper uh = new MySQLInsertUpdateHelper("account_time");
            uh.addPrimaryKey("account_id", object.getId());
            uh.addValue("last_active", object.getLastLoginTime());
            uh.addValue("session_duration", object.getSessionDuration());
            uh.addValue("accumulated_online", object.getAccumulatedOnlineTime());
            uh.addValue("accumulated_rest", object.getAccumulatedRestTime());
            uh.addValue("penalty_end", object.getPenaltyEnd());

            PreparedStatement st = c.prepareStatement(uh.toString());
            uh.setValuesTo(st);
            st.execute();
            st.close();
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

	@Override
	public AccountTime load(final Integer id) {
		final AccountTime ac = new AccountTime();
        db.select("SELECT * FROM account_time WHERE account_name = ?", new ParamReadStH() {
            @Override
            public void handleRead(ResultSet rset) throws SQLException {
                rset.next();
                ac.setLastLoginTime(rset.getTimestamp("last_Active"));
                ac.setSessionDuration(rset.getLong("session_duration"));
                ac.setAccumulatedOnlineTime(rset.getLong("accumulated_online"));
                ac.setAccumulatedRestTime(rset.getLong("accumulated_rest"));
                ac.setExpirationTime(rset.getTimestamp("expiration_time"));
                ac.setPenaltyEnd(rset.getTimestamp("penalty_end"));
				ac.setId(rset.getInt("account_id"));
            }

            @Override
            public void setParams(PreparedStatement stmt) throws SQLException {
                stmt.setInt(1, id);
            }
        });

        return ac.getId() != null ? ac : null;
	}

	@Override
	public void remove(Integer id) {
		throw new UnsupportedOperationException("MySQL cascade deletion is responsible for deleting this object");
	}
}
