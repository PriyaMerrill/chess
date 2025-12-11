package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String visitorName, Session session, int gameID) {
        var connection = new Connection(visitorName, session, gameID);
        connections.put(visitorName, connection);
    }

    public void remove(String visitorName) {
        connections.remove(visitorName);
    }

    public void broadcast(int gameID, String excludeUser, String message) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var conn : connections.values()) {
            if (conn.session.isOpen()) {
                if (conn.gameID == gameID && !conn.visitorName.equals(excludeUser)) {
                    conn.send(message);
                }
            } else {
                removeList.add(conn);
            }
        }
        // Clean up closed connections
        for (var conn : removeList) {
            connections.remove(conn.visitorName);
        }
    }

    public void sendToUser(String username, String message) throws IOException {
        var conn = connections.get(username);
        if (conn != null && conn.session.isOpen()) {
            conn.send(message);
        }
    }
}
