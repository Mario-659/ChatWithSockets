package ChatWithSockets.client;

import ChatWithSockets.shared.Request.Request;
import ChatWithSockets.client.state.State;
import ChatWithSockets.client.state.WithoutChannel;

import java.net.Socket;

public class Controller{
    private State state;
    private final SocketThread socketThread;
    private final UserInput userInput;

    public Controller(Socket socket) {
        socketThread = new SocketThread(socket, this);
        userInput = new UserInput(this);
    }

    public void start(){
        this.state = new WithoutChannel(this);
        socketThread.start();
        userInput.start();
    }

    public void processInput(String input){
        state.handleInput(input);
    }

    public void processRequest(Request request){
        state.handleRequest(request, this);
    }

    public void changeState(State state){
        this.state = state;
    }

    public void sendRequest(Request request){
        socketThread.sendRequest(request);
    }
}
