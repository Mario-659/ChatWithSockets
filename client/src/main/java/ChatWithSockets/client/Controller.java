package ChatWithSockets.client;

import ChatWithSockets.shared.Request.Request;
import ChatWithSockets.client.state.State;
import ChatWithSockets.client.state.WithoutChannel;
import ChatWithSockets.shared.Server;

import java.rmi.RemoteException;


public class Controller{
    private State state;
    private Server server;
    private final Client client;

    public Controller(Client client, Server server) {
        this.client = client;
        this.server = server;
        this.state = new WithoutChannel(this);
    }

    public void start(){
        state.run();
    }

    public void processRequest(Request request, Server server){
        state.handleRequest(request, this);
    }

    public void changeState(State state){
        this.state = state;
        state.run();
    }

    public void sendRequest(Request request){
        try {
            server.sendRequest(request, client);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
