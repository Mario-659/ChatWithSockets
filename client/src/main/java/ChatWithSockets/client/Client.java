package ChatWithSockets.client;

import java.io.IOException;
import java.net.Socket;

public class Client {
    private Controller controller;

    public Client(String host, int port) throws IOException {
        Socket socket = new Socket(host, port);
        controller = new Controller(socket);
    }

    public void startClient(){
        controller.start();
    }
}
