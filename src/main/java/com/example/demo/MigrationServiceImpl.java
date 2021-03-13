package com.example.demo;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class MigrationServiceImpl implements MigrationService {

    private Producer producer;
    private Consumer consumer;
    private List<String> tables;

    public MigrationServiceImpl(Producer producer, Consumer consumer, List<String> tables) {
        this.producer = producer;
        this.consumer = consumer;
        this.tables = tables;
    }

    @Override
    public void runMigration() {
        for (String tableName : tables) {
            List<Map<String, Object>> params = producer.getResultSet(tableName);
            consumer.applyResultSet(tableName, params);
        }
    }
}
