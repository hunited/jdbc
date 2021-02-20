package jdbctwo;

import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SelectEmployeesMain {

    private static final String NAME_AND_PW = "employees";

    public static void main(String[] args) throws SQLException {
        MariaDbDataSource dataSource = new MariaDbDataSource();
        dataSource.setUrl("jdbc:mariadb://localhost:3306/employees?useUnicode=true");
        dataSource.setUser(NAME_AND_PW);
        dataSource.setPassword(NAME_AND_PW);
        selectNameById(dataSource);
    }

    private static void selectNameById(MariaDbDataSource dataSource) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT `emp_name` FROM `employees`")) {
            System.out.println(getNames(resultSet));
        } catch (SQLException se) {
            throw new IllegalStateException("Cannot select employees", se);
        }
    }

    private static List<String> getNames(ResultSet resultSet) throws SQLException {
        List<String> names = new ArrayList<>();
        while (resultSet.next()) {
            String name = resultSet.getString("emp_name");
            names.add(name);
        }
        return names;
    }

}
