package blob;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.*;

public class ImagesDao {

    private final DataSource dataSource;

    public ImagesDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public long saveImage(String fileName, InputStream is) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO `images`(`filename`, `content`) VALUES (?, ?);", Statement.RETURN_GENERATED_KEYS
             )) {
            Blob blob = connection.createBlob();
            fillBlob(is, blob);
            statement.setString(1, fileName);
            statement.setBlob(2, blob);
            statement.executeUpdate();
            return getIdByStatement(statement);
        } catch (SQLException se) {
            throw new IllegalStateException("Can not insert image", se);
        }
    }

    public InputStream getImageByName(String name) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT `content` FROM `images` WHERE `filename` = ?;")) {
            statement.setString(1, name);
            return readInputStreamFromStatement(statement);
        } catch (SQLException se) {
            throw new IllegalStateException("Can not read image", se);
        }
    }

    private void fillBlob(InputStream is, Blob blob) throws SQLException {
        try (OutputStream os = blob.setBinaryStream(1)) {
            is.transferTo(os);
        } catch (IOException ioe) {
            throw new IllegalStateException("Can not write blob", ioe);
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

    private InputStream readInputStreamFromStatement(PreparedStatement statement) throws SQLException {
        try (ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                Blob blob = resultSet.getBlob("content");
                return blob.getBinaryStream();
            }
            throw new IllegalStateException("Not found");
        }
    }

}
