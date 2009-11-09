package system.database.mysql.utils;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Author: SoulKeeper
 */
public class MySQLInsertUpdateHelper {

    private final Map<String, Object> primaryKeys = new HashMap<String, Object>();

    private final Map<String, Object> values = new HashMap<String, Object>();

    private final String tableName;

    public MySQLInsertUpdateHelper(String tableName) {
        this.tableName = tableName;
    }

    public void addPrimaryKey(String name, Object value) {
        primaryKeys.put(name, value);
    }

    public void addValue(String name, Object value) {
        values.put(name, value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ").append(tableName);
        sb.append(" (");
        for (Iterator<String> it = primaryKeys.keySet().iterator(); it.hasNext();) {
            String key = it.next();
            sb.append(key);
            if (it.hasNext() || !values.isEmpty()) {
                sb.append(',');
            }
        }

        for (Iterator<String> it = values.keySet().iterator(); it.hasNext();) {
            String key = it.next();
            sb.append(key);
            if (it.hasNext()) {
                sb.append(',');
            }
        }

        sb.append(" ) VALUES (");

        for (int i = 0, n = primaryKeys.size() + values.size(); i < n; i++) {
            sb.append('?');
            if (i + 1 < n) {
                sb.append(',');
            }
        }
        sb.append(")");
        sb.append(" ON DUPLICATE KEY UPDATE ");
        for (Iterator<String> it = values.keySet().iterator(); it.hasNext();) {
            String key = it.next();
            sb.append(key).append("=?");
            if (it.hasNext()) {
                sb.append(',');
            }
        }

        return sb.toString();
    }

    public void setValuesTo(PreparedStatement statement) {
        try {
            int i = 1;
            for (Object obj : primaryKeys.values()) {
                statement.setObject(i, obj);
                i++;
            }

            for(Object obj : values.values()){
                statement.setObject(i, obj);
                i++;
            }

            for(Object obj : values.values()){
                statement.setObject(i, obj);
                i++;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
