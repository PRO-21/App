package ch.heigvd.pro.pdfauth.impl.api;

import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;

// Classe permettant de gérer la communication avec l'API
public class APIConnectionHandler {

    /**
     * Fonction permettant de se connecter à l'API
     * @param resource type de ressource à demander à l'API
     * @return connexion URL à l'API en HTTP
     */
    public static HttpURLConnection getConnection(String resource) throws IOException {

        // Désactivation de vérifications SSL car exceptions sinon
        // source : https://stackoverflow.com/questions/19540289/how-to-fix-the-java-security-cert-certificateexception-no-subject-alternative
        /*javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
                (hostname, sslSession) -> {
                    return hostname.equals("pro.simeunovic.ch"); // ou return true
                });*/

        URL url = new URL("https://pro.simeunovic.ch:8022/protest/api/" + resource);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);

        return conn;
    }

    /**
     * Fonction permettant d'envoyer des données à l'API sous forme de chaîne JSON
     * @param conn              connexion à l'API
     * @param jsonInputString   données à envoyer
     */
    public static void sendToAPI(HttpURLConnection conn, String jsonInputString) throws IOException {

        // Envoi des données à l'API
        try (BufferedOutputStream bos = new BufferedOutputStream(conn.getOutputStream())) {
            byte[] input = jsonInputString.getBytes();
            bos.write(input, 0, input.length);
            bos.flush();
        }
    }

    /**
     * Fonction permettant de recevoir la réponse de l'API sous forme de chaîne JSON
     * @param conn connexion à l'API
     * @return réponse de l'API sous forme JSON
     */
    public static String recvFromAPI(HttpURLConnection conn) {

        StringBuilder response = new StringBuilder();

        // Récupération du message de l'API si la requête est valide
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {

            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        } catch (IOException ex) { // Récupération du message d'erreur renvoyé par l'API

            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {

                // Dans le cas où une exception survient dans le try, il faut reconstruire la String
                response = new StringBuilder();

                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            } catch (IOException e) { // Dans le cas où il y a un problème lors de la lecture du stream d'erreur
                System.out.println("Error when reading error stream");
            }
        }
        return response.toString();
    }

    /**
     * Fonction permettant de déterminer si le token existant existe et est toujours valide
     * @return true s'il existe et qu'il est valide, false sinon
     */
    public static boolean tokenExistsAndIsValid(String path) throws IOException {

        // TODO : A voir dans quel dossier il faudra le créé lors de la release

        // Récupération du token existant pour demander à l'API s'il est toujours valide
        String token = getToken(path);

        if (token != null) {
            JSONObject jsonInput = new JSONObject();
            HttpURLConnection conn = getConnection("auth");

            conn.setRequestProperty("Authorization", "Bearer " + token);
            jsonInput.put("auth_type", "token");

            APIConnectionHandler.sendToAPI(conn, jsonInput.toString());

            String response = APIConnectionHandler.recvFromAPI(conn);

            conn.disconnect();

            JSONObject obj = new JSONObject(response);
            int HttpCode = obj.getJSONObject("status").getInt("code");

            // Remplacement du token existant par le nouveau, reçu par l'API (si le token existant est encore valide)
            // ce qui implique que l'utilisateur devra utiliser ses identifiants pour se connecter
            // seulement s'il n'utilise pas le programme pendant une durée supérieure à celle de la validité du token
            if (HttpCode == HttpURLConnection.HTTP_OK) {
                String newToken = obj.getJSONObject("data").getString("token");
                createToken(newToken, path);
            }

            // Si le token n'est pas/plus valide
            return HttpCode != HttpURLConnection.HTTP_UNAUTHORIZED;

        } else { // Le token n'existe pas
            return false;
        }
    }

    /**
     * Fonction permettant de créer le fichier contenant le token
     * @param token token à mettre dans le fichier
     * @param path  chemin du fichier à créer
     */
    public static void createToken(String token, String path) throws IOException {
        PrintWriter printWriter = new PrintWriter(new FileWriter(path));
        printWriter.print(token);
        printWriter.close();
    }

    /**
     * Fonction permettant d'extraire le nom d'utilisateur stocké dans le token
     * @param path chemin du fichier contenant le token
     * @return le nom d'utilisateur
     */
    public static String extractUsernameFromToken(String path) throws IOException {

        // Instanciation du décodeur pour récupérer les infos du token JWT
        Base64.Decoder decoder = Base64.getDecoder();

        // Extraction du nom d'utilisateur
        String payload = new String(decoder.decode(getToken(path).split("\\.")[1]));
        return new JSONObject(payload).getString("fullname");
    }

    /**
     * Fonction permettant de récupérer le token dans le fichier
     * @param path chemin du fichier contenant le token
     * @return le token
     */
    public static String getToken(String path) throws IOException {

        File tokenFile = new File(path);

        if (tokenFile.exists() && tokenFile.length() != 0) {
            return Files.readAllLines(tokenFile.toPath()).get(0);
        }
        else {
            return null;
        }
    }
}
