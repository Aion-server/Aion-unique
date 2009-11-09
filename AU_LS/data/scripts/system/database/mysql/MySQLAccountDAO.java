package system.database.mysql;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import com.aionemu.commons.database.ThreadLocalTransactionService;
import com.aionemu.commons.database.dao.scriptloader.DAOInfo;
import com.aionemu.commons.database.dao.scriptloader.SupportedDB;
import com.aionemu.loginserver.dao.AccountDAO;
import com.aionemu.loginserver.model.Account;
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
@DAOInfo(baseClass = AccountDAO.class,
        supportedDBs = {
                @SupportedDB(name = "MySQL", majorVersion = 4, minorVersion = 1)
        }
)
@Component
public class MySQLAccountDAO extends AccountDAO {

    private ThreadLocalTransactionService transactionService;

    private DB db;

    @Autowired
    public MySQLAccountDAO(ThreadLocalTransactionService transactionService, DB db) {
        this.transactionService = transactionService;
        this.db = db;
    }

    @Override
    public void save(Account object) {
        try {
            Connection c = transactionService.joinTransaction();

            MySQLInsertUpdateHelper uh = new MySQLInsertUpdateHelper("account_data");
			uh.addPrimaryKey("id", object.getId());
            uh.addValue("`name`", object.getName());
            uh.addValue("password", object.getPasswordHash());
            uh.addValue("access_level", object.getAccessLevel());
            uh.addValue("last_server", object.getLastServer());
            uh.addValue("last_ip", object.getLastIp());
            uh.addValue("ip_force", object.getIpForce());

            PreparedStatement ps = c.prepareStatement(uh.toString());
            uh.setValuesTo(ps);
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Account load(Integer id) {
		Account account = null;
		PreparedStatement st = null;
		try{
		 	st = db.prepareStatement("SELECT * FROM account_data WHERE id = ?");
			st.setInt(1, id);
			account = readAccountLoadResultSet(st.executeQuery());
		} catch(SQLException e){
			throw new RuntimeException("Exception while receiving account", e);
		} finally{
			if(st != null){
				DB.close(st);
			}
		}

        return account;
    }

    @Override
    public Account load(String name) {
        Account account = null;
		PreparedStatement st = null;
		try{
		 	st = db.prepareStatement("SELECT * FROM account_data WHERE `name` = ?");
			st.setString(1, name);
			account = readAccountLoadResultSet(st.executeQuery());
		} catch(SQLException e){
			throw new RuntimeException("Exception while receiving account", e);
		} finally{
			if(st != null){
				DB.close(st);
			}
		}

        return account;
    }

	protected Account readAccountLoadResultSet(ResultSet rs) throws SQLException{
		if(rs.next()){
			Account a = new Account();
			a.setId(rs.getInt("id"));
			a.setName(rs.getString("name"));
			a.setPasswordHash(rs.getString("password"));
			a.setAccessLevel(rs.getByte("access_level"));
			a.setLastIp(rs.getString("last_ip"));
			a.setIpForce(rs.getString("ip_force"));
			a.setLastServer(rs.getByte("last_server"));
			return a;
		} else {
			return null;
		}
	}

    @Override
    public void remove(final Integer id) {
        db.insertUpdate("DELETE FROM account_data WHERE account_data.id = ?", new IUStH() {

            @Override
            public void handleInsertUpdate(PreparedStatement stmt) throws SQLException {
                stmt.setInt(1, id);
                stmt.execute();
            }
        });
    }
}
