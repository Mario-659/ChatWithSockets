package ChatWithSockets.client;

import ChatWithSockets.shared.Request.Request;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketThread extends Thread {
    private Controller controller;
    private ObjectOutputStream toServer;
    private ObjectInputStream fromServer;

    public SocketThread(Socket socket, Controller controller){
        this.controller = controller;
        try{
            toServer = new ObjectOutputStream(socket.getOutputStream());
            fromServer = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try{
            while(true){
                Request request = (Request) fromServer.readObject();
                controller.processRequest(request);
            }
        }catch(IOException | ClassNotFoundException e){
            handleConnectionLost(e);
        }
    }

    public void sendRequest(Request request) {
        try {
            toServer.writeObject(request);
        } catch (IOException e) {
            handleConnectionLost(e);
        }
    }

    private void handleConnectionLost(Exception e){
        System.out.println("Lost connection with server\n" +
                "Error message: " + e.getMessage());
        System.exit(0);
    }
}

