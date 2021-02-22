package metadata;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MetaDataDaoTest {

    private static final String NAME_AND_PW = "employees";
    private MetaDataDao metaDataDao;
    private Flyway flyway;

    @BeforeEach
    void setUp() {
        MariaDbDataSource dataSource = new MariaDbDataSource();
        try {
            dataSource.setUrl("jdbc:mariadb://localhost:3306/employees?useUnicode=true");
            dataSource.setUser(NAME_AND_PW);
            dataSource.setPassword(NAME_AND_PW);
        } catch (SQLException se) {
            throw new IllegalStateException("Can not connect to database", se);
        }
        metaDataDao = new MetaDataDao(dataSource);
        flyway = Flyway.configure().dataSource(dataSource).load();
    }

    @Test
    void getTableNames() {
        flyway.clean();
        flyway.migrate();
        List<String> result = metaDataDao.getTableNames();
        assertTrue(result.contains("employees"));
    }

}
