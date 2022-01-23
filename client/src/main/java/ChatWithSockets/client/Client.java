package ChatWithSockets.client;

import ChatWithSockets.shared.Request.Request;
import ChatWithSockets.shared.Server;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Client implements ChatWithSockets.shared.Client {
    private Server server;
    Controller controller;

    public Client() throws  RemoteException {
        UnicastRemoteObject.exportObject(this, 0);
    }

    @Override
    public void sendRequest(Request request, Server server) throws RemoteException {
        controller.processRequest(request, server);
    }

    public void startClient() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        server = (Server) registry.lookup("Server");
        controller = new Controller(this, server);
        controller.start();
    }
}
