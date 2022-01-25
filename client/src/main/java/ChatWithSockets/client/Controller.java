package ChatWithSockets.client;

import ChatWithSockets.shared.Request.Request;
import ChatWithSockets.client.state.State;
import ChatWithSockets.client.state.WithoutChannel;

import java.net.Socket;


public class Controller{
    private State state;
    private SocketThread socketThread;

    public Controller(Socket socket) {
        socketThread = new SocketThread(socket, this);
        this.state = new WithoutChannel(this);
    }

    public void start(){
        socketThread.start();
        state.run();
    }

    public void processRequest(Request request){
        state.handleRequest(request, this);
    }

    public void changeState(State state){
        this.state = state;
        state.run();
    }

    public void sendRequest(Request request){
        socketThread.sendRequest(request);
    }
}
