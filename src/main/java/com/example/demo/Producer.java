package com.example.demo;

import java.util.List;
import java.util.Map;

public interface Producer {

    List<Map<String, Object>> getResultSet(String tableName);
}
