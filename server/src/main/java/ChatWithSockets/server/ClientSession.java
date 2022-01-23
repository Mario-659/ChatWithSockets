package ChatWithSockets.server;

import ChatWithSockets.server.channels.Channel;
import ChatWithSockets.server.requestHandler.RequestHandler;
import ChatWithSockets.server.util.IDManager;
import ChatWithSockets.shared.Client;
import ChatWithSockets.shared.Request.Request;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.rmi.RemoteException;

@Log4j2
@Getter
public class ClientSession {
    private final int sessionID;
    private final Client client;
    private final ClientManager clientManager;
    private boolean isInChannel;
    private Channel channel;
    private final RequestHandler reqHandler;

    public ClientSession(Client client, ClientManager clientManager) {
        this.sessionID = IDManager.getFreeId();
        this.client = client;
        this.clientManager = clientManager;
        setChannel(null);
        reqHandler = new RequestHandler(this, clientManager.getChannelManager());
    }

    public void setChannel(Channel channel){
        isInChannel = channel != null;
        this.channel = channel;
    }

    public void sendRequest(Request request){
        try {
            client.sendRequest(request, clientManager.getServer());
        } catch (RemoteException e) {
            log.debug("Error occurred while sending request to client. Session id: " + sessionID);
            clientManager.deleteClient(this);
        }
    }

    public void handleRequest(Request request) {
        reqHandler.handleRequest(request);
    }

    @Override
    public String toString() {
        return "ClientSession{" +
                "id=" + sessionID +
                ", client=" + client +
                ", manager=" + clientManager +
                ", isInChannel=" + isInChannel +
                ", channel=" + channel.getChannelName() +
                '}';
    }
}
