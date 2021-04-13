package ch.heigvd.pro.pdfauth.impl;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;

import ch.heigvd.pro.pdfauth.impl.api.APIConnectionHandler;
import org.json.JSONObject;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthenticationTests {

    static String token = "";
    static File tokenFile = null;

    @BeforeAll
    public static void createTestsStructure() {

        File directory = new File("src/test/java/ch/heigvd/pro/pdfauth/impl/test_folder");
        directory.mkdirs();
        Assertions.assertTrue(directory.exists());
    }

    @Test
    @Order(1)
    public void AppShouldGetConnectionFromAPI() throws IOException {

        HttpURLConnection conn = APIConnectionHandler.getConnection();
        Assertions.assertNotNull(conn);
    }

    @Test
    @Order(2)
    public void AppShouldReceiveToken() throws IOException {

        HttpURLConnection conn = APIConnectionHandler.getConnection();

        // Envoi de la demande de token à l'API
        String jsonInputString = "{\"auth_type\": \"credentials\"," +
                "\"email\": \"albert.dupontel@gmail.com\"," +
                "\"password\": \"pass\"}";

        APIConnectionHandler.sendToAPI(conn, jsonInputString);
        String response = APIConnectionHandler.recvFromAPI(conn);

        JSONObject obj = new JSONObject(response);
        int HttpCode = obj.getJSONObject("status").getInt("code");

        // Si le code est 200 alors la requête est valide
        Assertions.assertEquals(200, HttpCode);

        token = obj.getJSONObject("data").getString("token");

        Assertions.assertFalse(token.isEmpty());
    }

    @Test
    @Order(3)
    public void AppShouldCreateTokenFile() throws IOException {

        tokenFile = new File("src/test/java/ch/heigvd/pro/pdfauth/impl/test_folder/token");
        APIConnectionHandler.createToken(token, tokenFile.getPath());
        Assertions.assertTrue(tokenFile.exists());
        Assertions.assertTrue(tokenFile.length() != 0);
    }

    @Test
    @Order(4)
    public void AppShouldGetTrueIfTokenIsValid() throws IOException {
        Assertions.assertTrue(APIConnectionHandler.tokenExistsAndIsValid("src/test/java/ch/heigvd/pro/pdfauth/impl/test_folder/token"));
    }

    @AfterAll
    public static void DeleteTokenFile() {
        tokenFile.delete();
        Assertions.assertTrue(new File("src/test/java/ch/heigvd/pro/pdfauth/impl/test_folder").delete());
    }
}