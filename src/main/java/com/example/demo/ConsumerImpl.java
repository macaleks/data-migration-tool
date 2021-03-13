package com.example.demo;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConsumerImpl implements Consumer {

    private NamedParameterJdbcTemplate jdbcTemplate;

    public ConsumerImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void applyResultSet(String tableName, Stream<Map<String, Object>> rs) {
        rs.map(m -> {
            String sql = buildSqlStatement(tableName, m.keySet());
            Map<String, Object>[] params = new HashMap[1];
            params = Arrays.asList(m).toArray(params);

            jdbcTemplate.batchUpdate(sql, params);
            return 1;
        }).count();

    }

    private String buildSqlStatement(String tableName, Set<String> columnNames) {
        StringBuilder query = new StringBuilder("insert into ");
        query.append(tableName);
        query.append("(");
        query.append(columnNames.stream().collect(Collectors.joining(", ")));
        query.append(")");
        query.append("values(");
        query.append(columnNames.stream().map(c -> String.format(":%s", c)).collect(Collectors.joining(", ")));
        query.append(")");
        return query.toString();
    }
}
