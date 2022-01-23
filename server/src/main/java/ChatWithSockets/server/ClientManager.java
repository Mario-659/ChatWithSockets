package ChatWithSockets.server;

import ChatWithSockets.server.channels.ChannelManager;
import ChatWithSockets.server.util.Pinger;
import ChatWithSockets.shared.Client;
import ChatWithSockets.shared.Request.Request;
import ChatWithSockets.shared.Server;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;

@Log4j2
public class ClientManager {
    private final HashMap<Client, ClientSession> clients = new HashMap<>();
    private final ChannelManager channelManager = new ChannelManager();
    private final Server server;
    private final Pinger pinger;

    public ClientManager(Server server) {
        this.server = server;
        pinger = new Pinger(5);
    }

    public void processRequest(Request request, Client client) {
        ClientSession session = clients.get(client);
        if(session == null) {
            registerClient(client);
            processRequest(request, client);
        }
        else session.handleRequest(request);
    }

    public void deleteClient(ClientSession session){
        if(session.isInChannel()) channelManager.leaveChannel(session);
        clients.remove(session.getClient());
        pinger.deleteSession(session);
        log.debug("Deleted session with id: " + session.getSessionID());
    }

    public ChannelManager getChannelManager(){
        return channelManager;
    }

    private void registerClient(Client client) {
        if(clients.containsKey(client)){
            RuntimeException e = new RuntimeException("Tried to register client that was already registered." +
                    " Client=" + client + " AllClients=" + clients);
            log.debug(e.getMessage());
            throw e;
        }
        else {
            ClientSession session = new ClientSession(client, this);
            clients.put(client, session);
            pinger.addSession(session);
            log.debug("Registered new client");
        }
    }
    public Server getServer(){
        return this.server;
    }
}
