package local.db.migration;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ProducerImpl implements Producer {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final Function<ResultSet, Map<String, Object>> mapper;
    private int threshold = 1000;

    public ProducerImpl(NamedParameterJdbcTemplate jdbcTemplate, Function<ResultSet, Map<String, Object>> mapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mapper = mapper;
    }

    public void getResultSet(String tableName, Consumer consumer) {
        StringBuilder builder = new StringBuilder();
        builder.append("select * from ");
        builder.append(tableName);
        String sql = builder.toString();
        BatchHandler handler = new BatchHandler(threshold, mapper, consumer, tableName);
        jdbcTemplate.query(sql, new HashMap<>(), handler);
        int totalCount = handler.flush();
        System.out.printf("%s : %d\n", tableName, totalCount);
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }
}
