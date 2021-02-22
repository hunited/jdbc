package advancedrs;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmployeesDaoTest {

    private static final String NAME_AND_PW = "employees";
    private EmployeesDao employeesDao;

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
        Flyway flyway = Flyway.configure().dataSource(dataSource).load();
        flyway.clean();
        flyway.migrate();
    }

    @Test
    void createEmployeeWithReturnId() {
        long result = employeesDao.createEmployee("John Doe");
        assertEquals(5, result);
    }

    @Test
    void createEmployees() {
        employeesDao.createEmployees(Arrays.asList("Jack Doe", "Jane Doe", "Joe Doe"));
        List<String> names = employeesDao.listEmployeeNames();
        List<String> asserted = Arrays.asList(
                "John Doe", "Jane Doe", "Jack Doe", "Joe Doe", "Jack Doe", "Jane Doe", "Joe Doe"
        );
        assertEquals(asserted, names);
        assertEquals(7, names.size());
    }

    @Test
    void createEmployeesRollback() {
        List<Long> rowIds = employeesDao.createEmployees(Arrays.asList("Jack Doe", "xJane Doe", "Joe Doe"));
        List<String> names = employeesDao.listEmployeeNames();
        List<String> asserted = Arrays.asList("John Doe", "Jane Doe", "Jack Doe", "Joe Doe");
        assertEquals(asserted, names);
        assertEquals(0, rowIds.size());
    }

    @Test
    void updateNames() {
        employeesDao.updateNames();
        assertEquals("Mr. John Doe", employeesDao.listEmployeeNames().get(0));
    }

    @Test
    void listEmployeeNames() {
        List<String> asserted = Arrays.asList("John Doe", "Jane Doe", "Jack Doe", "Joe Doe");
        assertEquals(asserted, employeesDao.listEmployeeNames());
    }

    @Test
    void listOddEmployeeNames() {
        List<String> asserted = Arrays.asList("Jack Doe", "Joe Doe");
        assertEquals(asserted, employeesDao.listOddEmployeeNames());
    }

    @Test
    void findEmployeeNameById() {
        employeesDao.createEmployee("John Doe");
        assertEquals("John Doe", employeesDao.findEmployeeNameById(5));
    }

}
