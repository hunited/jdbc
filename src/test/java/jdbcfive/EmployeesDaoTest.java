package jdbcfive;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

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
    void createEmployeeWithReturnId() {
        flyway.clean();
        flyway.migrate();
        long result = employeesDao.createEmployee("John Doe");
        assertEquals(5, result);
    }

    @Test
    void createEmployees() {
        List<Long> rowIds = employeesDao.createEmployees(Arrays.asList("Jack Doe", "Jane Doe", "Joe Doe"));
        List<String> names = employeesDao.listEmployeeNames();
        List<String> asserted = Arrays.asList(
                "John Doe", "Jane Doe", "Jack Doe", "Joe Doe", "John Doe", "Jack Doe", "Jane Doe", "Joe Doe"
        );
        assertEquals(asserted, names);
        assertEquals(3, rowIds.size());
    }

    @Test
    void createEmployeesRollback() {
        List<Long> rowIds = employeesDao.createEmployees(Arrays.asList("Jack Doe", "xJane Doe", "Joe Doe"));
        List<String> names = employeesDao.listEmployeeNames();
        List<String> asserted = Arrays.asList(
                "John Doe", "Jane Doe", "Jack Doe", "Joe Doe", "John Doe"
        );
        assertEquals(asserted, names);
        assertEquals(0, rowIds.size());
    }

    @Test
    void listEmployeeNames() {
        List<String> asserted = Arrays.asList(
                "John Doe", "Jane Doe", "Jack Doe", "Joe Doe", "John Doe", "Jack Doe", "Jane Doe", "Joe Doe"
        );
        assertEquals(asserted, employeesDao.listEmployeeNames());
    }

    @Test
    void findEmployeeNameById() {
        assertEquals("John Doe", employeesDao.findEmployeeNameById(5));
    }

}