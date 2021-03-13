package com.example.demo;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public interface Producer {

    Stream<Map<String, Object>> getResultSet(String tableName);
}
