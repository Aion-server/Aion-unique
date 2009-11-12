package system.database.mysql;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import com.aionemu.commons.database.ReadStH;
import com.aionemu.commons.database.dao.scriptloader.DAOInfo;
import com.aionemu.commons.database.dao.scriptloader.SupportedDB;
import com.aionemu.loginserver.dao.BannedIpDAO;
import com.aionemu.loginserver.model.BannedIP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import system.database.mysql.utils.MySQLInsertUpdateHelper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * Author: SoulKeeper
 */
@DAOInfo(baseClass = BannedIpDAO.class,
		supportedDBs = {
				@SupportedDB(name = "MySQL", majorVersion = 4, minorVersion = 1)
		}
)
@Component
public class MySQLBannedIpDAO extends BannedIpDAO {

	private DB db;

	@Autowired
	public MySQLBannedIpDAO(DB db) {
		this.db = db;
	}

	@Override
	public Set<BannedIP> getAllBans() {
		final Set<BannedIP> result = new HashSet<BannedIP>();

		db.select("SELECT * FROM banned_ip", new ReadStH() {
			@Override
			public void handleRead(ResultSet rset) throws SQLException {
				while (rset.next()) {
					BannedIP bi = new BannedIP();
					bi.setMask(rset.getString("mask"));
					bi.setTimeEnd(rset.getTimestamp("time_end"));
					result.add(bi);
				}
			}
		});

		return result;
	}

	@Override
	public void save(BannedIP object) {

		PreparedStatement ps = null;

		try {

			MySQLInsertUpdateHelper iu = new MySQLInsertUpdateHelper("banned_ip");
			iu.addPrimaryKey("mask", object.getMask());
			iu.addValue("time_end", object.getTimeEnd());

			ps = db.prepareStatement(iu.toString());
			iu.setValuesTo(ps);
			ps.executeUpdate();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if(ps != null){
				DB.close(ps);
			}
		}
	}

	@Override
	public void remove(final String mask) {
		db.insertUpdate("DELETE FROM banned_ip WHERE banned_ip.mask = ?", new IUStH() {

			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException {
				stmt.setString(1, mask);
				stmt.execute();
			}
		});
	}
}
