package ChatWithSockets.server;

import ChatWithSockets.server.channels.Channel;
import ChatWithSockets.server.requestHandler.RequestHandler;
import ChatWithSockets.server.util.IDManager;
import ChatWithSockets.shared.Request.Request;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.net.Socket;

@Log4j2
@Getter
public class ClientSession {
    private final int sessionID;
    private final ClientManager clientManager;
    private boolean isInChannel;
    private Channel channel;
    private final RequestHandler reqHandler;
    private final SocketThread socketThread;

    public ClientSession(Socket clientSocket, ClientManager clientManager) {
        this.sessionID = IDManager.getFreeId();
        this.clientManager = clientManager;
        setChannel(null);
        reqHandler = new RequestHandler(this, clientManager.getChannelManager());
        socketThread = new SocketThread(clientSocket, this);
        socketThread.start();
    }

    public void setChannel(Channel channel){
        isInChannel = channel != null;
        this.channel = channel;
    }

    public void sendRequest(Request request){
        socketThread.sendRequest(request);
        //TODO add delete if connection lost
    }

    public void handleRequest(Request request) {
        reqHandler.handleRequest(request);
    }

    @Override
    public String toString() {
        return "ClientSession{" +
                "id=" + sessionID +
                ", manager=" + clientManager +
                ", isInChannel=" + isInChannel +
                ", channel=" + channel.getChannelName() +
                '}';
    }
}
