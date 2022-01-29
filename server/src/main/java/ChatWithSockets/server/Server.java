package ChatWithSockets.server;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

@Log4j2
public class Server {
    private final int BACKLOG = 50;
    private ServerSocket serverSocket;
    private ClientManager manager = new ClientManager();

    public Server(String host, int port) throws IOException{
        try {
            log.debug("Starting server at host: " + host + ", port: " + port);
            serverSocket = new ServerSocket(port, BACKLOG, InetAddress.getByName(host));
            log.debug("Server started");
        } catch (IOException e) {
            log.error(e);
            throw e;
        }
    }

    public void run(){
        while(true){
            try {
                Socket clientSocket = serverSocket.accept();
                log.debug("New client accepted");
                manager.addClient(clientSocket);
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }
}
