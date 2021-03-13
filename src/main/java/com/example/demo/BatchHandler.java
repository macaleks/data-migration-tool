package com.example.demo;

import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class BatchHandler implements RowCallbackHandler {

    private final int threshold;
    private final Function<ResultSet, Map<String, Object>> mapper;
    private final Consumer consumer;
    private final String tableName;

    private List<Map<String, Object>> buffer;

    public BatchHandler(int threshold, Function<ResultSet, Map<String, Object>> mapper, Consumer consumer, String tableName) {
        this.threshold = threshold;
        this.mapper = mapper;
        this.consumer = consumer;
        this.tableName = tableName;
        this.buffer = new ArrayList<>(threshold);
    }

    @Override
    public final void processRow(ResultSet rs) throws SQLException {
        Map<String, Object> params = mapper.apply(rs);

        if (params != null) {
            buffer.add(params);
        }

        if (buffer.size() >= threshold) {
            flush();
        }
    }

    public final void flush() {
        if (buffer.size() > 0) {
            consumer.applyResultSet(tableName, buffer);
            buffer = new ArrayList<>(threshold);
        }
    }
}
