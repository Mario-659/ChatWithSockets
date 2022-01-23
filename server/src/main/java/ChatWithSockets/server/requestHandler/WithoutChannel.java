package ChatWithSockets.server.requestHandler;

import ChatWithSockets.server.channels.ChannelManager;
import ChatWithSockets.server.ClientSession;
import ChatWithSockets.shared.Request.Request;
import ChatWithSockets.shared.Request.RequestType;

import java.io.IOException;
import java.util.Arrays;

public class WithoutChannel extends State{
    public WithoutChannel(ClientSession session, ChannelManager channelManager){
        super(session, channelManager);
    }

    @Override
    public void handleRequest(Request request) {

        switch (request.getType()) {
            case GETCHANNELS:
                handleGetChannels(request);
                break;
            case JOINCHANNEL:
                handleJoinChannel(request);
                break;
            case CREATECHANNEL:
                handleCreateChannel(request);
                break;
            default:
                handleInvalReq(request);
                break;
        }
    }

    private void handleGetChannels(Request request){
        sendRequest(RequestType.REQUESTSUCCEEDED, getChannelsPayload());
    }

    private void handleCreateChannel(Request request) {
        String[] data = retrieveData(request);
        if(data.length!=2) handleInvalFormat(request);
        else{
            try {
                channelManager.createAndJoinChannel(data[0], session, data[1]);
                changeToInChannel();
                sendRequest(RequestType.REQUESTSUCCEEDED,
                        "Created and joined channel " + data[0] + " with username " + data[1]);
            } catch (IOException e) {
                sendIOException(e);
            }
        }
    }

    private void handleJoinChannel(Request request){
        String[] data = retrieveData(request);
        if(data.length!=2) handleInvalFormat(request);
        else{
            try {
                channelManager.joinChannel(data[0], session, data[1]);
                changeToInChannel();
                sendRequest(RequestType.REQUESTSUCCEEDED,
                        "Joined channel " + data[0] +
                                " with username " + data[1]);
            } catch (IOException e) {
                sendIOException(e);
            }
        }
    }

    private String[] retrieveData(Request request){
        return request.getPayload().strip().split(",");
    }

    private String getChannelsPayload(){
        String[] channels = channelManager.getAvailableChannels();
        if(channels.length==0)
            return "No available channels";
        else
            return Arrays.toString(channels);
    }

    private void changeToInChannel(){
        session.getReqHandler().changeState(
                new InChannel(session, channelManager));
    }
}
