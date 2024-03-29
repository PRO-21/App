package ch.heigvd.pro.pdfauth.impl.api;

import org.json.JSONObject;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;

// Classe permettant de tester la communication avec l'API concernant l'authentification de l'utilisateur.
// Les tests sont exécutés dans l'ordre (avec @Order) car ensemble, ils représentent la séquence utilisée dans le code
// pour obtenir ou renouveler un token
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthenticationTests {

    static String token = "";
    static File tokenFile = null;

    @BeforeAll
    public static void createTestsStructure() {

        File directory = new File("src/test/java/ch/heigvd/pro/pdfauth/impl/api/test_folder");
        directory.mkdirs();
        Assertions.assertTrue(directory.exists());
    }

    @Test
    public void appShouldThrowExceptionIfResourceIsNull() {
        Assertions.assertThrows(NullPointerException.class, () -> APIConnectionHandler.getConnection(null));
    }

    @Test
    @Order(1)
    public void appShouldGetConnectionFromAPI() throws IOException {

        HttpURLConnection conn = APIConnectionHandler.getConnection("auth");
        Assertions.assertNotNull(conn);
    }

    @Test
    @Order(2)
    public void appShouldNotReceiveToken() throws IOException {

        HttpURLConnection conn = APIConnectionHandler.getConnection("auth");

        // Envoi de la demande de token à l'API
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("auth_type", "credentials");
        jsonInput.put("email", "inexistentuser@gmail.com");
        jsonInput.put("password", "pass");

        APIConnectionHandler.sendToAPI(conn, jsonInput.toString());
        String response = APIConnectionHandler.recvFromAPI(conn);

        JSONObject obj = new JSONObject(response);
        int HttpCode = obj.getJSONObject("status").getInt("code");

        // Si le code est 401 alors, l'accès n'est pas autorisé
        Assertions.assertEquals(HttpURLConnection.HTTP_UNAUTHORIZED, HttpCode);
    }

    @Test
    @Order(3)
    public void appShouldReceiveToken() throws IOException {

        HttpURLConnection conn = APIConnectionHandler.getConnection("auth");

        // Envoi de la demande de token à l'API
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("auth_type", "credentials");
        jsonInput.put("email", "albert.dupontel@gmail.com");
        jsonInput.put("password", "pass");

        APIConnectionHandler.sendToAPI(conn, jsonInput.toString());
        String response = APIConnectionHandler.recvFromAPI(conn);

        JSONObject obj = new JSONObject(response);
        int HttpCode = obj.getJSONObject("status").getInt("code");

        // Si le code est 200 alors la requête est valide
        Assertions.assertEquals(HttpURLConnection.HTTP_OK, HttpCode);

        token = obj.getJSONObject("data").getString("token");

        Assertions.assertFalse(token.isEmpty());
    }

    @Test
    @Order(4)
    public void appShouldCreateTokenFile() throws IOException {

        tokenFile = new File("src/test/java/ch/heigvd/pro/pdfauth/impl/api/test_folder/token");
        APIConnectionHandler.createToken(token, tokenFile.getPath());
        Assertions.assertTrue(tokenFile.exists());
        Assertions.assertTrue(tokenFile.length() != 0);
    }

    @Test
    @Order(5)
    public void appShouldGetTrueIfTokenIsValid() throws IOException {
        Assertions.assertTrue(APIConnectionHandler.tokenExistsAndIsValid("src/test/java/ch/heigvd/pro/pdfauth/impl/api/test_folder/token"));
    }

    @Test
    @Order(6)
    public void appShouldExtractUsernameFromTokenCorrectly() throws IOException {
        Assertions.assertEquals("Albert Dupontel", APIConnectionHandler.extractUsernameFromToken("src/test/java/ch/heigvd/pro/pdfauth/impl/api/test_folder/token"));
    }

    @Test
    @Order(7)
    public void appShouldDeleteTokenFile() {
        Assertions.assertTrue(APIConnectionHandler.deleteToken("src/test/java/ch/heigvd/pro/pdfauth/impl/api/test_folder/token"));
    }

    @Test
    @Order(8)
    public void deleteTokenShouldReturnFalseIfTokenIsAlreadyDeleted() {
        Assertions.assertFalse(APIConnectionHandler.deleteToken("src/test/java/ch/heigvd/pro/pdfauth/impl/api/test_folder/token"));
    }

    @Test
    @Order(9)
    public void functionShouldReturnFalseIfTokenNotExist() throws IOException {
        Assertions.assertFalse(APIConnectionHandler.tokenExistsAndIsValid("src/test/java/ch/heigvd/pro/pdfauth/impl/api/test_folder/token"));
    }

    @Test
    @Order(10)
    public void functionShouldReturnNullIfTokenNotExist() throws IOException {
        Assertions.assertNull(APIConnectionHandler.getToken("src/test/java/ch/heigvd/pro/pdfauth/impl/api/test_folder/token"));
    }

    @AfterAll
    public static void deleteTestFolder() {
        Assertions.assertTrue(new File("src/test/java/ch/heigvd/pro/pdfauth/impl/api/test_folder").delete());
    }
}