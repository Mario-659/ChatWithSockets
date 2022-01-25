package ChatWithSockets.server;

import ChatWithSockets.shared.Request.Request;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Log4j2
public class SocketThread extends Thread {
    private final ClientSession session;
    private ObjectOutputStream toClient;
    private ObjectInputStream fromClient;

    public SocketThread(Socket socket, ClientSession session){
        this.session = session;
        try{
            toClient = new ObjectOutputStream(socket.getOutputStream());
            fromClient = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            //TODO delete stackTrace
            e.printStackTrace();
            log.error("Error while creating IO stream objects: " + e);
        }
    }

    @Override
    public void run() {
        try{
            while(true){
                Request request = (Request) fromClient.readObject();
                session.handleRequest(request);
            }
        }catch(IOException | ClassNotFoundException e){
            //TODO delete stackTrace
            e.printStackTrace();
            log.error("Error while receiving request from client: " + e);
        }
    }

    public synchronized void sendRequest(Request request) {
        try {
            toClient.writeObject(request);
        } catch (IOException e) {
            //TODO delete stackTrace
            e.printStackTrace();
            log.error("Error while sending request to client: " + e);
        }
    }
}
