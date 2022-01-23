package ChatWithSockets.client.state;

import ChatWithSockets.shared.Request.Request;
import ChatWithSockets.client.Controller;
import ChatWithSockets.shared.Request.RequestType;

public class WithoutChannel extends State{

    public WithoutChannel(Controller controller){
        super(controller);
        displayInfo("You are now without a channel");
    }

    @Override
    public void run(){
        display(options);
        String input = getInput();
        switch (input){
            case "join":
                handleJoin();
                break;
            case "create":
                handleCreate();
                break;
            case "get":
                handleGet();
                break;
            default:
                displayInfo("Invalid data. Try again");
                run();
        }
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
                run();
        }
    }

    private void handleSucceeded(Request request) {
        if (lastRequest != null && lastRequest.getType() == RequestType.GETCHANNELS) {
            display("\n" + request.getPayload());
            run();
        } else {
            controller.changeState(new InChannel(controller, request.getPayload()));
        }
    }

    private void handleJoin(){
        sendRequest(RequestType.JOINCHANNEL, getChannelData());
    }

    private void handleCreate(){
        sendRequest(RequestType.CREATECHANNEL, getChannelData());
    }

    private void handleGet(){
        sendRequest(RequestType.GETCHANNELS, "");
    }

    private String getChannelData(){
        System.out.print("Channel name: ");
        String channelName = getInput().strip().replace(",", "");
        System.out.print("Username: ");
        String username = getInput().strip().replace(",", "");
        return channelName + "," + username;
    }

    private final String options = "\n\nAvailable options: \n" +
                                    "create: Creates and joins new channel\n" +
                                    "join: Joins existing channel\n" +
                                    "get: Gets available channels";
}
