package ChatWithSockets.shared;

import ChatWithSockets.shared.Request.Request;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Client extends Remote {
    void sendRequest(Request request, Server server) throws RemoteException;
}
