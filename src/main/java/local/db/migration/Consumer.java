package local.db.migration;

import java.util.List;
import java.util.Map;

public interface Consumer {

    void applyResultSet(String tableName, List<Map<String, Object>> rs);
}
