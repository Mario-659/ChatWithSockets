package ChatWithSockets.server;

import ChatWithSockets.shared.Client;
import ChatWithSockets.shared.Request.Request;
import lombok.extern.log4j.Log4j2;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

@Log4j2
public class Server implements ChatWithSockets.shared.Server {
    ClientManager clientManager = new ClientManager(this);

    public Server(int port) throws RemoteException {
        UnicastRemoteObject.exportObject(this, port);
    }

    @Override
    public void sendRequest(Request request, Client client) throws RemoteException {
        clientManager.processRequest(request, client);
    }
}
