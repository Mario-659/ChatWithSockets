package ChatWithSockets.client.state;

import ChatWithSockets.shared.Request.Request;
import ChatWithSockets.client.Controller;
import ChatWithSockets.shared.Request.RequestType;

import java.io.IOException;

public class WithoutChannel extends State{

    public WithoutChannel(Controller controller){
        super(controller);
        displayInfo("You are now without a channel");
        display(options);
    }
    @Override
    public void handleRequest(Request request, Controller controller) {
        switch (request.getType()){
            case PING:break;
            case REQUESTSUCCEEDED:
                handleSucceeded(request);
                break;
            default:
                display(request.getPayload());
                display(options);
        }
    }

    @Override
    public void handleInput(String input) {
        switch (extractOption(input)) {
            case "join":
                handleJoin(input);
                break;
            case "create":
                handleCreate(input);
                break;
            case "get":
                handleGet();
                break;
            case "exit":
                System.exit(0);
                break;
            default:
                displayInfo("Invalid input. Try again");
                display(options);
        }
    }

    private String extractOption(String input) {
        String[] data = input.split(" ");
        return data[0];
    }

    private void handleSucceeded(Request request) {
        if (lastRequest != null && lastRequest.getType() == RequestType.GETCHANNELS) {
            display("\n" + request.getPayload());
            display(options);
        } else {
            InChannel state = new InChannel(controller, request.getPayload());
            controller.changeState(state);
        }
    }

    private void handleJoin(String input){
        trySend(RequestType.JOINCHANNEL, input);
    }

    private void handleCreate(String input){
        trySend(RequestType.CREATECHANNEL, input);
    }

    private void trySend(RequestType type, String input){
        try {
            sendRequest(type, getChannelPayload(input));
        } catch (IOException e) {
            display(e.getMessage());
        }
    }

    private void handleGet(){
        sendRequest(RequestType.GETCHANNELS, "");
    }

    private String getChannelPayload(String input) throws IOException {
        String[] data = input.split(" ");
        if(data.length != 3) throw new IOException("Invalid input");
        else
            return  data[1].strip().replace(",", "") + "," +
                    data[2].strip().replace(",", "");
    }

    private final String options = "\nAvailable options: \n" +
                                    "create <channelName> <username>: Creates and joins new channel\n" +
                                    "join <channelName> <username>: Joins existing channel\n" +
                                    "get: Gets available channels\n" +
                                    "exit: Close application";
}
