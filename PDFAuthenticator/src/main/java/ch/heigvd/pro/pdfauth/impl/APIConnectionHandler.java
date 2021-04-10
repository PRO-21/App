package ch.heigvd.pro.pdfauth.impl;

import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;

// Classe permettant de gérer la communication avec l'API
public class APIConnectionHandler {

    /**
     * Fonction permettant de se connecter à l'API
     * @return connexion URL à l'API en HTTP
     * @throws IOException
     */
    public static HttpURLConnection getConnection() throws IOException {

        // Désactivation de vérifications SSL car exceptions sinon
        // source : https://stackoverflow.com/questions/19540289/how-to-fix-the-java-security-cert-certificateexception-no-subject-alternative
        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
                (hostname, sslSession) -> {
                    return hostname.equals("pro.simeunovic.ch"); // ou return true
                });

        // Connexion uniquement sur la partie authentification de l'API pour l'instant
        // Modification possible dans le futur pour l'envoi des données à protéger
        URL url = new URL("https://pro.simeunovic.ch:8022/protest/api/auth");
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
     * @throws IOException
     */
    public static void sendToAPI(HttpURLConnection conn, String jsonInputString) throws IOException {

        // Envoi de la demande de token à l'API
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
                System.out.println("Error with reading error stream");
            }
        }
        return response.toString();
    }

    /**
     * Fonction permettant de déterminer si le token existant existe et est toujours valide
     * @return true s'il existe et qu'il est valide, false sinon
     * @throws IOException
     */
    public static boolean tokenExistsAndIsValid() throws IOException {

        // A voir dans quel dossier il faudra le créé lors de la release
        File tokenFile = new File("src/main/resources/ch/heigvd/pro/pdfauth/impl/token");

        if (tokenFile.exists()) {

            String jsonInputString;
            HttpURLConnection conn = getConnection();

            // Récupération du token existant pour demander à l'API s'il est toujours valide
            String token = Files.readAllLines(tokenFile.toPath()).get(0);

            conn.setRequestProperty("Authorization", "Bearer " + token);
            jsonInputString = "{\"auth_type\": \"token\"}";

            APIConnectionHandler.sendToAPI(conn, jsonInputString);

            String response = APIConnectionHandler.recvFromAPI(conn);

            JSONObject obj = new JSONObject(response);
            int HttpCode = obj.getJSONObject("status").getInt("code");

            // Si le token n'est pas/plus valide
            return HttpCode != 401;

        } else { // Le token n'existe pas
            return false;
        }
    }
}
