package system.database.mysql;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import com.aionemu.commons.database.ReadStH;
import com.aionemu.commons.database.dao.scriptloader.DAOInfo;
import com.aionemu.commons.database.dao.scriptloader.SupportedDB;
import com.aionemu.loginserver.GameServer;
import com.aionemu.loginserver.dao.GameServerDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import system.database.mysql.utils.MySQLInsertUpdateHelper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: SoulKeeper
 */
@DAOInfo(baseClass = GameServerDAO.class,
        supportedDBs = {
                @SupportedDB(name = "MySQL", majorVersion = 4, minorVersion = 1)
        }
)
@Component
@Scope("prototype")
public class MySQLGameServerDAO extends GameServerDAO {

	private final DB db;

	@Autowired
	public MySQLGameServerDAO(DB db){
		this.db = db;
	}

	@Override
	public void save(GameServer object) {

		PreparedStatement st = null;

		try{

			MySQLInsertUpdateHelper iu = new MySQLInsertUpdateHelper("gameserver");
			iu.addPrimaryKey("id", object.getId());
			iu.addValue("mask", object.getIp());
			iu.addValue("password", object.getPassword());

			st = db.prepareStatement(iu.toString());
			iu.setValuesTo(st);
			st.executeUpdate();
		} catch(Exception e){
			throw new RuntimeException(e);
		} finally {
			if(st != null){
				DB.close(st);
			}
		}
	}

	@Override
	public void remove(final Byte id) {
		db.insertUpdate("DELETE FROM gameserver WHERE gameserver.id = ?", new IUStH() {
			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException {
				stmt.setByte(1, id);
				stmt.execute();
			}
		});
	}

	@Override
	public Map<Byte, GameServer> getAllGameServers() {

		final Map<Byte, GameServer> result = new HashMap<Byte, GameServer>();

		db.select("SELECT * FROM gameserver", new ReadStH() {
			@Override
			public void handleRead(ResultSet rset) throws SQLException {
				while(rset.next()){
					byte id = rset.getByte("id");
					String ipMask = rset.getString("mask");
					String password = rset.getString("password");
					GameServer gsi = new GameServer(id, ipMask, password);
					result.put(id, gsi);
				}
			}
		});

		return result;
	}
}
