package ChatWithSockets.server.channels;

import ChatWithSockets.server.ClientSession;
import ChatWithSockets.shared.Request.Request;
import ChatWithSockets.shared.Request.RequestType;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.HashMap;

@Log4j2
@Getter
public class Channel {
    private final HashMap<ClientSession, String> users = new HashMap<>();
    private final String channelName;

    protected Channel(String channelName){
        this.channelName = channelName;
    }

    protected void addUser(ClientSession session, String username) throws IOException {
        validateSession(session);
        validateUsername(username);
        users.put(session, username);
        session.setChannel(this);
        sendToAll(new Request(RequestType.SERVERMESSAGE, username + " joined the chat"), session);
    }

    protected void deleteUser(ClientSession clientSession){
        String username = users.get(clientSession);
        users.remove(clientSession);
        clientSession.setChannel(null);
        sendToAll(new Request(RequestType.SERVERMESSAGE, username + " left the chat"));
    }

    protected void sendToAll(Request request, ClientSession exclude){
        for (ClientSession clientSession : users.keySet()) {
            if(clientSession!=exclude) clientSession.sendRequest(request);
        }
    }

    protected void sendToAll(Request request){
        for (ClientSession clientSession : users.keySet()) {
            clientSession.sendRequest(request);
        }
    }

    public void sendChatMessage(String message, ClientSession sender){
        String username = users.get(sender);
        sendToAll(new Request(RequestType.SENDMESSAGE,
                message, username), sender);
    }

    private void validateSession(ClientSession session){
        for (ClientSession channelClient : users.keySet()) {
            if(channelClient == session) {
                RuntimeException e = new RuntimeException("Client already in channel. " +
                        "Client in channel=" + channelClient +
                        ", client to join=" + session);
                log.debug(e.getMessage());
                throw e;
            }
        }
    }

    private void validateUsername(String username) throws IOException {
        for (String channelUsername : users.values()) {
            if(channelUsername.equals(username)) throw new IOException("Username " + username + " is already taken in " + channelName);
        }
    }

    @Override
    public String toString() {
        return "Channel{" +
                "users=" + users +
                ", channelName='" + channelName + '}';
    }
}
