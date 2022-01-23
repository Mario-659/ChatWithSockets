package ChatWithSockets.server;

import lombok.extern.log4j.Log4j2;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

@Log4j2
public class RunServer {
    public static void main(String[] args) throws AlreadyBoundException, RemoteException {
        Server server = new Server(1099);
        Registry registry = LocateRegistry.createRegistry(1099);
        registry.bind("Server", server);
        System.out.println("Server started");
        log.debug("Server started");
    }
}
