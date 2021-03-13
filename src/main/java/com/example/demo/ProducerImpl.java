package com.example.demo;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ProducerImpl implements Producer {

    private NamedParameterJdbcTemplate jdbcTemplate;
    private RowMapper<Map<String, Object>> rowMapper;

    public ProducerImpl(NamedParameterJdbcTemplate jdbcTemplate, RowMapper<Map<String, Object>> rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
    }

    public Stream<Map<String, Object>> getResultSet(String tableName) {
        StringBuilder builder = new StringBuilder();
        builder.append("select * from ");
        builder.append(tableName);
        String sql = builder.toString();
        Stream<Map<String, Object>> mapStream = jdbcTemplate.queryForStream(sql, new HashMap<>(), rowMapper);
        return mapStream;
    }
}
