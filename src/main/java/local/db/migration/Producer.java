package local.db.migration;

public interface Producer {

    void getResultSet(String tableName, Consumer consumer);
}
