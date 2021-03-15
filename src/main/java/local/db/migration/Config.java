package local.db.migration;

import oracle.sql.TIMESTAMP;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionManager;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;

@Configuration
@PropertySource(
        value = {"classpath:datasource.properties"},
        ignoreResourceNotFound = true
)
public class Config {

    @Value("${source.url}")
    String sourceUrl;
    @Value("${source.user}")
    String sourceUser;
    @Value("${source.password}")
    String sourcePassword;
    @Value("${source.driver}")
    String sourceDriver;
    @Value("${source.init.sql}")
    String sourceInitSql;
    @Value("${source.validation.query}")
    String sourceValidationQuery;


    @Value("${target.url}")
    String targetUrl;
    @Value("${target.user}")
    String targetUser;
    @Value("${target.password}")
    String targetPassword;
    @Value("${target.driver}")
    String targetDriver;
    @Value("${target.init.sql}")
    String targetInitSql;
    @Value("${target.validation.query}")
    String targetValidationQuery;

    @Bean
    public DataSource source() {
        return createDataSource(sourceDriver, sourceUrl, sourceUser, sourcePassword, sourceInitSql, sourceValidationQuery);
    }

    @Bean
    public DataSource target() {
        return createDataSource(targetDriver, targetUrl, targetUser, targetPassword, targetInitSql, targetValidationQuery);
    }

    @Bean
    public NamedParameterJdbcTemplate sourceJdbcTemplate() {
        return new NamedParameterJdbcTemplate(source());
    }

    @Bean
    public NamedParameterJdbcTemplate targetJdbcTemplate() {
        return new NamedParameterJdbcTemplate(target());
    }

    @Bean
    public Function<ResultSet, Map<String, Object>> getMapper() {
        return rs -> {
            Map<String, Object> map = new HashMap<>();
            try {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = 0;
                columnCount = metaData.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    Object fieldValue = convertIfTIMESTAMP(rs.getObject(i));
                    fieldValue = convertSpecialCharacterToNull(fieldValue);
                    map.put(metaData.getColumnName(i), rs.getObject(i));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return map;
        };
    }

    private Object convertSpecialCharacterToNull(Object object) {
        if (object instanceof String && object.equals("\u0000")) {
            object = null;
        }
        return object;
    }

    private Object convertIfTIMESTAMP(Object object) throws SQLException {
        if (object instanceof TIMESTAMP) {
            TIMESTAMP ts = (TIMESTAMP) object;
            Date date = new Date(ts.timestampValue().getTime());
            return date;
        }
        return object;
    }

    @Bean
    public Producer producer(@Value("${source.threshold}") int threshold) {
        ProducerImpl producer = new ProducerImpl(sourceJdbcTemplate(), getMapper());
        producer.setThreshold(threshold);
        return producer;
    }

    @Bean
    public Consumer consumer() {
        return new ConsumerImpl(targetJdbcTemplate());
    }

    @Bean
    public List<String> tables() {
        List<String> tables = new ArrayList<>();
        tables.add("TEST1");
//        tables.add("TEST2");
        return tables;
    }

    public DataSource createDataSource(String driver, String url, String user, String password, String initSql, String query) {
        DataSource ds = new DataSource();
        ds.setInitSQL(initSql);
        ds.setDriverClassName(driver);
        ds.setUrl(url);
        ds.setUsername(user);
        ds.setPassword(password);
        ds.setInitialSize(0);
        ds.setMaxActive(1);
        ds.setMaxWait(600000);
        ds.setMinEvictableIdleTimeMillis(30000);
        ds.setValidationQuery(query);
        ds.setTestOnBorrow(true);
        ds.setTestWhileIdle(true);
        ds.setIgnoreExceptionOnPreLoad(true);
        ds.setDefaultAutoCommit(false);

        ds.setConnectionProperties("defaultRowPrefetch=1000");
        return ds;
    }

    @Bean
    public TransactionManager transactionManager() {
        TransactionManager transactionManager = new DataSourceTransactionManager(target());
        return transactionManager;
    }

}
