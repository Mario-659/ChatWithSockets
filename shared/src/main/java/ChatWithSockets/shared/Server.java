package ChatWithSockets.shared;

import ChatWithSockets.shared.Request.Request;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Server extends Remote {
    void sendRequest(Request request, Client client) throws RemoteException;
}
