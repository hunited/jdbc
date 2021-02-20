package jdbcone;

import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EmployeesMain {

    private final static String NAME_AND_PW = "employees";

    public static void main(String[] args) throws SQLException {
        MariaDbDataSource dataSource = new MariaDbDataSource();
        dataSource.setUrl("jdbc:mariadb://localhost:3306/employees?useUnicode=true");
        dataSource.setUser(NAME_AND_PW);
        dataSource.setPassword(NAME_AND_PW);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO `employees`(emp_name) VALUES (?);")) {
            statement.setString(1,"Jane Doe");
            statement.execute();
        } catch (SQLException se) {
            throw new IllegalStateException("Cannot insert", se);
        }
    }

}
