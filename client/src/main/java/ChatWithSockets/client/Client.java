package ChatWithSockets.client;

import java.net.Socket;


public class Client {
    Controller controller;

    public void startClient() {
        tryConnect();
        controller.start();
    }

    public void tryConnect(String host, int port) {
        try{
            Socket socket = new Socket(host, port);
            controller = new Controller(socket);
        } catch (Exception e) {
            System.out.println("Failed to connect with given arguments");
        }
    }

    public void tryConnect(){
        tryConnect("localhost", 55);
    }
}
