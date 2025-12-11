package client;

import com.google.gson.Gson;
import java.io.*;
import java.net.*;
import java.util.Map;

public class ServerFacade {
    private final String serverLink;
    private final Gson gson = new Gson();
    private String authToken = null;

    public ServerFacade(int port) {
        this.serverLink = "http://localhost:" + port;
    }

    public ServerFacade(String url) {
        this.serverLink = url;
    }


    private <T> T serverRequest(String method, String path, Object request, Class<T> responseClass) throws Exception {
        URL url = new URL(serverLink + path);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod(method);
        http.setDoOutput(true);
        http.addRequestProperty("Content-Type", "application/json");

        if (authToken != null) {
            http.addRequestProperty("Authorization", authToken);
        }

        if (request != null) {
            try (var outputStream = http.getOutputStream()) {
                String jsonRequest = gson.toJson(request);
                outputStream.write(jsonRequest.getBytes());
            }
        }

        http.connect();

        int status = http.getResponseCode();
        if (status != 200) {
            throw new Exception("Error: " + status);
        }

        if (responseClass != null) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                return gson.fromJson(reader, responseClass);
            }
        }
        return null;
    }

    public AuthData register(String username, String password, String email) throws Exception {
        var request = Map.of("username", username, "password", password, "email", email);
        AuthData authData = serverRequest("POST", "/user", request, AuthData.class);
        authToken = authData.authToken();
        return authData;
    }

    public AuthData login(String username, String password) throws Exception {
        var request = Map.of("username", username, "password", password);
        AuthData authData = serverRequest("POST", "/session", request, AuthData.class);
        authToken = authData.authToken();
        return authData;
    }

    public void logout() throws Exception {
        serverRequest("DELETE", "/session", null, null);
        authToken = null;
    }

    public GameData createGame(String gameName) throws Exception {
        var request = Map.of("gameName", gameName);
        return serverRequest("POST", "/game", request, GameData.class);
    }

    public Games games() throws Exception {
        return serverRequest("GET", "/game", null, Games.class);
    }

    public void joinGame(String playerColor, int gameID) throws Exception {
        var request = Map.of("playerColor", playerColor, "gameID", gameID);
        serverRequest("PUT", "/game", request, null);
    }

    public void clear() throws Exception {
        serverRequest("DELETE", "/db", null, null);
        authToken = null;
    }

    public record AuthData(String authToken, String username) {}
    public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, chess.ChessGame game) {}
    public record Games(GameData[] games) {}

}
