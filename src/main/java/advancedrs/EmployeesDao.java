package advancedrs;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EmployeesDao {

    private final DataSource dataSource;

    public EmployeesDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public long createEmployee(String name) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO `employees`(emp_name) VALUES (?);", Statement.RETURN_GENERATED_KEYS
             )) {
            statement.setString(1, name);
            statement.executeUpdate();
            return getIdByStatement(statement);
        } catch (SQLException se) {
            throw new IllegalStateException("Cannot insert", se);
        }
    }

    public List<Long> createEmployees(List<String> names) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            return processingSqlQueries(names, connection);
        } catch (SQLException se) {
            throw new IllegalStateException("Cannot insert", se);
        }
    }

    public List<String> listEmployeeNames() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT `emp_name` FROM `employees` ORDER BY `id`")) {
            return getNames(resultSet);
        } catch (SQLException se) {
            throw new IllegalStateException("Cannot select employees", se);
        }
    }

    public List<String> listOddEmployeeNames() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
             ResultSet resultSet = statement.executeQuery("SELECT `emp_name` FROM `employees` ORDER BY `emp_name`")) {
            if (!resultSet.next()) {
                return Collections.emptyList();
            }
            return getOddNames(resultSet);
        } catch (SQLException se) {
            throw new IllegalStateException("Cannot select employees", se);
        }
    }

    public void updateNames() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
             ResultSet resultSet = statement.executeQuery("SELECT `id`, `emp_name` FROM `employees` ORDER BY `emp_name`")) {
            while (resultSet.next()) {
                String name = resultSet.getString("emp_name");
                if (!name.startsWith("Jane")) {
                    resultSet.updateString("emp_name", "Mr. " + name);
                    resultSet.updateRow();
                }
            }
        } catch (SQLException se) {
            throw new IllegalStateException("Cannot update names", se);
        }
    }

    public String findEmployeeNameById(long id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT `emp_name` FROM `employees` WHERE `id` = ?")) {
            statement.setLong(1, id);
            return selectNameByResultSet(statement);
        } catch (SQLException se) {
            throw new IllegalStateException("Cannot select employees", se);
        }
    }

    private long getIdByStatement(PreparedStatement statement) throws SQLException {
        try (ResultSet resultSet = statement.getGeneratedKeys()) {
            if (resultSet.next()) {
                return resultSet.getLong(1);
            }
            throw new IllegalStateException("Can not get ID");
        }
    }

    private List<Long> processingSqlQueries(List<String> names, Connection connection) throws SQLException {
        List<Long> result = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO `employees`(emp_name) VALUES (?);", Statement.RETURN_GENERATED_KEYS
        )) {
            for (String name : names) {
                processingLine(result, statement, name);
            }
            connection.commit();
        } catch (IllegalArgumentException iae) {
            connection.rollback();
            result.clear();
        }
        return result;
    }

    private void processingLine(List<Long> result, PreparedStatement statement, String name) throws SQLException {
        if (name.startsWith("x")) {
            throw new IllegalArgumentException("Invalid name");
        }
        statement.setString(1, name);
        statement.executeUpdate();
        result.add(getIdByStatement(statement));
    }

    private static List<String> getNames(ResultSet resultSet) throws SQLException {
        List<String> names = new ArrayList<>();
        while (resultSet.next()) {
            String name = resultSet.getString("emp_name");
            names.add(name);
        }
        return names;
    }

    private static List<String> getOddNames(ResultSet resultSet) throws SQLException {
        List<String> names = new ArrayList<>();
        names.add(resultSet.getString("emp_name"));
        while (resultSet.relative(2)) {
            names.add(resultSet.getString("emp_name"));
        }
        return names;
    }

    private static String selectNameByResultSet(PreparedStatement statement) throws SQLException {
        try (ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getString("emp_name");
            } else {
                throw new IllegalArgumentException("Not found this id");
            }
        }
    }

}
