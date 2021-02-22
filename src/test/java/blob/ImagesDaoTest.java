package blob;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mariadb.jdbc.MariaDbDataSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class ImagesDaoTest {

    private static final String NAME_AND_PW = "employees";
    private ImagesDao imagesDao;

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
        imagesDao = new ImagesDao(dataSource);
        Flyway flyway = Flyway.configure().dataSource(dataSource).load();
        flyway.clean();
        flyway.migrate();
    }

    @Test
    void saveImage() {
        long result = imagesDao.saveImage("training360.gif", ImagesDaoTest.class.getResourceAsStream("/training360.gif"));
        assertEquals(1, result);
    }

    @Test
    void getImageByName() throws IOException {
        imagesDao.saveImage("training360.gif", ImagesDaoTest.class.getResourceAsStream("/training360.gif"));
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (InputStream is = imagesDao.getImageByName("training360.gif")) {
            is.transferTo(byteArrayOutputStream);
        }
        assertTrue(byteArrayOutputStream.size() > 5000);
    }

}
