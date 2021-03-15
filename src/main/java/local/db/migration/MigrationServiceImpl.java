package local.db.migration;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MigrationServiceImpl implements MigrationService {

    private final Producer producer;
    private final Consumer consumer;
    private final List<String> tables;

    public MigrationServiceImpl(Producer producer, Consumer consumer, List<String> tables) {
        this.producer = producer;
        this.consumer = consumer;
        this.tables = tables;
    }

    @Override
    public void runMigration() {
        for (String tableName : tables) {
            producer.getResultSet(tableName, consumer);
        }
    }
}
