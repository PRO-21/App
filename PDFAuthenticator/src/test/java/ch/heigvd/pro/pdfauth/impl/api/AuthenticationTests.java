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
    @Order(1)
    public void appShouldGetConnectionFromAPI() throws IOException {

        HttpURLConnection conn = APIConnectionHandler.getConnection("auth");
        Assertions.assertNotNull(conn);
    }

    @Test
    @Order(2)
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
        Assertions.assertEquals(200, HttpCode);

        token = obj.getJSONObject("data").getString("token");

        Assertions.assertFalse(token.isEmpty());
    }

    @Test
    @Order(3)
    public void appShouldCreateTokenFile() throws IOException {

        tokenFile = new File("src/test/java/ch/heigvd/pro/pdfauth/impl/api/test_folder/token");
        APIConnectionHandler.createToken(token, tokenFile.getPath());
        Assertions.assertTrue(tokenFile.exists());
        Assertions.assertTrue(tokenFile.length() != 0);
    }

    @Test
    @Order(4)
    public void appShouldGetTrueIfTokenIsValid() throws IOException {
        Assertions.assertTrue(APIConnectionHandler.tokenExistsAndIsValid("src/test/java/ch/heigvd/pro/pdfauth/impl/api/test_folder/token"));
    }

    @AfterAll
    public static void deleteTokenFile() {
        tokenFile.delete();
        Assertions.assertTrue(new File("src/test/java/ch/heigvd/pro/pdfauth/impl/api/test_folder").delete());
    }
}