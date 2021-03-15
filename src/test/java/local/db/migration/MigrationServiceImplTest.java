package local.db.migration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(locations = "classpath:datasource-test.properties")
@ContextConfiguration(classes = {Config.class, MigrationServiceImpl.class})
@JdbcTest
public class MigrationServiceImplTest {

    @Autowired
    MigrationService migrationService;

    @Autowired
    NamedParameterJdbcTemplate sourceJdbcTemplate;

    @Autowired
    NamedParameterJdbcTemplate targetJdbcTemplate;

    @Autowired
    List<String> tables;

    @Value("${source.create.sql}")
    String sourceCreateSql;

    @Value("${target.create.sql}")
    String targetCreateSql;

    @BeforeEach
    void prepareDbs() {
        sourceJdbcTemplate.getJdbcTemplate().execute(sourceCreateSql);
        targetJdbcTemplate.getJdbcTemplate().execute(targetCreateSql);
        tables.clear();
        tables.add("TEST7");
    }

    @Test
    void test1() {
        String sql = "select count(*) from test7";
        int countBefore = targetJdbcTemplate.queryForObject(sql, new HashMap<>(), Integer.class);
        assertThat(countBefore).isEqualTo(0);
        migrationService.runMigration();
        int countAfter = targetJdbcTemplate.queryForObject(sql, new HashMap<>(), Integer.class);
        assertThat(countAfter).isEqualTo(2);
    }
}
