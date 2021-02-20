package jdbctwo;

import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.*;

public class FindByIdMain {

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
             PreparedStatement statement = connection.prepareStatement("SELECT `emp_name` FROM `employees` WHERE `id` = ?")) {
            statement.setLong(1, 1L);
            selectNameByResultSet(statement);
        } catch (SQLException se) {
            throw new IllegalStateException("Cannot select employees", se);
        }
    }

    private static void selectNameByResultSet(PreparedStatement statement) throws SQLException {
        try (ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                String name = resultSet.getString("emp_name");
                System.out.println(name);
            } else {
                throw new IllegalArgumentException("Not found this id");
            }
        }
    }

}
