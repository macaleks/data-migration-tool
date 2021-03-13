package com.example.demo;

import java.util.Map;
import java.util.stream.Stream;

public interface Consumer {

    void applyResultSet(String tableName, Stream<Map<String, Object>> rs);
}
