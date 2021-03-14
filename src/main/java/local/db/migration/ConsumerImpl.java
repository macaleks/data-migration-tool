package local.db.migration;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ConsumerImpl implements Consumer {

    private NamedParameterJdbcTemplate jdbcTemplate;

    public ConsumerImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void applyResultSet(String tableName, List<Map<String, Object>> rs) {
        String sql = buildSqlStatement(tableName, rs);
        Map<String, Object>[] params = new HashMap[rs.size()];
        params = rs.toArray(params);

        jdbcTemplate.batchUpdate(sql, params);
    }

    private String buildSqlStatement(String tableName, List<Map<String, Object>> rs) {
        StringBuilder query = new StringBuilder("insert into ");
        query.append(tableName);
        query.append("(");
        Set<String> columnNames = rs.get(0).keySet();
        query.append(columnNames.stream().collect(Collectors.joining(", ")));
        query.append(")");
        query.append("values(");
        query.append(columnNames.stream().map(c -> String.format(":%s", c)).collect(Collectors.joining(", ")));
        query.append(")");
        return query.toString();
    }
}
