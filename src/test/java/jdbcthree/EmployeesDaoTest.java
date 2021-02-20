package jdbcthree;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class EmployeesDaoTest {

    private static final String NAME_AND_PW = "employees";
    private EmployeesDao employeesDao;
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
        employeesDao = new EmployeesDao(dataSource);
        flyway = Flyway.configure().dataSource(dataSource).load();
    }

    @Test
    void createEmployee() {
        flyway.clean();
        flyway.migrate();
        employeesDao.createEmployee("John Doe");
        assertEquals(5, employeesDao.listEmployeeNames().size());
    }

    @Test
    void listEmployeeNames() {
        assertEquals("[John Doe, Jane Doe, Jack Doe, Joe Doe, John Doe]", employeesDao.listEmployeeNames().toString());
    }

    @Test
    void findEmployeeNameById() {
        assertEquals("John Doe", employeesDao.findEmployeeNameById(5));
    }

}
