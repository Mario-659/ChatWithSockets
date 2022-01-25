package ChatWithSockets.server;

import ChatWithSockets.server.channels.ChannelManager;
import ChatWithSockets.server.util.Pinger;
import lombok.extern.log4j.Log4j2;

import java.net.Socket;
import java.util.HashSet;

@Log4j2
public class ClientManager {
    private final HashSet<ClientSession> clients = new HashSet<>();
    private final ChannelManager channelManager = new ChannelManager();
    private final Pinger pinger;

    public ClientManager() {
        pinger = new Pinger(5);
    }

    public void deleteClient(ClientSession session){
        if(session.isInChannel()) channelManager.leaveChannel(session);
        clients.remove(session);
        pinger.deleteSession(session);
        log.debug("Deleted session with id: " + session.getSessionID());
    }

    public ChannelManager getChannelManager(){
        return channelManager;
    }

    public void addClient(Socket clientSocket) {
        ClientSession session = new ClientSession(clientSocket, this);
        clients.add(session);
        pinger.addSession(session);
    }
}
