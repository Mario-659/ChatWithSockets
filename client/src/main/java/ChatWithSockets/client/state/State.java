package ChatWithSockets.client.state;

import ChatWithSockets.shared.Request.Request;
import ChatWithSockets.client.Controller;
import ChatWithSockets.shared.Request.RequestType;

import java.util.Scanner;

public abstract class State{
    public abstract void handleRequest(Request request, Controller controller);
    public abstract void handleInput(String input);

    protected final Controller controller;
    protected Request lastRequest = null;

    protected State(Controller controller){
        this.controller = controller;
    }

    protected void displayInline(String output){
        System.out.print(output);
    }


    protected void display(String text){
        System.out.println(text);
    }

    protected void displayInfo(String info){
        System.out.println("*****************************");
        System.out.println(info);
        System.out.println("*****************************");
    }

    protected void display(Request request){
        switch (request.getType()){
            case SENDMESSAGE:
                displayChannelMessage(request);
                break;
            case SERVERMESSAGE:
                displayServerMessage(request);
                break;
            case ERROR:
                displayError(request);
                break;
            default:
                display(request.toString());
        }
    }

    private void displayChannelMessage(Request request){
        System.out.println(request.getSender() + ": " + request.getPayload());
    }

    private void displayServerMessage(Request request){
        System.out.println(request.getPayload());
    }

    private void displayError(Request request){
        System.out.println("Error: " + request.getPayload());
    }

    protected void sendRequest(RequestType type, String payload){
        Request request = new Request(type, payload);
        lastRequest = request;
        controller.sendRequest(request);
    }
}
